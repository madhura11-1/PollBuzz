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
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.navFragments.FavouriteFeed;
import com.PollBuzz.pollbuzz.navFragments.HomeFeed;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.navFragments.VotedFeed;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import java.util.Objects;

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
                                if (!document1.exists())
                                    startIntent(UID, type);
                                else {
                                    dialog.dismissWithAnimation();
                                    Intent intent = new Intent(MainActivity.this, PercentageResult.class);
                                    intent.putExtra("UID", UID);
                                    intent.putExtra("type", type);
                                    startActivity(intent);
                                }
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

    private void startIntent(String uid, String pollType) {
        Intent intent;
        try {
            Log.d("MainActivity", uid + " " + pollType);
            switch (pollType) {
                case "SINGLE CHOICE":
                    intent = new Intent(this, Single_type_response.class);
                    break;
                case "MULTI SELECT":
                    intent = new Intent(this, Multiple_type_response.class);
                    break;
                case "RANKED":
                    intent = new Intent(this, Ranking_type_response.class);
                    break;
                case "PICTURE BASED":
                    intent = new Intent(this, Image_type_responses.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + pollType);
            }
            intent.putExtra("UID", uid);
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
        fragmentListener(fm);
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.container, new HomeFeed(), "home")
                .commit();
        logout = view.findViewById(R.id.logout);
    }

    void fragmentListener(FragmentManager fm) {
        fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentResumed(fm, f);
                Log.d("FragmentResume", f.getTag());
                if (f.getTag() != null) {
                    Log.d("Fragmentmanager", f.getTag());
                    switch (f.getTag()) {
                        case "home":
                            bottomBar.setActiveItem(0);
                            break;
                        case "favourite":
                            bottomBar.setActiveItem(1);
                            break;
                        case "voted":
                            bottomBar.setActiveItem(2);
                            break;
                        case "profile":
                            bottomBar.setActiveItem(3);
                            break;
                    }
                }
            }
        }, true);
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
                    newFragment(new HomeFeed(), "home");
                    break;
                case 1:
                    newFragment(new FavouriteFeed(), "favourite");
                    break;
                case 2:
                    newFragment(new VotedFeed(), "voted");
                    break;
                case 3:
                    newFragment(new ProfileFeed(), "profile");
                    break;

            }
        });
    }

    private void newFragment(Fragment fragment, String id) {
        try {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(R.id.container, fragment, id)
                    .addToBackStack(id)
                    .commit();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            if (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals("home")) {
                finish();
            } else {
                fragmentManager.popBackStack();
                Log.d("LastFrag", fm.getBackStackEntryCount() + "");
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    Log.d("LastFrag", fm.getBackStackEntryAt(i).getName());
                }
                switch (Objects.requireNonNull(fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 2).getName())) {
                    case "home":
                        bottomBar.setActiveItem(0);
                        break;
                    case "favourite":
                        bottomBar.setActiveItem(1);
                        break;
                    case "voted":
                        bottomBar.setActiveItem(2);
                        break;
                    case "profile":
                        bottomBar.setActiveItem(3);
                        break;
                }
            }
        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            fragmentManager.popBackStack();
            bottomBar.setActiveItem(0);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}