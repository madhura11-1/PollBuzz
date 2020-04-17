package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Image_type_responses extends AppCompatActivity {

    TextView query, author;
    ImageView image1, image2;
    RadioGroup group;
    RadioButton b1, b2;
    MaterialButton submit;
    Map<String, Integer> options, update;
    Map<String, Object> response;
    String key, imageoption1, imageoption2;
    Typeface typeface;
    Dialog dialog;
    firebase fb;
    ImageButton logout, home, fav_author;
    PollDetails polldetails;
    KAlertDialog dialog1;
    SpotsDialog dialog2;
    ImageView id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_responses);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();

        b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    b2.setChecked(false);
                }
            }
        });
        b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    b1.setChecked(false);
                }
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1.setChecked(true);
                b2.setChecked(false);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b2.setChecked(true);
                b1.setChecked(false);
            }
        });
        retrieveData();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(b1.isChecked() || b2.isChecked())) {
                    Toast.makeText(Image_type_responses.this, "Please section a option...", Toast.LENGTH_SHORT).show();
                } else
                    submitResponse();

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

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y = polldetails.getPoll_accessID().toString();
                new PowerMenu.Builder(Image_type_responses.this)
                        .setTextColor(R.color.black)
                        .setTextGravity(Gravity.CENTER)
                        .setTextSize(18)
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f)
                        .addItem(new PowerMenuItem(y, false))
                        .build()
                        .showAsAnchorCenter(view);
            }
        });

    }

    private void submitResponse() {

        showKAlertDialog();
        Integer i = polldetails.getPollcount();
        i++;
        Integer p = 0;
        fb.getPollsCollection().document(key).update("pollcount", i);
        if (b1.isChecked()) {
            response.put("option", b1.getText().toString().trim());
            p = update.get(imageoption1);
            p++;
            update.put(imageoption1, p);
        }
        if (b2.isChecked()) {
            response.put("option", b2.getText().toString().trim());
            p = update.get(imageoption2);
            p++;
            update.put(imageoption2, p);
        }
        fb.getPollsCollection().document(key).update("map", update);
        if (fb.getUser() != null) {
            response.put("timestamp", Timestamp.now().getSeconds());
            fb.getPollsCollection().document(key).collection("Response")
                    .document(fb.getUserId()).set(response).addOnSuccessListener(aVoid -> {
                Map<String, Object> mapi = new HashMap<>();
                mapi.put("pollId", fb.getUserId());
                mapi.put("timestamp", Timestamp.now().getSeconds());
                fb.getUsersCollection().document(fb.getUserId()).collection("Voted").document(key).set(mapi).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(Image_type_responses.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                            Intent i1 = new Intent(Image_type_responses.this, MainActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i1);
                        } else {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(Image_type_responses.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(Image_type_responses.this, "Unable to submit.\nPlease try again ", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void retrieveData() {
        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                polldetails = snapshot.toObject(PollDetails.class);
                                query.setText(polldetails.getQuestion().trim());
                                options = polldetails.getMap();
                                author.setText(polldetails.getAuthor());
                                if (fb.getUserId().equals(polldetails.getAuthorUID())) {
                                    fav_author.setVisibility(View.GONE);
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

                                            }
                                        }
                                    });
                                }
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                    update.put(entry.getKey(), entry.getValue());
                                    if (i == 0) {
                                        loadProfilePic(image1, entry.getKey());
                                        imageoption1 = entry.getKey();
                                    }
                                    if (i == 1) {
                                        loadProfilePic(image2, entry.getKey());
                                        imageoption2 = entry.getKey();
                                    }
                                    i++;

                                }
                                dialog.dismiss();
                                Date date = Calendar.getInstance().getTime();
                                if (polldetails != null) {
                                    if (polldetails.isLivePoll()) {
                                        if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                                            polldetails.setLive(false);
                                            fb.getPollsCollection().document(key).update("live", false);
                                            callKalert();
                                        } else if (!polldetails.isLive())
                                            callKalert();
                                    } else if (polldetails.getExpiry_date() != null && (polldetails.getExpiry_date().compareTo(date) < 0)) {
                                        Intent intent = new Intent(Image_type_responses.this, PercentageResult.class);
                                        intent.putExtra("UID", key);
                                        intent.putExtra("type", "PICTURE BASED");
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            } else {
                                finish();
                                Toast.makeText(Image_type_responses.this, "This url does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void callKalert() {
        KAlertDialog dialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
        dialog.setCancelable(false);
        dialog.setTitleText("This Live Poll has ended")
                .setConfirmText("OK")
                .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                        Intent i = new Intent(Image_type_responses.this, PercentageResult.class);
                        i.putExtra("UID", key);
                        i.putExtra("type", "SINGLE CHOICE");
                        startActivity(i);
                        finish();
                    }
                })
                .show();
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Image_type_responses.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }


    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");

    }

    private void setGlobals(View view) {
        logout = view.findViewById(R.id.logout);
        home = view.findViewById(R.id.home);
        group = findViewById(R.id.options);
        options = new HashMap<>();
        response = new HashMap<>();
        update = new HashMap<>();
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Image_type_responses.this);
        query = findViewById(R.id.query);
        submit = findViewById(R.id.submit);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);
        fb = new firebase();
        id = findViewById(R.id.id1);
        dialog1 = new KAlertDialog(Image_type_responses.this, SweetAlertDialog.PROGRESS_TYPE);
        fav_author = findViewById(R.id.fav_author);
        dialog2 = new SpotsDialog(Image_type_responses.this, R.style.Custom);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        author = findViewById(R.id.author);
    }


    private void loadProfilePic(ImageView view, String url) {
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .transform(new RoundedCorners(50))
                    .into(view);
        } else {
            view.setImageResource(R.drawable.place_holder);
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
