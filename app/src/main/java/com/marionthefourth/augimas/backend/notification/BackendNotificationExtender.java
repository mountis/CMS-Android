package com.marionthefourth.augimas.backend.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.SignInActivity;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;
import org.json.JSONObject;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

/**
 * Created by MGR4 on 8/6/17.
 */

public class BackendNotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult osNotificationReceivedResult) {
        Intent intent = new Intent(this, SignInActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        JSONObject messageBody = osNotificationReceivedResult.payload.additionalData;
        String navigationDirection;
        if (messageBody.has(Constants.Strings.UIDs.SENDER_UID)) {
            try {
                if (!messageBody.getString(Constants.Strings.UIDs.SENDER_UID).equals(getCurrentUser().getUID())) {
                    if (messageBody.has(Constants.Strings.Fields.FRAGMENT)) {
                        navigationDirection = messageBody.getString(Constants.Strings.Fields.FRAGMENT);
                        intent.putExtra(Constants.Strings.Fields.FRAGMENT,navigationDirection);
                    }
                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.filled_logo)
                            .setContentTitle("Augimas")
                            .setContentText(messageBody.getString(Constants.Strings.Fields.MESSAGE))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
