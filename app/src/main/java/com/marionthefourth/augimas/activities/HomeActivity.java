package com.marionthefourth.augimas.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.Chat;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.fragments.ChatListFragment;
import com.marionthefourth.augimas.fragments.NotificationsFragment;
import com.marionthefourth.augimas.fragments.SettingsFragment;
import com.marionthefourth.augimas.fragments.TeamsFragment;

import java.io.Serializable;

public class HomeActivity extends AppCompatActivity implements ChatListFragment.OnChatListFragmentInteractionListener, TeamsFragment.OnTeamsFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            android.app.FragmentManager regularManager = getFragmentManager();

            if (regularManager.findFragmentByTag("Settings") != null) {
                regularManager.beginTransaction().remove(regularManager.findFragmentByTag("Settings")).commit();
            }

            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    manager.beginTransaction().replace(R.id.container, new TeamsFragment()).commit();
                    return true;
                case R.id.navigation_chat:
                    manager.beginTransaction().replace(R.id.container, new ChatListFragment()).commit();
                    return true;
                case R.id.navigation_notifications:
                    manager.beginTransaction().replace(R.id.container, new NotificationsFragment()).commit();
                    return true;
                case R.id.navigation_settings:
                    regularManager.beginTransaction().replace(R.id.container, new SettingsFragment(), "Settings").commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.container, new TeamsFragment()).commit();
    }

    @Override
    public void onChatListFragmentInteraction(final Context context, final Chat chatItem, final Team teamItem) {
        transitionUserToChatActivity(context,chatItem,teamItem);
    }

    private void transitionUserToChatActivity(Context context, Chat chatItem, Team teamItem) {
        Intent chatIntent = new Intent(context,ChatActivity.class);
        chatIntent.putExtra(Constants.Strings.TEAM, (Serializable) teamItem);
        chatIntent.putExtra(Constants.Strings.CHATS, (Parcelable) chatItem);
        context.startActivity(chatIntent);
    }

    @Override
    public void onTeamsFragmentInteraction(Context context, final Team teamItem) {
        // Display Branding Elements for that Team
        Intent brandingElementIntent = new Intent(context,BrandingElementsActivity.class);
        brandingElementIntent.putExtra(Constants.Strings.TEAM, (Serializable) teamItem);
        context.startActivity(brandingElementIntent);
    }

}