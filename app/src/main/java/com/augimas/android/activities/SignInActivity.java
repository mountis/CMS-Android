package com.augimas.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onesignal.OneSignal;

public final class SignInActivity extends AppCompatActivity {
//    Activity Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Backend.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), this);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}