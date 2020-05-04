package com.PollBuzz.pollbuzz;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.PollBuzz.pollbuzz.LoginSignup.LoginActivity;
import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.LoginSignup.ProfileSetUp;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.Utils.helper;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.google.firebase.firestore.DocumentSnapshot;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SplashScreen extends AppCompatActivity {

    ProgressBar splashProgress;

    int SPLASH_TIME = 3000; //3 seconds
    FirebaseAnalytics mFirebaseAnalytics;
    firebase fb;
    TextView tv;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fb = new firebase();
        /*splashProgress=findViewById(R.id.splashProgress);
        splashProgress.setVisibility(View.VISIBLE);*/
        image = (ImageView) findViewById(R.id.imagee);
    }


   /*private void playProgress() {
     ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(3000)
               .start();
    }*/

    public void fade() {
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade);
        image.startAnimation(animation1);
    }

    /*public void tvanim() {
        Animation animation2 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.txt_animation_2);
        tv.startAnimation(animation2);
    }*/

    private void startIntent() {
        if (!isUserLoggedIn()) {
            helper.removeProfileSetUpPref(getApplicationContext());
            Bundle bundle = new Bundle();
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            mFirebaseAnalytics.logEvent("open_by_unknown", bundle);
            Intent i = new Intent(this, LoginActivity.class);
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

    boolean isUserLoggedIn() {
        return fb.getUser() != null;
    }

    private boolean isInternetAvailable(Context context) {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = true;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private void showDialog() {
        Dialog dialog = new Dialog(SplashScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.no_internet_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
        splashProgress.setVisibility(View.GONE);
        /*tv.getAnimation().cancel();
        tv.clearAnimation();*/
        image.getAnimation().cancel();
        image.clearAnimation();
        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
       //playProgress();
        //fade();
        //tvanim();
        boolean f = isInternetAvailable(SplashScreen.this);
        if (!f)
            showDialog();
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startIntent();
                }
            }, SPLASH_TIME);
        }
    }
}
