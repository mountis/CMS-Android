package com.marionthefourth.augimas.dialogs;

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
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.sendNotification;

public final class InviteMemberDialog extends AlertDialog.Builder {
    public InviteMemberDialog(final View containingView) {
        super(containingView.getContext());
        setupDialog(containingView);
    }

    private void setupDialog(final View containingView) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_invite_member));

        // Add Username or Email Field
        final TextInputLayout usernameLayout = new TextInputLayout(getContext());
        final TextInputEditText usernameOrEmailEditText = new TextInputEditText(getContext());
        final AppCompatSpinner memberRoleSpinner = new AppCompatSpinner(containingView.getContext());
        setupDialogLayout(containingView,usernameLayout,usernameOrEmailEditText,memberRoleSpinner);

        // Set Positive "Email Me" Button
        setupPositiveButton(usernameOrEmailEditText,memberRoleSpinner);

        // Showing Alert Message
        show();

    }

    private void setupPositiveButton(final TextInputEditText usernameOrEmailEditText, final AppCompatSpinner memberRoleSpinner) {
        setPositiveButton(R.string.invite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Check that text field is filled
                if (!usernameOrEmailEditText.getText().equals("")) {
                    // Check against firebase
                    FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                User currentUser = null;
                                User invitedUser = null;

                                for (DataSnapshot userReference:dataSnapshot.getChildren()) {
                                    final User userItem = new User(userReference);

                                    if (getCurrentUser().getUID().equals(userItem.getUID())) {
                                        currentUser = userItem;

                                        if (currentUser.getUsername().equals(usernameOrEmailEditText.getText()) || currentUser.getEmail().equals(usernameOrEmailEditText.getText())) {
                                            // Error, you can't invite yourself
                                            // TODO: Display Error Stating User Cannot Invite Themselves
                                            dialog.dismiss();
                                            return;
                                        }
                                    }

                                    // If Team Matches Input, Add User to Member's List
                                    if (userItem.getUsername().equals(usernameOrEmailEditText.getText()) || userItem.getEmail().equals(usernameOrEmailEditText.getText())) {
                                        // Add User to Team
                                        invitedUser = userItem;
                                    }
                                }
                                if (invitedUser == null) {
                                    // TODO: Display Error Stating User Doesn't Exist
                                    dialog.dismiss();
                                    return;
                                }

                                final User currentUserItem = currentUser;
                                final User invitedUserItem = invitedUser;
                                FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            final Team teamItem = new Team(dataSnapshot);
                                            addUserToTeam(dialog, teamItem, currentUserItem, invitedUserItem, memberRoleSpinner);
                                        } else {
                                            // Error
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Error
                                    }
                                });

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

    private void addUserToTeam(final DialogInterface dialog, final Team teamItem, final User currentUserItem, final User invitedUserItem, final AppCompatSpinner memberRoleSpinner) {

        FirebaseEntity.EntityRole role = FirebaseEntity.EntityRole.NONE;
        FirebaseEntity.EntityStatus status = FirebaseEntity.EntityStatus.AWAITING;
        Notification.NotificationVerbType verb = Notification.NotificationVerbType.REQUEST;

        if (currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
            status = FirebaseEntity.EntityStatus.APPROVED;
            role = (FirebaseEntity.EntityRole) memberRoleSpinner.getSelectedItem();
            verb = Notification.NotificationVerbType.JOIN;
        }

        invitedUserItem.setType(currentUserItem.getType());

        teamItem.addUser(invitedUserItem, role, status);
        FirebaseHelper.update(getContext(),invitedUserItem);
        FirebaseHelper.update(getContext(),teamItem);
        // Alert User that the team has been alerted of your request

        sendNotification(getContext(),invitedUserItem,verb,teamItem);

        dialog.dismiss();
    }

    private void setupDialogLayout(final View containingView, final TextInputLayout inputLayout, final TextInputEditText usernameEditText, final AppCompatSpinner memberRoleSpinner) {
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

        FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = new User(dataSnapshot);

                    if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                        memberRoleSpinner.setLayoutParams(layoutParams);
                        ArrayList<FirebaseEntity.EntityRole> entityRoles = FirebaseEntity.EntityRole.getAllRoles();
                        entityRoles.remove(FirebaseEntity.EntityRole.DEFAULT);

                        if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.OWNER)) {
                            entityRoles.remove(FirebaseEntity.EntityRole.OWNER);
                        }

                        entityRoles.remove(FirebaseEntity.EntityRole.DEFAULT);
                        ArrayAdapter<FirebaseEntity.EntityRole> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, FirebaseEntity.EntityRole.getAllRoles());
                        memberRoleSpinner.setAdapter(adapter);
                        layout.addView(memberRoleSpinner);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setView(layout);
    }
}
