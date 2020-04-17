package com.PollBuzz.pollbuzz.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.kinda.alert.KAlertDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import Utils.helper;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Descriptive_type_poll extends AppCompatActivity {
    MaterialButton post_descriptive;
    TextInputLayout query;
    TextInputEditText question_descriptive;
    firebase fb;
    ImageButton home, logout;
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    KAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_type);
        setGlobals();

        final String formatteddate = dateFormat.format(date);
        setListeners(formatteddate);
        setActionBarFunctionality();
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Descriptive_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }

    private void setListeners(String formatteddate) {
        post_descriptive.setOnClickListener(view -> {
            closeKeyboard();
            if (question_descriptive.getText().toString().isEmpty()) {
                question_descriptive.setError("Please enter the question");
                question_descriptive.requestFocus();
            } else {
                addToDatabase(formatteddate);
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addToDatabase(String formatteddate) {
        try {
            showDialog();
            post_descriptive.setEnabled(false);
            if (fb.getUser() != null) {
                PollDetails polldetails = new PollDetails();
                polldetails.setQuestion(question_descriptive.getText().toString().trim());
                polldetails.setCreated_date(dateFormat.parse(formatteddate));
                polldetails.setPoll_type("DESCRIPTIVE POLL");
                polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
                polldetails.setAuthorUID(fb.getUserId());
                polldetails.setTimestamp(Timestamp.now().getSeconds());
                CollectionReference docCreated = fb.getUserDocument().collection("Created");
                DocumentReference doc = fb.getPollsCollection().document();
                doc.set(polldetails)
                        .addOnSuccessListener(aVoid -> {
                            Map<String, Object> m = new HashMap<>();
                            m.put("pollId", doc.getId());
                            m.put("timestamp", Timestamp.now().getSeconds());
                            docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Descriptive_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismissWithAnimation();
                                        Intent intent = new Intent(Descriptive_type_poll.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Descriptive_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismissWithAnimation();
                                        post_descriptive.setEnabled(true);
                                    }
                                }
                            });

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Descriptive_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                            dialog.dismissWithAnimation();
                            post_descriptive.setEnabled(true);
                        });
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
    }


    private void setGlobals() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        home = findViewById(R.id.home);
        logout = findViewById(R.id.logout);
        post_descriptive = findViewById(R.id.post_descriptive);
        question_descriptive = findViewById(R.id.question_descriptive);
        dialog = new KAlertDialog(Descriptive_type_poll.this, SweetAlertDialog.PROGRESS_TYPE);

    }
}