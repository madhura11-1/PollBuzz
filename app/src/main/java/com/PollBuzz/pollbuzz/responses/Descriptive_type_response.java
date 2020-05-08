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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;

public class Descriptive_type_response extends AppCompatActivity {
    Button submit;
    TextView query;
    Map<String, String> response;
    Typeface typeface;
    Dialog dialog;
    ImageButton home, logout;
    firebase fb;
    TextInputLayout answer;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        Intent intent = getIntent();
        getIntentExtras(intent);
        View view = getSupportActionBar().getCustomView();
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();
        retrieveData();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResponse();

            }
        });


    }

    private void submitResponse() {
        response.put("option", answer.getEditText().getText().toString());
        System.out.println(response);
        fb.getPollsCollection().document(key).collection("Response")
                .document(fb.getUserId()).set(response);
        Map<String, Object> mapi = new HashMap<>();
        mapi.put("pollId", fb.getUserId());
        mapi.put("timestamp", Timestamp.now().getSeconds());
        fb.getUserDocument().collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Descriptive_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Descriptive_type_response.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(e -> Toast.makeText(Descriptive_type_response.this, "Unable to submit.\nPlease try again ", Toast.LENGTH_SHORT).show());
    }

    private void retrieveData() {
        fb.getPollsCollection().document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot data = task.getResult();
                    if (data.exists()) {
                        dialog.dismiss();
                        PollDetails polldetails = data.toObject(PollDetails.class);
                        query.setText(polldetails.getQuestion());
                    }
                }
            }
        });

    }


    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Descriptive_type_response.this, MainActivity.class);
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

    private void setGlobals(View view) {
        submit = findViewById(R.id.submit);
        query = findViewById(R.id.query);
        answer = findViewById(R.id.answer);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Descriptive_type_response.this);
        fb = new firebase();
        response = new HashMap<>();
        home = view.findViewById(R.id.home);
    }
}
