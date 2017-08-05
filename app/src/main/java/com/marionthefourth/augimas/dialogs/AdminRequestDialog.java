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
import com.google.firebase.messaging.FirebaseMessaging;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.save;
import static com.marionthefourth.augimas.backend.Backend.upstreamNotification;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.classes.constants.Constants.Strings.ADMIN_REQUEST_CODE;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class AdminRequestDialog extends AlertDialog.Builder {


    public AdminRequestDialog(final Activity activity, final View containingView) {
        super(containingView.getContext());
        setupDialog(activity,containingView);
    }

    private void setupDialog(final Activity activity, final View containingView) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_admin_request));

        // Setting Icon to Dialog
        setIcon(R.drawable.ic_person);

        // Create LinearLayout to add TextInputLayouts with EditTexts
        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        final ArrayList<TextInputEditText> inputs = new ArrayList<>();

        setupAdminRequestDialogLayouts(layouts,inputs);


        // Setting Positive "Request" Button
        setupAdminRequestPositiveButton(activity,containingView,inputs);

        show();
    }

    private void setupAdminRequestPositiveButton(final Activity activity, final View containingView, final ArrayList<TextInputEditText> inputs) {
        setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Constants.Bools.FeaturesAvailable.REQUEST_ADMIN_ROLE) {
                    // Connect to Firebase
                    if (inputs.get(0).getText().toString().equals(ADMIN_REQUEST_CODE) ||
                            inputs.get(0).getText().toString().equals(Constants.Strings.ADMIN_ACCESS_CODE)) {
                        Backend.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final User currentUser = new User(dataSnapshot);
                                    Backend.getReference(activity,R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                                    final Team teamItem = new Team(teamReference);

                                                    if (currentUser.getTeamUID().equals(teamItem.getUID())) {
                                                        // TODO: Display Error Stating User Is Already On Team or Awaiting Approval
                                                        return;
                                                    }

                                                    addUserToAdminTeamBasedOnAdminCode(activity,inputs,currentUser,teamItem);
                                                }
                                            } else {
                                                createAdminTeamAndChat(activity,currentUser);
                                            }

                                            update(activity,currentUser);
                                            display(containingView,TOAST,R.string.welcome_to_augimas);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        display(containingView,TOAST,R.string.failed_request);
                    }
                } else {

                }
            }

        });

    }

    private void addUserToAdminTeamBasedOnAdminCode(final Activity activity, final ArrayList<TextInputEditText> inputs, final User currentUser, final Team teamItem) {
        if (inputs.get(0).getText().toString().equals(ADMIN_REQUEST_CODE)) {
            // Requesting Addition to Admin Team
            teamItem.addUser(currentUser, FirebaseEntity.EntityRole.NONE, FirebaseEntity.EntityStatus.AWAITING);
            upstreamNotification(activity,currentUser, Notification.NotificationVerbType.REQUEST,teamItem);
        } else {
            // Bypass, Adds to Admin Team as Owner
            teamItem.addUser(currentUser, FirebaseEntity.EntityRole.OWNER, FirebaseEntity.EntityStatus.APPROVED);
            upstreamNotification(activity,currentUser, Notification.NotificationVerbType.JOIN,teamItem);
        }

        FirebaseMessaging.getInstance().subscribeToTopic(teamItem.getUID());

        update(activity,teamItem);
    }

    private void createAdminTeamAndChat(final Activity activity, final User currentUser) {
        // Create Team
        final Team adminTeam = new Team(Constants.Strings.ADMIN_TEAM_NAME,Constants.Strings.ADMIN_TEAM_USERNAME, FirebaseEntity.EntityType.US);
        adminTeam.setType(FirebaseEntity.EntityType.US);
        adminTeam.setStatus(FirebaseEntity.EntityStatus.APPROVED);
        adminTeam.addUser(currentUser, FirebaseEntity.EntityRole.OWNER, FirebaseEntity.EntityStatus.APPROVED);

        save(activity,adminTeam);


        FirebaseMessaging.getInstance().subscribeToTopic(adminTeam.getUID());


        currentUser.setTeamUID(adminTeam.getUID());
    }

    private void setupAdminRequestDialogLayouts(final ArrayList<TextInputLayout> layouts, ArrayList<TextInputEditText> inputs) {
        final LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        layouts.add(new TextInputLayout(getContext()));
        inputs.add(new TextInputEditText(getContext()));
        layouts.get(0).addView(inputs.get(0), 0, lp);
        layout.addView(layouts.get(0));
        inputs.get(0).setHint(getContext().getString(R.string.request_code));

        setView(layout);
    }

}
