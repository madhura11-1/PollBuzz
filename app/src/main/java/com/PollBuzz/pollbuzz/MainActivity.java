package com.PollBuzz.pollbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.navFragments.FavouriteFeed;
import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import Utils.firebase;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    public static SmoothBottomBar bottomBar;
    private FragmentManager fm;
    private FloatingActionButton fab;
    private ImageButton logout;
    firebase fb;
    public static String PARAMS_UID = "UID";
    public static String PARAMS_TYPE = "type";
    private KAlertDialog dialog;
    private String type, UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        type = getIntent().getStringExtra(PARAMS_TYPE);
        UID = getIntent().getStringExtra(PARAMS_UID);
        fb = new firebase();
        if (fb.getUser() == null) {
            Intent intent = new Intent(this, LoginSignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            if (type != null && UID != null) {
                showDialog();
                fb.getPollsCollection().document(UID).collection("Response").document(fb.getUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document1 = task.getResult();
                            if (document1 != null) {
                                if (document1.exists())
                                    startIntent(UID, type, 1);
                                else startIntent(UID, type, 0);
                            }
                        }
                    }
                });
            }
            getSupportActionBar().show();
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar);
            View view = getSupportActionBar().getCustomView();
            view.findViewById(R.id.home).setVisibility(View.GONE);
            setGlobals(view);
            setBottomBar();
            setListeners();
        }
    }

    private void startIntent(String uid, String pollType, int flag) {
        Intent intent;
        try {
            Log.d("MainActivity", uid + " " + pollType);
            switch (pollType) {
                case "0":
                    intent = new Intent(this, Single_type_response.class);
                    break;
                case "1":
                    intent = new Intent(this, Multiple_type_response.class);
                    break;
                case "2":
                    intent = new Intent(this, Ranking_type_response.class);
                    break;
                case "3":
                    intent = new Intent(this, Image_type_responses.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + pollType);
            }
            intent.putExtra("UID", uid);
            intent.putExtra("flag", flag);
            dialog.dismissWithAnimation();
            Log.d("MainActivity", "intent");
            startActivity(intent);
        } catch (IllegalStateException e) {
            dialog.dismissWithAnimation();
            Toast.makeText(this, "This url does not exist.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog() {
        dialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Loading poll...");
        dialog.setCancelable(false);
        dialog.show();
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
        logout = view.findViewById(R.id.logout);
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
                    newFragment(new FavouriteFeed(), "1");
                    break;
                case 2:
                    newFragment(new VotedFeed(), "2");
                    break;
                case 3:
                    newFragment(new ProfileFeed(), "3");
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
        finish();
    }
}