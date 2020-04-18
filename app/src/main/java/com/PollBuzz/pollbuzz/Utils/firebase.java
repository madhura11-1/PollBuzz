package com.PollBuzz.pollbuzz.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kinda.alert.KAlertDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class firebase {
    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public FirebaseUser getUser() {
        return getAuth().getCurrentUser();
    }

    public String getUserId() {
        return getUser().getUid();
    }

    private FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }

    public CollectionReference getUsersCollection() {
        return getDatabase().collection("Users");
    }

    public CollectionReference getPollsCollection() {
        return getDatabase().collection("Polls");
    }

    public DocumentReference getUserDocument() {
        return getUsersCollection().document(getUserId());
    }

    public void signOut(Context context) {
        KAlertDialog dialog = new KAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Logging Out...");
        dialog.setCancelable(false);
        dialog.show();
        com.PollBuzz.pollbuzz.Utils.helper.removeProfileSetUpPref(context);
        if (getUser() != null) {
            getUserDocument().collection("Favourite Authors").get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult()!=null){
                    for(DocumentSnapshot dS:task.getResult()){
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(dS.getId());
                        Log.d("UnSubscribedFrom",dS.getId());
                    }
                }else{
                    Log.d("UnSubscribedFrom",task.getException().getMessage());
                }
                getAuth().signOut();
            });
        }
        Intent i=new Intent(context, LoginSignupActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        dialog.dismissWithAnimation();
        context.startActivity(i);
    }

    public StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }
}
