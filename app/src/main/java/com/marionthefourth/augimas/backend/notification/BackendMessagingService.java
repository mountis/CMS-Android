package com.marionthefourth.augimas.backend.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.SignInActivity;
import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.Map;

public final class BackendMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getData());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private void sendNotification(Map<String, String> messageBody) {
        Intent intent = new Intent(this, SignInActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String message = "Default Message";

        if (messageBody.containsKey("message")) {
            message = messageBody.get("message");
        }

        String navigationDirection = Constants.Strings.Fragments.HOME;

        if (messageBody.containsKey(Constants.Strings.Fields.FRAGMENT)) {
            navigationDirection = messageBody.get(Constants.Strings.Fields.FRAGMENT);
        }

        intent.putExtra(Constants.Strings.Fields.FRAGMENT,navigationDirection);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logl)
                .setContentTitle("Augimas")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
