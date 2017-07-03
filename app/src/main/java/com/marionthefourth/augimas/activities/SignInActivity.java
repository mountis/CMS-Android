package com.marionthefourth.augimas.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marionthefourth.augimas.R;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.printIDToken;

public final class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        printIDToken();
    }
}
