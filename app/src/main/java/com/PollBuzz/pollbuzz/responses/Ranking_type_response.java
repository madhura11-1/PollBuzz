package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Ranking_type_response extends AppCompatActivity {

    MaterialButton submit;
    MaterialTextView query_ranking;
    LinearLayout group, sequence;
    CollectionReference ref;
    Map<String, Integer> options;
    Map<String, Object> response;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton logout, home;
    int c;
    firebase fb;
    PollDetails polldetails;
    KAlertDialog dialog1;
    ArrayList<String> resp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        key = intent.getExtras().getString("UID");
        setGlobals(view);
        showDialog();
        setActionBarFunctionality();
        retrieveData();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sequence.getChildCount() != c) {
                    Toast.makeText(getApplicationContext(), "Select all options", Toast.LENGTH_LONG).show();
                } else {
                    setResponse(fb);
                }
            }
        });
    }

    private void retrieveData() {
        fb.getPollsCollection().document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot data = task.getResult();
                    if (data.exists()) {
                        group.removeAllViews();
                        dialog.dismiss();
                        polldetails = data.toObject(PollDetails.class);
                        query_ranking.setText(polldetails.getQuestion());
                        options = polldetails.getMap();
                        c = options.size();
                        setOptions();

                    }
                }
            }
        });

    }


    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Ranking_type_response.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }

    private void setGlobals(View view) {
        logout = view.findViewById(R.id.logout);
        home = view.findViewById(R.id.home);
        group = findViewById(R.id.options);
        sequence = findViewById(R.id.sequence);
        options = new HashMap<>();
        response = new HashMap<>();
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Ranking_type_response.this);
        query_ranking = findViewById(R.id.query);
        submit = findViewById(R.id.submit);
        fb = new firebase();
        dialog1 = new KAlertDialog(Ranking_type_response.this, SweetAlertDialog.PROGRESS_TYPE);
    }

    private void setOptions() {
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            CheckBox button = new CheckBox(getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 20, 5, 20);
            button.setLayoutParams(layoutParams);
            button.setTypeface(typeface);
            button.setText(entry.getKey());
            button.setTextSize(20.0f);
            group.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox b = (CheckBox) v;
                    if (((CheckBox) v).isChecked())
                        resp.add(((CheckBox) v).getText().toString());
                    else
                        resp.remove((((CheckBox) v).getText().toString()));
                    setSequenceArea();

                }
            });

        }

    }

    private void setResponse(firebase fb) {
        Map<String, Object> option = new HashMap<>();
        DocumentReference ref = fb.getPollsCollection().document(key).collection("OptionsCount").document("count");
        String id;
        fb.getPollsCollection().document(key).collection("OptionsCount").document("count").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot result = task.getResult();
                    if (result.exists()) {
                        Map<String, Object> ans = result.getData();
                        updateRanks(ans, ref);
                    }

                }


            }
        });

        for (int i = 0; i < c; i++) {
            response.put("option" + i, resp.get(i));
        }

        response.put("timestamp", Timestamp.now().getSeconds());
        submitResponse(fb);
        return;
    }

    private void updateRanks(Map<String, Object> ans, DocumentReference ref) {
        Map<String, Object> ans1 = ans;
        Map<String, Long> map;
        System.out.println(ans);
        for (int i = 0; i < c; i++) {


            map = (Map<String, Long>) ans1.get(resp.get(i));
            if (map.containsKey(String.valueOf(i + 1))) {

                Long k = map.get(String.valueOf(i + 1));
                Log.d(resp.get(i) + " " + String.valueOf(i + 1), String.valueOf(k));
                map.remove(String.valueOf(i + 1));
                map.put(String.valueOf(i + 1), k + 1);


                ans.remove(resp.get(i));
                ans.put(resp.get(i), map);
                //System.out.println(ans);

            }

            if (i == (c - 1)) {
                System.out.println(ans);
                ref.set(ans).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            System.out.println("Updated");

                    }
                });
            }


        }
    }

    private void submitResponse(firebase fb) {
        showKAlertDialog();
        fb.getPollsCollection().document(key).collection("Response").document(fb.getUserId()).set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Integer i = polldetails.getPollcount();
                i++;
                fb.getPollsCollection().document(key).update("pollcount", i);
                Map<String, Object> mapi = new HashMap<>();
                mapi.put("pollId", fb.getUserId());
                mapi.put("timestamp", Timestamp.now().getSeconds());
                fb.getUserDocument().collection("Voted").document(key).set(mapi).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully submitted your response", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Ranking_type_response.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog1.dismissWithAnimation();
                        Toast.makeText(Ranking_type_response.this, "Unable to submit your.\nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setSequenceArea() {
        sequence.removeAllViews();

        for (int i = 0; i < resp.size(); i++) {
            TextView v = new TextView(getApplicationContext());
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 20, 5, 20);
            v.setLayoutParams(layoutParams);
            v.setTypeface(typeface);
            v.setTextSize(20.0f);
            v.setText(Integer.toString(i + 1) + ". " + resp.get(i));
            v.setTextColor(getResources().getColor(R.color.black));
            sequence.addView(v);
            sequence.requestFocus();

        }

    }

    private void showDialog() {
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

    private void showKAlertDialog() {
        dialog1.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog1.setTitleText("Uploading your response");
        dialog1.setCancelable(false);
        dialog1.show();
    }
}
