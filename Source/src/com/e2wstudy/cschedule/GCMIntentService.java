package com.e2wstudy.cschedule;

import java.util.Iterator;
import java.util.Set;

import com.e2wstudy.cschedule.utils.CommConstant;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    String TAG="GCMIntentService";

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Bundle extras = intent.getExtras();
//        
//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//        // The getMessageType() intent parameter must be the intent you received
//        // in your BroadcastReceiver.
//        String messageType = gcm.getMessageType(intent);
//
//        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
//            /*
//             * Filter messages based on message type. Since it is likely that GCM
//             * will be extended in the future with new message types, just ignore
//             * any message types you're not interested in, or that you don't
//             * recognize.
//             */
//            if (GoogleCloudMessaging.
//                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
//            } else if (GoogleCloudMessaging.
//                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " +
//                        extras.toString());
//            // If it's a regular GCM message, do some work.
//            } else if (GoogleCloudMessaging.
//                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                // This loop represents the service doing some work.
//                for (int i=0; i<5; i++) {
//                    Log.i(TAG, "Working... " + (i+1)
//                            + "/5 @ " + SystemClock.elapsedRealtime());
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                    }
//                }
//                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
//                // Post notification of received message.
//                sendNotification("Received: " + extras.toString());
//                Log.i(TAG, "Received: " + extras.toString());
//            }
//        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
//        GcmBroadcastReceiver.completeWakefulIntent(intent);
    	/****************************Vu Le Added*********************************/
    	Bundle bundle = intent.getExtras();

		String code = "";
		String message = "";

		Set<String> keys = bundle.keySet();
		Iterator<String> it = keys.iterator();
		if (bundle != null) {
			 sendNotification(bundle.toString());
			
			while (it.hasNext()) {
				String key = it.next();
				Log.e("PUSH", "[" + key + "=" + bundle.get(key) + "]");
//				Toast.makeText(getApplicationContext(),"[" + key + "=" + bundle.get(key) + "]" ,Toast.LENGTH_LONG).show();
				if(key.equals("message"))
				{
				   sendNotification(bundle.get(key).toString());
				   break;
				}
			}
		}
     
        
        /****************************End*********************************/
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
//        mNotificationManager = (NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);

//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, DemoActivity.class), 0);
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//        .setSmallIcon(R.drawable.ic_stat_gcm)
//        .setContentTitle("GCM Notification")
//        .setStyle(new NotificationCompat.BigTextStyle()
//        .bigText(msg))
//        .setContentText(msg);

//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    	/****************************Vu Le Added*********************************/
        Context context = getBaseContext();
        int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(icon).setContentTitle("CSchedule")
				.setContentText(msg);

		Intent notificationIntent = new Intent(context, BaseActivity.class);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(intent);

		mBuilder.setDefaults(Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE);
		mBuilder.setAutoCancel(true);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(CommConstant.NOTIFICATION_ID++, mBuilder.build());
		/****************************End*********************************/
    }
}