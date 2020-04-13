package com.PollBuzz.pollbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import Utils.firebase;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    public static SmoothBottomBar bottomBar;
    private FragmentManager fm;
    private FloatingActionButton fab;
    private ImageButton logout;
    firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().show();
        final ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        view.findViewById(R.id.home).setVisibility(View.GONE);
        setGlobals(view);
        setBottomBar();
        setListeners();
    }

    private void setGlobals(View view) {
        bottomBar = findViewById(R.id.bottom);
        fab = findViewById(R.id.fab);
        try {
            YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(fab);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
        fm = getSupportFragmentManager();
        newFragment(new HomeFeed(), "0");
        logout=view.findViewById(R.id.logout);
        fb=new firebase();

    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(MainActivity.this, PollList.class);
            startActivity(i);
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.signOut(MainActivity.this);
            }
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