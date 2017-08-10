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

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.backend.Backend.sendNotification;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.classes.constants.Constants.Strings.HOST_REQUEST_CODE;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class HostRequestDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public HostRequestDialog(final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final View containingView, final Activity activity) {
        setTitle(getContext().getString(R.string.title_admin_request));

        setIcon(R.drawable.ic_person);

        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        final ArrayList<TextInputEditText> inputs = new ArrayList<>();

        setupHostRequestDialogLayouts(layouts,inputs);
        setupHostRequestPositiveButton(inputs, containingView, activity);

        show();
    }
    private void setupHostRequestDialogLayouts(final ArrayList<TextInputLayout> layouts, ArrayList<TextInputEditText> inputs) {
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
    private void setupHostRequestPositiveButton(final ArrayList<TextInputEditText> inputs, final View containingView, final Activity activity) {
        setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Constants.Bools.FeaturesAvailable.REQUEST_ADMIN_ROLE) {
                    if (inputs.get(0).getText().toString().equals(HOST_REQUEST_CODE) ||
                            inputs.get(0).getText().toString().equals(Constants.Strings.ADMIN_ACCESS_CODE)) {
                        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final User currentUser = new User(dataSnapshot);
                                    Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                for(final Team teamItem:Team.toArrayList(dataSnapshot)) {
                                                    if (currentUser.getTeamUID().equals(teamItem.getUID())) {
                                                        // TODO: Display Error Stating User Is Already On Team or Awaiting Approval
                                                        return;
                                                    } else {
                                                        addUserToAdminTeamBasedOnAdminCode(currentUser, teamItem, inputs, activity);
                                                    }
                                                }
                                            } else {
                                                createHostTeam(currentUser, activity);
                                            }

                                            display(TOAST, R.string.welcome_to_augimas, containingView);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else {
                        display(TOAST, R.string.failed_request, containingView);
                    }
                } else {

                }
            }

        });

    }
    //    Functional Methods
    private void createHostTeam(final User currentUser, final Activity activity) {
        final Team hostTeam = new Team(Constants.Strings.ADMIN_TEAM_NAME,Constants.Strings.ADMIN_TEAM_USERNAME, FirebaseEntity.EntityType.HOST);
        hostTeam.setType(FirebaseEntity.EntityType.HOST);
        hostTeam.setStatus(FirebaseEntity.EntityStatus.APPROVED);
        hostTeam.addUser(currentUser, FirebaseEntity.EntityRole.OWNER, FirebaseEntity.EntityStatus.APPROVED);

        Backend.create(hostTeam, activity);
        currentUser.setTeamUID(hostTeam.getUID());
        update(currentUser, activity);

        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,hostTeam.getUID());
    }
    private void addUserToAdminTeamBasedOnAdminCode(final User currentUser, final Team hostTeam, final ArrayList<TextInputEditText> inputs, final Activity activity) {
        if (inputs.get(0).getText().toString().equals(HOST_REQUEST_CODE)) {
            // Requesting Addition to Admin Team
            hostTeam.addUser(currentUser, FirebaseEntity.EntityRole.NONE, FirebaseEntity.EntityStatus.AWAITING);
            Backend.sendUpstreamNotification(sendNotification(hostTeam, currentUser, RecentActivity.NotificationVerbType.REQUEST,activity), hostTeam.getUID(), currentUser.getUID(),Constants.Strings.Headers.USER_REQUEST, activity, true);
        } else {
            // Bypass, Adds to Admin Team as Owner
            hostTeam.addUser(currentUser, FirebaseEntity.EntityRole.OWNER, FirebaseEntity.EntityStatus.APPROVED);
            sendNotification(hostTeam, currentUser, RecentActivity.NotificationVerbType.JOIN, activity);
        }

        update(hostTeam, activity);
        update(currentUser, activity);

        Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,hostTeam.getUID());
    }
}