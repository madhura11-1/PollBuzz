package com.PollBuzz.pollbuzz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import Utils.AuthCheck;

public class SplashScreen extends AppCompatActivity {

    ProgressBar splashProgress;
    int SPLASH_TIME = 3000; //3 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashProgress = findViewById(R.id.splashProgress);
        playProgress();

        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mySuperIntent;
                mySuperIntent = new Intent(SplashScreen.this, AuthCheck.class );
                startActivity(mySuperIntent);


                finish();

            }
        }, SPLASH_TIME);
    }

    
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(1500)
                .start();
    }
}
