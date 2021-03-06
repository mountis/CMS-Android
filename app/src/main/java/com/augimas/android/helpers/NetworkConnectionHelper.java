package com.augimas.android.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.augimas.android.R;
import com.augimas.android.activities.SignInActivity;

import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

/**
 * Created on 9/1/17.
 */

public final class NetworkConnectionHelper {
    public static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (isConnected(context)){
                    FragmentHelper.display(TOAST,R.string.message_network_connection_connected,context);
                } else {
                    FragmentHelper.display(TOAST,R.string.message_network_connection_lost,context);
                }
            }
        }
    }

    /**
     * Check if there is any connectivity
     * @param context
     * @return
     */
    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private static void sendNotification(Context context) {
        // Send Notification Stating Connection Lost
        final int flags = Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK;
        final int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        final Intent newIntent = new Intent(context, SignInActivity.class);
        newIntent.addFlags(flags);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, uniqueInt, newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.filled_logo)
                .setContentTitle("Connection Lost")

                .setContentText("You appear to not be connected to the network. You will not be able to access the features of the application without it.")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
