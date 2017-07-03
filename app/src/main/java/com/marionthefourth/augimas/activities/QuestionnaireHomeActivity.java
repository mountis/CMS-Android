package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;

public final class QuestionnaireHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                transitionBackToSettingsScreen();
                return true;
        }

    }

    private void transitionBackToSettingsScreen() {
        final Intent settingsIntent = new Intent(this, HomeActivity.class);
        startActivityForResult(settingsIntent, 0);
    }
}
