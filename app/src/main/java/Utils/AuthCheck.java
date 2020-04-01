package Utils;

import com.PollBuzz.pollbuzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.LoginSignup.ProfileSetUp;
import com.PollBuzz.pollbuzz.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AuthCheck extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_check);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);
        firebase fb = new firebase();
        Intent i = getIntent(fb);
        startActivity(i);
    }

    private Intent getIntent(firebase fb) {
        Intent i = new Intent(AuthCheck.this, LoginSignupActivity.class);
        if (!isUserLoggedIn(fb)) {
            helper.removeProfileSetUpPref(getApplicationContext());
            Bundle bundle = new Bundle();
            bundle.putLong("timestamp", Timestamp.now().getSeconds());
            mFirebaseAnalytics.logEvent("opened_unknown", bundle);
        }
        else {
            fb.getUserDocument().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot dS=task.getResult();
                        Bundle bundle = new Bundle();
                        bundle.putString("_id",fb.getUserId());
                        if (dS != null) {
                            bundle.putString("username",dS.get("username").toString());
                        }
                        bundle.putLong("timestamp", Timestamp.now().getSeconds());
                        mFirebaseAnalytics.logEvent("opened", bundle);
                    }
                }
            });
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
