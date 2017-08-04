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
    public InviteMemberDialog(final Activity activity, final View containingView) {
        super(containingView.getContext());
        setupDialog(activity, containingView);
    }

    private void setupDialog(final Activity activity, final View containingView) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_invite_member));

        // Add Username or Email Field
        final TextInputLayout usernameLayout = new TextInputLayout(getContext());
        final TextInputEditText usernameOrEmailEditText = new TextInputEditText(getContext());
        final AppCompatSpinner memberRoleSpinner = new AppCompatSpinner(containingView.getContext());
        setupDialogLayout(activity,containingView,usernameLayout,usernameOrEmailEditText,memberRoleSpinner);

        // Set Positive "Email Me" Button
        setupPositiveButton(activity, usernameOrEmailEditText,memberRoleSpinner);

        // Showing Alert Message
        show();
    }

    private void setupPositiveButton(final Activity activity, final TextInputEditText usernameOrEmailEditText, final AppCompatSpinner memberRoleSpinner) {
        setPositiveButton(R.string.invite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Check that text field is filled
                if (!usernameOrEmailEditText.getText().equals("")) {
                    // Check against firebase
                    Backend.getReference(activity,R.string.firebase_users_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                User currentUser = null;
                                User invitedUser = null;

                                for (DataSnapshot userReference:dataSnapshot.getChildren()) {
                                    final User userItem = new User(userReference);

                                    if (getCurrentUser().getUID().equals(userItem.getUID())) {
                                        currentUser = userItem;

                                        if (currentUser.getUsername().equals(usernameOrEmailEditText.getText().toString()) || currentUser.getEmail().equals(usernameOrEmailEditText.getText().toString())) {
                                            // Error, you can't invite yourself
                                            dialog.dismiss();
                                            FragmentHelper.display(activity.findViewById(R.id.container),TOAST,R.string.you_cant_invite_yourself);
                                            return;
                                        }
                                    }

                                    // If Team Matches Input, Add User to Member's List
                                    if (userItem.getUsername().equals(usernameOrEmailEditText.getText().toString()) || userItem.getEmail().equals(usernameOrEmailEditText.getText().toString())) {
                                        // Add User to Team
                                        if (!userItem.getTeamUID().equals("")) {
                                            dialog.dismiss();
                                            FragmentHelper.display(activity.findViewById(R.id.container),TOAST,R.string.this_user_is_already_in_a_team);
                                            return;
                                        } else {
                                            invitedUser = userItem;
                                        }

                                    }

                                    if (invitedUser != null && currentUser != null) {
                                        break;
                                    }

                                }

                                // No User Found
                                if (invitedUser == null) {
                                    dialog.dismiss();
                                    FragmentHelper.display(activity.findViewById(R.id.container),TOAST,R.string.no_user_found);
                                    return;
                                }

                                final User currentUserItem = currentUser;
                                final User invitedUserItem = invitedUser;

                                if (currentUserItem != null && invitedUserItem != null) {
                                    Backend.getReference(activity,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                final Team teamItem = new Team(dataSnapshot);
                                                addUserToTeam(activity,dialog, teamItem, currentUserItem, invitedUserItem, memberRoleSpinner);
                                            } else {
                                                // Error
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Error
                                        }
                                    });
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

    private void addUserToTeam(final Activity activity, final DialogInterface dialog, final Team teamItem, final User currentUserItem, final User invitedUserItem, final AppCompatSpinner memberRoleSpinner) {

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
        Backend.update(activity,invitedUserItem);
        Backend.update(activity,teamItem);
        // Alert User that the team has been alerted of your request
        FragmentHelper.display(activity.findViewById(R.id.container),TOAST,R.string.you_added_to_the_team);
        sendNotification(activity,invitedUserItem,verb,teamItem);

        dialog.dismiss();
    }

    private void setupDialogLayout(final Activity activity, final View containingView, final TextInputLayout inputLayout, final TextInputEditText usernameEditText, final AppCompatSpinner memberRoleSpinner) {
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

        Backend.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
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
