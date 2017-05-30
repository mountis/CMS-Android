package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar(getSupportActionBar());

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            default:
                transitionUserToHomeActivity(this);
                return true;
        }
    }

    private void setupActionBar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    private void transitionUserToHomeActivity(AppCompatActivity activity) {
        Intent homeIntent = new Intent(activity, HomeActivity.class);
        activity.startActivityForResult(homeIntent, 0);
    }
}
