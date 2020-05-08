package com.PollBuzz.pollbuzz.results;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;

public class Descriptive_type_result extends AppCompatActivity {
    TextView query, answer;
    Map<String, Object> response;
    Typeface typeface;
    Dialog dialog;
    ImageButton home, logout;
    FirebaseAuth.AuthStateListener listener;
    String key, uid;
    Integer integer;
    firebase fb = new firebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();

        setGlobals(view);
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        setAuthStateListener();
        showDialog();
        retriveData(fb);

    }

    private void setAuthStateListener() {

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(Descriptive_type_result.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };
    }

    private void retriveData(firebase fb) {

        fb.getPollsCollection().document(key)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                                     if (task.isSuccessful()) {

                                                         DocumentSnapshot data = task.getResult();
                                                         if (data.exists()) {
                                                             dialog.dismiss();
                                                             PollDetails polldetails = data.toObject(PollDetails.class);
                                                             query.setText(polldetails.getQuestion());
                                                             fb.getPollsCollection().document(key).collection("Response").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                     if (task.isSuccessful()) {
                                                                         DocumentSnapshot data = task.getResult();
                                                                         if (data.exists()) {
                                                                             response = data.getData();
                                                                             setAnswer();
                                                                         }
                                                                     }

                                                                 }
                                                             });

                                                         }
                                                     }

                                                 }
                                             }
        );
    }

    private void setActionBarFunctionality() {


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Descriptive_type_result.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.signOut(Descriptive_type_result.this);
            }
        });
    }

    private void getIntentExtras(Intent intent) {

        key = intent.getExtras().getString("UID");
        integer = intent.getExtras().getInt("flag");

        if (integer == 1) {
            uid = intent.getExtras().getString("UIDUser");
        }
        if (integer == 0) {
            uid = fb.getUserId();
        }

    }

    private void setGlobals(View view) {
        home = view.findViewById(R.id.home);
        query = findViewById(R.id.query);
        answer = findViewById(R.id.answer);
        response = new HashMap<>();

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Descriptive_type_result.this);
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

    private void setAnswer() {
        String key = "";
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            key = key + entry.getValue();
        }
        answer.setText(key);
        dialog.dismiss();
    }
}
