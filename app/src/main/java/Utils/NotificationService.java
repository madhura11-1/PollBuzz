package Utils;

import android.content.pm.ResolveInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService{
    private ResolveInfo resolveInfo;
    public static int id=0;
    String TAG = "NotificationService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("LOG_DATA","Inside Notification service");
        if(remoteMessage.getData()!=null){
//            try{
//                Log.d(TAG,"ResponseNotif: "+remoteMessage.getData().toString());
//                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
//                String title=jsonObject.getString("title");
//                Log.d(TAG,"Title: "+jsonObject.getString("title"));
//                String message=jsonObject.getString("message");
//                String uniqueId = jsonObject.getString("uniqueId");
//                String entityId = jsonObject.getString("entityId");
//                Log.d(TAG,"EntityId: " + entityId);
//                String imageUrl;
//                if(jsonObject.has("imageUrl"))
//                    imageUrl = jsonObject.getString("imageUrl");
//                else
//                    imageUrl = "";
//                String kind = jsonObject.getString("kind");
//                Log.d("Notif",kind);
//                sendNotification(title,message, entityId, kind, imageUrl,uniqueId);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//                Crashlytics.logException(e);
//                Log.e(TAG,e.getMessage());
//            }
        }
    }

//    private void sendNotification(String title,String message, String entityId, String kind, String imageUrl, String uniqueId){
//
//        String postId = null;
//        String followerId = null;
//        Boolean isComment=FALSE;
//        String clanid = null;
//        Log.d("Notification",kind);
//        if(kind.equals("comment_recieved"))
//        {
//            postId = entityId;
//            followerId = null;
//            isComment=Boolean.TRUE;
//        }
//        else if(kind.equals("new_post") || kind.equals("post_upvoted") || kind.equals("trending_post")){
//            postId = entityId;
//            followerId = null;
//        }
//        else if (kind.equals("new_follower")){
//            postId = null;
//            followerId = entityId;
//        }
//        else if(kind.equals("tagged_in_comment")){
//            postId = entityId;
//            followerId = null;
//            isComment=Boolean.TRUE;
//        }
//        else if(kind.equals("tagged_in_post")){
//            postId = entityId;
//            followerId = null;
//        }else if(kind.equals("share_clan")){
//            clanid=entityId;
//        }
//        Bitmap image = null;
//        if(imageUrl.isEmpty() || imageUrl.equals("null") || imageUrl.equals(""))
//        {
//            image = BitmapFactory.decodeResource(this.getResources(),
//                    R.drawable.ic_onesignal_large_icon_default);
//        }
//        else
//        {
//            try {
//                URL newurl = new URL(imageUrl);
//                image = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
//                //image =
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//                Crashlytics.log(e.toString());
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            Intent intent;
//            intent = new Intent(getApplicationContext(), HomeFeed.class);
//            intent.putExtra(HomeFeed.PARAM_TOKEN,postId);
//            intent.putExtra(HomeFeed.PARAM_TOKEN_ProfileID,followerId);
//            intent.putExtra(HomeFeed.PARAM_ISCOMMENT,isComment);
//            intent.putExtra(HomeFeed.PARAM_TOKEN_ClanId,clanid);
//            intent.putExtra("NotifClicked",uniqueId);
//            intent.putExtra("Notification",true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(title)
////                    .setContentText(message)
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setAutoCancel(true)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                    .setSound(defaultSoundUri)
//                    //.setLargeIcon(getDrawable(R.drawable.ic_onesignal_large_icon_default))
//                    //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
//                    //      R.drawable.ic_onesignal_large_icon_default))
//                    .setLargeIcon(image)
//                    .setContentIntent(pendingIntent);
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            /* Create or update. */
//            NotificationChannel channel = new NotificationChannel(channel_id,
//                    "Notification channel",
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//            Log.d("LOG_DATA","Inside channel. ");
//            channel.setDescription("Desc");
//            notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
//        }
//        else{
//            Intent intent;
//            intent = new Intent(getApplicationContext(), HomeFeed.class);
//            intent.putExtra(HomeFeed.PARAM_TOKEN,postId);
//            intent.putExtra(HomeFeed.PARAM_TOKEN_ProfileID,followerId);
//            intent.putExtra(HomeFeed.PARAM_ISCOMMENT,isComment);
//            intent.putExtra(HomeFeed.PARAM_TOKEN_ClanId,clanid);
//            intent.putExtra("NotifClicked",uniqueId);
//            intent.putExtra("Notification",true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(title)
////                    .setContentText(message)
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setLargeIcon(image)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                    .setContentIntent(pendingIntent);
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
//        }
//        id++;
//    }
}

