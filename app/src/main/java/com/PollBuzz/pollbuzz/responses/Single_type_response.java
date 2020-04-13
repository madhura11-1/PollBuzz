package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Single_type_response extends AppCompatActivity {
    TextView query;
    RadioGroup group;
    Map<String, Integer> options;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton home, logout;
    Button submit;
    Map<String, Object> response;
    String resp;
    firebase fb;
    PollDetails polldetails;
    Map<String, Integer> update;
    KAlertDialog dialog1;


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

                                DocumentSnapshot data = task.getResult();
                                if (data.exists()) {
                                    group.removeAllViews();
                                    dialog.dismiss();
                                    polldetails = data.toObject(PollDetails.class);
                                    query.setText(polldetails.getQuestion());
                                    options = polldetails.getMap();

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
