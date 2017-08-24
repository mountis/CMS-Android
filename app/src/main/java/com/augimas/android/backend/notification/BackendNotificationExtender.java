package com.augimas.android.backend.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.classes.constants.Constants;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

import static com.augimas.android.backend.Backend.getCurrentUser;

/**
 * Created on 8/6/17.
 */

public class BackendNotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult osNotificationReceivedResult) {

        final int flags = Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK;
        final int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

        final JSONObject messageBody = osNotificationReceivedResult.payload.additionalData;

        try {
            String header = "";
            String message = "";
            String senderUID = "";
            String recentActivityUID = "";
            String navigationDirection = "";
            if (messageBody.has(Constants.Strings.Fields.FRAGMENT)) {
                navigationDirection = messageBody.getString(Constants.Strings.Fields.FRAGMENT);
            }
            if (messageBody.has(Constants.Strings.Fields.HEADER)) {
                header = messageBody.getString(Constants.Strings.Fields.HEADER);
            }
            if (messageBody.has(Constants.Strings.UIDs.RECENT_ACTIVITY_UID)) {
                recentActivityUID = messageBody.getString(Constants.Strings.UIDs.RECENT_ACTIVITY_UID);
            }
            if (messageBody.has(Constants.Strings.UIDs.SENDER_UID)) {
                senderUID = messageBody.getString(Constants.Strings.UIDs.SENDER_UID);
            }
            if (messageBody.has(Constants.Strings.Fields.MESSAGE)) {
                message = messageBody.getString(Constants.Strings.Fields.MESSAGE);
            }

            if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                if (!senderUID.equals("") && !senderUID.equals(getCurrentUser().getUID())) {
                    final Intent intent = new Intent(BackendNotificationExtender.this,HomeActivity.class);
                    intent.putExtra(Constants.Strings.UIDs.RECENT_ACTIVITY_UID,recentActivityUID);
                    intent.addFlags(flags);

                    final PendingIntent pendingIntent = PendingIntent.getActivity(BackendNotificationExtender.this, uniqueInt, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BackendNotificationExtender.this)
                            .setSmallIcon(R.drawable.filled_logo)
                            .setContentTitle(header)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0, notificationBuilder.build());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
