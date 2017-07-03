package com.marionthefourth.augimas.dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.sendNotification;

public final class LeaveTeamDialog extends AlertDialog.Builder {
    public LeaveTeamDialog(final View containingView) {
        super(containingView.getContext());
        setupDialog();
    }

    private void setupDialog() {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_leave_team));
        setMessage(R.string.leave_team_confirmation_message);

        // Set Positive "Email Me" Button
        setupPositiveButton();
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

    private void setupPositiveButton() {
        setPositiveButton(R.string.leave_team_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Read Data of Team
                FirebaseHelper.getReference(getContext(), R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUserItem = new User(dataSnapshot);
                            FirebaseHelper.getReference(getContext(), R.string.firebase_teams_directory).child(currentUserItem.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final Team teamItem = new Team(dataSnapshot);
                                        teamItem.removeUser(currentUserItem);
                                        sendNotification(getContext(), currentUserItem, Notification.NotificationVerbType.LEFT, teamItem);

                                    } else {
                                        // TODO: Display Error With Server
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