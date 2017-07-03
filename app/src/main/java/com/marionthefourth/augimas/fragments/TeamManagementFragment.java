package com.marionthefourth.augimas.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.TeamMembersAdapter;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.dialogs.CreateTeamDialog;
import com.marionthefourth.augimas.dialogs.InviteMemberDialog;
import com.marionthefourth.augimas.dialogs.JoinTeamDialog;
import com.marionthefourth.augimas.dialogs.LeaveTeamDialog;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

public class TeamManagementFragment extends Fragment {

    public TeamManagementFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_team_management, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.team_member_recycler_view);
        // Set the adapter
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            if (PROTOTYPE_MODE) {
                loadPrototypeTeamMembers(view, recyclerView);
            } else {
                loadTeam(view, recyclerView, getCurrentUser());
            }
        }

        return view;
    }

    private void loadTeam(final View view, final RecyclerView recyclerView, final User user) {
        if (user != null) {
            FirebaseHelper.getReference(view.getContext(),R.string.firebase_users_directory).child(user.getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User currentUser = new User(dataSnapshot);
                        if (currentUser.getTeamUID().equals("")) {
                            // Display Join & Create Teams
                            populateNoTeamLayout(view);
                            LinearLayoutCompat layout = (LinearLayoutCompat)view.findViewById(R.id.in_team_layout);
                            layout.setVisibility(View.GONE);
                        } else {
                            FirebaseHelper.getReference(view.getContext(),R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                        populateInTeamLayout(view,recyclerView,new Team(dataSnapshot));
                                        LinearLayoutCompat layout = (LinearLayoutCompat)view.findViewById(R.id.no_team_layout);
                                        layout.setVisibility(View.GONE);
                                    } else {
                                        // Display Join & Create Teams
                                        populateNoTeamLayout(view);
                                        LinearLayoutCompat layout = (LinearLayoutCompat)view.findViewById(R.id.in_team_layout);
                                        layout.setVisibility(View.GONE);
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
    }

    private void populateNoTeamLayout(final View view) {
        LinearLayoutCompat layout = (LinearLayoutCompat)view.findViewById(R.id.no_team_layout);
        layout.setVisibility(View.VISIBLE);

        AppCompatButton createTeamButton = (AppCompatButton) view.findViewById(R.id.button_create_team);
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateTeamDialog(view);
            }
        });
        AppCompatButton joinTeamButton = (AppCompatButton) view.findViewById(R.id.button_join_team);
        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JoinTeamDialog(view);
            }
        });
    }

    private void populateInTeamLayout(final View view, final RecyclerView recyclerView, final Team team) {
        final LinearLayoutCompat layout = (LinearLayoutCompat)view.findViewById(R.id.in_team_layout);
        layout.setVisibility(View.VISIBLE);

        final TextInputEditText nameEditText = (TextInputEditText) view.findViewById(R.id.input_team_name);
        nameEditText.setText(team.getName());
        final TextInputEditText usernameEditText = (TextInputEditText) view.findViewById(R.id.input_team_username);
        usernameEditText.setText(team.getUsername());

        final AppCompatButton updateButton = (AppCompatButton) view.findViewById(R.id.button_update_team_info);

        final AppCompatButton inviteMembers = (AppCompatButton)view.findViewById(R.id.button_invite_member);

        final AppCompatButton leaveTeam = (AppCompatButton)view.findViewById(R.id.button_leave_team);

        inviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InviteMemberDialog(view);
            }
        });

        leaveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LeaveTeamDialog(view);
            }
        });

        FirebaseHelper.getReference(view.getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get Text From Fields
                            }
                        });
                    } else {
                        nameEditText.setEnabled(false);
                        usernameEditText.setEnabled(false);
                        updateButton.setVisibility(View.GONE);
                    }

                    if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        inviteMembers.setEnabled(false);
                    }

                    loadTeamMembers(view,recyclerView,currentUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadTeamMembers(final View view, final RecyclerView recyclerView, final User user) {
        if (user != null) {
            FirebaseHelper.getReference(
                    view.getContext(),
                    R.string.firebase_teams_directory
            ).child(user.getTeamUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        final Team teamItem = new Team(dataSnapshot);
                        // Get all team members

                        FirebaseHelper.getReference(view.getContext(),R.string.firebase_users_directory).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    final ArrayList<User> teamMembers = new ArrayList<>();
                                    for (DataSnapshot userReference:dataSnapshot.getChildren()) {
                                        final User userItem = new User(userReference);
                                        if (userItem.isInTeam(teamItem)) {
                                            teamMembers.add(userItem);
                                        }
                                    }
                                    if (teamMembers.size() == 0 || teamItem == null) {
                                        recyclerView.setAdapter(null);
                                    } else {
                                        FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    final User currentUser = new User(dataSnapshot);
                                                    if (currentUser != null) {
                                                        recyclerView.setAdapter(new TeamMembersAdapter(getContext(),teamItem,teamMembers,currentUser));
                                                    }
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

    private void loadPrototypeTeamMembers(final View view, final RecyclerView recyclerView) {
        final ArrayList<User> members = new ArrayList<>();
        members.add(new User("Joel", "Bael", FirebaseEntity.EntityRole.OWNER));
        members.add(new User("Elizabeth", "Jenkins", FirebaseEntity.EntityRole.ADMIN));
        members.add(new User("Will", "Smith", FirebaseEntity.EntityRole.EDITOR));
        members.add(new User("Lowe", "Ende", FirebaseEntity.EntityRole.CHATTER));
        members.add(new User("Khanverse" ,"Aetion", FirebaseEntity.EntityRole.VIEWER));

        final Team google = new Team("Google","172351235",members);
        google.setUsername("google");

        final TextInputEditText teamName = (TextInputEditText)view.findViewById(R.id.input_team_name);
        teamName.setText(google.getName());

        final TextInputEditText teamUsername = (TextInputEditText)view.findViewById(R.id.input_team_username);
        teamUsername.setText(google.getUsername());

        recyclerView.setAdapter(new TeamMembersAdapter(getContext(),google,members, new User()));

    }

}