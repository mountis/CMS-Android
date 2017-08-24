package com.augimas.android.fragments;

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
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.adapters.RecentActivitiesAdapter;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;

import java.util.ArrayList;
import java.util.Collections;

import static com.augimas.android.backend.Backend.getCurrentUser;

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
        loadRecentActivityData(recyclerView, view, activity);

        recyclerView.setVisibility(View.VISIBLE);

        return view;
    }

    private void loadRecentActivityData(final RecyclerView recyclerView, final View view, final Activity activity) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User(userSnapshot);
                        setupActionBarName(currentUser,activity);
                        if (!currentUser.getTeamUID().equals("") && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                            Backend.getReference(R.string.firebase_recent_activities_directory, activity).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot recentActivitySnapshot) {
                                    if (recentActivitySnapshot.hasChildren()) {
                                        final ArrayList<RecentActivity> filteredRecentActivities = new ArrayList<>();
                                        for (final RecentActivity recentActivityItem : RecentActivity.toArrayList(recentActivitySnapshot)) {
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
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        } else {
                            recyclerView.setAdapter(null);
                            view.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void setupActionBarName(final User currentUser, final Activity activity) {
        Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot teamSnapshot) {
                if (teamSnapshot.exists()) {
                    final Team teamItem = new Team(teamSnapshot);
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
}