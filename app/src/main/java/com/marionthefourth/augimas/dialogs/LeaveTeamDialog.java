package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.delete;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.sendNotification;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

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
                Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User currentUserItem = new User(dataSnapshot);
                        if (!currentUserItem.getTeamUID().equals("")) {
                            Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUserItem.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final Team teamItem = new Team(dataSnapshot);
                                        teamItem.removeUser(currentUserItem);

                                        update(currentUserItem, activity);
                                        Backend.unSubscribeFrom(teamItem.getUID());


                                        Backend.sendUpstreamNotification(sendNotification(teamItem, currentUserItem, Notification.NotificationVerbType.LEFT, activity),teamItem.getUID(), currentUserItem.getUID(),Constants.Strings.Headers.USER_LEFT, activity);
                                        FragmentHelper.display(TOAST, R.string.left_team, activity.findViewById(R.id.container));

                                        Backend.getReference(R.string.firebase_users_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    final ArrayList<User> usersInTeam = User.toFilteredArrayList(dataSnapshot, Constants.Strings.UIDs.TEAM_UID,teamItem.getUID());

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
                                                        delete(teamItem, activity);
                                                    }
                                                } else {
                                                    delete(teamItem, activity);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                    dialog.dismiss();
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
        });
    }
}