package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.objects.FirebaseEntity.EntityStatus.APPROVED;
import static com.marionthefourth.augimas.classes.objects.FirebaseEntity.EntityStatus.AWAITING;
import static com.marionthefourth.augimas.classes.objects.FirebaseEntity.EntityStatus.BLOCKED;

public final class TeamStatusDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public TeamStatusDialog(final Team teamItem, final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(teamItem, containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final Team teamItem, View view, final Activity activity) {
        setTitle(getContext().getString(R.string.title_team_status_updater));
        setupButtons(new ArrayList<AppCompatButton>(), teamItem, view, activity);
    }
    private void setupLayout(final ArrayList<AppCompatButton> buttons, final View view) {
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);
        for(AppCompatButton button:buttons) {
            layout.addView(button);
        }
        setView(layout);
    }
    private void setupButtons(final ArrayList<AppCompatButton> buttons, final Team teamItem, final View view, final Activity activity) {
        Backend.getReference(R.string.firebase_teams_directory,activity).child(teamItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Team currentTeam = new Team(dataSnapshot);
                    if (currentTeam.getStatus() == AWAITING) {
                        buttons.add(new AppCompatButton(activity));
                        buttons.get(0).setText(APPROVED.toVerb());
                        buttons.get(0).setTextColor(Color.WHITE);
                        setupButtonsOnClickListeners(buttons.get(0), teamItem,activity);
                        setupLayout(buttons, view);
                    }
                    show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void setupButtonsOnClickListeners(final AppCompatButton button, final Team teamItem, final Activity activity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getCurrentUser() != null ? getCurrentUser().getUID():null)!= null) {
                    Backend.getReference(R.string.firebase_users_directory, getContext()).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userSnapshot) {
                            final User currentUser = new User(userSnapshot);
                            Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot teamSnapshot) {
                                    final ArrayMap<FirebaseEntity.EntityType,Team> teamMap = Team.toClientAndHostTeamMap(teamSnapshot,teamItem.getUID());
                                    final RecentActivity.ActivityVerbType verbType;
                                    if (button.getText().toString().equals(APPROVED.toVerb())) {
                                        verbType = RecentActivity.ActivityVerbType.APPROVE;
                                        teamMap.get(FirebaseEntity.EntityType.CLIENT).setStatus(APPROVED);
                                    } else {
                                        verbType = RecentActivity.ActivityVerbType.BLOCK;
                                        teamMap.get(FirebaseEntity.EntityType.CLIENT).setStatus(BLOCKED);
                                    }
                                    update(teamMap.get(FirebaseEntity.EntityType.CLIENT), activity);

                                    sendNotification(currentUser,verbType,teamItem,teamMap,activity);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }
    private void sendNotification(final User currentUser, final RecentActivity.ActivityVerbType verbType, final Team teamItem, final ArrayMap<FirebaseEntity.EntityType, Team> teamMap, final Activity activity) {
        final RecentActivity hostRecentActivity = new RecentActivity(currentUser,teamItem, verbType);
        final RecentActivity clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),teamItem, verbType);
        Backend.sendUpstreamNotification(hostRecentActivity, currentUser.getTeamUID(), currentUser.getUID(), Constants.Strings.Headers.STATUS_UPDATED, activity, true);
        Backend.sendUpstreamNotification(clientRecentActivity, teamItem.getUID(), currentUser.getUID(), Constants.Strings.Headers.STATUS_UPDATED, activity, true);
    }
}