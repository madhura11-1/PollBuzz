package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.PollBuzz.pollbuzz.results.PercentageResult;
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
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Ranking_type_response extends AppCompatActivity {

    MaterialButton submit;
    MaterialTextView query_ranking,author;
    LinearLayout group, sequence;
    CollectionReference ref;
    Map<String, Integer> options;
    Map<String, Object> response;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton logout, home, fav_author;
    int c;
    firebase fb;
    PollDetails polldetails;
    KAlertDialog dialog1;
    ArrayList<String> resp = new ArrayList<>();
    SpotsDialog dialog2;
    ImageView id;
    int flag;
    Boolean f=false;

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
        flag=intent.getIntExtra("flag",0);
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
                                            Toast.makeText(getApplicationContext(),polldetails.getAuthor()+" removed from favourite authors",Toast.LENGTH_LONG).show();
                                            fav_author.setImageResource(R.drawable.ic_star_border_dark_24dp);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // dialog1.dismissWithAnimation();
                                                    dialog2.dismiss();
                                                    fav_author.setEnabled(true);
                                                    Toast.makeText(getApplicationContext(),"Failed "+polldetails.getAuthor()+" removing from favourite authors",Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }
                            } else {
                                //Log.d(TAG, "Document does not exist!");

                                fav_author.setEnabled(false);
                                Map<String,String> map=new HashMap<>();
                                map.put("Username",(polldetails.getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        dialog2.dismiss();
                                        fav_author.setEnabled(true);
                                        Toast.makeText(getApplicationContext(),polldetails.getAuthor()+" added to your favourite authors",Toast.LENGTH_LONG).show();
                                        fav_author.setImageResource(R.drawable.ic_star_gold_24dp);

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //dialog1.dismissWithAnimation();
                                                dialog2.dismiss();
                                                fav_author.setEnabled(true);
                                                Toast.makeText(getApplicationContext(),"Failed to add "+polldetails.getAuthor()+" to your favourite authors",Toast.LENGTH_LONG).show();
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

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y = polldetails.getPoll_accessID().toString();
                new PowerMenu.Builder(Ranking_type_response.this)
                        .setTextColor(R.color.black)
                        .setTextSize(18)
                        .setTextGravity(Gravity.CENTER)
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f)
                        .addItem(new PowerMenuItem(y,false))
                        .build()
                        .showAsAnchorCenter(view);
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
                        polldetails = data.toObject(PollDetails.class);
                        query_ranking.setText(polldetails.getQuestion());
                        options = polldetails.getMap();
                        c = options.size();
                        author.setText(polldetails.getAuthor());
                        if(fb.getUserId().equals(polldetails.getAuthorUID())){
                            fav_author.setVisibility(View.GONE);
                            f=true;
                        }else {
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
                                        Toast.makeText(Ranking_type_response.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
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
        fav_author=findViewById(R.id.fav_author);
        id = findViewById(R.id.id1);
        dialog2= new SpotsDialog(Ranking_type_response.this,R.style.Custom);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        author=findViewById(R.id.author);
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
        dialog.dismiss();
        Date date = Calendar.getInstance().getTime();
        if (polldetails != null) {
            if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                polldetails.setLive(false);
                fb.getPollsCollection().document(key).update("live", false);
                new KAlertDialog(this,KAlertDialog.WARNING_TYPE)
                        .setTitleText("This Live Poll has ended")
                        .setCancelText("OK")
                        .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                Intent i = new Intent(Ranking_type_response.this, PercentageResult.class);
                                i.putExtra("UID",key);
                                i.putExtra("type","RANKED");
                                if(!f)
                                    i.putExtra("flag",1);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                        })
                        .show();
            }
            else if(polldetails.getExpiry_date() != null && (polldetails.getExpiry_date().compareTo(date)< 0 || flag == 1 ))
            {
                Intent i = new Intent(Ranking_type_response.this, PercentageResult.class);
                i.putExtra("UID",key);
                i.putExtra("type","RANKED");
                if(!f)
                    i.putExtra("flag",1);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
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
