package com.PollBuzz.pollbuzz.LoginSignup;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.PollBuzz.pollbuzz.R;

public class LoginSignupActivity extends AppCompatActivity {
    private Button login, signup;
    private Fragment login_frag, signup_frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        setGlobals();
        setLoginFragment();
        setListeners();
    }

    private void setListeners() {
        login.setOnClickListener(v -> setLoginFragment());
        signup.setOnClickListener(v -> setSignupFragment());
    }

    void setGlobals() {
        getSupportActionBar().hide();
        login_frag = new LoginFragment();
        signup_frag = new SignupFragment();
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
    }

    void setLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, login_frag, "Login Fragment");
        ft.commit();
        login.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        login.setTextColor(getResources().getColor(R.color.white));
        login.setAlpha(1.0f);
        signup.setBackgroundColor(getResources().getColor(R.color.white));
        signup.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        signup.setAlpha(0.5f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            signup.setElevation(-0.5f);
        }
    }

    void setSignupFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, signup_frag, "Signup Fragment");
        ft.commit();
        signup.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        signup.setTextColor(getResources().getColor(R.color.white));
        login.setBackgroundColor(getResources().getColor(R.color.white));
        login.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        signup.setAlpha(1.0f);
        login.setAlpha(0.5f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            login.setElevation(-0.5f);
        }
    }

    public void refreshUI() {
        finish();
        startActivity(getIntent());
    }
}
