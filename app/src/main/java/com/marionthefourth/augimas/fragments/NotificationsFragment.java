package com.marionthefourth.augimas.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.NotificationsAdapter;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

public final class NotificationsFragment extends Fragment {

    public NotificationsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            if (PROTOTYPE_MODE) {
                loadPrototypeNotifications(recyclerView);
            } else {
                loadNotificationData(recyclerView);
            }
        }

        return view;
    }

    private void loadNotificationData(final RecyclerView recyclerView) {

        FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("") && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                        FirebaseHelper.getReference(getContext(),R.string.firebase_notifications_directory).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    final ArrayList<Notification> notifications = new ArrayList<>();
                                    for (DataSnapshot notificationReference:dataSnapshot.getChildren()) {
                                        final Notification notificationItem = new Notification(notificationReference);
                                        if (notificationItem.goesToUID(currentUser.getTeamUID())) {
                                            notifications.add(notificationItem);
                                        }

                                    }
                                    recyclerView.setAdapter(new NotificationsAdapter(getContext(),notifications));

                                } else {
                                    recyclerView.setAdapter(null);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        recyclerView.setAdapter(null);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void loadPrototypeNotifications(RecyclerView recyclerView) {
        Team google = new Team("Google","google");
        Team walmart = new Team("Walmart","walmart");
        Team aol = new Team("AOL","aol");

        // Display a Requesting Approval Notification
        // Display a Updated Mission Statement Notification
        // Display a Client Approved Element Notification

        ArrayList<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(google, Notification.NotificationVerbType.APPROVE));
        notifications.add(new Notification(walmart, Notification.NotificationVerbType.UPDATE, BrandingElement.ElementType.MISSION_STATEMENT));
        notifications.add(new Notification(aol, Notification.NotificationVerbType.APPROVE, BrandingElement.ElementType.DOMAIN_NAME));

        recyclerView.setAdapter(new NotificationsAdapter(getContext(),notifications));

    }

}
