package com.marionthefourth.augimas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.BrandingElementsActivity;
import com.marionthefourth.augimas.activities.ChatActivity;
import com.marionthefourth.augimas.activities.NotificationActivity;
import com.marionthefourth.augimas.activities.SettingsActivity;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupButtons(view);
        
        return view;
    }

    private void setupButtons(final View view) {
        FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Button dashboardButton = (Button) view.findViewById(R.id.dashboard_button);
                final Button chatButton = (Button) view.findViewById(R.id.chats_button);
                final Button notificationButton = (Button) view.findViewById(R.id.notifications_button);
                final Button settingsButton = (Button) view.findViewById(R.id.settings_button);

                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        if (currentTeam.getStatus().equals(FirebaseEntity.EntityStatus.APPROVED)) {
                                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                                                dashboardButton.setEnabled(true);
                                                chatButton.setEnabled(true);
                                                notificationButton.setEnabled(true);
                                            }
                                        } else {
                                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                                                dashboardButton.setEnabled(false);
                                                if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                                                    chatButton.setEnabled(true);
                                                } else {
                                                    chatButton.setEnabled(false);
                                                }
                                                notificationButton.setEnabled(true);
                                            }
                                        }

                                        if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)){
                                            dashboardButton.setEnabled(false);
                                            chatButton.setEnabled(false);
                                            notificationButton.setEnabled(false);
                                        }

                                        settingsButton.setEnabled(true);

                                        // Setup Dashboard Button
                                        dashboardButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final Intent brandingElementsIntent = new Intent(getContext(), BrandingElementsActivity.class);
                                                brandingElementsIntent.putExtra(Constants.Strings.UIDs.TEAM_UID,currentTeam.getUID());
                                                startActivity(brandingElementsIntent);
                                            }
                                        });

                                        // Setup Chat Button

                                            chatButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (Constants.Bools.FeaturesAvailable.DISPLAY_CHATS) {
                                                        FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                    for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                                                        final Team adminTeam = new Team(teamReference);
                                                                        if (adminTeam.getType().equals(FirebaseEntity.EntityType.US)) {

                                                                            loadChannelUIDs(adminTeam);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    } else {
                                                        display(view,TOAST,R.string.feature_unavailable);
                                                    }

                                                }
                                            });


                                    } else {
                                        dashboardButton.setEnabled(false);
                                        chatButton.setEnabled(false);
                                        notificationButton.setEnabled(false);
                                        settingsButton.setEnabled(true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        dashboardButton.setEnabled(false);
                        chatButton.setEnabled(false);
                        notificationButton.setEnabled(false);
                        settingsButton.setEnabled(true);
                    }
                }

                // Setup Notification Button

                    notificationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Constants.Bools.FeaturesAvailable.DISPLAY_NOTIFICATIONS) {
                                startActivity(new Intent(getContext(), NotificationActivity.class));
                            } else {
                                display(view,TOAST,R.string.feature_unavailable);
                            }
                        }
                    });

                // Setup Settings Button
                settingsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), SettingsActivity.class));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadChannelUIDs(final Team adminTeam) {
        FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        FirebaseHelper.getReference(getContext(),R.string.firebase_chats_directory).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                    for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                                                        final Chat chatItem = new Chat(chatReference);
                                                        for (int i = 0; i < chatItem.getTeamUIDs().size(); i++) {
                                                            if (chatItem.getTeamUIDs().get(i).equals(currentTeam.getUID())) {
                                                                if (chatItem != null & !chatItem.getUID().equals("")) {
                                                                    FirebaseHelper.getReference(getContext(),R.string.firebase_channels_directory).addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                                final ArrayList<Channel> channels = new ArrayList<>();
                                                                                for (DataSnapshot channelReference:dataSnapshot.getChildren()) {
                                                                                    final Channel channelItem = new Channel(channelReference);
                                                                                    if (channelItem.getChatUID().equals(chatItem.getUID())) {
                                                                                        if (channelItem.getName().equals(currentTeam.getName()) || channelItem.getName().equals(""))
                                                                                            channels.add(channelItem);
                                                                                    }
                                                                                }


                                                                                ArrayList<String> channelUIDs = sortChannels(channels);

                                                                                final Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                                                chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.US.toInt(false),currentTeam.getUID());
                                                                                chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.THEM.toInt(false),adminTeam.getUID());
                                                                                chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.US.toInt(false),channelUIDs.get(0));
                                                                                chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.THEM.toInt(false),channelUIDs.get(1));
                                                                                startActivity(chatIntent);
                                                                                return;
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private ArrayList<String> sortChannels(final ArrayList<Channel> channels) {
        final ArrayList<Channel> sortedChannels = new ArrayList<>();
        final ArrayList<String> channelUIDs = new ArrayList<>();
        if (channels.size() == 2) {
            for (int i = 0; i < channels.size(); i++) {
                if (channels.get(i).getName().equals("")) {
                    sortedChannels.add(0,channels.get(i));
                } else {
                    sortedChannels.add(1,channels.get(i));

                }
            }

            for (int i = 0; i < sortedChannels.size(); i++) {
                channelUIDs.add(sortedChannels.get(i).getUID());
            }
        }

        return channelUIDs;
    }

}