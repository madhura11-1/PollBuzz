package com.PollBuzz.pollbuzz.results;

import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class PercentageResult extends AppCompatActivity {

    TextView question_percentage, date_percentage;

    firebase fb;
    ImageButton home, logout;
    String uid, type;
    int flag;
    Map<String, Integer> map;
    LinearLayout linearLayout;
    public static Double total;
    Dialog dialog;
    MaterialButton result, pie_charts, selfVote;
    TextView vote_count;
    Typeface typeface;
    public static Map<String, Integer> data = new HashMap<>();
    public static String question;
    Boolean flagVoted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percentage_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        setGlobals(view);
        getIntentExtras(intent);
        setActionBarFunctionality();
        retrievedata(fb);

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PercentageResult.this, ResultActivity.class);
                intent.putExtra("UID", uid);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
        pie_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PercentageResult.this, PieChartActivity.class);
                startActivity(i);
            }
        });
        selfVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Case", type);
                fb.getPollsCollection().document(uid).collection("Response").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot dS1 : task.getResult()) {
                                if (dS1.getId().equals(fb.getUserId())) {
                                    flagVoted = false;
                                    Toast.makeText(PercentageResult.this, "You have already voted once...", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                        } else flagVoted = true;
                        if (flagVoted) {
                            Intent intent;
                            switch (type) {
                                case "SINGLE CHOICE":
                                    intent = new Intent(getApplicationContext(), Single_type_response.class);
                                    break;
                                case "MULTI SELECT":
                                    intent = new Intent(getApplicationContext(), Multiple_type_response.class);
                                    break;
                                case "RANKED":
                                    intent = new Intent(getApplicationContext(), Ranking_type_response.class);
                                    break;
                                case "PICTURE BASED":
                                    intent = new Intent(getApplicationContext(), Image_type_responses.class);
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + type);
                            }
                            intent.putExtra("UID", uid);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

        private void retrievedata (firebase fb){

            showDialog();
            fb.getPollsCollection().document(uid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                PollDetails pollDetails = documentSnapshot.toObject(PollDetails.class);
                                question_percentage.setText(pollDetails.getQuestion());
                                question = pollDetails.getQuestion();
                                String d = "Created on: " + pollDetails.getCreated_date();
                                date_percentage.setText(d);
                                map = pollDetails.getMap();
                                total = Double.valueOf(pollDetails.getPollcount());
                                String vote = "Total Voters:" + pollDetails.getPollcount();
                                vote_count.setText(vote);
                                setProgressbar(map);
                            } else
                                Log.d("hello", "hii");
                        }
                    });
        }

        private void setProgressbar (Map < String, Integer > map){
            dialog.dismiss();
            linearLayout.removeAllViews();
            data.clear();
            if (type.equals("IMAGE POLL")) {
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    Integer per;
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                    layoutParams.setMargins(30, 10, 10, 10);
                    imageView.setLayoutParams(layoutParams);
                    loadProfilePic(imageView, entry.getKey());
                    LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams1.setMargins(30, 40, 10, 40);
                    linearLayout1.setLayoutParams(layoutParams1);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams2.setMargins(30, 10, 10, 10);
                    RoundCornerProgressBar progressBar = new RoundCornerProgressBar(PercentageResult.this, null, android.R.attr.progressBarStyleHorizontal);
                    //progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    progressBar.setProgressColor(Color.parseColor("#56d2c2"));
                    progressBar.setProgressBackgroundColor(getResources().getColor(R.color.grey));
                    LinearLayout wrap_bar = new LinearLayout(getApplicationContext());
                    wrap_bar.setOrientation(LinearLayout.VERTICAL);
                    progressBar.setPadding(5, 20, 10, 20);
                    progressBar.setRadius(20);
                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams3.setMargins(10, 30, 10, 30);
                    wrap_bar.setLayoutParams(layoutParams2);
                    wrap_bar.setBackgroundColor(getResources().getColor(R.color.grey));
                    ;
                    progressBar.setLayoutParams(layoutParams3);
                    progressBar.setScaleY(15);

                    TextView textView = new TextView(this);
                    TextView voted_by = new TextView(this);
                    textView.setTypeface(typeface);
                    voted_by.setTypeface(typeface);
                    int v_by;
                    if (total != 0) {
                        per = (int) ((entry.getValue() / total) * 100);
                        textView.setText(per + "%");
                        progressBar.setProgress(per);
                        v_by = entry.getValue();
                        String text = "Voted by: " + v_by;
                        voted_by.setText(text);

                    } else {
                        per = 0;
                        textView.setText(per + "%");
                        String text = "Voted by: " + 0;
                        voted_by.setText(text);
                        progressBar.setProgress(per);

                    }
                    data.put(entry.getKey(), entry.getValue());
                    textView.setLayoutParams(layoutParams2);
                    voted_by.setLayoutParams(layoutParams2);
                    textView.setTextSize(20.0f);
                    voted_by.setTextSize(20.0f);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    voted_by.setTextColor(getResources().getColor(R.color.black));
                    linearLayout1.addView(imageView);
                    linearLayout1.addView(textView);
                    linearLayout1.addView(wrap_bar);
                    wrap_bar.addView(progressBar);
                    linearLayout1.addView(voted_by);
                    linearLayout.addView(linearLayout1);

                }

            } else {

                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);
                    RoundCornerProgressBar progressBar = new RoundCornerProgressBar(PercentageResult.this, null, android.R.attr.progressBarStyleHorizontal);
                    //progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    progressBar.setProgressColor(Color.parseColor("#56d2c2"));
                    progressBar.setProgressBackgroundColor(getResources().getColor(R.color.grey));
                    LinearLayout wrap_bar = new LinearLayout(getApplicationContext());
                    wrap_bar.setOrientation(LinearLayout.VERTICAL);
                    progressBar.setPadding(5, 20, 10, 20);
                    progressBar.setRadius(20);
                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams3.setMargins(10, 30, 10, 30);


                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 20, 10, 20);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams1.setMargins(30, 40, 10, 40);
                    linearLayout1.setLayoutParams(layoutParams1);
                    wrap_bar.setLayoutParams(layoutParams);
                    wrap_bar.setBackgroundColor(getResources().getColor(R.color.grey));
                    ;
                    progressBar.setLayoutParams(layoutParams3);
                    progressBar.setScaleY(15);
                    TextView textView = new TextView(this);
                    TextView voted_by = new TextView(this);
                    int v_by;
                    Integer per;
                    Log.d("option", entry.getKey());
                    if (total != 0) {

                        per = (int) ((entry.getValue() / total) * 100);
                        v_by = entry.getValue();
                        String text = "Voted by: " + v_by;
                        voted_by.setText(text);
                        textView.setText(entry.getKey() + " - " + per + "%");
                        progressBar.setProgress(per);
                    } else {
                        per = 0;
                        textView.setText(entry.getKey() + " - " + per + "%");
                        String text = "Voted by: " + 0;
                        voted_by.setText(text);
                        progressBar.setProgress(per);
                    }
                    data.put(entry.getKey(), entry.getValue());
                    textView.setLayoutParams(layoutParams);
                    voted_by.setLayoutParams(layoutParams);
                    textView.setGravity(Gravity.START);
                    voted_by.setGravity(Gravity.START);
                    textView.setTextSize(20.0f);
                    voted_by.setTextSize(20.0f);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    textView.setTypeface(typeface);
                    voted_by.setTypeface(typeface);
                    voted_by.setTextColor(getResources().getColor(R.color.black));
                    linearLayout1.addView(textView);
                    wrap_bar.addView(progressBar);
                    linearLayout1.addView(wrap_bar);
                    linearLayout1.addView(voted_by);
                    linearLayout.addView(linearLayout1);
                }
            }
        }

        private void loadProfilePic (ImageView view, String url){
            if (url != null) {
                Glide.with(this)
                        .load(url)
                        .into(view);
            } else {
                view.setImageResource(R.drawable.place_holder);
            }
        }


        private void setActionBarFunctionality () {
            home.setOnClickListener(v -> {
                Intent i = new Intent(PercentageResult.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            });
            logout.setOnClickListener(v -> {
                fb.signOut();
                Intent i = new Intent(PercentageResult.this, LoginSignupActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            });
        }

        private void showDialog () {
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

        private void getIntentExtras (Intent intent){
            uid = intent.getExtras().getString("UID");
            type = intent.getExtras().getString("type");
            flag = intent.getIntExtra("flag", 0);
            if (flag == 1) {
                result.setVisibility(View.GONE);
                selfVote.setVisibility(View.GONE);
            }

        }

        private void setGlobals (View view){
            linearLayout = findViewById(R.id.percentage);
            fb = new firebase();
            home = view.findViewById(R.id.home);
            logout = view.findViewById(R.id.logout);
            question_percentage = findViewById(R.id.question_percentage);
            date_percentage = findViewById(R.id.date_percentage);
            result = findViewById(R.id.result);
            selfVote = findViewById(R.id.selfVote);
            map = new HashMap<>();
            vote_count = findViewById(R.id.vote_count);
            dialog = new Dialog(PercentageResult.this);
            typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.maven_pro);
            pie_charts = findViewById(R.id.pie);
        }

        @Override
        protected void onStart () {
            super.onStart();

        }

    }
