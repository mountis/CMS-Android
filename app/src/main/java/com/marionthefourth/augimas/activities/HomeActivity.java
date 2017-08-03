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
import com.marionthefourth.augimas.fragments.TeamsFragment;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;
import static com.marionthefourth.augimas.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class HomeActivity extends AppCompatActivity implements ChatListFragment.OnChatListFragmentInteractionListener, TeamsFragment.OnTeamsFragmentInteractionListener {

    private final int DASHBOARD     = 0x19151AB;
    private final int CHAT          = 0x8816231;
    private final int NOTIFICATION  = 0x4718591;
    private final int SETTINGS      = 0x9102612;
    private int selectedFragment = DASHBOARD;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final FragmentManager manager = getSupportFragmentManager();
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        checkNavigationItem(this);
        // get current User
        FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        // get current Team
                        FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        if (currentTeam.getType().equals(FirebaseEntity.EntityType.US)) {
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
    private void determineHomeState(final MenuItem item) {
        // Get Current User
        setupItemNavigation(item);
    }
    private void setupItemNavigation(final MenuItem item) {
        final FragmentManager manager = getSupportFragmentManager();
        final android.app.FragmentManager rManager = getFragmentManager();
        FirebaseHelper.getReference(this,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);

                    if (selectedFragment == SETTINGS && item.getItemId() != R.id.navigation_settings) {
                        handleNonSupportFragmentRemoval(rManager);
                    }

                    switch (item.getItemId()) {
                        case R.id.navigation_dashboard:
                            if (selectedFragment != DASHBOARD) {
                                handleDashboardNavigation(currentUser,manager);
                                selectedFragment = DASHBOARD;
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            }
                            break;
                        case R.id.navigation_chat:
                            if (selectedFragment != CHAT) {
                                handleChatNavigation(currentUser,manager);
                                selectedFragment = CHAT;
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            }
                            break;
                        case R.id.navigation_notifications:
                            if (selectedFragment != NOTIFICATION) {
                                handleNotificationNavigation(manager);
                                selectedFragment = NOTIFICATION;
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            }
                            break;
                        case R.id.navigation_settings:
                            if (selectedFragment != SETTINGS) {
                                handleSettingsNavigation(rManager);
                                selectedFragment = SETTINGS;
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void handleNotificationNavigation(final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new NotificationsFragment()).commitAllowingStateLoss();
        } else {
            display(findViewById(R.id.content),TOAST,R.string.feature_unavailable);
        }
    }
    private void handleChatNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_CHATS) {
            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                if (currentUser.getType().equals(FirebaseEntity.EntityType.US)) {
                    manager.beginTransaction().replace(R.id.container, new ChatListFragment()).commit();
                } else {
                    // Get Channel UIDs & Team UIDs
                    final ArrayList<String> teamUIDs = new ArrayList<>();
                    final ArrayList<Channel> channels = new ArrayList<>();

                    teamUIDs.add(currentUser.getTeamUID());

                    FirebaseHelper.getReference(this,R.string.firebase_chats_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                                    final Chat chatItem = new Chat(chatReference);
                                    if (chatItem != null) {
                                        if (currentUser.isInChat(chatItem)) {
                                            // Get Other Team Admin Team UID
                                            FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                        for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                                            final Team teamItem = new Team(teamReference);
                                                            if (teamItem != null && !currentUser.isInTeam(teamItem) && chatItem.hasTeam(teamItem.getUID())) {
                                                                teamUIDs.add(teamItem.getUID());
                                                                // Get Channel UIDs
                                                                FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_channels_directory).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                                                        chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.THEM.toInt(false),channels.get(i).getUID());
                                                                                    } else {
                                                                                        chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.US.toInt(false),channels.get(i).getUID());
                                                                                    }
                                                                                }

                                                                                chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.US.toInt(false),teamUIDs.get(0));
                                                                                chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.THEM.toInt(false),teamUIDs.get(1));

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
        } else {
            display(findViewById(R.id.content),TOAST,R.string.feature_unavailable);
        }
    }
    private void handleDashboardNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_DASHBOARD) {
            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                if (currentUser.getType().equals(FirebaseEntity.EntityType.US)) {
                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new TeamsFragment()).commit();
                } else {
                    if (!currentUser.getTeamUID().equals("")) {
                        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, new BrandingElementsFragment().newInstance(currentUser.getTeamUID())).commit();
                    }
                }
            } else {

            }
        } else {
            display(findViewById(R.id.content),TOAST,R.string.feature_unavailable);
        }
    }
    private void handleSettingsNavigation(final android.app.FragmentManager rManager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_SETTINGS) {
            rManager.beginTransaction().setTransition(
                    android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(
                            R.id.container,
                    new SettingsFragment(),
                    Constants.Strings.Fragments.SETTINGS).commit();
        } else {
            display(findViewById(R.id.content),TOAST,R.string.feature_unavailable);
        }
    }
    private void removeNavigationItem() {
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        Menu menu = navigationView.getMenu();

//        MenuItem target = menu.findItem(R.id.navigation_chat);
        menu.removeItem(R.id.navigation_chat);
//        target.setVisible(false);
    }
    private void checkNavigationItem(final Activity activity) {
        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

                final Menu menu = navigationView.getMenu();

                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser.getType().equals(FirebaseEntity.EntityType.US)) {
                        menu.removeItem(R.id.navigation_chat);
                        if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                            menu.removeItem(R.id.navigation_dashboard);
                            menu.removeItem(R.id.navigation_notifications);
                        } else {

                        }
                    } else {
                        if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER) || currentUser.getTeamUID().equals("")) {
                            navigationView.setSelectedItemId(R.id.navigation_settings);
                            selectedFragment = SETTINGS;
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
    @Override
    public void onChatListFragmentInteraction(final Context context, final Chat chatItem, final Team teamItem) {
        transitionUserToChatFragment(context,chatItem,teamItem);
    }
    private void transitionUserToChatFragment(final Context context, final Chat chatItem, final Team teamItem) {
        FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_channels_directory).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final User currentUser = new User(dataSnapshot);
                                if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                                    FirebaseHelper.getReference(HomeActivity.this,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                final Team currentTeam = new Team(dataSnapshot);
                                                if (currentTeam != null) {
                                                    ArrayList<String> channelUIDs = sortChannels(channels);

                                                    final Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);

                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.US.toInt(false),currentTeam.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.THEM.toInt(false),teamItem.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.US.toInt(false),channelUIDs.get(0));
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.THEM.toInt(false),channelUIDs.get(1));
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
    private ArrayList<String> sortChannels(final ArrayList<Channel> channels) {
        final ArrayList<Channel> sortedChannels = new ArrayList<>(channels.size());
        final ArrayList<String> channelUIDs = new ArrayList<>(channels.size());
        if (channels.size() == 2) {
            if (channels.get(0).getName().equals("")) {
                sortedChannels.add(channels.get(1));
                sortedChannels.add(channels.get(0));
            } else {
                sortedChannels.add(channels.get(0));
                sortedChannels.add(channels.get(1));
            }

            for (int i = 0; i < sortedChannels.size(); i++) {
                channelUIDs.add(sortedChannels.get(i).getUID());
            }
        }

        return channelUIDs;
    }
    @Override
    public void onTeamsFragmentInteraction(final Context context, final Team teamItem) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, BrandingElementsFragment.newInstance(teamItem.getUID())).commit();
    }

}