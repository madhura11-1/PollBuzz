package com.PollBuzz.pollbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomBar;
    private FragmentManager fm;
    private FloatingActionButton fab;
    firebase fb;
    public static String PARAMS_UID = "UID";
    public static String PARAMS_TYPE = "type";
    private KAlertDialog dialog;
    private String type, UID;
    private PollDetails polldetails;
    String date1,left;
    View view;

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
                                if (!document1.exists()) {
                                    fb.getPollsCollection().document(UID).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    polldetails = documentSnapshot.toObject(PollDetails.class);
                                                    if (polldetails.getCreated_date() != null) {
                                                        long y = ((Timestamp.now().toDate().getTime() - polldetails.getCreated_date().getTime())*24)/86400000;
                                                        long y1 = ((Timestamp.now().toDate().getTime() - polldetails.getCreated_date().getTime()))/86400000;
                                                        if(y1<=0) {
                                                            if (y > 0)
                                                                date1 = "• " + y + " hr ago";
                                                            else
                                                                date1 = "• few minutes ago";
                                                        }
                                                        else{
                                                            if(y1 == 1)
                                                                date1 = "• " + y1 + " day ago";
                                                            else
                                                                date1 = "• " + y1 + " days ago";
                                                        }
                                                    }
                                                    Date date = Calendar.getInstance().getTime();
                                                    if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                                                        left = "• Expired";
                                                        fb.getPollsCollection().document(polldetails.getUID()).update("live",false);
                                                        polldetails.setLive(false);
                                                    } else if (polldetails.isLive()) {
                                                        if(polldetails.getSeconds() == Long.MAX_VALUE)
                                                        {
                                                            left = "• " + "Custom";
                                                        }
                                                        else {
                                                            long x=polldetails.getSeconds()-Timestamp.now().getSeconds()+polldetails.getTimestamp();
                                                            left = "• " + x + " seconds left";
                                                        }
                                                    } else {
                                                        if (polldetails.getExpiry_date() != null && polldetails.getExpiry_date().compareTo(date) >= 0)
                                                        {
                                                            Date one = polldetails.getExpiry_date();
                                                            long x =  (one.getTime()-date.getTime())/86400000;
                                                            if(x>0) {
                                                                if(x == 1){
                                                                    left = "• " + x + " day left";
                                                                }
                                                                else
                                                                    left = "• " + x + " days left";
                                                            }
                                                            else
                                                                left = "• Expires Today";
                                                        }

                                                        else
                                                        {
                                                            left = "• Expired";
                                                        }
                                                    }


                                                    startIntent(UID, type,date1,left);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Unable to load data", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                } else {
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

            setGlobals();
            setListeners();
        }
    }

    private void startIntent(String uid, String pollType,String date1,String left) {
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
            intent.putExtra("card_date", date1);
            intent.putExtra("card_status", left);
            dialog.dismissWithAnimation();
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

    private void setGlobals() {
        bottomBar = findViewById(R.id.bottom);
        fab = findViewById(R.id.fab);
        fab.setColorFilter(getResources().getColor(R.color.white));
        try {
            YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(fab);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.container, new HomeFeed(), "home")
                .commit();
    }

    public View getview(){
         return view;
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
                            bottomBar.setSelectedItemId(R.id.home);
                            break;
                        case "favourite":
                            bottomBar.setSelectedItemId(R.id.favourite);
                            break;
                        case "profile":
                            bottomBar.setSelectedItemId(R.id.profile);
                            break;
                    }
                }
            }
        }, false);
    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(MainActivity.this, PollList.class);
            startActivity(i);
        });

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        newFragment(new HomeFeed(), "home");
                        break;
                    case R.id.favourite:
                        newFragment(new FavouriteFeed(), "favourite");
                        break;
                    case R.id.profile:
                        newFragment(new ProfileFeed(), "profile");
                        break;

                }
                return true;
            }
        });
    }



    private void newFragment(Fragment fragment, String id) {
        try {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.container, fragment, id)
                    .addToBackStack(id)
                    .commit();

        }catch(Exception e){
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
                       // bottomBar.setActiveItem(0);
                        bottomBar.setSelectedItemId(R.id.home);
                        break;
                    case "favourite":
                        //bottomBar.setActiveItem(1);
                        bottomBar.setSelectedItemId(R.id.favourite);
                        break;
                    case "profile":
                        //bottomBar.setActiveItem(3);
                        bottomBar.setSelectedItemId(R.id.profile);
                        break;
                }

            }
        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            fragmentManager.popBackStack();
            //bottomBar.setActiveItem(0);
            bottomBar.setSelectedItemId(R.id.home);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}