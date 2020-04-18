package com.PollBuzz.pollbuzz;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.LoginSignup.ProfileSetUp;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.Utils.helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.google.firebase.firestore.DocumentSnapshot;

public class SplashScreen extends AppCompatActivity {

    ProgressBar splashProgress;
    int SPLASH_TIME = 3000; //3 seconds
    FirebaseAnalytics mFirebaseAnalytics;
    firebase fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fb = new firebase();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startIntent();
            }
        }, SPLASH_TIME);
        splashProgress = findViewById(R.id.splashProgress);
        playProgress();
        fade();
        tvanim();
    }


    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(3000)
                .start();
    }

    public void fade() {
        ImageView image = (ImageView) findViewById(R.id.imagee);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade);
        image.startAnimation(animation1);
    }

    public void tvanim() {
        TextView tv = (TextView) findViewById(R.id.txt);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.txt_animation_1);
        tv.startAnimation(animation1);
        Animation animation2 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.txt_animation_2);
        tv.startAnimation(animation2);

    }
    private void startIntent() {
        if (!isUserLoggedIn(fb)) {
            helper.removeProfileSetUpPref(getApplicationContext());
            Bundle bundle = new Bundle();
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            mFirebaseAnalytics.logEvent("open_by_unknown", bundle);
            Intent i = new Intent(this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            fb.getUsersCollection().document(fb.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Intent i = new Intent(SplashScreen.this, ProfileSetUp.class);
                    if (task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot dS = task.getResult();
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", fb.getUserId());
                        if (dS != null && dS.get("username") != null) {
                            bundle.putString("username", dS.get("username").toString());
                        }
                        bundle.putString("timestamp", Timestamp.now().toDate().toString());
                        mFirebaseAnalytics.logEvent("opened", bundle);
                        i = new Intent(SplashScreen.this, MainActivity.class);
                    }
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
        }
    }

    boolean isUserLoggedIn(firebase fb) {
        return fb.getUser() != null;
    }
}
