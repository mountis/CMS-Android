package com.marionthefourth.augimas.backend.notification;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.entities.Device;

public final class BackendInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        final Context context = getApplicationContext();
        Backend.getReference(context, R.string.firebase_devices_directory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot deviceSnapshot:dataSnapshot.getChildren()) {
                        final Device device = new Device(deviceSnapshot);
                        if (device.getToken().equals(token)) {
                            return;
                        }
                    }

                    final Device currentDevice = new Device(token);
                    Backend.save(context,currentDevice);
                } else {
                    final Device currentDevice = new Device(token);
                    Backend.save(context,currentDevice);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
