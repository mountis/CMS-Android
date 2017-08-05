package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.onesignal.OneSignal;

public final class SignInActivity extends AppCompatActivity {
//    Activity Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Backend.sendRegistrationToServer(this, FirebaseInstanceId.getInstance().getToken());

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        if (Backend.getCurrentUser() != null) {
            final Bundle intent = getIntent().getExtras();
            if (intent != null) {
                final Intent homeIntent = new Intent(this,HomeActivity.class);
                homeIntent.putExtras(intent);
                startActivity(homeIntent);
                return;
            }
        }
    }
}