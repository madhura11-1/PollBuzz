package Utils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.LoginSignup.ProfileSetUp;
import com.PollBuzz.pollbuzz.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AuthCheck extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebase fb = new firebase();
        Intent i = getIntent(fb);
        startActivity(i);
    }

    private Intent getIntent(firebase fb) {
        Intent i = new Intent(AuthCheck.this, LoginSignupActivity.class);
        if (!isUserLoggedIn(fb)) {
            helper.removeProfileSetUpPref(getApplicationContext());
<<<<<<< HEAD
            Bundle bundle = new Bundle();
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            mFirebaseAnalytics.logEvent("open_by_unknown", bundle);
        }
        else {
            fb.getUserDocument().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot dS=task.getResult();
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id",fb.getUserId());
                        if (dS != null) {
                            bundle.putString("username",dS.get("username").toString());
                        }
                        bundle.putString("timestamp", Timestamp.now().toDate().toString());
                        mFirebaseAnalytics.logEvent("opened", bundle);
                    }
                }
            });
=======
        }
        else {
>>>>>>> parent of 85d8e2c... Events added
            i = isProfileSetUp() ? new Intent(AuthCheck.this, MainActivity.class) :
                    new Intent(AuthCheck.this, ProfileSetUp.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return i;
    }

    Boolean isUserLoggedIn(firebase fb) {
        return fb.getUser() != null;
    }

    Boolean isProfileSetUp() {
        return helper.getProfileSetUpPref(getApplicationContext());
    }
}
