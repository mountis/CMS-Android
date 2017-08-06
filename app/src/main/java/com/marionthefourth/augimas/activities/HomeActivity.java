package com.marionthefourth.augimas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.fragments.BrandingElementsFragment;
import com.marionthefourth.augimas.fragments.ChatListFragment;
import com.marionthefourth.augimas.fragments.NotificationsFragment;
import com.marionthefourth.augimas.fragments.SettingsFragment;
import com.marionthefourth.augimas.fragments.TeamManagementFragment;
import com.marionthefourth.augimas.fragments.TeamsFragment;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.classes.objects.communication.Channel.sortChannels;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;
import static com.marionthefourth.augimas.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class HomeActivity extends AppCompatActivity implements ChatListFragment.OnChatListFragmentInteractionListener {
    private int selectedFragment = Constants.Ints.Fragments.DASHBOARD;
//    Activity Methods
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final FragmentManager manager = getSupportFragmentManager();
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        checkNavigationItem(this);
        // get current User

        Backend.getReference(R.string.firebase_users_directory, HomeActivity.this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,currentUser.getTeamUID());
                        // get current Team
                        final Bundle intent = getIntent().getExtras();
                        if (intent != null) {
                            final String navigationSelection = intent.getString(Constants.Strings.Fields.FRAGMENT);
                            switch (navigationSelection) {
                                case Constants.Strings.Fragments.DASHBOARD:
                                case Constants.Strings.Fragments.BRANDING_ELEMENTS:
                                    navigation.setSelectedItemId(R.id.navigation_dashboard);
                                    break;
                                case Constants.Strings.Fragments.CHAT:
                                    navigation.setSelectedItemId(R.id.navigation_chat);
                                    break;
                                case Constants.Strings.Fragments.NOTIFICATIONS:
                                    navigation.setSelectedItemId(R.id.navigation_notifications);
                                    break;
                                case Constants.Strings.Fragments.SETTINGS:
                                    navigation.setSelectedItemId(R.id.navigation_settings);
                                    break;
                                case Constants.Strings.Fragments.TEAM_MANAGEMENT:
                                    navigation.setSelectedItemId(R.id.navigation_settings);
                                    break;
                            }
                        } else {
                            Backend.getReference(R.string.firebase_teams_directory, HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final Team currentTeam = new Team(dataSnapshot);
                                        if (currentTeam != null) {
                                            if (currentTeam.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                                removeNavigationItem();
                                                manager.beginTransaction().replace(R.id.container, new TeamsFragment()).commit();
                                            } else {

                                                manager.beginTransaction().replace(R.id.container, new BrandingElementsFragment().newInstance(currentUser.getTeamUID())).commit();
                                            }
                                        }
                                    } else {
                                        manager.beginTransaction().replace(R.id.container, new BrandingElementsFragment().newInstance(currentUser.getTeamUID())).commit();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
//    Navigation Methods
    private void removeNavigationItem() {
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        Menu menu = navigationView.getMenu();

//        MenuItem target = menu.findItem(R.id.navigation_chat);
        menu.removeItem(R.id.navigation_chat);
//        target.setVisible(false);
    }
    private void determineHomeState(final MenuItem item) {
        // Get Current User
        setupItemNavigation(item);
    }
    private void setupItemNavigation(final MenuItem item) {
        final FragmentManager manager = getSupportFragmentManager();
        final android.app.FragmentManager rManager = getFragmentManager();
        Backend.getReference(R.string.firebase_users_directory, this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);

                    if (selectedFragment == Constants.Ints.Fragments.SETTINGS && item.getItemId() != R.id.navigation_settings) {
                        if (item.getItemId() == R.id.navigation_notifications && Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
                            handleNonSupportFragmentRemoval(rManager);
                        } else if (item.getItemId() == R.id.navigation_chat && Constants.Bools.FeaturesAvailable.DISPLAY_CHATS) {
                            handleNonSupportFragmentRemoval(rManager);
                        } else if (item.getItemId() == R.id.navigation_dashboard && Constants.Bools.FeaturesAvailable.DISPLAY_DASHBOARD) {
                            handleNonSupportFragmentRemoval(rManager);
                        }
                    }

                    switch (item.getItemId()) {
                        case R.id.navigation_dashboard:
                            handleDashboardNavigation(currentUser,manager);
                            break;
                        case R.id.navigation_chat:
                            handleChatNavigation(currentUser,manager);
                            break;
                        case R.id.navigation_notifications:
                            handleNotificationNavigation(manager);
                            break;
                        case R.id.navigation_settings:
                            handleSettingsNavigation(rManager);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void checkNavigationItem(final Activity activity) {
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

                final Menu menu = navigationView.getMenu();

                final User currentUser = new User(dataSnapshot);
                if (currentUser != null) {
                    if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                        menu.removeItem(R.id.navigation_chat);
                        if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                            menu.removeItem(R.id.navigation_dashboard);
                            menu.removeItem(R.id.navigation_notifications);
                        } else {

                        }
                    } else {
                        if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER) || currentUser.getTeamUID().equals("")) {
                            navigationView.setSelectedItemId(R.id.navigation_settings);
                            menu.removeItem(R.id.navigation_chat);
                            menu.removeItem(R.id.navigation_settings);
                            menu.removeItem(R.id.navigation_dashboard);
                            menu.removeItem(R.id.navigation_notifications);

                            final android.app.FragmentManager manager = getFragmentManager();
                            handleSettingsNavigation(manager);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void handleNotificationNavigation(final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
            if (selectedFragment != Constants.Ints.Fragments.NOTIFICATION) {
                selectedFragment = Constants.Ints.Fragments.NOTIFICATION;
                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new NotificationsFragment()).commitAllowingStateLoss();
            }
        } else {
            display(TOAST, R.string.feature_unavailable, findViewById(R.id.content));
        }
    }
    private void handleSettingsNavigation(final android.app.FragmentManager rManager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_SETTINGS) {
            final Bundle intent = getIntent().getExtras();
            if (intent != null) {
                final String nav = intent.getString(Constants.Strings.Fields.FRAGMENT);
                final String teamUID = intent.getString(Constants.Strings.UIDs.TEAM_UID);
                if (nav != null && teamUID != null) {
                    switch (nav) {
                        case Constants.Strings.Fragments.TEAM_MANAGEMENT:
                            Backend.getReference(R.string.firebase_teams_directory, this).child(teamUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.container, TeamManagementFragment.newInstance(new Team(dataSnapshot))).commit();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;
                    }
                } else {
                    rManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(
                            R.id.container,
                            new SettingsFragment(),
                            Constants.Strings.Fragments.SETTINGS).commit();
                }

            } else {
                if (selectedFragment != Constants.Ints.Fragments.SETTINGS) {
                    rManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(
                            R.id.container,
                            new SettingsFragment(),
                            Constants.Strings.Fragments.SETTINGS).commit();
                }
            }
            selectedFragment = Constants.Ints.Fragments.SETTINGS;

        } else {
            display(TOAST, R.string.feature_unavailable, findViewById(R.id.content));
        }
    }
    private void handleChatNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_CHATS) {
            if (getIntent().getExtras() != null) {
//                intent.putExtra(Constants.Strings.UIDs.TEAM_UID,holder.notificationItem.getObject().getUID());

            } else {
                if (selectedFragment != Constants.Ints.Fragments.CHAT) {
                    selectedFragment = Constants.Ints.Fragments.CHAT;
                    if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                        if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                            manager.beginTransaction().replace(R.id.container, new ChatListFragment()).commit();
                        } else {
                            // Get Channel UIDs & Team UIDs
                            final ArrayList<String> teamUIDs = new ArrayList<>();
                            final ArrayList<Channel> channels = new ArrayList<>();

                            teamUIDs.add(currentUser.getTeamUID());

                            Backend.getReference(R.string.firebase_chats_directory, this).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                        for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                                            final Chat chatItem = new Chat(chatReference);
                                            if (chatItem != null) {
                                                if (currentUser.isInChat(chatItem)) {
                                                    // Get Other Team Admin Team UID
                                                    Backend.getReference(R.string.firebase_teams_directory, HomeActivity.this).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                                                    final Team teamItem = new Team(teamReference);
                                                                    if (teamItem != null && !currentUser.isInTeam(teamItem) && chatItem.hasTeam(teamItem.getUID())) {
                                                                        teamUIDs.add(teamItem.getUID());
                                                                        // Get Channel UIDs
                                                                        Backend.getReference(R.string.firebase_channels_directory, HomeActivity.this).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                                    for (DataSnapshot channelReference:dataSnapshot.getChildren()) {
                                                                                        final Channel channelItem = new Channel(channelReference);
                                                                                        if (channelItem != null && channelItem.getChatUID().equals(chatItem.getUID())) {
                                                                                            channels.add(channelItem);
                                                                                        }
                                                                                    }

                                                                                    if (channels.size() != 0) {
                                                                                        Intent chatIntent = new Intent(HomeActivity.this,ChatActivity.class);
                                                                                        for (int i = 0; i < channels.size();i++) {
                                                                                            if (channels.get(i).getName().equals(teamItem.getName())) {
                                                                                            } else if(channels.get(i).getName().equals("")) {
                                                                                                chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false),channels.get(i).getUID());
                                                                                            } else {
                                                                                                chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false),channels.get(i).getUID());
                                                                                            }
                                                                                        }

                                                                                        chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false),teamUIDs.get(0));
                                                                                        chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false),teamUIDs.get(1));

                                                                                        startActivity(chatIntent);

                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {}
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });

                        }
                    }
                }
            }

        } else {
            display(TOAST, R.string.feature_unavailable, findViewById(R.id.content));
        }
    }
    private void handleDashboardNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_DASHBOARD) {
            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                final Bundle intent = getIntent().getExtras();
                if (intent != null) {
                    final String nav = intent.getString(Constants.Strings.Fields.FRAGMENT);
                    final String teamUID = intent.getString(Constants.Strings.UIDs.TEAM_UID);
                    if (nav != null && teamUID != null) {
                        switch (nav) {
                            case Constants.Strings.Fragments.BRANDING_ELEMENTS:
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new BrandingElementsFragment().newInstance(teamUID)).commit();
                                break;
                            case Constants.Strings.Fragments.DASHBOARD:
                                if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new TeamsFragment()).commit();
                                } else {
                                    if (!currentUser.getTeamUID().equals("")) {
                                        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new BrandingElementsFragment().newInstance(currentUser.getTeamUID())).commit();
                                    }
                                }
                        }
                    } else {
                        if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new TeamsFragment()).commit();
                        } else {
                            if (!currentUser.getTeamUID().equals("")) {
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new BrandingElementsFragment().newInstance(currentUser.getTeamUID())).commit();
                            }
                        }
                    }
                } else {
                    if (selectedFragment != Constants.Ints.Fragments.DASHBOARD) {
                        if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new TeamsFragment()).commit();
                        } else {
                            if (!currentUser.getTeamUID().equals("")) {
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new BrandingElementsFragment().newInstance(currentUser.getTeamUID())).commit();
                            }
                        }
                    }

                }
                selectedFragment = Constants.Ints.Fragments.DASHBOARD;

            }

        } else {
            display(TOAST, R.string.feature_unavailable, findViewById(R.id.content));

        }
    }
//    Listener Methods
    @Override
    public void onChatListFragmentInteraction(final Context context, final Chat chatItem, final Team teamItem) {
        Backend.getReference(R.string.firebase_channels_directory, HomeActivity.this).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    final ArrayList<Channel> channels = new ArrayList<>();
                    for (DataSnapshot channelReference:dataSnapshot.getChildren()) {
                        final Channel channelItem = new Channel(channelReference);
                        if (channelItem.getChatUID().equals(chatItem.getUID())) {
                            if (!channelItem.getName().equals(teamItem.getName())) {
                                channels.add(channelItem);
                            }
                        }
                    }

                    Backend.getReference(R.string.firebase_users_directory, HomeActivity.this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final User currentUser = new User(dataSnapshot);
                                if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                                    Backend.getReference(R.string.firebase_teams_directory, HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                final Team currentTeam = new Team(dataSnapshot);
                                                if (currentTeam != null) {
                                                    ArrayList<String> channelUIDs = sortChannels(channels);

                                                    final Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);

                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false),currentTeam.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false),teamItem.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false),channelUIDs.get(0));
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false),channelUIDs.get(1));
                                                    startActivity(chatIntent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            determineHomeState(item);
            return true;
        }

    };
}