package com.marionthefourth.augimas.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

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
import com.marionthefourth.augimas.fragments.ChatListFragment;
import com.marionthefourth.augimas.fragments.HomeFragment;
import com.marionthefourth.augimas.fragments.NotificationsFragment;
import com.marionthefourth.augimas.fragments.SettingsFragment;
import com.marionthefourth.augimas.fragments.TeamsFragment;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class HomeActivity extends AppCompatActivity implements ChatListFragment.OnChatListFragmentInteractionListener, TeamsFragment.OnTeamsFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            final android.app.FragmentManager rManager = getFragmentManager();

            if (rManager.findFragmentByTag("Settings") != null) {
                rManager.beginTransaction().remove(rManager.findFragmentByTag("Settings")).commit();
            }

            determineHomeState(item);

            return false;
        }

    };

    private void determineHomeState(final MenuItem item) {
        // get current user
        FirebaseHelper.getReference(getApplicationContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User userItem = new User(dataSnapshot);

                    if (userItem != null) {
                        // get current team
                        FirebaseHelper.getReference(getApplicationContext(),R.string.firebase_teams_directory).child(userItem.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team teamItem = new Team(dataSnapshot);
                                    final FragmentManager manager = getSupportFragmentManager();
                                    if (teamItem != null && teamItem.getType().equals(FirebaseEntity.EntityType.US)) {
                                        setupItemNavigation(item,manager);
                                    } else {
                                        manager.beginTransaction().replace(R.id.container, new HomeFragment()).commit();
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupItemNavigation(final MenuItem item, FragmentManager manager) {
        final android.app.FragmentManager rManager = getFragmentManager();

        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                manager.beginTransaction().replace(R.id.container, new TeamsFragment()).commit();
                return;
            case R.id.navigation_chat:
                if (Constants.Bools.FeaturesAvailable.DISPLAY_CHATS) {
                    manager.beginTransaction().replace(R.id.container, new ChatListFragment()).commit();
                } else {
                    display(findViewById(R.id.content),TOAST,R.string.feature_unavailable);
                }
                return;
            case R.id.navigation_notifications:
                if (Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
                    manager.beginTransaction().replace(R.id.container, new NotificationsFragment()).commit();
                } else {
                    display(findViewById(R.id.content),TOAST,R.string.feature_unavailable);
                }
                return;
            case R.id.navigation_settings:
                rManager.beginTransaction().replace(R.id.container, new SettingsFragment(), "Settings").commit();
                return;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final FragmentManager manager = getSupportFragmentManager();
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        // get current User
        FirebaseHelper.getReference(getApplicationContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null) {
                        // get current Team
                        FirebaseHelper.getReference(getApplicationContext(),R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        if (currentTeam.getType().equals(FirebaseEntity.EntityType.US)) {
                                            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                                            manager.beginTransaction().replace(R.id.container, new TeamsFragment()).commit();
                                        } else {
                                            navigation.setVisibility(View.GONE);
                                            manager.beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                                        }
                                    }
                                } else {
                                    navigation.setVisibility(View.GONE);
                                    manager.beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        navigation.setVisibility(View.GONE);
                        manager.beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onChatListFragmentInteraction(final Context context, final Chat chatItem, final Team teamItem) {
        transitionUserToChatActivity(context,chatItem,teamItem);
    }

    private void transitionUserToChatActivity(final Context context, final Chat chatItem, final Team teamItem) {
        FirebaseHelper.getReference(context,R.string.firebase_channels_directory).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    FirebaseHelper.getReference(context,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final User currentUser = new User(dataSnapshot);
                                if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                                    FirebaseHelper.getReference(context,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                final Team currentTeam = new Team(dataSnapshot);
                                                if (currentTeam != null) {
                                                    ArrayList<String> channelUIDs = sortChannels(channels);

                                                    final Intent chatIntent = new Intent(context, ChatActivity.class);
                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.US.toInt(false),currentTeam.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.THEM.toInt(false),teamItem.getUID());
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.US.toInt(false),channelUIDs.get(0));
                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.THEM.toInt(false),channelUIDs.get(1));
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

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<String> sortChannels(final ArrayList<Channel> channels) {
        final ArrayList<Channel> sortedChannels = new ArrayList<>(channels.size());
        final ArrayList<String> channelUIDs = new ArrayList<>(channels.size());
        if (channels.size() == 2) {
            if (channels.get(0).getName().equals("")) {
                sortedChannels.add(channels.get(0));
                sortedChannels.add(channels.get(1));
            } else {
                sortedChannels.add(channels.get(1));
                sortedChannels.add(channels.get(0));
            }

            for (int i = 0; i < sortedChannels.size(); i++) {
                channelUIDs.add(sortedChannels.get(i).getUID());
            }
        }

        return channelUIDs;
    }

    @Override
    public void onTeamsFragmentInteraction(Context context, final Team teamItem) {
        final Intent brandingElementIntent = new Intent(context,BrandingElementsActivity.class);
        brandingElementIntent.putExtra(Constants.Strings.UIDs.TEAM_UID,teamItem.getUID());
        context.startActivity(brandingElementIntent);
    }

}