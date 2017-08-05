package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.sendNotification;

public final class InviteMemberDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public InviteMemberDialog(final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(activity, containingView);
    }
//    Dialog Setup Methods
    private void setupDialog(final Activity activity, final View containingView) {
        setTitle(getContext().getString(R.string.title_invite_member));

        final TextInputEditText usernameOrEmailEditText = new TextInputEditText(getContext());
        final AppCompatSpinner memberRoleSpinner = new AppCompatSpinner(containingView.getContext());
        setupDialogLayout(memberRoleSpinner, new TextInputLayout(getContext()), usernameOrEmailEditText, containingView, activity);

        setupPositiveButton(memberRoleSpinner, usernameOrEmailEditText, activity);

        show();
    }
    private void setupPositiveButton(final AppCompatSpinner memberRoleSpinner, final TextInputEditText usernameOrEmailEditText, final Activity activity) {
        setPositiveButton(R.string.invite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!usernameOrEmailEditText.getText().equals("")) {
                    Backend.getReference(R.string.firebase_users_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                User currentUser = null;
                                User invitedUser = null;

                                for(final User userItem:User.toArrayList(dataSnapshot)) {

                                    if (getCurrentUser().getUID().equals(userItem.getUID())) {
                                        currentUser = userItem;
//                                        You Cannot Invite Yourself!
                                        if(currentUser.usernameOrEmailMatches(usernameOrEmailEditText.getText().toString())) {
                                            dialog.dismiss();
                                            FragmentHelper.display(TOAST, R.string.you_cant_invite_yourself, activity.findViewById(R.id.container));
                                            return;
                                        }
                                    } else if (userItem.usernameOrEmailMatches(usernameOrEmailEditText.getText().toString())) {
                                        // Add User to Team
                                        if (!userItem.getTeamUID().equals("")) {
                                            dialog.dismiss();
                                            FragmentHelper.display(TOAST, R.string.this_user_is_already_in_a_team, activity.findViewById(R.id.container));
                                            return;
                                        } else {
                                            invitedUser = userItem;
                                        }
                                    } else if (invitedUser != null && currentUser != null) {
                                        break;
                                    }

                                }

                                // No User Found
                                if (invitedUser == null) {
                                    dialog.dismiss();
                                    FragmentHelper.display(TOAST, R.string.no_user_found, activity.findViewById(R.id.container));
                                    return;
                                } else {
                                    final User currentUserItem = currentUser;
                                    final User invitedUserItem = invitedUser;

                                    if (currentUserItem != null) {
                                        Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    addUserToTeam(activity,dialog,new Team(dataSnapshot), currentUserItem, invitedUserItem, memberRoleSpinner);
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
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }
    private void setupDialogLayout(final AppCompatSpinner memberRoleSpinner, final TextInputLayout inputLayout, final TextInputEditText usernameEditText, final View containingView, final Activity activity) {
        final LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        usernameEditText.setLayoutParams(layoutParams);
        usernameEditText.setHint(containingView.getContext().getString(R.string.username_or_email_text));

        inputLayout.addView(usernameEditText,0,layoutParams);
        layout.addView(inputLayout);

        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = new User(dataSnapshot);
                    ArrayList<FirebaseEntity.EntityRole> entityRoles = FirebaseEntity.EntityRole.getAllRoles();
                    entityRoles.remove(FirebaseEntity.EntityRole.DEFAULT);
                    memberRoleSpinner.setLayoutParams(layoutParams);

                    if (currentUser.getType() != FirebaseEntity.EntityType.HOST) {
                        if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {

                            if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.OWNER)) {
                                entityRoles.remove(FirebaseEntity.EntityRole.OWNER);
                            }
                        }
                    }

                    ArrayAdapter<FirebaseEntity.EntityRole> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, FirebaseEntity.EntityRole.getAllRoles());
                    memberRoleSpinner.setAdapter(adapter);
                    layout.addView(memberRoleSpinner);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        setView(layout);
    }
//    Functional Methods
    private void addUserToTeam(final Activity activity, final DialogInterface dialog, final Team teamItem, final User currentUserItem, final User invitedUserItem, final AppCompatSpinner memberRoleSpinner) {
        FirebaseEntity.EntityRole role = FirebaseEntity.EntityRole.NONE;
        FirebaseEntity.EntityStatus status = FirebaseEntity.EntityStatus.AWAITING;
        Notification.NotificationVerbType verb = Notification.NotificationVerbType.REQUEST;

        if (currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
            status = FirebaseEntity.EntityStatus.APPROVED;
            verb = Notification.NotificationVerbType.JOIN;
            role = (FirebaseEntity.EntityRole) memberRoleSpinner.getSelectedItem();
        }

        invitedUserItem.setType(currentUserItem.getType());

        teamItem.addUser(invitedUserItem, role, status);
        Backend.update(invitedUserItem, activity);
        Backend.update(teamItem, activity);

        FragmentHelper.display(TOAST, R.string.you_added_to_the_team, activity.findViewById(R.id.container));
        Backend.sendUpstreamNotification(sendNotification(teamItem, invitedUserItem, verb, activity), teamItem.getUID());

        dialog.dismiss();
    }
}