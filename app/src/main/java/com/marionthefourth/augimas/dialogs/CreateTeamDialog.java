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
//    Dialog Constructor
    public CreateTeamDialog(final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final View containingView, final Activity activity) {
        setTitle(containingView.getContext().getString(R.string.title_create_team));

        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        final ArrayList<TextInputEditText> editTexts = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            layouts.add(new TextInputLayout(containingView.getContext()));
            editTexts.add(new TextInputEditText(containingView.getContext()));
        }

        setupDialogLayout(layouts,editTexts);
        setupPositiveButton(editTexts, activity);

        show();
    }
    private void setupPositiveButton(final ArrayList<TextInputEditText> editTexts, final Activity activity) {
        setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (FragmentHelper.fieldsAreFilled(editTexts) && FragmentHelper.fieldsPassWhitelist(editTexts)) {
                    Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for(final Team teamItem:Team.toArrayList(dataSnapshot)) {
                                    if (teamItem.getUsername().equals(editTexts.get(1).getText().toString())) {
                                        return;
                                    }
                                }
                            }

                            createTeamAndAddUserToTeam(editTexts, activity);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }
    private void setupDialogLayout(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> editTexts) {
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
//    Functional Methods
    private void createElements(final Team newClientTeam, final Activity activity) {
//        TODO - Extend to Add More Element Types
        for (int i = 0; i < BrandingElement.ElementType.getNumberOfElementTypes(); i++) {
            final BrandingElement elementItem = new BrandingElement(BrandingElement.ElementType.getType(i));
            elementItem.setTeamUID(newClientTeam.getUID());
            Backend.create(activity,elementItem);
        }
    }
    private void createChannels(Chat connectedChat, Team newClientTeam, Team hostTeam, final Activity activity) {
        for (int i = 0; i < 3; i++) {
            final Channel channel = new Channel(connectedChat);
            switch(i) {
                case 0:
                    channel.setName(hostTeam.getName());
                    break;
                case 2:
                    channel.setName(newClientTeam.getName());
                    break;
                default:
                    break;
            }
            Backend.create(activity,channel);
        }
    }
    private void createTeamAndAddUserToTeam(final ArrayList<TextInputEditText> editTexts, final Activity activity) {
        // Get Full User Data
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                Team adminTeam;
                                for(final Team teamItem:Team.toArrayList(dataSnapshot)) {
                                    if (teamItem.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                        adminTeam = teamItem;
                                        final Team newTeam = new Team(editTexts.get(0).getText().toString(),editTexts.get(1).getText().toString(),currentUser, FirebaseEntity.EntityRole.OWNER,FirebaseEntity.EntityStatus.AWAITING);
                                        newTeam.setStatus(FirebaseEntity.EntityStatus.AWAITING);
                                        newTeam.setType(FirebaseEntity.EntityType.CLIENT);

                                        Backend.create(activity,newTeam);
                                        currentUser.setTeamUID(newTeam.getUID());
                                        update(currentUser, activity);

//                                        Create Team Branding Elements
                                        createElements(newTeam, activity);
//                                        Create Chat between the two chats
                                        final Chat connectedChat = new Chat(adminTeam, newTeam, FirebaseCommunication.CommunicationType.B);
                                        Backend.create(activity,connectedChat);
//                                        Create Channels
                                        createChannels(connectedChat, newTeam, teamItem, activity);
//                                        Create and Send Notifications
                                        final Notification teamCreatedNotification = new Notification(currentUser,newTeam, Notification.NotificationVerbType.CREATE);
                                        teamCreatedNotification.getReceiverUIDs().add(teamItem.getUID());
                                        send(activity,teamCreatedNotification);
                                        final Notification joinedTeamNotification = new Notification(currentUser,newTeam, Notification.NotificationVerbType.JOIN);
                                        send(activity,joinedTeamNotification);

                                        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,teamItem.getUID());
                                        Backend.sendUpstreamNotification(teamCreatedNotification, teamItem.getUID());

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
}