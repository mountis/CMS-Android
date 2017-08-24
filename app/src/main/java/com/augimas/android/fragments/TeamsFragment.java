package com.augimas.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.adapters.TeamsAdapter;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.entities.Team;

import java.util.ArrayList;

public class TeamsFragment extends Fragment {
//    Fragment Constructor
    public TeamsFragment() {}
//    Fragment Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_teams, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final Activity activity = getActivity();
            loadTeams(recyclerView, activity);
        }
        return view;
    }
//    Population Methods
    private void loadTeams(final RecyclerView recyclerView, final Activity activity) {
        Backend.getReference(R.string.firebase_teams_directory, activity).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final ArrayList<Team> teamItems = Team.toFilteredArrayList(dataSnapshot,Constants.Strings.Fields.ENTITY_TYPE,FirebaseEntity.EntityType.CLIENT.toString());

                    if (teamItems.size() == 0) {
                        recyclerView.setAdapter(null);
                    } else {
                        recyclerView.setAdapter(new TeamsAdapter(teamItems, activity));
                    }
                } else {
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                recyclerView.setAdapter(null);
            }
        });
    }
}