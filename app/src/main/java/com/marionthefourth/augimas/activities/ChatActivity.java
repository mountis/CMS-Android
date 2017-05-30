package com.marionthefourth.augimas.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.Team;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.Constants.Strings.TEAM;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupActionBar(getApplicationContext(),getIntent(),getSupportActionBar());
    }

    private void setupActionBar(final Context context, final Intent intent, final ActionBar actionBar) {
        setActionBarTitle(actionBar,intent);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        setupActionBarRenamingListener(context, actionBar, intent);
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

    private void setActionBarTitle(ActionBar actionBar,Intent intent) {
        if (PROTOTYPE_MODE) {
            String title = "Chat";
            actionBar.setTitle(title);
        } else {
            if (intent != null && intent.hasExtra(TEAM)) {
            Team team = (Team) intent.getSerializableExtra(TEAM);
                String title = team.getName();
                actionBar.setTitle(title);
            }
        }

    }
}