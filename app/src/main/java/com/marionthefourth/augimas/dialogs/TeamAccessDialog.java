package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.ChatActivity;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.fragments.BrandingElementsFragment;
import com.marionthefourth.augimas.fragments.TeamManagementFragment;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

/**
 * Created on 7/24/17.
 */

public final class TeamAccessDialog extends Builder {
    public TeamAccessDialog(final Activity activity, final Team teamItem) {
        super(activity);
        setupDialog(activity,teamItem);
    }

    private void setupDialog(final Activity activity, final Team teamItem) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_item_full_team, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        setupLayout(activity,alertDialog,dialogView,teamItem);

        alertDialog.show();
    }

    private void setupLayout(final Activity activity, final AlertDialog dialogBuilder, final View dialogView, final Team teamItem) {

        final int[] BUTTON_IDS = new int[] {
                R.id.button_status,
                R.id.button_dashboard,
                R.id.button_chat,
                R.id.button_team
        };

        ArrayList<AppCompatImageButton> buttons = new ArrayList<>();

        AppCompatTextView teamName = (AppCompatTextView) dialogView.findViewById(R.id.item_label_team_display_name);
        teamName.setText(teamItem.getName());

        for (int i = 0; i < BUTTON_IDS.length; i++) {
            buttons.add((AppCompatImageButton)dialogView.findViewById(BUTTON_IDS[i]));
            final int finalI = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            // Setup Status Dialog
                            new TeamStatusDialog(activity,activity.findViewById(R.id.container),teamItem);
                            break;
                        case 1:
                            // Setup Dashboard Button
                            ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.container, BrandingElementsFragment.newInstance(teamItem.getUID())).commit();
                            break;
                        case 2:
                            // Setup Chat Button
                            transitionUserToChatFragment(activity, teamItem);
                            break;
                        case 3:
                            // Setup Team Management Button
                            ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.container, TeamManagementFragment.newInstance(teamItem)).commit();
                            break;
                    }

                    dialogBuilder.dismiss();

                }
            });
        }
    }

    private void transitionUserToChatFragment(final Activity activity, final Team teamItem) {
        FirebaseHelper.getReference(activity,R.string.firebase_chats_directory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                        final Chat chatItem = new Chat(chatReference);
                        if (chatItem != null && chatItem.hasTeam(teamItem.getUID())) {
                            FirebaseHelper.getReference(activity,R.string.firebase_channels_directory).addListenerForSingleValueEvent(new ValueEventListener() {
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

                                        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    final User currentUser = new User(dataSnapshot);
                                                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                                                        FirebaseHelper.getReference(activity,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                    final Team currentTeam = new Team(dataSnapshot);
                                                                    if (currentTeam != null) {
                                                                        ArrayList<String> channelUIDs = sortChannels(channels);

                                                                        final Intent chatIntent = new Intent(activity, ChatActivity.class);

                                                                        chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.US.toInt(false),currentTeam.getUID());
                                                                        chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.THEM.toInt(false),teamItem.getUID());
                                                                        chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.US.toInt(false),channelUIDs.get(0));
                                                                        chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.THEM.toInt(false),channelUIDs.get(1));
                                                                        activity.startActivity(chatIntent);
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
                    }
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

}
