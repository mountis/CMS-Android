package com.augimas.android.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseCommunication;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.communication.Channel;
import com.augimas.android.classes.objects.communication.Chat;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.backend.Backend.update;
import static com.augimas.android.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

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
        for (int i = 0; i < BrandingElement.ElementType.getNumberOfElementTypes(); i++) {
            final BrandingElement elementItem = new BrandingElement(BrandingElement.ElementType.getType(i));
            elementItem.setTeamUID(newClientTeam.getUID());
            Backend.create(elementItem, activity);
        }
    }
    private void createChannels(final Chat connectedChat, final Team newClientTeam, final Team hostTeam, final Activity activity) {
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
            Backend.create(channel, activity);
        }
    }
    private void createTeamAndAddUserToTeam(final ArrayList<TextInputEditText> editTexts, final Activity activity) {
        // Get Full User Data
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User(userSnapshot);
                        Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot teamSnapshot) {
                                if (teamSnapshot.hasChildren()) {
                                    for(final Team teamItem:Team.toArrayList(teamSnapshot)) {
                                        if (teamItem.getType().equals(FirebaseEntity.EntityType.HOST)) {
                                            handleTeamUpdate(teamItem,currentUser,editTexts,activity);
                                            return;
                                        }
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
    private void handleTeamUpdate(final Team teamItem, final User currentUser, final ArrayList<TextInputEditText> editTexts, final Activity activity) {
        final Team adminTeam = teamItem;
        final Team newTeam = new Team(editTexts.get(0).getText().toString(),editTexts.get(1).getText().toString());
        Backend.create(newTeam, activity);
        newTeam.addUser(currentUser, FirebaseEntity.EntityRole.OWNER, FirebaseEntity.EntityStatus.AWAITING);
        currentUser.setTeamUID(newTeam.getUID());
        update(currentUser, activity);
//                                          Create Team Branding Elements
        createElements(newTeam, activity);
        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,newTeam.getUID());
//                                        Create Chat between the two teams
        final Chat connectedChat = new Chat(adminTeam, newTeam, FirebaseCommunication.CommunicationType.B);
        Backend.create(connectedChat, activity);
//                                                Create Channels
        createChannels(connectedChat, newTeam, teamItem, activity);
//                                        Create and Send Notifications
        Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot teamsSnapshot) {
                sendNotification(teamsSnapshot,newTeam,currentUser,activity);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void sendNotification(final DataSnapshot teamsSnapshot, final Team newTeam, final User currentUser, final Activity activity) {
        final ArrayMap<FirebaseEntity.EntityType,Team> teamArrayMap = Team.toClientAndHostTeamMap(teamsSnapshot,newTeam.getUID());
        // Send Client Notifications
        final RecentActivity clientTeamCreatedRecentActivity = new RecentActivity(currentUser,newTeam, RecentActivity.ActivityVerbType.CREATE);
        clientTeamCreatedRecentActivity.addReceiverUID(teamArrayMap.get(FirebaseEntity.EntityType.HOST));
        Backend.sendUpstreamNotification(clientTeamCreatedRecentActivity, newTeam.getUID(), currentUser.getUID(),Constants.Strings.Headers.NEW_TEAM, activity, true);
        // Send Host Notifications
        Backend.sendUpstreamNotification(clientTeamCreatedRecentActivity, teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(), currentUser.getUID(),Constants.Strings.Headers.NEW_TEAM, activity, false);
        activity.startActivity(new Intent(activity, HomeActivity.class));
    }
}