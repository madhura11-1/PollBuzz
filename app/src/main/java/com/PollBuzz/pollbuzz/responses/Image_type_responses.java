package com.PollBuzz.pollbuzz.responses;

import com.PollBuzz.pollbuzz.polls.Descriptive_type_poll;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
import com.kinda.alert.KAlertDialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class Image_type_responses extends AppCompatActivity {

    TextView  query;
    ImageView image1 ,image2;
    RadioGroup group;
    RadioButton b1,b2;
    MaterialButton submit;
    Map<String,Integer> options,update;
    Map<String,Object> response;
    String key,imageoption1,imageoption2;
    Typeface typeface;
    Dialog dialog;
    firebase fb;
    ImageButton logout,home;
    PollDetails polldetails;
    KAlertDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_responses);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();

        b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    b2.setChecked(false);
                }
            }
        });
        b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    b1.setChecked(false);
                }
            }
        });
        retrieveData();



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitResponse();

            }
        });

    }

    private void submitResponse() {

        showKAlertDialog();
        Integer i = polldetails.getPollcount();
        i++;
        Integer p=0;
        fb.getPollsCollection().document(key).update("pollcount",i);
        if(b1.isChecked())
        {
            response.put("option",b1.getText().toString().trim());
            p = update.get(imageoption1);
            p++;
            update.put(imageoption1,p);
        }
        if(b2.isChecked())
        {
            response.put("option",b2.getText().toString().trim());
            p = update.get(imageoption2);
            p++;
            update.put(imageoption2,p);
        }
        fb.getPollsCollection().document(key).update("map",update);
        if (fb.getUser() != null)
        {
            response.put("timestamp",Timestamp.now().getSeconds());
            fb.getPollsCollection().document(key).collection("Response")
                    .document(fb.getUserId()).set(response).addOnSuccessListener(aVoid -> {
                        Map<String,Object> mapi = new HashMap<>();
                        mapi.put("pollId", fb.getUserId());
                        mapi.put("timestamp", Timestamp.now().getSeconds());
                        fb.getUsersCollection().document(fb.getUserId()).collection("Voted").document(key).set(mapi).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog1.dismissWithAnimation();
                                    Toast.makeText(Image_type_responses.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                                    Intent i1 =new Intent(Image_type_responses.this, MainActivity.class);
                                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i1);
                                }
                                else{
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
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot snapshot = task.getResult();
                            if(snapshot.exists())
                            {
                                dialog.dismiss();
                                 polldetails = snapshot.toObject(PollDetails.class);
                                query.setText(polldetails.getQuestion().trim());
                                options =polldetails.getMap();
                                int i=0;
                                for(Map.Entry<String,Integer> entry : options.entrySet())
                                {
                                    update.put(entry.getKey(),entry.getValue());
                                    if(i==0)
                                    {
                                        loadProfilePic(image1,entry.getKey());
                                        imageoption1 = entry.getKey();
                                    }
                                    if(i==1)
                                    {
                                        loadProfilePic(image2,entry.getKey());
                                        imageoption2 = entry.getKey();
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                });

    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Image_type_responses.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Image_type_responses.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }


    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");

    }

    private void setGlobals(View view) {
        logout =view.findViewById(R.id.logout);
        home=view.findViewById(R.id.home);
        group=findViewById(R.id.options);
        options = new HashMap<>();
        response = new HashMap<>();
        update = new HashMap<>();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Image_type_responses.this);
        query = findViewById(R.id.query);
        submit = findViewById(R.id.submit);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);
        fb = new firebase();
        dialog1=new KAlertDialog(Image_type_responses.this, SweetAlertDialog.PROGRESS_TYPE);
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
    private void showKAlertDialog(){
        dialog1.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog1.setTitleText("Uploading your response");
        dialog1.setCancelable(false);
        dialog1.show();
    }
}
