package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;

public final class TeamManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                transitionUserBackToHomeScreen();
                return true;
        }

    }

    private void transitionUserBackToHomeScreen() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivityForResult(homeIntent, 0);
    }

}