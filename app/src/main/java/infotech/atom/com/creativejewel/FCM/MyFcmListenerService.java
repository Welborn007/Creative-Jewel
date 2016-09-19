package infotech.atom.com.creativejewel.FCM;



import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import infotech.atom.com.creativejewel.Category_Listing;
import infotech.atom.com.creativejewel.R;
import infotech.atom.com.creativejewel.login_screen;
import infotech.atom.com.creativejewel.network.Constants;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";
    String url;
    Bitmap bitmap;

    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin" ;
    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;

    String username,status;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String message_string = message.getData().toString();
        Log.d(TAG, "From: " + message.getFrom());
        Log.d(TAG, "Message: " + message_string);

        /*if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }*/

//        String[] StringAll;
//        StringAll = message.split("\\^");
//        String status = StringAll[0];
//        String scheduleid = StringAll[1];
//        String username = StringAll[2];
//        String msg = StringAll[3];
//        String img = StringAll[4];
//        String schedule_no = StringAll[5];
//
//        showNotification(message);

        try {

            JSONObject resultMessage = new JSONObject(message.getData());

            String message_json = resultMessage.getString("message");

            Log.i("message_json",message_json);

            sendNotification(message_json);

        }catch (JSONException je)
        {

        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String message) {

        Intent myIntent = null;
        PendingIntent pendingIntent = null;
        Notification notification = null;
//            url = "http://www.creativejewel.in/items/1_volume2.jpg";
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
        username = sharedpreferencesLogin.getString("username", null);
        status = sharedpreferencesLogin.getString("status", null);

        if (username != null && status != null) {

            if (!username.isEmpty() && status.contains("Login Successful")) {
                myIntent = new Intent();
                myIntent.putExtra("volume","new_arrival");
                myIntent.setClass(getApplicationContext(), Category_Listing.class);
            }
        }
        else {
            myIntent = new Intent();
            myIntent.setClass(getApplicationContext(), login_screen.class);
        }

//        try {
//            bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
//            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
//        notiStyle.setBigContentTitle("Creative Jewel");
//        notiStyle.setSummaryText(message);
//
//        notiStyle.bigPicture(bitmap);

        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification =  new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("Creative Jewel")
                .setContentText(message)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        // notification.flags |= Notification.FLAG_INSISTENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        //	notification.number = Integer.valueOf(notCount);
        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(id, notification);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String message) {
        Intent myIntent = null;

            String[] StringAll;
            StringAll = message.split("\\^");
            String content = StringAll[0];
            String img = StringAll[1];
            String cat = StringAll[2];

            Log.i("content",content);
            Log.i("img",img);
            Log.i("cat",cat);

        String url = Constants.CREATIVE_URL_VERSION_TWO + "push_images/" + img;

        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
        username = sharedpreferencesLogin.getString("username", null);
        status = sharedpreferencesLogin.getString("status", null);

        if (username != null && status != null) {

            if (!username.isEmpty() && status.contains("Login Successful")) {
                myIntent = new Intent();
                myIntent.putExtra("volume",cat);
                myIntent.setClass(getApplicationContext(), Category_Listing.class);
            }
        }
        else {
            myIntent = new Intent();
            myIntent.setClass(getApplicationContext(), login_screen.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, myIntent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Creative Jewel")
//                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("shhs djjsdl jjffjv sdjs dkkds hshsj dskcdcjds djsd sdjjdjsdjsj dscjcdcjsdjc sdcjcdsh"))
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(largeIcon))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo_trans);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        if(img.contains("NA"))
        {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content)).setContentText(content);
        }
        else
        {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                bitmap = Bitmap.createScaledBitmap(bitmap, 350, 150, false);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)).setSubText(content);
        }

//        notification.defaults |= Notification.DEFAULT_SOUND;
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        // notification.flags |= Notification.FLAG_INSISTENT;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //	notification.number = Integer.valueOf(notCount);
        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(id, notificationBuilder.build());

//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}