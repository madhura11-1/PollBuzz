package Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.PollBuzz.pollbuzz.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class DeepLinkActivity extends AppCompatActivity {
    String UID, type;
    static String TAG="DeepLink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        String code = getData(getIntent().getData());
        if (code != null) {
            type = code.substring(0, 1);
            UID = code.substring(1);
        }
        startIntent(UID, type);
    }

    private void startIntent(String uid, String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.PARAMS_UID, uid);
        intent.putExtra(MainActivity.PARAMS_TYPE, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d(TAG,"intent");
        startActivity(intent);
        finish();
    }

    private String getData(Uri data) {
        if (data != null && data.isHierarchical()) {
            String url = data.toString();
            Bundle bundle = new Bundle();
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            bundle.putString("link",url);
            FirebaseAnalytics.getInstance(this).logEvent("link_clicked", bundle);
            int lastIndex = url.lastIndexOf("/");
            return url.substring(lastIndex + 1);
        }
        return null;
    }
}
