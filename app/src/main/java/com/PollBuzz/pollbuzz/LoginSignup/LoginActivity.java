package com.PollBuzz.pollbuzz.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kinda.alert.KAlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email, password;
    private TextView create_account,forget_password;
    private Button login;
    private LinearLayout gsignin;
    private GoogleSignInClient googleSignInClient;
    private firebase fb;
    private KAlertDialog dialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setGlobals();
        setListeners();
    }

    private void setListeners() {
        create_account.setPaintFlags(create_account.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forget_password.setPaintFlags(forget_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        login.setOnClickListener(v -> {
            login();
        });
        gsignin.setOnClickListener(v -> googleSignin());

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    void setGlobals() {
        email = (TextInputLayout)findViewById(R.id.email);
        password = (TextInputLayout)findViewById(R.id.password);
        login = findViewById(R.id.fragment_login);
        gsignin = findViewById(R.id.gsignin);
        create_account = findViewById(R.id.create_account);
        forget_password = findViewById(R.id.forget_password);
        fb = new firebase();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(),gso);
        googleSignInClient.signOut();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
    }

    private void login() {
        String emailS = email.getEditText().getText().toString();
        String passwordS = password.getEditText().getText().toString();
        if (emailS.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email can't be empty", Toast.LENGTH_SHORT).show();
            this.email.requestFocus();
        } else if (passwordS.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
            this.password.requestFocus();
        } else {
            closeKeyboard();
            showDialog();
            fb.getAuth().signInWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!fb.getUser().isEmailVerified()) {
                        dialog.dismissWithAnimation();
                        Toast.makeText(LoginActivity.this, "Please verify your mail.", Toast.LENGTH_SHORT).show();
                        fb.signOut(LoginActivity.this);
                    } else {
                        fb.getUserDocument().get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot dS = task1.getResult();
                                Bundle bundle = new Bundle();
                                bundle.putString("user_id", fb.getUserId());
                                if (dS != null && dS.exists() && dS.get("username") != null)
                                    bundle.putString("username", dS.get("username").toString());
                                bundle.putString("timestamp", Timestamp.now().toDate().toString());
                                mFirebaseAnalytics.logEvent("login", bundle);
                                Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                isProfileSet(dS);
                            } else {
                                dialog.dismissWithAnimation();
                                Toast.makeText(LoginActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                password.getEditText().getText().clear();
                            }
                        });
                    }
                } else {
                    dialog.dismissWithAnimation();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    password.getEditText().getText().clear();
                }
            });
        }
    }

    private void isProfileSet(DocumentSnapshot dS) {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        if (dS != null && dS.exists()) {
            fb.getUserDocument().collection("Favourite Authors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot dS : task.getResult()) {
                            FirebaseMessaging.getInstance().subscribeToTopic(dS.getId());
                        }
                    }
                }
            });
            setSharedPreference(dS);
        } else {
            i = new Intent(LoginActivity.this, ProfileSetUp.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        dialog.dismissWithAnimation();
        startActivity(i);
    }

    private void setSharedPreference(DocumentSnapshot dS) {
        if (dS.get("pic") != null)
            com.PollBuzz.pollbuzz.Utils.helper.setpPicPref(LoginActivity.this, String.valueOf(dS.get("pic")));
        else
            com.PollBuzz.pollbuzz.Utils.helper.setpPicPref(LoginActivity.this, null);
        com.PollBuzz.pollbuzz.Utils.helper.setusernamePref(LoginActivity.this, String.valueOf(dS.get("username")));
    }

    private void googleSignin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                if (e.getMessage() != null) {
                    Log.d("error", e.getMessage());
                    FirebaseCrashlytics.getInstance().log(e.getMessage());
                }
                Toast.makeText(LoginActivity.this, "Google Sign In failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        try {
            closeKeyboard();
            showDialog();
            final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            fb.getAuth().signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            fb.getUserDocument().get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot dS = task1.getResult();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("user_id", fb.getUserId());
                                    if (dS != null && dS.exists() && dS.get("username") != null)
                                        bundle.putString("username", dS.get("username").toString());
                                    bundle.putString("timestamp", Timestamp.now().toDate().toString());
                                    mFirebaseAnalytics.logEvent("gLogin", bundle);
                                    Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                    isProfileSet(dS);
                                } else {
                                    dialog.dismissWithAnimation();
                                    Toast.makeText(LoginActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("UID", task1.getException().toString());
                                    password.getEditText().getText().clear();
                                }
                            });
                        } else {
                            dialog.dismissWithAnimation();
                            Toast.makeText(LoginActivity.this, "Google Sign In Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void showDialog() {
        dialog = new KAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Getting things ready for you...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void closeKeyboard() {
        if (getParent()!= null) {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }
}
