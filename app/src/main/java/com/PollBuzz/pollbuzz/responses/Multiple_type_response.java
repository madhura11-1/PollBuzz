package com.PollBuzz.pollbuzz.responses;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.kinda.alert.KAlertDialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class Multiple_type_response extends AppCompatActivity {
    TextView query;
    LinearLayout group;
    Map<String,Integer> options;
    String key;
    Typeface typeface;
    Dialog dialog;
    ImageButton home,logout;
    Button submit;
    firebase fb;
    int c;
    Map<String,Integer> update;
    PollDetails polldetails;
    Map<String,Object> response;
    KAlertDialog dialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();
        retrieveData();

        submit.setOnClickListener(v -> submitResponse());
    }

    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");

    }

    private void submitResponse() {

        showKAlertDialog();
        Integer p = polldetails.getPollcount();
        p++;
        for(Map.Entry<String,Object> e : response.entrySet()){
            Integer i = update.get(e.getValue());
            i++;
            update.put(e.getValue().toString(),i);
        }
        fb.getPollsCollection().document(key).update("pollcount",p);
        fb.getPollsCollection().document(key).update("map",update);

        response.put("timestamp", Timestamp.now().getSeconds());
        fb.getPollsCollection().document(key).collection("Response")
                .document(fb.getUserId()).set(response);
        Map<String,Object> mapi = new HashMap<>();
        mapi.put("pollId", fb.getUserId());
        mapi.put("timestamp", Timestamp.now().getSeconds());
        fb.getUserDocument().collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Multiple_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Multiple_type_response.this,MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog1.dismissWithAnimation();
                        Toast.makeText(Multiple_type_response.this, "Unable to submit.\nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void retrieveData() {
        fb.getPollsCollection().document(key).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot data = task.getResult();
                        if (data.exists()) {

                            dialog.dismiss();
                             polldetails = data.toObject(PollDetails.class);
                            query.setText(polldetails.getQuestion());
                            options = polldetails.getMap();
                            group.removeAllViews();
                            response.clear();

                            int i = 0;
                            for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                CheckBox button = new CheckBox(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(5, 20, 5, 20);
                                button.setLayoutParams(layoutParams);
                                button.setTypeface(typeface);

                                button.setText(entry.getKey());
                                update.put(entry.getKey(),entry.getValue());
                                button.setTextSize(20.0f);
                                group.addView(button);
                                int finalI = i;
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CheckBox b = (CheckBox) v;
                                        if (b.isChecked())
                                            response.put("option" + finalI, b.getText().toString());
                                        else
                                            response.values().remove(b.getText().toString());
                                    }
                                });
                                i++;
                                dialog.dismiss();
                                if (polldetails != null) {
                                    if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                                        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                                                .setTitleText("This Live Poll has ended")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(KAlertDialog kAlertDialog) {
                                                        Intent intent1 = new Intent(Multiple_type_response.this, MainActivity.class);
                                                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent1);
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            }
                        }
                    }

                }
        );
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Multiple_type_response.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Multiple_type_response.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void setGlobals(View view) {
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        c=0;
        submit=findViewById(R.id.submit);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        options=new HashMap<>();
        response=new HashMap<>();
        update = new HashMap<>();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Multiple_type_response.this);
        dialog1=new KAlertDialog(Multiple_type_response.this, SweetAlertDialog.PROGRESS_TYPE);
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
    private void showKAlertDialog(){
        dialog1.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog1.setTitleText("Uploading your response");
        dialog1.setCancelable(false);
        dialog1.show();
    }
}
