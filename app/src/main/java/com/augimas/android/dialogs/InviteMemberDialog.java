package com.augimas.android.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

public final class InviteMemberDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public InviteMemberDialog(final Team teamItem, final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(teamItem,containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final Team teamItem, final View containingView, final Activity activity) {
        setTitle(getContext().getString(R.string.title_invite_member));
        final TextInputEditText usernameOrEmailEditText = new TextInputEditText(getContext());
        final AppCompatSpinner memberRoleSpinner = new AppCompatSpinner(containingView.getContext());
        setupDialogLayout(memberRoleSpinner, new TextInputLayout(getContext()), usernameOrEmailEditText, containingView, activity);
        setupPositiveButton(teamItem,memberRoleSpinner, usernameOrEmailEditText, activity);
        show();
    }
    private void setupPositiveButton(final Team teamItem, final AppCompatSpinner memberRoleSpinner, final TextInputEditText usernameOrEmailEditText, final Activity activity) {
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
                                    if ((getCurrentUser() != null ? getCurrentUser().getUID():null)!= null) {
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
                                }

                                // No User Found
                                if (invitedUser == null) {
                                    dialog.dismiss();
                                    FragmentHelper.display(TOAST, R.string.no_user_found, activity.findViewById(R.id.container));
                                } else {
                                    final User currentUserItem = currentUser;
                                    final User invitedUserItem = invitedUser;

                                    if (currentUserItem != null) {
                                        Backend.getReference(R.string.firebase_teams_directory, activity).child(teamItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot teamSnapshot) {
                                                if (teamSnapshot.exists()) {
                                                    addUserToTeam(invitedUserItem, currentUserItem, new Team(teamSnapshot), memberRoleSpinner, dialog, activity);
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

        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User currentUser = new User(dataSnapshot);
                        ArrayList<FirebaseEntity.EntityRole> entityRoles = FirebaseEntity.EntityRole.getAllRoles();
                        entityRoles.remove(FirebaseEntity.EntityRole.DEFAULT);
                        memberRoleSpinner.setLayoutParams(layoutParams);

                        if (currentUser.getType() != FirebaseEntity.EntityType.HOST) {
                            for(FirebaseEntity.EntityRole role:FirebaseEntity.EntityRole.getAllRoles()){
                                if (role.toInt(false) >= FirebaseEntity.EntityRole.EDITOR.toInt(false)) {
                                    if (!currentUser.hasInclusiveAccess(role)) {
                                        entityRoles.remove(role);
                                    }
                                }
                            }
                        }

                        ArrayAdapter<FirebaseEntity.EntityRole> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, entityRoles);
                        memberRoleSpinner.setAdapter(adapter);
                        layout.addView(memberRoleSpinner);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        setView(layout);
    }
//    Functional Methods
    private void addUserToTeam(final User invitedUserItem, final User currentUserItem, final Team teamItem, final AppCompatSpinner memberRoleSpinner, final DialogInterface dialog, final Activity activity) {
        final FirebaseEntity.EntityStatus status;
        final RecentActivity.ActivityVerbType verb;

        if (currentUserItem.getType() == FirebaseEntity.EntityType.HOST || currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
            status = FirebaseEntity.EntityStatus.APPROVED;
            verb = RecentActivity.ActivityVerbType.ADD;
        } else {
            status = FirebaseEntity.EntityStatus.AWAITING;
            verb = RecentActivity.ActivityVerbType.INVITE;
        }

        final FirebaseEntity.EntityRole role = (FirebaseEntity.EntityRole) memberRoleSpinner.getSelectedItem();

        invitedUserItem.setType(currentUserItem.getType());

        teamItem.addUser(invitedUserItem, role, status);
        Backend.update(invitedUserItem, activity);
        FragmentHelper.display(TOAST, R.string.you_added_to_the_team, activity.findViewById(R.id.container));

        sendNotification(currentUserItem,invitedUserItem,verb,teamItem,activity);
        dialog.dismiss();
    }
    private void sendNotification(final User currentUserItem, final User invitedUserItem, final RecentActivity.ActivityVerbType verb, final Team teamItem, final Activity activity) {
        final RecentActivity recentActivity;
        recentActivity = new RecentActivity(currentUserItem,invitedUserItem,verb,teamItem.getName());
        recentActivity.addReceiverUID(invitedUserItem.getTeamUID());

        if (invitedUserItem.getType() != FirebaseEntity.EntityType.HOST) {
            Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final ArrayMap<FirebaseEntity.EntityType,Team> teamArrayMap = Team.toClientAndHostTeamMap(dataSnapshot,currentUserItem.getTeamUID());
                        recentActivity.addReceiverUID(teamArrayMap.get(FirebaseEntity.EntityType.HOST));
                        if (currentUserItem.getType() == FirebaseEntity.EntityType.HOST) {
                            recentActivity.setExtraString(teamArrayMap.get(FirebaseEntity.EntityType.HOST).getName());
                            recentActivity.setMessage();
                        }
                        Backend.sendUpstreamNotification(recentActivity, teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(), currentUserItem.getUID(), Constants.Strings.Headers.USER_INVITATION, activity, true);
                        Backend.sendUpstreamNotification(recentActivity, teamArrayMap.get(FirebaseEntity.EntityType.CLIENT).getUID(), currentUserItem.getUID(), Constants.Strings.Headers.USER_INVITATION, activity, false);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            Backend.sendUpstreamNotification(recentActivity, teamItem.getUID(), currentUserItem.getUID(), Constants.Strings.Headers.USER_INVITATION, activity, true);
        }
    }
}