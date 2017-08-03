package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.helpers.FirebaseHelper;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.delete;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.sendNotification;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.update;

public final class LeaveTeamDialog extends AlertDialog.Builder {
    public LeaveTeamDialog(final Activity activity, final View containingView) {
        super(containingView.getContext());
        setupDialog(activity);
    }

    private void setupDialog(final Activity activity) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_leave_team));
        setMessage(R.string.leave_team_confirmation_message);

        // Set Positive "Email Me" Button
        setupPositiveButton(activity);
        setupNeutralButton();

        // Showing Alert Message
        show();
    }

    private void setupNeutralButton() {
        setNegativeButton(R.string.dialog_cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void setupPositiveButton(final Activity activity) {
        setPositiveButton(R.string.leave_team_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Read Data of Team
                FirebaseHelper.getReference(activity, R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUserItem = new User(dataSnapshot);
                            if (currentUserItem.getTeamUID().equals("")) {

                            } else {
                                FirebaseHelper.getReference(activity, R.string.firebase_teams_directory).child(currentUserItem.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            final Team teamItem = new Team(dataSnapshot);
                                            teamItem.removeUser(currentUserItem);
                                            try {
                                                Pushy.unsubscribe(teamItem.getUID(),getContext());
                                            } catch (PushyException e) {
                                                e.printStackTrace();
                                            }

                                            update(activity,currentUserItem);
                                            sendNotification(activity, currentUserItem, Notification.NotificationVerbType.LEFT, teamItem);
                                            FragmentHelper.display(activity.findViewById(R.id.container), TOAST,R.string.left_team);

                                            FirebaseHelper.getReference(activity,R.string.firebase_users_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                        ArrayList<User> usersInTeam = new ArrayList<>();
                                                        for (DataSnapshot userReference:dataSnapshot.getChildren()) {

                                                            final User userItem = new User(userReference);
                                                            if (userItem.isInTeam(teamItem)) {
                                                                // Don't Delete Team
                                                                usersInTeam.add(userItem);

                                                            }
                                                        }

                                                        // Check if there were any users left in the team
                                                        if (usersInTeam.size() > 0) {
                                                            // If there is only 1 user left, elevate him/her
                                                            if (usersInTeam.size() == 1) {
                                                                usersInTeam.get(0).setStatus(FirebaseEntity.EntityStatus.APPROVED);
                                                                usersInTeam.get(0).setRole(FirebaseEntity.EntityRole.OWNER);
                                                                update(activity,usersInTeam.get(0));
                                                            } else {
                                                                // Find user with the highest level

                                                                ArrayList<User> adminUsers = new ArrayList<>();
                                                                ArrayList<User> editorUsers = new ArrayList<>();
                                                                ArrayList<User> chatterUsers = new ArrayList<>();
                                                                ArrayList<User> viewerUsers = new ArrayList<>();
                                                                ArrayList<User> noneUsers = new ArrayList<>();

                                                                for (User user:usersInTeam) {
                                                                    switch(user.getRole()) {
                                                                        case OWNER:
                                                                            return;
                                                                        case ADMIN:
                                                                            adminUsers.add(user);
                                                                        case EDITOR:
                                                                            editorUsers.add(user);
                                                                        case CHATTER:
                                                                            chatterUsers.add(user);
                                                                        case VIEWER:
                                                                            viewerUsers.add(user);
                                                                        case NONE:
                                                                            noneUsers.add(user);
                                                                        case DEFAULT:
                                                                            break;
                                                                    }
                                                                }

                                                                ArrayList<User> elevatedGroup = new ArrayList<>();

                                                                if (adminUsers.size() > 0) {
                                                                    // elevate all users to
                                                                    elevatedGroup.addAll(adminUsers);
                                                                } else if (editorUsers.size() > 0) {
                                                                    elevatedGroup.addAll(editorUsers);
                                                                } else if (chatterUsers.size() > 0) {
                                                                    elevatedGroup.addAll(chatterUsers);
                                                                } else if (viewerUsers.size() > 0) {
                                                                    elevatedGroup.addAll(viewerUsers);
                                                                } else if (noneUsers.size() > 0) {
                                                                    elevatedGroup.addAll(noneUsers);
                                                                } else {
                                                                    delete(activity,teamItem);
                                                                }

                                                                for(User user:elevatedGroup) {
                                                                    user.setRole(FirebaseEntity.EntityRole.OWNER);
                                                                    user.setStatus(FirebaseEntity.EntityStatus.APPROVED);
                                                                    update(activity,user);
                                                                }
                                                                return;
                                                            }
                                                        } else {
                                                            // If not, delete it
                                                            delete(activity,teamItem);
                                                        }
                                                    } else {
                                                        delete(activity,teamItem);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {}
                                            });

//                                            final Intent homeIntent = new Intent(activity, HomeActivity.class);
//                                            activity.startActivity(homeIntent);
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
        });
    }
}