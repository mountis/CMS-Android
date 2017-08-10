package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

public final class JoinTeamDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public JoinTeamDialog(final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final View containingView, final Activity activity) {
        setTitle(getContext().getString(R.string.title_join_team));

        final TextInputEditText usernameEditText = new TextInputEditText(getContext());
        setupDialogLayout(usernameEditText, new TextInputLayout(getContext()), containingView);

        setupPositiveButton(usernameEditText, activity);

        show();
    }
    private void setupPositiveButton(final TextInputEditText usernameEditText, final Activity activity) {
        setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!usernameEditText.getText().equals("")) {
                    Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for(final Team teamItem:Team.toArrayList(dataSnapshot)) {
                                    if (teamItem.getUsername().equals(usernameEditText.getText().toString())) {
                                        // Add User to Team
                                        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    addUserToTeam(new User(dataSnapshot), teamItem, FirebaseEntity.EntityRole.NONE, FirebaseEntity.EntityStatus.AWAITING, dialog, activity);
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
        });
    }
    private void setupDialogLayout(final TextInputEditText usernameEditText, final TextInputLayout inputLayout, final View containingView) {
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

        setView(layout);
    }
    //    Functional Methods
    private void addUserToTeam(final User user, final Team teamItem, final FirebaseEntity.EntityRole memberRole, final FirebaseEntity.EntityStatus memberStatus, final DialogInterface dialog, final Activity activity) {
        teamItem.addUser(user,memberRole,memberStatus);
        Backend.update(user, activity);
        Backend.update(teamItem, activity);
        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,teamItem.getUID());

        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.sendUpstreamNotification(Backend.sendNotification(teamItem, user, RecentActivity.NotificationVerbType.REQUEST, activity), teamItem.getUID(), getCurrentUser().getUID(),Constants.Strings.Headers.NEW_USER, activity, true);
        }

        dialog.dismiss();
    }
}