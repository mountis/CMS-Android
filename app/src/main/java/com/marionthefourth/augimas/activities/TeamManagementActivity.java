package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;

public class TeamManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                transitionUserBackToHomeScreen(this);
                return true;
        }

    }

    private void transitionUserBackToHomeScreen(AppCompatActivity activity) {
        Intent homeIntent = new Intent(activity, HomeActivity.class);
        activity.startActivityForResult(homeIntent, 0);
    }

}