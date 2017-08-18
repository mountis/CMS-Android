package com.marionthefourth.augimas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.fragments.BrandingElementsFragment;
import com.marionthefourth.augimas.fragments.ChatListFragment;
import com.marionthefourth.augimas.fragments.RecentActivitiesFragment;
import com.marionthefourth.augimas.fragments.SettingsFragment;
import com.marionthefourth.augimas.fragments.TeamManagementFragment;
import com.marionthefourth.augimas.fragments.TeamsFragment;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.classes.constants.Constants.Strings.Fragments.BRANDING_ELEMENTS;
import static com.marionthefourth.augimas.classes.objects.communication.Channel.sortChannels;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;
import static com.marionthefourth.augimas.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class HomeActivity extends AppCompatActivity implements ChatListFragment.OnChatListFragmentInteractionListener {
    public boolean shouldHandleNavigation = true;
    private int selectedTopFragment = Constants.Ints.Fragments.DASHBOARD;
//    Activity Methods
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final FragmentManager manager = getSupportFragmentManager();
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final String recentActivityUID = bundle.getString(Constants.Strings.UIDs.RECENT_ACTIVITY_UID);
            if (recentActivityUID != null && !recentActivityUID.equals("")) {
                Backend.getReference(R.string.firebase_recent_activities_directory,this).child(recentActivityUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot recentActivitySnapshot) {
                        if (recentActivitySnapshot.exists()) {
                            new RecentActivity(recentActivitySnapshot).navigate(HomeActivity.this);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            } else {
                setupHomeActivity(manager,navigation);
            }
        } else {
            setupHomeActivity(manager, navigation);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                final Intent intent = new Intent(this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(new Intent(this,HomeActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupActionBar(final ActionBar actionBar, final String title) {
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
    private void setupHomeActivity(final FragmentManager manager, final BottomNavigationView navigation) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            checkNavigationItem(getCurrentUser(),this);
            Backend.getReference(R.string.firebase_users_directory, this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User currentUser = new User(dataSnapshot);
                        if (!currentUser.getTeamUID().equals("")) {
                            Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,currentUser.getTeamUID());
                            // get current Team
                            final Bundle intent = getIntent().getExtras();
                            if (intent != null) {
                                final String navigationSelection = intent.getString(Constants.Strings.Fields.FRAGMENT);
                                if (navigationSelection != null) {
                                    switch (navigationSelection) {
                                        case Constants.Strings.Fragments.DASHBOARD:
                                        case BRANDING_ELEMENTS:
                                            navigation.setSelectedItemId(R.id.navigation_dashboard);
                                            break;
                                        case Constants.Strings.Fragments.CHAT:
                                            navigation.setSelectedItemId(R.id.navigation_chat);
                                            break;
                                        case Constants.Strings.Fragments.RECENT_ACTIVITIES:
                                            navigation.setSelectedItemId(R.id.navigation_recent_activities);
                                            break;
                                        case Constants.Strings.Fragments.SETTINGS:
                                            navigation.setSelectedItemId(R.id.navigation_settings);
                                            break;
                                        case Constants.Strings.Fragments.TEAM_MANAGEMENT:
                                            navigation.setSelectedItemId(R.id.navigation_settings);
                                            break;
                                    }
                                }
                            } else {
                                Backend.getReference(R.string.firebase_teams_directory, HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Team currentTeam = new Team(dataSnapshot);
                                        if (currentTeam.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).replace(R.id.container,new TeamsFragment(),Constants.Strings.Fragments.TEAMS).commit();
                                        } else {
                                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER) && currentTeam.getStatus() == FirebaseEntity.EntityStatus.APPROVED) {
                                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).replace(R.id.container,BrandingElementsFragment.newInstance(currentUser.getTeamUID()),Constants.Strings.Fragments.SIGN_IN).addToBackStack(Constants.Strings.Fragments.DASHBOARD).commit();
                                            } else {
                                                final android.app.FragmentManager rManager = getFragmentManager();
                                                rManager.beginTransaction().setTransition(
                                                        android.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).replace(R.id.container,
                                                        new SettingsFragment(),
                                                        Constants.Strings.Fragments.SETTINGS).commit();
                                                selectedTopFragment = Constants.Ints.Fragments.SETTINGS;
                                                navigation.setSelectedItemId(R.id.navigation_settings);
                                            }
                                        }
                                        setupActionBar(getSupportActionBar(),currentTeam.getName());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                        } else {
                            final android.app.FragmentManager rManager = getFragmentManager();
                            rManager.beginTransaction().setTransition(
                                    android.app.FragmentTransaction.TRANSIT_NONE).replace(R.id.container,
                                    new SettingsFragment(),
                                    Constants.Strings.Fragments.SETTINGS).commit();
                            selectedTopFragment = Constants.Ints.Fragments.SETTINGS;
                            navigation.setSelectedItemId(R.id.navigation_settings);
                        }
                    } else {
                        startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            startActivity(new Intent(this,SignInActivity.class));
        }
    }
//    Navigation Methods
    private void setupItemNavigation(final MenuItem item) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User currentUser = new User(dataSnapshot);
                        Backend.getReference(R.string.firebase_teams_directory,HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    final FragmentManager manager = getSupportFragmentManager();
                                    final android.app.FragmentManager rManager = getFragmentManager();
                                    if (currentTeam.getStatus() != FirebaseEntity.EntityStatus.BLOCKED) {

                                        switch (item.getItemId()) {
                                            case R.id.navigation_dashboard:
                                                handleNonSupportFragmentRemoval(rManager);
                                                if (currentTeam.getStatus() == FirebaseEntity.EntityStatus.APPROVED) {
                                                    handleDashboardNavigation(currentUser,manager);
                                                }
                                                break;
                                            case R.id.navigation_chat:
                                                handleNonSupportFragmentRemoval(rManager);
                                                handleChatNavigation(currentUser, manager);
                                                break;
                                            case R.id.navigation_recent_activities:
                                                handleNonSupportFragmentRemoval(rManager);
                                                handleRecentActivitiesNavigation(currentUser,manager);
                                                break;
                                            case R.id.navigation_settings:
                                                handleSettingsNavigation(rManager);
                                                break;
                                        }
                                    } else {
                                        FragmentHelper.display(SNACKBAR,R.string.you_dont_have_access,item.getActionView());
                                        startActivity(new Intent(HomeActivity.this,SignInActivity.class));
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
        } else {
            startActivity(new Intent(HomeActivity.this,SignInActivity.class));
        }
    }
    private void checkNavigationItem(final User currentUser, final Activity activity) {
        Backend.getReference(R.string.firebase_users_directory, activity).child(currentUser.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                final BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

                final Menu menu = navigationView.getMenu();
                final User currentUser = new User(userSnapshot);

                if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                    menu.removeItem(R.id.navigation_chat);
                    if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                        menu.removeItem(R.id.navigation_dashboard);
                        menu.removeItem(R.id.navigation_recent_activities);
                    }
                } else {
                    if (!currentUser.getTeamUID().equals("")) {
                        Backend.getReference(R.string.firebase_teams_directory,HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot teamSnapshot) {
                                if (teamSnapshot.exists()) {
                                    final Team currentTeam = new Team(teamSnapshot);
                                    if (currentTeam.getStatus() != FirebaseEntity.EntityStatus.APPROVED) {
                                        menu.removeItem(R.id.navigation_dashboard);
                                    }

                                    if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                                        navigationView.setSelectedItemId(R.id.navigation_settings);
                                        menu.removeItem(R.id.navigation_chat);
                                        menu.removeItem(R.id.navigation_recent_activities);

                                        final android.app.FragmentManager manager = getFragmentManager();
                                        handleSettingsNavigation(manager);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else {
                        navigationView.setSelectedItemId(R.id.navigation_settings);
                        menu.removeItem(R.id.navigation_chat);
                        menu.removeItem(R.id.navigation_dashboard);
                        menu.removeItem(R.id.navigation_recent_activities);

                        final android.app.FragmentManager manager = getFragmentManager();
                        handleSettingsNavigation(manager);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void handleRecentActivitiesNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                if (shouldHandleNavigation) {
                    if (selectedTopFragment != Constants.Ints.Fragments.NOTIFICATION) {
                        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new RecentActivitiesFragment(),Constants.Strings.Fragments.RECENT_ACTIVITIES).commitAllowingStateLoss();
                    } else {
                        RecentActivitiesFragment recentActivitiesFragment = (RecentActivitiesFragment) getSupportFragmentManager().findFragmentByTag(Constants.Strings.Fragments.RECENT_ACTIVITIES);
                        if (recentActivitiesFragment == null || !recentActivitiesFragment.isVisible()) {
                            getSupportFragmentManager().beginTransaction().setTransition(
                                    android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                                    new RecentActivitiesFragment(),
                                    Constants.Strings.Fragments.RECENT_ACTIVITIES).commit();
                        }
                    }
                }
                selectedTopFragment = Constants.Ints.Fragments.NOTIFICATION;
            } else {
                FragmentHelper.display(TOAST,R.string.you_dont_have_access,findViewById(R.id.container));
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
                                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,TeamManagementFragment.newInstance(new Team(dataSnapshot)), Constants.Strings.Fragments.TEAM_MANAGEMENT).commit();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            break;
                    }
                } else {
                    rManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                            new SettingsFragment(),
                            Constants.Strings.Fragments.SETTINGS).commit();
                }
            } else {
                if (shouldHandleNavigation) {
                    if (selectedTopFragment != Constants.Ints.Fragments.SETTINGS) {
                        rManager.beginTransaction().setTransition(
                                android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                                new SettingsFragment(),
                                Constants.Strings.Fragments.SETTINGS).commit();
                    } else {
                        SettingsFragment settingsFragment = (SettingsFragment) rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS);
                        if (settingsFragment == null || !settingsFragment.isVisible()) {
                            rManager.beginTransaction().setTransition(
                                    android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                                    new SettingsFragment(),
                                    Constants.Strings.Fragments.SETTINGS).commit();
                        }
                    }
                }

                selectedTopFragment = Constants.Ints.Fragments.SETTINGS;
            }
        } else {
            display(TOAST, R.string.feature_unavailable, findViewById(R.id.content));
        }
    }
    private void handleChatNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_CHATS) {
            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                if (shouldHandleNavigation) {
                    if (selectedTopFragment != Constants.Ints.Fragments.CHAT) {
                        if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                            if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new ChatListFragment(),Constants.Strings.Fragments.CHAT_LIST).commit();
                            } else {
                                FragmentHelper.transitionClientUserToChatFragment(currentUser,this,"");
                            }
                        }
                    }
                }

                selectedTopFragment = Constants.Ints.Fragments.CHAT;
            } else {
                FragmentHelper.display(TOAST,R.string.you_dont_have_access,findViewById(R.id.container));
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
                            case BRANDING_ELEMENTS:
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementsFragment.newInstance(teamUID), BRANDING_ELEMENTS).commit();
                                break;
                            case Constants.Strings.Fragments.DASHBOARD:
                                if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new TeamsFragment(),Constants.Strings.Fragments.TEAMS).commit();
                                } else {
                                    if (!currentUser.getTeamUID().equals("")) {
                                        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementsFragment.newInstance(currentUser.getTeamUID()), BRANDING_ELEMENTS).commit();
                                    }
                                }
                        }
                    } else {
                        if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new TeamsFragment(),Constants.Strings.Fragments.TEAMS).commit();
                        } else {
                            if (!currentUser.getTeamUID().equals("")) {
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementsFragment.newInstance(currentUser.getTeamUID()), BRANDING_ELEMENTS).commit();
                            }
                        }
                    }
                } else {
                    if (shouldHandleNavigation) {
                        if (selectedTopFragment != Constants.Ints.Fragments.DASHBOARD) {
                            if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new TeamsFragment(),Constants.Strings.Fragments.TEAMS).commit();
                            } else {
                                if (!currentUser.getTeamUID().equals("")) {
                                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementsFragment.newInstance(currentUser.getTeamUID()), BRANDING_ELEMENTS).commit();
                                }
                            }
                        } else {
                            if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                TeamsFragment teamsFragment = (TeamsFragment) getSupportFragmentManager().findFragmentByTag(Constants.Strings.Fragments.TEAMS);
                                if (teamsFragment == null || !teamsFragment.isVisible()) {
                                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new TeamsFragment(),Constants.Strings.Fragments.TEAMS).commit();
                                }
                            } else {
                                BrandingElementsFragment brandingElementsFragment = (BrandingElementsFragment) getSupportFragmentManager().findFragmentByTag(BRANDING_ELEMENTS);
                                if (brandingElementsFragment == null || !brandingElementsFragment.isVisible()) {
                                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementsFragment.newInstance(currentUser.getTeamUID()), BRANDING_ELEMENTS).commit();
                                }
                            }
                        }
                    }
                }
                selectedTopFragment = Constants.Ints.Fragments.DASHBOARD;
            } else {
                FragmentHelper.display(TOAST,R.string.you_dont_have_access,findViewById(R.id.container));
            }
        } else {
            display(TOAST, R.string.feature_unavailable, findViewById(R.id.content));
        }
    }
//    Listener Methods
    @Override
    public void onChatListFragmentInteraction(final Team teamItem, final Chat chatItem, final Context context) {
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

                    if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                        Backend.getReference(R.string.firebase_users_directory, HomeActivity.this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final User currentUser = new User(dataSnapshot);
                                    if (!currentUser.getTeamUID().equals("")) {
                                        Backend.getReference(R.string.firebase_teams_directory, HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot teamSnapshot) {
                                                if (teamSnapshot.exists() && teamSnapshot.hasChildren()) {
                                                    final Team currentTeam = new Team(teamSnapshot);
                                                    ArrayList<String> channelUIDs = sortChannels(channels);

                                                    final Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);

                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false),currentTeam.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false),teamItem.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false),channelUIDs.get(0));
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false),channelUIDs.get(1));
                                                    startActivity(chatIntent);
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            setupItemNavigation(item);
            return true;
        }

    };
}