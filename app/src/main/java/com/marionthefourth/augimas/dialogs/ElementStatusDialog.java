package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.BrandingElementsAdapter;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.send;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

public final class ElementStatusDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public ElementStatusDialog(final BrandingElementsAdapter.ViewHolder holder, final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(holder, containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final BrandingElementsAdapter.ViewHolder holder, final View view, final Activity activity) {
        setTitle(getContext().getString(R.string.title_element_status_updater));

        ArrayList<AppCompatButton> buttons = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            buttons.add(new AppCompatButton(getContext()));
        }

        setupButtons(buttons, holder, activity);
        setupLayout(view,buttons);

        show();
    }
    private void setupLayout(final View view, final ArrayList<AppCompatButton> buttons) {
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        for(AppCompatButton button:buttons) {
            layout.addView(button);
        }

        setView(layout);
    }
    private void setupButtons(final ArrayList<AppCompatButton> buttons, final BrandingElementsAdapter.ViewHolder holder, final Activity activity) {
        final AlertDialog dialog = this.create();

        for (int i = 0; i < buttons.size();i++) {
            final int finalI = i;
            buttons.get(i).setText(BrandingElement.ElementStatus.getStatus(i).toVerb());

            if (holder.elementItem.getStatus().toInt(false) == i) {
                buttons.get(i).setVisibility(View.GONE);
            }

            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.ElementStatus.getStatus(finalI).toDrawable(getContext()));

                    if (!PROTOTYPE_MODE) {
                        Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(holder.elementItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                                elementItem.setStatus(BrandingElement.ElementStatus.getStatus(finalI));
                                update(elementItem, activity);
                                sendNotifications(holder.elementItem, activity);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }

                }
            });
        }
    }
//    Functional Methods
    private void sendNotifications(final BrandingElement elementItem, final Activity activity) {
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (!currentUser.getTeamUID().equals("")) {
                        final Notification userUpdatedStatusNotification = new Notification();
                        final Notification teamUpdatedStatusNotification = new Notification();

                        userUpdatedStatusNotification.setSubject(currentUser);
                        userUpdatedStatusNotification.setSubjectType(Notification.NotificationSubjectType.MEMBER);

                        Notification.NotificationVerbType verb = Notification.NotificationVerbType.toVerbType(elementItem.getStatus());

                        userUpdatedStatusNotification.setVerbType(verb);
                        teamUpdatedStatusNotification.setVerbType(verb);

                        userUpdatedStatusNotification.setObject(elementItem);
                        userUpdatedStatusNotification.setObjectType(Notification.NotificationObjectType.BRANDING_ELEMENT);
                        teamUpdatedStatusNotification.setObject(elementItem);
                        teamUpdatedStatusNotification.setObjectType(Notification.NotificationObjectType.BRANDING_ELEMENT);

                        // Send modifying notification to both Teams
                        Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    for (final Team teamItem:Team.toArrayList(dataSnapshot)) {
                                        // Get the Client's Team & Host's Team
                                        if (teamItem.getUID().equals(elementItem.getTeamUID()) && elementItem.getTeamUID().equals(currentUser.getTeamUID())) {
                                            // The Client Team Modified It
                                            userUpdatedStatusNotification.getReceiverUIDs().add(teamItem.getUID());
                                            Backend.sendUpstreamNotification(userUpdatedStatusNotification, teamItem.getUID());
                                        } else if (teamItem.getType().equals(FirebaseEntity.EntityType.HOST) && elementItem.getTeamUID().equals(currentUser.getTeamUID())) {
                                            // The Admin Team Modified It
                                            teamUpdatedStatusNotification.setSubject(currentUser);
                                            teamUpdatedStatusNotification.getReceiverUIDs().add(teamItem.getUID());
                                            Backend.sendUpstreamNotification(userUpdatedStatusNotification, teamItem.getUID());
                                        }
                                    }
                                    send(activity,teamUpdatedStatusNotification);
                                    send(activity,userUpdatedStatusNotification);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}