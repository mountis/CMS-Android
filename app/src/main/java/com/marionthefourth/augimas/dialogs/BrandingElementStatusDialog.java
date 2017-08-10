package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
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
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

public final class BrandingElementStatusDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public BrandingElementStatusDialog(final BrandingElementsAdapter.ViewHolder holder, final View containingView, final Activity activity) {
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
                @Override
                public void onClick(View v) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.ElementStatus.getStatus(finalI).toDrawable(getContext()));
                    if (!PROTOTYPE_MODE) {
                        Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(holder.elementItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);

                                elementItem.setStatus(BrandingElement.ElementStatus.fromVerb(buttons.get(finalI).getText().toString()));
                                holder.elementItem = elementItem;

                                update(elementItem, activity);
                                sendStatusDialogNotifications(holder.elementItem, activity);
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
    private void sendStatusDialogNotifications(final BrandingElement elementItem, final Activity activity) {
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (!currentUser.getTeamUID().equals("")) {
                        final RecentActivity.NotificationVerbType verb = RecentActivity.NotificationVerbType.toVerbType(elementItem.getStatus());

                        // Send modifying notification to both Teams
                        Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    final ArrayMap<FirebaseEntity.EntityType,Team> teamArrayMap = Team.toClientAndHostTeamMap(dataSnapshot,elementItem.getTeamUID());

                                    final RecentActivity hostRecentActivity;
                                    final RecentActivity clientRecentActivity;

                                    if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                        hostRecentActivity = new RecentActivity(currentUser,elementItem,verb,teamArrayMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                        clientRecentActivity = new RecentActivity(teamArrayMap.get(currentUser.getType()),elementItem,verb);
                                    } else {
                                        hostRecentActivity = new RecentActivity(teamArrayMap.get(currentUser.getType()),elementItem,verb);
                                        clientRecentActivity = new RecentActivity(currentUser,elementItem,verb);
                                    }

                                    Backend.sendUpstreamNotification(hostRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(), currentUser.getUID(), elementItem.getType().toString(), activity, true);
                                    Backend.sendUpstreamNotification(clientRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.CLIENT).getUID(), currentUser.getUID(), elementItem.getType().toString(), activity, true);
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