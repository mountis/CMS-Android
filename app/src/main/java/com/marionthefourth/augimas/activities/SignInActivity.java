package com.marionthefourth.augimas.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.entities.Device;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import me.pushy.sdk.Pushy;

public final class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
//
        new RegisterForPushNotificationsAsync().execute();

    }

    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {
                // Assign a unique token to this device
                final String deviceToken = Pushy.register(getApplicationContext());

                // Log it for debugging purposes
                Log.d("MyApp", "Pushy device token: " + deviceToken);

                // Send the token to your backend server via an HTTP GET request

                FirebaseHelper.getReference(SignInActivity.this,R.string.firebase_devices_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                            for (DataSnapshot deviceSnapshot:dataSnapshot.getChildren()) {
                                final Device device = new Device(deviceSnapshot);
                                if (device.getToken().equals(deviceToken)) {

                                    Pushy.listen(SignInActivity.this);

                                    // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
                                        // Pushy SDK will be able to persist the device token in the external storage
                                        ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                    }
                                    return;
                                }
                            }

                            final Device currentDevice = new Device(deviceToken);
                            FirebaseHelper.save(SignInActivity.this,currentDevice);
                            Pushy.listen(SignInActivity.this);

                            // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
                                // Pushy SDK will be able to persist the device token in the external storage
                                ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }

                        } else {
                            final Device currentDevice = new Device(deviceToken);
                            FirebaseHelper.save(SignInActivity.this,currentDevice);
                            Pushy.listen(SignInActivity.this);

                            // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
                                // Pushy SDK will be able to persist the device token in the external storage
                                ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });            }
            catch (Exception exc) {
                // Return exc to onPostExecute
                return exc;
            }

            // Success
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc) {
            // Failed?
            if (exc != null) {
                // Show error as toast message
                Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            // Succeeded, do something to alert the user
        }
    }

}
