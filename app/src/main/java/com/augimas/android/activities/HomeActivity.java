package com.augimas.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.communication.Channel;
import com.augimas.android.classes.objects.communication.Chat;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.fragments.AccountFragment;
import com.augimas.android.fragments.BrandingElementFragment;
import com.augimas.android.fragments.BrandingElementsFragment;
import com.augimas.android.fragments.ChatListFragment;
import com.augimas.android.fragments.RecentActivitiesFragment;
import com.augimas.android.fragments.SettingsFragment;
import com.augimas.android.fragments.TeamManagementFragment;
import com.augimas.android.fragments.TeamsFragment;
import com.augimas.android.helpers.DeviceHelper;
import com.augimas.android.helpers.FragmentHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.augimas.android.classes.constants.Constants.Strings.Fragments.BRANDING_ELEMENTS;
import static com.augimas.android.classes.objects.communication.Channel.sortChannels;
import static com.augimas.android.helpers.DeviceHelper.encodeToBase64;
import static com.augimas.android.helpers.FragmentHelper.display;
import static com.augimas.android.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class HomeActivity extends AppCompatActivity implements ChatListFragment.OnChatListFragmentInteractionListener {
    public boolean shouldHandleNavigation = true;
    public BottomNavigationView navigation = null;
    private int selectedTopFragment = Constants.Ints.Fragments.DASHBOARD;
//    Activity Methods

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final FragmentManager manager = getSupportFragmentManager();
        navigation = findViewById(R.id.navigation);
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
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                    Backend.getReference(R.string.firebase_users_directory,this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final User currentUser = new User(dataSnapshot);
                                checkNavigationItem(currentUser,HomeActivity.this);
                                if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                    if (navigation.getMenu().getItem(0).isChecked()) {
                                        checkDashboard(supportFragmentManager);
                                    } else if (navigation.getMenu().getItem(1).isChecked()){
                                        checkRecentActivities(supportFragmentManager);
                                    } else {
                                        checkSettings(supportFragmentManager);
                                    }
                                } else {
                                    if (navigation.getMenu().getItem(0).isChecked()) {
                                        checkDashboard(supportFragmentManager);
                                    } else if (navigation.getMenu().getItem(1).isChecked()){
                                        selectedTopFragment = Constants.Ints.Fragments.CHAT;
                                    } else if (navigation.getMenu().getItem(2).isChecked()){
                                        checkRecentActivities(supportFragmentManager);
                                    } else {
                                        checkSettings(supportFragmentManager);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkSettings(FragmentManager supportFragmentManager) {
        selectedTopFragment = Constants.Ints.Fragments.SETTINGS;
        final android.app.FragmentManager fragmentManager = getFragmentManager();
        SettingsFragment settingsFragment = (SettingsFragment) fragmentManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS);
        if (settingsFragment != null && settingsFragment.isVisible()) {
            fragmentManager.beginTransaction().setTransition(
                    android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                    new SettingsFragment(),
                    Constants.Strings.Fragments.SETTINGS).commit();
        } else {
            final TeamManagementFragment teamManagementFragment = (TeamManagementFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.TEAM_MANAGEMENT);
            if (teamManagementFragment != null && teamManagementFragment.isVisible()) {
                if (teamManagementFragment.getArguments() != null) {
                    final Team teamItem = (Team) teamManagementFragment.getArguments().getSerializable(Constants.Strings.TEAM);
                    if (teamItem != null) {
                        supportFragmentManager.beginTransaction().setTransition(
                                android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                                TeamManagementFragment.newInstance(teamItem),
                                Constants.Strings.Fragments.TEAM_MANAGEMENT).commit();
                    }
                } else {
                    supportFragmentManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                            new TeamManagementFragment(),
                            Constants.Strings.Fragments.TEAM_MANAGEMENT).commit();
                }
            } else {
                final AccountFragment accountFragment = (AccountFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.ACCOUNT);
                if (accountFragment != null && accountFragment.isVisible()) {
                    supportFragmentManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                            AccountFragment.newInstance(accountFragment.getArguments().getString(Constants.Strings.UIDs.USER_UID)),
                            Constants.Strings.Fragments.TEAM_MANAGEMENT).commit();
                }
            }
        }
    }

    private void checkRecentActivities(FragmentManager supportFragmentManager) {
        selectedTopFragment = Constants.Ints.Fragments.RECENT_ACTIVITIES;
        final RecentActivitiesFragment recentActivitiesFragment = (RecentActivitiesFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.RECENT_ACTIVITIES);
        if (recentActivitiesFragment != null && recentActivitiesFragment.isVisible()) {
            supportFragmentManager.beginTransaction().setTransition(
                    android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                    new RecentActivitiesFragment(),
                    Constants.Strings.Fragments.RECENT_ACTIVITIES).commit();
        }
    }

    private void checkDashboard(FragmentManager supportFragmentManager) {
        selectedTopFragment = Constants.Ints.Fragments.DASHBOARD;
        final TeamsFragment teamsFragment = (TeamsFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.TEAMS);
        if (teamsFragment != null && teamsFragment.isVisible()) {
            supportFragmentManager.beginTransaction().setTransition(
                    android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                    new TeamsFragment(),
                    Constants.Strings.Fragments.TEAMS).commit();
        } else {
            final BrandingElementsFragment brandingElementsFragment = (BrandingElementsFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.BRANDING_ELEMENTS);
            if (brandingElementsFragment != null && brandingElementsFragment.isVisible()) {
                String teamUID = brandingElementsFragment.getArguments().getString(Constants.Strings.UIDs.TEAM_UID);

                if (teamUID != null && !teamUID.equals("")) {
                    supportFragmentManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                            BrandingElementsFragment.newInstance(teamUID),
                            Constants.Strings.Fragments.BRANDING_ELEMENTS).commit();
                } else {
                    supportFragmentManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                            new BrandingElementsFragment(),
                            Constants.Strings.Fragments.BRANDING_ELEMENTS).commit();
                }

            } else {
                final BrandingElementFragment brandingElementFragment = (BrandingElementFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.BRANDING_ELEMENT);
                if (brandingElementFragment != null && brandingElementFragment.isVisible()) {
                    supportFragmentManager.beginTransaction().setTransition(
                            android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                            BrandingElementFragment.newInstance(brandingElementFragment.getArguments()),
                            Constants.Strings.Fragments.BRANDING_ELEMENT).commit();
                } else {
                    final TeamManagementFragment teamManagementFragment = (TeamManagementFragment) supportFragmentManager.findFragmentByTag(Constants.Strings.Fragments.TEAM_MANAGEMENT);
                    if (teamManagementFragment != null && teamManagementFragment.isVisible()) {
                        supportFragmentManager.beginTransaction().setTransition(
                                android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                                TeamManagementFragment.newInstance((Team) teamManagementFragment.getArguments().getSerializable(Constants.Strings.TEAM)),
                                Constants.Strings.Fragments.TEAM_MANAGEMENT).commit();
                    }
                }
            }
        }
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
                        Backend.setConnectionListener(HomeActivity.this);
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
    private void checkNavigationItem(final User currentUser, final Activity activity) {
    Backend.getReference(R.string.firebase_users_directory, activity).child(currentUser.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot userSnapshot) {
            final BottomNavigationView navigationView = findViewById(R.id.navigation);

            final Menu menu = navigationView.getMenu();
            final User currentUser = new User(userSnapshot);

            if (currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
                menu.removeItem(R.id.navigation_chat);
                if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                    menu.getItem(0).setEnabled(false);
                    menu.getItem(1).setEnabled(false);
                } else {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(1).setEnabled(true);
                }
            } else {
                if (!currentUser.getTeamUID().equals("")) {
                    Backend.getReference(R.string.firebase_teams_directory,HomeActivity.this).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot teamSnapshot) {
                            if (teamSnapshot.exists()) {
                                final Team currentTeam = new Team(teamSnapshot);
                                if (currentTeam.getStatus() != FirebaseEntity.EntityStatus.APPROVED) {
                                    menu.getItem(0).setEnabled(false);
                                } else {
                                    menu.getItem(0).setEnabled(true);
                                }

                                if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                                    navigationView.setSelectedItemId(R.id.navigation_settings);
                                    menu.getItem(1).setEnabled(false);
                                    menu.getItem(2).setEnabled(false);
                                    final android.app.FragmentManager manager = getFragmentManager();
                                    handleSettingsNavigation(manager);
                                } else {
                                    menu.getItem(1).setEnabled(true);
                                    menu.getItem(2).setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                } else {
                    navigationView.setSelectedItemId(R.id.navigation_settings);
                    // Chat is Item 1
                    menu.getItem(1).setEnabled(false);
                    // Dashboard is Item 0
                    menu.getItem(0).setEnabled(false);
                    // Recent Activities is Item 2
                    menu.getItem(2).setEnabled(false);
                    final android.app.FragmentManager manager = getFragmentManager();
                    handleSettingsNavigation(manager);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    });
}
    private void handleItemNavigation(final MenuItem item) {
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
                                        if (shouldHandleNavigation) {
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
    private void handleRecentActivitiesNavigation(final User currentUser, final FragmentManager manager) {
        if (Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                if (shouldHandleNavigation) {
                    if (selectedTopFragment != Constants.Ints.Fragments.RECENT_ACTIVITIES) {
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
                selectedTopFragment = Constants.Ints.Fragments.RECENT_ACTIVITIES;
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
            handleItemNavigation(item);
            return true;
        }
    };

    @Override
    protected void onActivityResult(int reqCode, int resultCode, final Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == Constants.Ints.Results.RESULT_LOAD_IMG) {
            if (resultCode == RESULT_OK) {
                try {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        final JSONObject object = DeviceHelper.readFromJSONFile(HomeActivity.this);
                        final String myBase64Image = encodeToBase64(selectedImage, Bitmap.CompressFormat.JPEG, 25);
                        final String brandingElementUID = object.getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
                        final int itemPosition = object.getInt(Constants.Strings.Fields.SELECTED_INDEX);
                        if (brandingElementUID != null && !brandingElementUID.equals("") && itemPosition != -1) {
                            Backend.getReference(R.string.firebase_branding_elements_directory,this).child(brandingElementUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final BrandingElement element = new BrandingElement(dataSnapshot);
                                        if (element.getData().size() > itemPosition) {
                                            if (!element.getData().get(itemPosition).equals(myBase64Image)) {
                                                element.getData().set(itemPosition,myBase64Image);
                                                sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.UPDATE);
                                            }
                                        } else {
                                            element.getData().add(myBase64Image);
                                            sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.ADD);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void sendBrandingElementNotification(final BrandingElement brandingName, final RecentActivity.ActivityVerbType verbType) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_teams_directory,this).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayMap<FirebaseEntity.EntityType,Team> teamMap = Team.toClientAndHostTeamMap(dataSnapshot,brandingName.getTeamUID());
                    Backend.getReference(R.string.firebase_users_directory,HomeActivity.this).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User currentUser = new User(dataSnapshot);

                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                                final RecentActivity hostRecentActivity;
                                final RecentActivity clientRecentActivity;

                                switch (verbType) {
                                    case UPDATE:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType);
                                        }
                                        break;
                                    default:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType);
                                        }
                                        break;
                                }

                                Backend.sendUpstreamNotification(hostRecentActivity,teamMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),brandingName.getType().toString(),HomeActivity.this, true);
                                Backend.sendUpstreamNotification(clientRecentActivity,teamMap.get(FirebaseEntity.EntityType.CLIENT).getUID(),currentUser.getUID(),brandingName.getType().toString(),HomeActivity.this, true);

                                Backend.update(brandingName, HomeActivity.this);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    public void setShouldHandleNavigation(boolean shouldHandleNavigation) {
        this.shouldHandleNavigation = shouldHandleNavigation;
    }
}