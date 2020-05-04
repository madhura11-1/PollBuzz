package com.PollBuzz.pollbuzz.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kinda.alert.KAlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SigninActivity extends AppCompatActivity {

    private TextInputLayout emailL, passwordL, password2L;
    private Button signup;
    private firebase fb;
    private KAlertDialog dialog;
    private TextView already_account;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setGlobals();
        setListeners();
    }

    private void setListeners() {
        already_account.setPaintFlags(already_account.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        already_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void signup() {
        String email = emailL.getEditText().getText().toString();
        String password = passwordL.getEditText().getText().toString();
        String password2 = password2L.getEditText().getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(SigninActivity.this, "Email can't be empty", Toast.LENGTH_SHORT).show();
            emailL.requestFocus();
        } else if (password.isEmpty()) {
            Toast.makeText(SigninActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
            passwordL.requestFocus();
        } else if (password2.isEmpty()) {
            Toast.makeText(SigninActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
            password2L.requestFocus();
        } else if (!password.equals(password2)) {
            Toast.makeText(SigninActivity.this, "Passwords must be same", Toast.LENGTH_SHORT).show();
            passwordL.getEditText().getText().clear();
            password2L.getEditText().getText().clear();
            passwordL.requestFocus();
        } else {
            try {
                showDialog();
                fb.getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fb.getUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", fb.getUserId());
                            bundle.putString("timestamp", Timestamp.now().toDate().toString());
                            mFirebaseAnalytics.logEvent("signup", bundle);
                            dialog.dismissWithAnimation();
                            Toast.makeText(SigninActivity.this, "Signup successful.\nPlease verify your mail.", Toast.LENGTH_LONG).show();
                            fb.getAuth().signOut();
                        });
                    } else {
                        dialog.dismissWithAnimation();
                        Toast.makeText(SigninActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        passwordL.getEditText().getText().clear();
                        password2L.getEditText().getText().clear();
                        emailL.clearFocus();
                        passwordL.clearFocus();
                        password2L.clearFocus();
                    }
                });
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(e.getMessage());
                Crashlytics.logException(e);
            }
        }
    }

    private void setGlobals() {
        emailL = (TextInputLayout)findViewById(R.id.email);
        passwordL = (TextInputLayout)findViewById(R.id.password);
        password2L = (TextInputLayout)findViewById(R.id.password2);
        signup = findViewById(R.id.fragment_signup);
        already_account = findViewById(R.id.already_account);
        fb = new firebase();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
    }

    private void showDialog() {
        dialog = new KAlertDialog(getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Creating account...");
        dialog.setCancelable(false);
        dialog.show();
    }
}
