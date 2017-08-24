package com.augimas.android.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.delete;
import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.backend.Backend.update;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

public final class LeaveTeamDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public LeaveTeamDialog(final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(activity);
    }
//    Dialog Setup Methods
    private void setupNeutralButton() {
        setNegativeButton(R.string.dialog_cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
    private void setupDialog(final Activity activity) {
        setTitle(getContext().getString(R.string.title_leave_team));
        setMessage(R.string.leave_team_confirmation_message);
        setupNeutralButton();
        setupPositiveButton(activity);
        show();
    }
    private void setupPositiveButton(final Activity activity) {
        setPositiveButton(R.string.leave_team_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
            // Read Data of Team
            if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnapshot) {
                        final User currentUserItem = new User(userSnapshot);
                        if (!currentUserItem.getTeamUID().equals("")) {
                            Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot teamSnapshot) {
                                    if (teamSnapshot.exists()) {
                                        final ArrayMap<FirebaseEntity.EntityType,Team> teamArrayMap = Team.toClientAndHostTeamMap(teamSnapshot,currentUserItem.getTeamUID());
                                        final FirebaseEntity.EntityType teamType = currentUserItem.getType();

                                        sendNotification(currentUserItem,teamType,teamArrayMap,activity);

                                        teamArrayMap.get(teamType).removeUser(currentUserItem);
                                        update(currentUserItem, activity);
                                        Backend.unSubscribeFrom(teamArrayMap.get(teamType).getUID());
                                        FragmentHelper.display(TOAST, R.string.left_team, activity.findViewById(R.id.container));

                                        handleUserLeaving(teamType,teamArrayMap,activity);
                                    }
                                    dialog.dismiss();
                                    activity.startActivity(new Intent(activity, HomeActivity.class));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
            }
        });
    }
    private void handleUserLeaving(final FirebaseEntity.EntityType teamType, final ArrayMap<FirebaseEntity.EntityType, Team> teamArrayMap, final Activity activity) {
        Backend.getReference(R.string.firebase_users_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                if (userSnapshot.hasChildren()) {
                    final ArrayList<User> usersInTeam = User.toFilteredArrayList(userSnapshot, Constants.Strings.UIDs.TEAM_UID,teamArrayMap.get(teamType).getUID());

                    if (usersInTeam.size() > 0) {
                        // Elevate Highest Tier Group to Owner Status to Ensure Continuity of Team
                        final ArrayMap<FirebaseEntity.EntityRole,ArrayList<User>> userArrayMap = User.toRoleFilteredArrayMap(usersInTeam);

                        for(FirebaseEntity.EntityRole role: FirebaseEntity.EntityRole.getAllRoles()) {
                            ArrayList<User> userGroup = userArrayMap.get(role);
                            if (userGroup.size() > 0) {
                                if (role == FirebaseEntity.EntityRole.OWNER) {
                                    return;
                                } else {
                                    for(final User user:userGroup) {
                                        user.setRole(FirebaseEntity.EntityRole.OWNER);
                                        user.setStatus(FirebaseEntity.EntityStatus.APPROVED);
                                        update(user, activity);
                                    }
                                    return;
                                }
                            }
                        }
                    } else {
                        delete(teamArrayMap.get(teamType), activity);
                    }
                } else {
                    delete(teamArrayMap.get(teamType), activity);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void sendNotification(final User currentUserItem, final FirebaseEntity.EntityType teamType, final ArrayMap<FirebaseEntity.EntityType, Team> teamArrayMap, final Activity activity) {
        final RecentActivity leftRecentActivity = new RecentActivity(currentUserItem,teamArrayMap.get(teamType), RecentActivity.ActivityVerbType.LEFT);
        if (teamArrayMap.size() == 2) {
            leftRecentActivity.addReceiverUID(teamArrayMap.get(FirebaseEntity.EntityType.HOST));
            if (teamType != FirebaseEntity.EntityType.HOST) {
                leftRecentActivity.addReceiverUID(teamArrayMap.get(FirebaseEntity.EntityType.CLIENT));
                Backend.sendUpstreamNotification(leftRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.CLIENT).getUID(), currentUserItem.getUID(), Constants.Strings.Headers.USER_LEFT, activity, true);
            } else {
                Backend.sendUpstreamNotification(leftRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(), currentUserItem.getUID(),Constants.Strings.Headers.USER_LEFT, activity, true);
            }
        } else {
            leftRecentActivity.addReceiverUID(currentUserItem);
            Backend.sendUpstreamNotification(leftRecentActivity,teamArrayMap.get(teamType).getUID(), currentUserItem.getUID(),Constants.Strings.Headers.USER_LEFT, activity, true);
        }
    }
}