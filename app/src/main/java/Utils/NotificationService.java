package Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NotificationService extends FirebaseMessagingService {
    public static int id = 0;
    String TAG = "NotificationService";
    String channel_id = "PollBuzz";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("LOG_DATA", "Inside Notification service");
        initExceptions();
        if (remoteMessage.getData() != null) {
            try {
                Log.d(TAG, "ResponseNotif: " + remoteMessage.getData().toString());
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                String pollId = jsonObject.getString("pollId");
                String pollTitle = jsonObject.getString("title");
                String username = jsonObject.getString("username");
                String imageUrl = null;
                if (jsonObject.has("profilePic"))
                    imageUrl = jsonObject.getString("profilePic");
                String type = jsonObject.getString("type");
                sendNotification(pollId, pollTitle, username, imageUrl, type);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                FirebaseCrashlytics.getInstance().log(e.getMessage());
            }
        }
    }

    private void sendNotification(String pollId, String pollTitle, String username, String imageUrl, String type) {
        Bitmap image = null;
        if (imageUrl == null) {
            image = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.ic_launcher);
        } else {
            try {
                URL newurl = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) newurl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream is = connection.getInputStream();
                image = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String title = "New Poll By " + username + "!";
        String message = pollTitle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent;
            switch (type) {
                case "SINGLE CHOICE":
                    intent = new Intent(getApplicationContext(), Single_type_response.class);
                    break;
                case "MULTI SELECT":
                    intent = new Intent(getApplicationContext(), Multiple_type_response.class);
                    break;
                case "RANKED":
                    intent = new Intent(getApplicationContext(), Ranking_type_response.class);
                    break;
                case "PICTURE BASED":
                    intent = new Intent(getApplicationContext(), Image_type_responses.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            intent.putExtra("UID", pollId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSound(defaultSoundUri)
                    .setLargeIcon(image)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(channel_id,
                    "Notification channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            Log.d("LOG_DATA", "Inside channel. ");
            channel.setDescription("Desc");
            notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
        } else {
            Intent intent;
            switch (type) {
                case "SINGLE CHOICE":
                    intent = new Intent(getApplicationContext(), Single_type_response.class);
                    break;
                case "MULTI SELECT":
                    intent = new Intent(getApplicationContext(), Multiple_type_response.class);
                    break;
                case "RANKED":
                    intent = new Intent(getApplicationContext(), Ranking_type_response.class);
                    break;
                case "PICTURE BASED":
                    intent = new Intent(getApplicationContext(), Image_type_responses.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            intent.putExtra("UID", pollId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setLargeIcon(image)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
        }
        id++;
    }

    void initExceptions() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                this.startActivity(intent);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }
}

