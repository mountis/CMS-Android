package com.marionthefourth.augimas.backend.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.SignInActivity;
import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.Map;

public final class BackendMessagingService extends FirebaseMessagingService {
//    Service Methods
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData(),remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification() != null) {
            sendNotification(null,remoteMessage.getNotification().getBody());
        }
    }
//    Notification Sending Method
    private void sendNotification(Map<String, String> messageBody, String body) {
        Intent intent = new Intent(this, SignInActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String message = "Default Message";
        String navigationDirection = Constants.Strings.Fragments.HOME;

        if (messageBody != null) {
            if (messageBody.containsKey("message")) {
                message = messageBody.get("message");
            }
            if (messageBody.containsKey(Constants.Strings.Fields.FRAGMENT)) {
                navigationDirection = messageBody.get(Constants.Strings.Fields.FRAGMENT);
            }

            intent.putExtra(Constants.Strings.Fields.FRAGMENT,navigationDirection);
        } else {
            if (body != null) {
                message = body;
            }
        }

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
