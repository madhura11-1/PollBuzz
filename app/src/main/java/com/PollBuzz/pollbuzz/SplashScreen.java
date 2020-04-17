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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.Utils.AuthCheck;

public class SplashScreen extends AppCompatActivity {

    ProgressBar splashProgress;
    int SPLASH_TIME = 3000; //3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        splashProgress = findViewById(R.id.splashProgress);
        playProgress();
        fade();
        tvanim();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mySuperIntent;
                mySuperIntent = new Intent(SplashScreen.this, AuthCheck.class);
                startActivity(mySuperIntent);


                finish();

            }
        }, SPLASH_TIME);
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
}
