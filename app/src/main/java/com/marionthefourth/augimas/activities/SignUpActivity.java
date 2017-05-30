package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                transitionUserBackToSignInScreen(this);
                return true;
        }

    }

    private void transitionUserBackToSignInScreen(AppCompatActivity activity) {
        Intent signInIntent = new Intent(activity, SignInActivity.class);
        activity.startActivityForResult(signInIntent, 0);
    }
}
