package com.PollBuzz.pollbuzz.LoginSignup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
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

import java.util.Objects;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginFragment extends Fragment {

    private TextInputLayout email, password;
    private Button login;
    private SignInButton gsignin;
    private GoogleSignInClient googleSignInClient;
    private firebase fb;
    private KAlertDialog dialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setGlobals(view);
        setListeners();
    }

    private void setListeners() {
        login.setOnClickListener(v -> {
            login();
        });
        gsignin.setOnClickListener(v -> googleSignin());
    }

    void setGlobals(View view) {
        email = (TextInputLayout) view.findViewById(R.id.email);
        password = (TextInputLayout) view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        gsignin = view.findViewById(R.id.gsignin);
        dialog = new KAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        fb = new firebase();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInClient.signOut();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    private void login() {
        String emailS = email.getEditText().getText().toString();
        String passwordS = password.getEditText().getText().toString();
        if (emailS.isEmpty()) {
            Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
            this.email.requestFocus();
        } else if (passwordS.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            this.password.requestFocus();
        } else {
            closeKeyboard();
            showDialog();
            fb.getAuth().signInWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!fb.getUser().isEmailVerified()) {
                        dialog.dismissWithAnimation();
                        Toast.makeText(getContext(), "Please verify your mail.", Toast.LENGTH_SHORT).show();
                        fb.signOut(getContext());
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
                                Toast.makeText(getActivity(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                isProfileSet(dS);
                            } else {
                                dialog.dismissWithAnimation();
                                Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                //password.getEditText().getText().clear();
                                ((LoginSignupActivity) Objects.requireNonNull(getActivity())).refreshUI();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    //password.getEditText().getText().clear();
                    ((LoginSignupActivity) Objects.requireNonNull(getActivity())).refreshUI();
                }
            });
        }
    }

    private void isProfileSet(DocumentSnapshot dS) {
        Intent i = new Intent(getActivity(), MainActivity.class);
        if (dS != null && dS.exists()) {
            fb.getUserDocument().collection("Favourite Authors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && task.getResult()!=null){
                        for(DocumentSnapshot dS:task.getResult()){
                            FirebaseMessaging.getInstance().subscribeToTopic(dS.getId());
                        }
                    }
                }
            });
            setSharedPreference(dS);
        } else {
            removeSharedPreference();
            i = new Intent(getActivity(), ProfileSetUp.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        dialog.dismissWithAnimation();
        startActivity(i);
    }

    private void removeSharedPreference() {
        Utils.helper.setProfileSetUpPref(getContext(), false);
    }

    private void setSharedPreference(DocumentSnapshot dS) {
        Utils.helper.setProfileSetUpPref(getContext(), true);
        if (dS.get("pic") != null)
            Utils.helper.setpPicPref(getContext(), String.valueOf(dS.get("pic")));
        else
            Utils.helper.setpPicPref(getContext(), null);
        Utils.helper.setusernamePref(getContext(), String.valueOf(dS.get("username")));
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
                Toast.makeText(getContext(), "Google Sign In failed!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                                    dialog.dismissWithAnimation();
                                    isProfileSet(dS);
                                } else {
                                    dialog.dismissWithAnimation();
                                    Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("UID", task1.getException().toString());
                                    password.getEditText().getText().clear();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Google Sign In Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Getting things ready for you...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void closeKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }
}
