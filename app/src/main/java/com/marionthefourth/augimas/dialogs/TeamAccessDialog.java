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
import com.marionthefourth.augimas.backend.Backend;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.objects.communication.Channel.sortChannels;

/**
 * Created on 7/24/17.
 */

public final class TeamAccessDialog extends Builder {
//    Dialog Constructor
    public TeamAccessDialog(final Team teamItem, final Activity activity) {
        super(activity);
        setupDialog(teamItem, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final Team teamItem, final Activity activity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_item_full_team, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        setupLayout(teamItem, alertDialog, dialogView, (AppCompatActivity) activity);

        alertDialog.show();
    }
    private void setupLayout(final Team teamItem, final AlertDialog dialogBuilder, final View dialogView, final AppCompatActivity activity) {
        final int[] BUTTON_IDS = new int[] {
                R.id.button_status,
                R.id.button_dashboard,
                R.id.button_chat,
                R.id.button_team
        };

        AppCompatTextView teamName = (AppCompatTextView) dialogView.findViewById(R.id.item_label_team_display_name);
        teamName.setText(teamItem.getName());

        ArrayList<AppCompatImageButton> buttons = new ArrayList<>();
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            buttons.add((AppCompatImageButton)dialogView.findViewById(BUTTON_IDS[i]));
            final int buttonIndex = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (buttonIndex) {
                        case 0:
                            new TeamStatusDialog(teamItem, activity.findViewById(R.id.container), activity);
                            break;
                        case 1:
                            // Setup Dashboard Button
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, BrandingElementsFragment.newInstance(teamItem.getUID())).commit();
                            break;
                        case 2:
                            // Setup Chat Button
                            transitionUserToChatFragment(teamItem, activity);
                            break;
                        case 3:
                            // Setup Team Management Button
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, TeamManagementFragment.newInstance(teamItem)).commit();
                            break;
                    }

                    dialogBuilder.dismiss();
                }
            });
        }
    }
//    Transitional Method
    private void transitionUserToChatFragment(final Team teamItem, final Activity activity) {
        Backend.getReference(R.string.firebase_chats_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (final Chat chatItem:Chat.toArrayList(dataSnapshot)) {
                        if (chatItem.hasTeam(teamItem.getUID())) {
                            Backend.getReference(R.string.firebase_channels_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        final ArrayList<Channel> channels = Channel.toArrayList(dataSnapshot);
                                        for (final Channel channelItem:channels) {
                                            if (channelItem.getChatUID().equals(chatItem.getUID())) {
                                                if (!channelItem.getName().equals(teamItem.getName())) {
                                                    channels.remove(channelItem);
                                                }
                                            }
                                        }

                                        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    final User currentUser = new User(dataSnapshot);
                                                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                                                        Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                    final Team currentTeam = new Team(dataSnapshot);
                                                                    ArrayList<String> channelUIDs = sortChannels(channels);

                                                                    final Intent chatIntent = new Intent(activity, ChatActivity.class);

                                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false),currentTeam.getUID());
                                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false),teamItem.getUID());
                                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false),channelUIDs.get(0));
                                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false),channelUIDs.get(1));
                                                                    activity.startActivity(chatIntent);
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
}
