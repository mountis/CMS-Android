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
    public ElementStatusDialog(final Activity activity, final View containingView, final BrandingElementsAdapter.ViewHolder holder) {
        super(containingView.getContext());
        setupDialog(activity,containingView, holder);
    }

    private void setupDialog(final Activity activity, View view, BrandingElementsAdapter.ViewHolder holder) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_element_status_updater));

        // Add Button Fields
        ArrayList<AppCompatButton> buttons = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            buttons.add(new AppCompatButton(getContext()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupButtons(activity,buttons,holder);
        }

        setupLayout(view,buttons);

        // Showing Alert Message
        show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupButtons(final Activity activity, final ArrayList<AppCompatButton> buttons, final BrandingElementsAdapter.ViewHolder holder) {
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
                        Backend.getReference(activity,R.string.firebase_branding_elements_directory).child(holder.elementItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                                elementItem.setStatus(BrandingElement.ElementStatus.getStatus(finalI));
                                update(activity,elementItem);
                                sendNotifications(activity,holder.elementItem);
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

    private void sendNotifications(final Activity activity, final BrandingElement elementItem) {
        Backend.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        final Notification userUpdatedStatusNotification = new Notification();
                        final Notification teamUpdatedStatusNotification = new Notification();

                        userUpdatedStatusNotification.setSubject(currentUser);
                        userUpdatedStatusNotification.setSubjectType(Notification.NotificationSubjectType.MEMBER);

                        Notification.NotificationVerbType verb;
                        switch (elementItem.getStatus()) {
                            case APPROVED:
                                verb = Notification.NotificationVerbType.APPROVE;
                                break;
                            case AWAITING:
                                verb = Notification.NotificationVerbType.AWAIT;
                                break;
                            case INCOMPLETE:
                                verb = Notification.NotificationVerbType.DISAPPROVE;
                                break;
                            case NONE:
                                verb = Notification.NotificationVerbType.UPDATE;
                                break;
                            default:
                                verb = Notification.NotificationVerbType.DEFAULT;
                                break;
                        }

                        userUpdatedStatusNotification.setVerbType(verb);
                        teamUpdatedStatusNotification.setVerbType(verb);

                        userUpdatedStatusNotification.setObject(elementItem);
                        userUpdatedStatusNotification.setObjectType(Notification.NotificationObjectType.BRANDING_ELEMENT);
                        teamUpdatedStatusNotification.setObject(elementItem);
                        teamUpdatedStatusNotification.setObjectType(Notification.NotificationObjectType.BRANDING_ELEMENT);

                        // Send modifying notification to both Teams
                        Backend.getReference(activity,R.string.firebase_teams_directory).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                        final Team teamItem = new Team(teamReference);
                                        // Get the Client's Team
                                        if (teamItem.getUID().equals(elementItem.getTeamUID())) {
                                            if (elementItem.getTeamUID().equals(currentUser.getTeamUID())) {
                                                // The Client Team Modified It
                                                userUpdatedStatusNotification.getReceiverUIDs().add(teamItem.getUID());
                                            }
                                        // Get the Admin's Team
                                        } else if (teamItem.getType().equals(FirebaseEntity.EntityType.US)) {
                                            if (elementItem.getTeamUID().equals(currentUser.getTeamUID())) {
                                                // The Admin Team Modified It
                                                teamUpdatedStatusNotification.setSubject(currentUser);
                                                teamUpdatedStatusNotification.setSubjectType(Notification.NotificationSubjectType.MEMBER);
                                                teamUpdatedStatusNotification.getReceiverUIDs().add(teamItem.getUID());
                                            }
                                        }

                                    }

                                    send(activity,teamUpdatedStatusNotification);
                                    send(activity,userUpdatedStatusNotification);



                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupLayout(final View view, final ArrayList<AppCompatButton> buttons) {
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        for (int i = 0; i < buttons.size();i++) {
            layout.addView(buttons.get(i));
        }
        setView(layout);
    }
}