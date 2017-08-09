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
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

public final class TeamStatusDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public TeamStatusDialog(final Team teamItem, final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(teamItem, containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final Team teamItem, View view, final Activity activity) {
        setTitle(getContext().getString(R.string.title_team_status_updater));

        ArrayList<AppCompatButton> buttons = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            buttons.add(new AppCompatButton(getContext()));
        }

        setupButtons(buttons, teamItem, activity);
        setupLayout(buttons, view);

        show();
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
    private void setupButtons(final ArrayList<AppCompatButton> buttons, final Team teamItem, final Activity activity) {
        for (int i = 0; i < buttons.size();i++) {
            switch (i) {
                case 0: buttons.get(i).setText(FirebaseEntity.EntityStatus.APPROVED.toVerb()); break;
                case 1: buttons.get(i).setText(FirebaseEntity.EntityStatus.BLOCKED.toVerb()); break;
            }

            buttons.get(i).setTextColor(Color.WHITE);
            setupButtonsOnClickListeners(buttons.get(i),i,teamItem,activity);
        }
    }
    private void setupButtonsOnClickListeners(final AppCompatButton button, final int buttonIndex, final Team teamItem, final Activity activity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.getReference(R.string.firebase_users_directory, getContext()).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User currentUser = new User(dataSnapshot);

                        Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayMap<FirebaseEntity.EntityType,Team> teamMap = Team.toClientAndHostTeamMap(dataSnapshot,teamItem.getUID());
                                teamMap.get(FirebaseEntity.EntityType.CLIENT).setStatus(FirebaseEntity.EntityStatus.getVerbStatus(buttonIndex));
                                update(teamMap.get(FirebaseEntity.EntityType.CLIENT), activity);

                                final Notification.NotificationVerbType verbType;
                                switch (buttonIndex) {
                                    case 0: verbType = Notification.NotificationVerbType.APPROVE; break;
                                    default: verbType = Notification.NotificationVerbType.BLOCK; break;
                                }

                                final Notification hostNotification = new Notification(currentUser,teamItem, verbType);
                                final Notification clientNotification = new Notification(teamMap.get(FirebaseEntity.EntityType.HOST),teamItem, verbType);

                                Backend.sendUpstreamNotification(hostNotification, currentUser.getTeamUID(), currentUser.getUID(),Constants.Strings.Headers.STATUS_UPDATED, activity);
                                Backend.sendUpstreamNotification(clientNotification, teamItem.getUID(), currentUser.getUID(), Constants.Strings.Headers.STATUS_UPDATED, activity);
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
        });
    }
}