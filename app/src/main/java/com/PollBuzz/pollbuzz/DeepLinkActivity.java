package com.PollBuzz.pollbuzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Date;

import Utils.firebase;

public class DeepLinkActivity extends AppCompatActivity {
    String UID, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);
        String code = getData(getIntent().getData());
        type = code.substring(0, 1);
        UID = code.substring(1);
        checkAccess(UID);
        //startIntent(UID,type);
    }

    private void checkAccess(String uid) {
        firebase fb = new firebase();

        if (fb.getUser() != null) {
            fb.getPollsCollection().document(uid).collection("Response").document(fb.getUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document1 = task.getResult();
                        if (document1.exists())
                            startIntent(UID, type, 1);
                        else startIntent(UID, type, 0);
                    }

                }
            });
        } else {
            Intent intent = new Intent(this, LoginSignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    private String getData(Uri data) {
        if (data != null && data.isHierarchical()) {
            String url = data.toString();
            int lastIndex = url.lastIndexOf("/");
            String code = url.substring(lastIndex + 1);
            return code;
        }
        return null;
    }

    private void startIntent(String uid, String pollType, int flag) {
        Intent intent;
        switch (pollType) {
            case "0":
                intent = new Intent(DeepLinkActivity.this, Single_type_response.class);
                break;
            case "1":
                intent = new Intent(DeepLinkActivity.this, Multiple_type_response.class);
                break;
            case "2":
                intent = new Intent(DeepLinkActivity.this, Ranking_type_response.class);
                break;
            case "3":
                intent = new Intent(DeepLinkActivity.this, Image_type_responses.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pollType);
        }
        intent.putExtra("UID", uid);
        intent.putExtra("flag", flag);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }
}
