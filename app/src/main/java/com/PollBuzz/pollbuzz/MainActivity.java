package com.PollBuzz.pollbuzz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    public static SmoothBottomBar bottomBar;
    private FragmentManager fm;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setGlobals();
        setBottomBar();
        setListeners();
    }

    private void setGlobals() {
        bottomBar = findViewById(R.id.bottom);
        fab = findViewById(R.id.fab);
        try {
            YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(fab);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
        fm = getSupportFragmentManager();
        newFragment(new HomeFeed(), "0");
    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(MainActivity.this, PollList.class);
            startActivity(i);
        });
    }

    private void setBottomBar() {
        bottomBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case 0:
                    newFragment(new HomeFeed(), "0");
                    break;
                case 1:
                    newFragment(new VotedFeed(), "1");
                    break;
                case 2:
                    newFragment(new ProfileFeed(), "2");
                    break;
            }
        });
    }

    private void newFragment(Fragment fragment, String id) {
        try {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.container, fragment, id)
                    .commit();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}