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
import java.util.Map;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

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
//    RecentActivity Sending Method
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
                JSONObject jsonObject1 = new JSONObject();

                int i = 0;

                for(Map.Entry<String, String> entrySet:mapBody.entrySet()) {
                    try {
                        if (i == 0) {
                            jsonObject.put(entrySet.getKey(),entrySet.getValue());
                            if (jsonObject != null) {
                                for (int j = 0; j < 1; j++) {
                                    jsonObject1.put("body",mapBody.get(j));
                                }
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

        if (messageBody.containsKey(Constants.Strings.UIDs.SENDER_UID)) {
            if (!messageBody.get(Constants.Strings.UIDs.SENDER_UID).equals(getCurrentUser().getUID())) {
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
                int aSlice = sub1.indexOf("\"")+1;
                int bSlice = sub1.indexOf("\"",aSlice+1);
                Log.i("","Slice A: " + aSlice + " & Slice B: " + bSlice + " & String Length: " + custom.length());
                if (aSlice == -1 || bSlice == -1) {
                    return result;
                }

                // Check content if it matches particular keys

                final String key = sub1.substring(aSlice,bSlice);

                final String newCustom = sub1.substring(bSlice+1);
                if (key.contains(Constants.Strings.Fields.FRAGMENT) ||
                        key.contains(Constants.Strings.Fields.MESSAGE) ||
                        key.contains(Constants.Strings.UIDs.SENDER_UID)) {
                    // If it matches, capture the data between the next two quotes
                    int cSlice = newCustom.indexOf("\"")+1;
                    int dSlice = newCustom.indexOf("\"",cSlice+1);

                    Log.i("","Slice C: " + aSlice + " & Slice D: " + bSlice + " & String Length: " + newCustom.length());

                    if (cSlice == -1 || dSlice == -1 && cSlice > dSlice) {
                        return result;
                    }

                    final String data = newCustom.substring(cSlice,dSlice);

                    if (key.contains(Constants.Strings.Fields.FRAGMENT)) {
                        // You'll be able to judge
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
                    } else if (key.contains(Constants.Strings.Fields.MESSAGE)){
                        result.put(Constants.Strings.Fields.MESSAGE,data);
                    } else if (key.contains(Constants.Strings.UIDs.SENDER_UID)) {
                        result.put(Constants.Strings.UIDs.SENDER_UID,data);
                    }

                    if (result.size() == 3) {
                        return result;
                    }

                    custom = newCustom.substring(dSlice+1);
                    indexOfQuote = custom.indexOf("\"");
                    Log.i("",result.toString());
                } else {
                    indexOfQuote = bSlice+1;
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
