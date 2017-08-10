package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.adapters.RecentActivitiesAdapter;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;

import java.util.ArrayList;
import java.util.Collections;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;

public final class RecentActivitiesFragment extends Fragment {

    public RecentActivitiesFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_activities, container, false);

        final Activity activity = getActivity();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.notifications_recycler_view);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        final ContentLoadingProgressBar contentProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.notification_progressbar);
        contentProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        contentProgressBar.show();
        if (PROTOTYPE_MODE) {
            loadPrototypeNotifications(activity,recyclerView);
        } else {
            loadNotificationData(view,activity,recyclerView);
        }
        recyclerView.setVisibility(View.VISIBLE);

        return view;
    }

    private void loadNotificationData(final View view, final Activity activity, final RecyclerView recyclerView) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User currentUser = new User(dataSnapshot);

                        Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team teamItem = new Team(dataSnapshot);
                                    final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                                    if (actionBar != null) {
                                        actionBar.setTitle(teamItem.getName());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if (!currentUser.getTeamUID().equals("") && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                            Backend.getReference(R.string.firebase_notifications_directory, activity).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot notificationSnapshot) {
                                    if (notificationSnapshot.hasChildren()) {
                                        final ArrayList<RecentActivity> filteredRecentActivities = new ArrayList<>();
                                        for (final RecentActivity recentActivityItem : RecentActivity.toArrayList(notificationSnapshot)) {
                                            if (recentActivityItem.goesToUID(currentUser.getTeamUID()) && !recentActivityItem.getSubjectUID().equals(currentUser.getTeamUID())) {
                                                filteredRecentActivities.add(recentActivityItem);
                                            }
                                        }

                                        if (filteredRecentActivities.size() > 0) {

                                            Collections.reverse(filteredRecentActivities);
                                            view.findViewById(R.id.no_content).setVisibility(View.GONE);
                                            recyclerView.setAdapter(new RecentActivitiesAdapter(activity, filteredRecentActivities));
                                        } else {
                                            recyclerView.setAdapter(null);
                                            view.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                                        }


                                    } else {
                                        recyclerView.setAdapter(null);
                                        view.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                                    }

                                    ContentLoadingProgressBar contentLoadingProgressBar = (ContentLoadingProgressBar) activity.findViewById(R.id.notification_progressbar);
                                    if (contentLoadingProgressBar != null) {
                                        contentLoadingProgressBar.hide();
                                        contentLoadingProgressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            if (!currentUser.getTeamUID().equals("")) {
                                Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            final Team teamItem = new Team(dataSnapshot);
                                            final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                                            if (actionBar != null) {
                                                actionBar.setTitle(teamItem.getName());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            recyclerView.setAdapter(null);
                            view.findViewById(R.id.no_content).setVisibility(View.VISIBLE);

                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    private void loadPrototypeNotifications(final Activity activity, RecyclerView recyclerView) {
        Team google = new Team("Google","google");
        Team walmart = new Team("Walmart","walmart");
        Team aol = new Team("AOL","aol");

        // Display a Requesting Approval RecentActivity
        // Display a Updated Mission Statement RecentActivity
        // Display a Client Approved Element RecentActivity

        ArrayList<RecentActivity> recentActivities = new ArrayList<>();
        recentActivities.add(new RecentActivity(google, RecentActivity.NotificationVerbType.APPROVE));
        recentActivities.add(new RecentActivity(walmart, RecentActivity.NotificationVerbType.UPDATE, BrandingElement.ElementType.MISSION_STATEMENT));
        recentActivities.add(new RecentActivity(aol, RecentActivity.NotificationVerbType.APPROVE, BrandingElement.ElementType.DOMAIN_NAME));

        recyclerView.setAdapter(new RecentActivitiesAdapter(activity, recentActivities));
    }

}