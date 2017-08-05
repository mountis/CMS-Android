package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseCommunication;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.send;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

public final class CreateTeamDialog extends AlertDialog.Builder {
    public CreateTeamDialog(final Activity activity, final View containingView) {
        super(containingView.getContext());
        setupDialog(activity,containingView);
    }

    private void setupDialog(final Activity activity, final View containingView) {
        // Setting Dialog Title
        setTitle(containingView.getContext().getString(R.string.title_create_team));

        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        final ArrayList<TextInputEditText> editTexts = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            layouts.add(new TextInputLayout(containingView.getContext()));
            editTexts.add(new TextInputEditText(containingView.getContext()));
        }

        setupDialogLayout(layouts,editTexts);

        // Set Positive "Email Me" Button
        setupPositiveButton(activity,editTexts);

        // Showing Alert Message
        show();
    }

    private void setupDialogLayout(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> editTexts) {
        // Create LinearLayout to add TextInputLayout with Edit Text
        final LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < (layouts.size()+editTexts.size())/2; i++) {
            editTexts.get(i).setLayoutParams(layoutParams);
            editTexts.get(i).setEnabled(true);
            int resource;
            switch (i) {
                case 0:
                    resource = R.string.team_name_text;
                    break;
                default:
                    resource = R.string.username_text;
                    break;
            }
            editTexts.get(i).setHint(getContext().getString(resource));
            layouts.get(i).addView(editTexts.get(i),0,layoutParams);
            layout.addView(layouts.get(i));
        }

        setView(layout);
    }

    private void setupPositiveButton(final Activity activity, final ArrayList<TextInputEditText> editTexts) {
        setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Check that text field is filled
                if (FragmentHelper.fieldsAreFilled(editTexts) && FragmentHelper.fieldsPassWhitelist(editTexts)) {
                    // Check against firebase
                    Backend.getReference(activity,R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                    final Team teamItem = new Team(teamReference);
                                    // If Team Matches Input, Add User to Member's List
                                    if (teamItem.getUsername().equals(editTexts.get(1).getText().toString())) {
                                        return;
                                    }
                                }

                                // Create Team
                                createTeamAndAddUserToTeam(activity,editTexts);
                            } else {
                                createTeamAndAddUserToTeam(activity,editTexts);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                } else {
                    // Display Error
                }
            }
        });
    }

    private void createTeamAndAddUserToTeam(final Activity activity, final ArrayList<TextInputEditText> editTexts) {
        // Get Full User Data
        Backend.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final User currentUser = new User(dataSnapshot);

                    Backend.getReference(activity,R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                Team adminTeam;
                                for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                    final Team teamItem = new Team(teamReference);
                                    if (teamItem.getType().equals(FirebaseEntity.EntityType.US)) {
                                        adminTeam = teamItem;
                                        final Team newTeam = new Team(editTexts.get(0).getText().toString(),editTexts.get(1).getText().toString(),currentUser, FirebaseEntity.EntityRole.OWNER,FirebaseEntity.EntityStatus.AWAITING);
                                        newTeam.setStatus(FirebaseEntity.EntityStatus.AWAITING);
                                        newTeam.setType(FirebaseEntity.EntityType.THEM);

                                        Backend.create(activity,newTeam);
                                        createElements(activity,newTeam);

                                        // Create Chat between the two chats
                                        final Chat connectedChat = new Chat(adminTeam, newTeam, FirebaseCommunication.CommunicationType.B);

                                        Backend.create(activity,connectedChat);
                                        // Create Channels
                                        createChannels(activity,teamItem, newTeam, connectedChat);

                                        currentUser.setTeamUID(newTeam.getUID());
                                        update(activity,currentUser);

                                        // Create and Send Notifications
                                        final Notification teamCreatedNotification = new Notification(currentUser,newTeam, Notification.NotificationVerbType.CREATE);
                                        teamCreatedNotification.getReceiverUIDs().add(teamItem.getUID());
                                        send(activity,teamCreatedNotification);
                                        final Notification joinedTeamNotification = new Notification(currentUser,newTeam, Notification.NotificationVerbType.JOIN);
                                        send(activity,joinedTeamNotification);

                                        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,teamItem.getUID());

                                        final Intent homeIntent = new Intent(activity, HomeActivity.class);
                                        activity.startActivity(homeIntent);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createElements(final Activity activity, final Team teamItem) {
        for (int i = 0; i < BrandingElement.ElementType.getNumberOfElementTypes(); i++) {
            final BrandingElement elementItem = new BrandingElement(BrandingElement.ElementType.getType(i));
            elementItem.setTeamUID(teamItem.getUID());
            Backend.create(activity,elementItem);
        }
    }

    private void createChannels(final Activity activity,Team teamItem, Team newTeam, Chat connectedChat) {
        for (int i = 0; i < 3; i++) {
            final Channel channel = new Channel(connectedChat);
            switch(i) {
                case 0:
                    channel.setName(teamItem.getName());
                    break;
                case 2:
                    channel.setName(newTeam.getName());
                    break;
                default:
                    break;
            }
            Backend.create(activity,channel);
        }
    }

}