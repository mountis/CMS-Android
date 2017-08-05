package com.marionthefourth.augimas.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marionthefourth.augimas.R;
import com.onesignal.OneSignal;

public final class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        //Call the token service to save the token in the database
//        final String token = FirebaseInstanceId.getInstance().getToken();

//        TokenService tokenService = new TokenService(this, this);
//        tokenService.registerTokenInDB(token);
//        GoogleApiAvailability.makeGooglePlayServicesAvailable();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }

}