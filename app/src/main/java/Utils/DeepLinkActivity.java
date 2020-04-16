package Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class DeepLinkActivity extends AppCompatActivity {
    String UID, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_deep_link);
        String code = getData(getIntent().getData());
        if (code != null) {
            type = code.substring(0, 1);
            UID = code.substring(1);
        }
        startIntent(UID, type);
        //startIntent(UID,type);
    }

    private void startIntent(String uid, String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.PARAMS_UID, uid);
        intent.putExtra(MainActivity.PARAMS_TYPE, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String getData(Uri data) {
        if (data != null && data.isHierarchical()) {
            String url = data.toString();
            int lastIndex = url.lastIndexOf("/");
            return url.substring(lastIndex + 1);
        }
        return null;
    }
}
