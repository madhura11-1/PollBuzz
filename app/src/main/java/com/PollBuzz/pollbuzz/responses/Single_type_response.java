package com.PollBuzz.pollbuzz.responses;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class Single_type_response extends AppCompatActivity {
    TextView title, query;
    RadioGroup group;
    Map<String,Integer> options;
    String key;
    Typeface typeface;
     Dialog dialog;
    ImageButton home,logout;
    Button submit;
    Map<String,String> response;
    String resp;
    firebase fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();
        retrieveData(fb);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResponse(fb);
            }
        });
    }

    private void submitResponse(firebase fb) {
        response.put("option",resp);

        fb.getPollsCollection().document(key).collection("Response").document(fb.getUserId()).set(response);

        Map<String,String> mapi = new HashMap<>();
        mapi.put("pollId",fb.getUserId());
        fb.getUsersCollection().document(fb.getUserId()).collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Single_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Single_type_response.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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

                                DocumentSnapshot data = task.getResult();
                                if (data.exists()) {
                                    group.removeAllViews();
                                    dialog.dismiss();
                                    PollDetails polldetails = data.toObject(PollDetails.class);
                                    title.setText(polldetails.getTitle());
                                    title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                    query.setText(polldetails.getQuestion());
                                    options = polldetails.getMap();

                                    for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                        RadioButton button = new RadioButton(getApplicationContext());
                                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 20, 5, 20);
                                        button.setLayoutParams(layoutParams);
                                        button.setTypeface(typeface);
                                        button.setText(entry.getKey());
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
                                }
                            }

                        }

                );

    }

    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");

    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Single_type_response.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Single_type_response.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void setGlobals(View view) {
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        title=findViewById(R.id.title);
        submit=findViewById(R.id.submit);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        options=new HashMap<>();
        response=new HashMap<>();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Single_type_response.this);
        fb = new firebase();

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
}
