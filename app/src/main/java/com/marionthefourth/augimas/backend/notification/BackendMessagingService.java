package com.marionthefourth.augimas.backend.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.SignInActivity;
import com.marionthefourth.augimas.classes.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BackendMessagingService extends FirebaseMessagingService {
//    Service Methods
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getNotification() == null) {
                sendNotification(remoteMessage.getData(),null);
            } else {
                sendNotification(remoteMessage.getData(),remoteMessage.getNotification().getBody());
            }
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
            if (!messageBody.containsKey(Constants.Strings.Fields.MESSAGE) || !messageBody.containsKey(Constants.Strings.Fields.FRAGMENT)) {
                final ArrayMap<String,String> mapBody = (ArrayMap<String, String>) messageBody;

                JSONObject jsonObject = new JSONObject();

                final Iterator<Map.Entry<String, String>> obj = mapBody.entrySet().iterator();

                int i = 0;

                for(Map.Entry<String, String> entrySet:mapBody.entrySet()) {
                    try {
                        if (i == 0) {
                            jsonObject.put(entrySet.getKey(),entrySet.getValue());
                            if (jsonObject != null) {
                                messageBody = parseCustomJSONString(jsonObject.getString("custom"));
                                if (messageBody.containsKey(Constants.Strings.Fields.MESSAGE)) {
                                    message = messageBody.get(Constants.Strings.Fields.MESSAGE);
                                }
                                if (messageBody.containsKey(Constants.Strings.Fields.FRAGMENT)) {
                                    navigationDirection = messageBody.get(Constants.Strings.Fields.FRAGMENT);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i++;
                }

            } else {
                if (messageBody.containsKey(Constants.Strings.Fields.MESSAGE)) {
                    message = messageBody.get(Constants.Strings.Fields.MESSAGE);
                }
                if (messageBody.containsKey(Constants.Strings.Fields.FRAGMENT)) {
                    navigationDirection = messageBody.get(Constants.Strings.Fields.FRAGMENT);
                }
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

    private HashMap<String,String> parseCustomJSONString(String custom) {
        final HashMap<String,String> result = new HashMap<>();
        // Search custom text for groups of 2 quotes
        // Ignore the first one because it because the object
        // after that, pair the two
        // "a"
        // :{
        // "fragment"
        // :
        // "Branding Elements"
        // ,
        // "message":
        // "Paul Orrow approved Social Media Name."
        // },
        // "i"
        int indexOfQuote = custom.indexOf("\"");
        Log.i("","All Data: " + custom);

        int quoteCount = 1;
        while (indexOfQuote != -1) {
            final String nC = custom.substring(indexOfQuote);

            Log.i("","SubStringData[" + quoteCount + "]: " + nC);

            if (quoteCount != 1) {
                // Read next 2 quotes
                final String sub1 = nC;
                int aSlice = sub1.indexOf("\"");

                final String sub2 = custom.substring(aSlice);
                int bSlice = sub2.indexOf("\"");
                Log.i("","Sub 1: " + sub1 + " & Sub 2: " + sub2);
                Log.i("","Slice A: " + aSlice + " & Slice B: " + bSlice + " & String Length: " + custom.length());
                if (aSlice == -1 || bSlice == -1) {
                    return result;
                }

                // Check content if it matches particular keys

                final String key = custom.substring(aSlice,bSlice);

                final String newCustom = custom.substring(bSlice);
                if (key.contains(Constants.Strings.Fields.FRAGMENT) ||
                        key.contains(Constants.Strings.Fields.MESSAGE)) {
                    // If it matches, capture the data between the next two quotes
                    int cSlice = newCustom.indexOf("\"");
                    final String sub4 = newCustom.substring(cSlice);
                    int dSlice = sub4.indexOf("\"");

                    Log.i("","Sub 3: " + newCustom + " & Sub 4: " + sub4);
                    Log.i("","Slice C: " + aSlice + " & Slice D: " + bSlice + " & String Length: " + newCustom.length());

                    if (cSlice == -1 || dSlice == -1 && cSlice > dSlice) {
                        return result;
                    }

                    if (key.contains(Constants.Strings.Fields.FRAGMENT)) {
                        // You'll be able to judge
                        final String data = custom.substring(cSlice,dSlice);
                        if (data.contains(Constants.Strings.Fragments.HOME) || data.contains(Constants.Strings.Fragments.DASHBOARD)) {
                            result.put(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.HOME);
                        } else if (data.contains(Constants.Strings.Fragments.CHAT)) {
                            result.put(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.CHAT);
                        }  else if (data.contains(Constants.Strings.Fragments.BRANDING_ELEMENTS)) {
                            result.put(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.BRANDING_ELEMENTS);
                        } else if (data.contains(Constants.Strings.Fragments.TEAM_MANAGEMENT)) {
                            result.put(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.TEAM_MANAGEMENT);
                        } else {
                            // CIJISOFA
                        }
                    } else {
                        final String data = custom.substring(cSlice,dSlice-1);
                        result.put(Constants.Strings.Fields.MESSAGE,data);
                    }

                    indexOfQuote = custom.substring(dSlice).indexOf("\"");
                    Log.i("",result.toString());
                } else {
                    indexOfQuote = bSlice;
                    Log.i("",result.toString());
                }
            } else {
                indexOfQuote = custom.substring(indexOfQuote).indexOf("\"");

            }


            quoteCount++;
        }

        return result;
    }
}
