package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
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
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Single_type_response extends AppCompatActivity {

    TextView query, author;
    RadioGroup group;
    Map<String, Integer> options;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton home, logout;
    MaterialButton submit;
    Map<String, Object> response;
    String resp;
    firebase fb;
    PollDetails polldetails;
    Map<String, Integer> update;
    KAlertDialog dialog1;
    ImageButton fav_author;
    SpotsDialog dialog2;
    int flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();
        retrieveData(fb);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resp == null) {
                    Toast.makeText(Single_type_response.this, "Please select a option...", Toast.LENGTH_SHORT).show();
                } else
                    submitResponse(fb);
            }
        });
        fav_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog2.show();
                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                {

                                    fav_author.setEnabled(false);
                                    //Log.d(TAG, "Document exists!");
                                    fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //dialog1.dismissWithAnimation();
                                            dialog2.dismiss();
                                            fav_author.setEnabled(true);
                                            Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " removed from favourite authors", Toast.LENGTH_LONG).show();
                                            fav_author.setImageResource(R.drawable.ic_star_border_dark_24dp);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // dialog1.dismissWithAnimation();
                                                    dialog2.dismiss();
                                                    fav_author.setEnabled(true);
                                                    Toast.makeText(getApplicationContext(), "Failed " + polldetails.getAuthor() + " removing from favourite authors", Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }
                            } else {
                                //Log.d(TAG, "Document does not exist!");

                                fav_author.setEnabled(false);
                                Map<String, String> map = new HashMap<>();
                                map.put("Username", (polldetails.getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        dialog2.dismiss();
                                        fav_author.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " added to your favourite authors", Toast.LENGTH_LONG).show();
                                        fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //dialog1.dismissWithAnimation();
                                                dialog2.dismiss();
                                                fav_author.setEnabled(true);
                                                Toast.makeText(getApplicationContext(), "Failed to add " + polldetails.getAuthor() + " to your favourite authors", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            //Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    private void submitResponse(firebase fb) {

        showKAlertDialog();
        Integer i = polldetails.getPollcount();
        i++;
        response.put("option", resp);
        Integer p = update.get(resp);
        p++;
        update.put(resp, p);
        fb.getPollsCollection().document(key).update("pollcount", i);
        fb.getPollsCollection().document(key).update("map", update);
        response.put("timestamp", Timestamp.now().getSeconds());

        fb.getPollsCollection().document(key).collection("Response").document(fb.getUserId()).set(response);

        Map<String, Object> mapi = new HashMap<>();
        mapi.put("pollId", fb.getUserId());
        mapi.put("timestamp", Timestamp.now().getSeconds());
        fb.getUserDocument().collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Single_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Single_type_response.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog1.dismissWithAnimation();
                        Toast.makeText(Single_type_response.this, "Unable to submit.\nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void retrieveData(firebase fb) {
        fb.getPollsCollection()
                .document(key)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Boolean f = false;
                        DocumentSnapshot data = task.getResult();
                        if (data != null && data.exists()) {
                            group.removeAllViews();
                            polldetails = data.toObject(PollDetails.class);
                            query.setText(polldetails.getQuestion());
                            options = polldetails.getMap();
                            author.setText(polldetails.getAuthor());
                            if (fb.getUserId().equals(polldetails.getAuthorUID())) {
                                fav_author.setVisibility(View.GONE);
                                f = true;
                            } else {
                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                                            } else {
                                                fav_author.setImageResource(R.drawable.ic_star_border_dark_24dp);
                                            }
                                        } else {
                                            Toast.makeText(Single_type_response.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                RadioButton button = new RadioButton(getApplicationContext());
                                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(5, 20, 5, 20);
                                button.setLayoutParams(layoutParams);
                                button.setTypeface(typeface);
                                button.setText(entry.getKey());
                                update.put(entry.getKey(), entry.getValue());
                                button.setTextSize(20.0f);
                                group.addView(button);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RadioButton b = (RadioButton) v;
                                        if (b.isChecked())
                                            resp = b.getText().toString();
                                    }
                                });
                            }

                            dialog.dismiss();
                            Date date = Calendar.getInstance().getTime();
                            if (polldetails != null) {
                                if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                                    polldetails.setLive(false);
                                    Boolean finalF = f;
                                    new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                                            .setTitleText("This Live Poll has ended")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(KAlertDialog kAlertDialog) {
                                                    Intent i = new Intent(Single_type_response.this, PercentageResult.class);
                                                    i.putExtra("UID", key);
                                                    i.putExtra("type", "SINGLE CHOICE");
                                                    if (!finalF)
                                                        i.putExtra("flag", 1);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.putExtra("from", 1);
                                                    startActivity(i);
                                                }
                                            })
                                            .show();
                                } else if (polldetails.getExpiry_date() != null && (polldetails.getExpiry_date().compareTo(date) < 0 || flag == 1)) {
                                    Intent i = new Intent(Single_type_response.this, PercentageResult.class);
                                    i.putExtra("UID", key);
                                    i.putExtra("type", "SINGLE CHOICE");
                                    if (!f)
                                        i.putExtra("flag", 1);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("from", 1);
                                    startActivity(i);
                                }

                            }

                        }
                    }
                });
    }


    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");
        flag = intent.getIntExtra("flag", 0);
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Single_type_response.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }

    private void setGlobals(View view) {
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        submit = findViewById(R.id.submit);
        query = findViewById(R.id.query);
        group = findViewById(R.id.options);
        options = new HashMap<>();
        response = new HashMap<>();
        update = new HashMap<>();
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Single_type_response.this);
        fb = new firebase();
        dialog1 = new KAlertDialog(Single_type_response.this, SweetAlertDialog.PROGRESS_TYPE);
        fav_author = findViewById(R.id.fav_author);
        dialog2 = new SpotsDialog(Single_type_response.this, R.style.Custom);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        author = findViewById(R.id.author);
        //dialog2.create();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(Single_type_response.this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

    }

}
