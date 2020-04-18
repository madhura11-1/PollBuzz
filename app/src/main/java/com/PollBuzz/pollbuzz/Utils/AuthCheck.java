package com.PollBuzz.pollbuzz.Utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.LoginSignup.ProfileSetUp;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;

public class AuthCheck extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;
    firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_check);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fb = new firebase();
        startIntent();
    }

    private void startIntent() {
        if (!isUserLoggedIn(fb)) {
            helper.removeProfileSetUpPref(getApplicationContext());
            Bundle bundle = new Bundle();
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            mFirebaseAnalytics.logEvent("open_by_unknown", bundle);
            Intent i = new Intent(AuthCheck.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            fb.getUsersCollection().document(fb.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Intent i = new Intent(AuthCheck.this, ProfileSetUp.class);
                    if (task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot dS = task.getResult();
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", fb.getUserId());
                        if (dS != null && dS.get("username") != null) {
                            bundle.putString("username", dS.get("username").toString());
                        }
                        bundle.putString("timestamp", Timestamp.now().toDate().toString());
                        mFirebaseAnalytics.logEvent("opened", bundle);
                        i = new Intent(AuthCheck.this, MainActivity.class);
                    }
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        }
    }

    Boolean isUserLoggedIn(firebase fb) {
        return fb.getUser() != null;
    }
}
