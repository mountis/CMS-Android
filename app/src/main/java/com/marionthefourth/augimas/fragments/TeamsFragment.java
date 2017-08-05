package com.marionthefourth.augimas.fragments;

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
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.TeamsAdapter;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.backend.Backend;

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
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Load User Contacts from Firebase
            final Activity activity = getActivity();
            if (Constants.Bools.PROTOTYPE_MODE) {
                loadPrototypeTeams(recyclerView);
            } else {
                loadTeams(Backend.getCurrentUser(), recyclerView, activity);
            }

        }
        return view;
    }
//    Population Methods
    private void loadPrototypeTeams(final RecyclerView recyclerView) {
        final ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Google","google"));
        teams.add(new Team("AOL","aol") );
        teams.add(new Team("Walmart","walmart"));

//        recyclerView.setAdapter(new TeamsAdapter(activity,teams,teamListener));
    }
    private void loadTeams(final User user, final RecyclerView recyclerView, final Activity activity) {
        if (user != null) {
            Backend.getReference(R.string.firebase_teams_directory, activity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        final ArrayList<Team> teamItems = Team.toFilteredArrayList(dataSnapshot,Constants.Strings.Fields.ENTITY_TYPE,FirebaseEntity.EntityType.CLIENT.toString());

                        if (teamItems.size() == 0) {
                            recyclerView.setAdapter(null);
                        } else {
                            recyclerView.setAdapter(new TeamsAdapter(activity,teamItems));
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
}