package com.marionthefourth.augimas.fragments;

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
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.classes.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

public class TeamsFragment extends Fragment {

    private OnTeamsFragmentInteractionListener mListener;

    public TeamsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_teams, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // Load User Contacts from Firebase
            if (Constants.Bools.PROTOTYPE_MODE) {
                loadPrototypeTeams(recyclerView);
            } else {
                loadTeams(view, recyclerView, FirebaseHelper.getCurrentUser());
            }

        }
        return view;
    }

    private void loadPrototypeTeams(final RecyclerView recyclerView) {
        final ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Google","google"));
        teams.add(new Team("AOL","aol") );
        teams.add(new Team("Walmart","walmart"));

        recyclerView.setAdapter(new TeamsAdapter(getContext(),teams,mListener));
    }

    private void loadTeams(final View view, final RecyclerView recyclerView, final User user) {
        if (user != null) {
            FirebaseHelper.getReference(
                    view.getContext(),
                    R.string.firebase_teams_directory
            ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        final ArrayList<Team> teamItems = new ArrayList<>();
                        // Get contacts that belong to the User
                        for (DataSnapshot contactReference : dataSnapshot.getChildren()) {
                            Team teamItem = new Team(contactReference);
                            if (teamItem.getUserUIDs().equals(user.getUID())) {
                                teamItems.add(new Team(contactReference));
                            }
                        }

                        if (teamItems.size() == 0) {
                            recyclerView.setAdapter(null);
                        } else {
                            // Get User Information for each Contact
//                            getCorrespondingUsersForContacts(view, recyclerView, teamItems);
                        }
                    } else {
                        recyclerView.setAdapter(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTeamsFragmentInteractionListener) {
            mListener = (OnTeamsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTeamsFragmentInteractionListener {
        void onTeamsFragmentInteraction(Context context, Team teamItem);
    }

}