package com.PollBuzz.pollbuzz.LoginSignup;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.R;
import com.kinda.alert.KAlertDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupFragment extends Fragment {

    private TextInputLayout emailL, passwordL, password2L;
    private Button signup;
    private firebase fb;
    private KAlertDialog dialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    public SignupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setGlobals(view);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup() {
        String email = emailL.getEditText().getText().toString();
        String password = passwordL.getEditText().getText().toString();
        String password2 = password2L.getEditText().getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
            emailL.requestFocus();
        } else if (password.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            passwordL.requestFocus();
        } else if (password2.isEmpty()) {
            Toast.makeText(getContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
            password2L.requestFocus();
        } else if (!password.equals(password2)) {
            Toast.makeText(getContext(), "Passwords must be same", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Signup successful.\nPlease verify your mail.", Toast.LENGTH_LONG).show();
                            fb.getAuth().signOut();
                        });
                    } else {
                        dialog.dismissWithAnimation();
                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        passwordL.getEditText().getText().clear();
                        password2L.getEditText().getText().clear();
                    }
                });
            }catch (Exception e){
                FirebaseCrashlytics.getInstance().log(e.getMessage());
            }
        }
    }

    private void setGlobals(View view) {
        emailL = (TextInputLayout) view.findViewById(R.id.email);
        passwordL = (TextInputLayout) view.findViewById(R.id.password);
        password2L = (TextInputLayout) view.findViewById(R.id.password2);
        signup = view.findViewById(R.id.signup);
        dialog=new KAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        fb = new firebase();
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getContext());
    }
    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Creating account...");
        dialog.setCancelable(false);
        dialog.show();
    }
}
