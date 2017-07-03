package com.marionthefourth.augimas.dialogs;

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
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

public final class JoinTeamDialog extends AlertDialog.Builder {

    public JoinTeamDialog(final View containingView) {
        super(containingView.getContext());
        setupDialog(containingView);
    }

    private void setupDialog(final View containingView) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_join_team));

        // Add Username or Email Field
        final TextInputLayout usernameLayout = new TextInputLayout(getContext());
        final TextInputEditText usernameEditText = new TextInputEditText(getContext());
        setupDialogLayout(containingView,usernameLayout,usernameEditText);

        // Set Positive "Email Me" Button
        setupPositiveButton(usernameEditText);

        // Showing Alert Message
        show();

    }

    private void setupPositiveButton(final TextInputEditText usernameEditText) {
        setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Check that text field is filled
                if (!usernameEditText.getText().equals("")) {
                    // Check against firebase
                    FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                    final Team teamItem = new Team(teamReference);
                                    // If Team Matches Input, Add User to Member's List
                                    if (teamItem.getUsername().equals(usernameEditText.getText())) {
                                        // Add User to Team
                                        FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    User user = new User(dataSnapshot);
                                                    addUserToTeam(dialog, teamItem, user, FirebaseEntity.EntityRole.NONE, FirebaseEntity.EntityStatus.AWAITING);
                                                } else {
                                                    // Error
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Error
                                            }
                                        });
                                        teamItem.addUser(getCurrentUser(), FirebaseEntity.EntityRole.NONE, FirebaseEntity.EntityStatus.AWAITING);
                                    }
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // Display Error
                }
            }
        });
    }

    private void addUserToTeam(final DialogInterface dialog, final Team teamItem, final User user, final FirebaseEntity.EntityRole memberRole, final FirebaseEntity.EntityStatus memberStatus) {
        teamItem.addUser(user,memberRole,memberStatus);
        FirebaseHelper.update(getContext(),user);
        FirebaseHelper.update(getContext(),teamItem);
        // Alert User that the team has been alerted of your request

        // TODO: Send Notification To Team
//      sendNotification(user, Notification.NotificationVerbType.REQUEST, teamItem);

        dialog.dismiss();
    }

    private void setupDialogLayout(final View containingView, final TextInputLayout inputLayout, final TextInputEditText usernameEditText) {
        // Create LinearLayout to add TextInputLayout with Edit Text
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
}
