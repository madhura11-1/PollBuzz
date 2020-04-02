package com.PollBuzz.pollbuzz.results;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.TreeMap;

import Utils.firebase;

public class Ranking_type_result extends AppCompatActivity {

    MaterialTextView query_ranking_result;
    LinearLayout group;
    firebase fb = new firebase();
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    String key,uid;
    Typeface typeface;
    Dialog dialog;
    Map<String, Object> response;
    TreeMap<String, Object> options;
    Integer integer;
    TextView poll_stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_response);
        View view = getSupportActionBar().getCustomView();



        setGlobals(view);
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        setAuthStateListener();
        showDialog();
        retriveData(fb);

    }

    private void setAuthStateListener() {



        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Ranking_type_result.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };
    }

    private void retriveData(firebase fb) {

        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot data = task.getResult();
                            if (data.exists()) {
                                group.removeAllViews();

                                PollDetails polldetails = data.toObject(PollDetails.class);
                                query_ranking_result.setText(polldetails.getQuestion());
                                fb.getPollsCollection().document(key).collection("Response").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot != null) {
                                                response = documentSnapshot.getData();
                                                setAccordingToPriority();


                                            }
                                        }

                                    }
                                });
                            }
                        }

                    }
                });
    }

    private void setActionBarFunctionality() {
        poll_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Ranking_type_result.this,PercentageResult.class);
                i.putExtra("UID",key);
                i.putExtra("type","PRIORITY POLL");
                i.putExtra("flag",1);
                startActivity(i);
            }
        });


    }

    private void getIntentExtras(Intent intent) {


        key = intent.getExtras().getString("UID");
        integer = intent.getExtras().getInt("flag");

        if(integer == 1)
        {
            uid = intent.getExtras().getString("UIDUser");
        }
        if(integer == 0)
        {
            uid = auth.getCurrentUser().getUid();
        }


    }

    private void setGlobals(View view) {
        poll_stats=view.findViewById(R.id.poll_stats);
        auth = FirebaseAuth.getInstance();
        options=new TreeMap<>();
        query_ranking_result = findViewById(R.id.query_ranking_result);
        group = findViewById(R.id.options_ranking_result);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Ranking_type_result.this);


    }

    private void setAccordingToPriority() {
        options.putAll(response);
        for(Map.Entry<String,Object> entry : options.entrySet())
        {
            if(!entry.getKey().equals("timestamp"))
            {
                TextView v=new TextView(getApplicationContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(8, 20, 5, 20);
                v.setLayoutParams(layoutParams);
                v.setTypeface(typeface);
                v.setText(entry.getKey());
                v.setTextSize(20.0f);
                v.setTextColor(getResources().getColor(R.color.black));
                group.addView(v);
                String s=(Integer.parseInt(entry.getKey().substring(6))+1)+". "+entry.getValue().toString();
                v.setText(s);
            }



        }
        dialog.dismiss();

    }

    private void showDialog()
    {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }
}