package com.augimas.android.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.adapters.TeamMembersAdapter;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.dialogs.CreateTeamDialog;
import com.augimas.android.dialogs.InviteMemberDialog;
import com.augimas.android.dialogs.JoinTeamDialog;
import com.augimas.android.dialogs.LeaveTeamDialog;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.backend.Backend.update;

public class TeamManagementFragment extends Fragment {
//    Fragment Constructor
    public TeamManagementFragment() { }
    //    Fragment Methods
    public static TeamManagementFragment newInstance(Team teamItem) {
        final Bundle args = new Bundle();
        if (teamItem != null) args.putSerializable(Constants.Strings.TEAM,teamItem);

        TeamManagementFragment fragment = new TeamManagementFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_team_management, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.team_member_recycler_view);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            final Activity activity = getActivity();

            Team teamItem = null;

            if (getArguments() != null) {
                teamItem = (Team) getArguments().getSerializable(Constants.Strings.TEAM);
                if (teamItem != null) {
                    final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(Constants.Strings.Titles.TEAM + " " + teamItem.getName());
                    }
                }
            }

            setupBackButton(teamItem,view,activity);

            if (teamItem != null) {
                loadTeam(teamItem, recyclerView, view, activity);
            } else {
                loadTeam(getCurrentUser(), recyclerView, view, activity);
            }
        }
        return view;
    }
    private void setupBackButton(final Team teamItem, final View view, final Activity activity) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    final FragmentManager rManager = activity.getFragmentManager();
                    final android.support.v4.app.FragmentManager manager = ((AppCompatActivity) activity).getSupportFragmentManager();
                    if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
                        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final User currentUser = new User(dataSnapshot);
                                if (teamItem != null) {
                                    if (currentUser.isInTeam(teamItem)) {
                                        handleTransitionToSettings(manager,rManager);
                                    } else {
                                        if (currentUser.getTeamUID().equals("") || !currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                                            handleTransitionToSettings(manager,rManager);
                                        } else {
                                            handleTransitionToHome(manager);
                                        }
                                    }
                                } else {
                                    handleTransitionToSettings(manager,rManager);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        return true;
                    }
                }
                return false;
            }
        });
    }
    //    Functional Methods
    private void handleTransitionToHome(final android.support.v4.app.FragmentManager manager) {
        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove(TeamManagementFragment.this).commit();
        final Activity activity = getActivity();
        activity.startActivity(new Intent(activity, HomeActivity.class));
    }
    private void handleTransitionToSettings(final android.support.v4.app.FragmentManager manager, final FragmentManager rManager){
        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove(this).commit();
        rManager.beginTransaction().setTransition(
                android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                new SettingsFragment(),
                Constants.Strings.Fragments.SETTINGS).commit();
    }
    private void loadTeam(final Team teamItem, final RecyclerView recyclerView, final View view, Activity activity) {
        populateTeamLayout(teamItem, view, recyclerView, activity);
    }
    private void loadTeam(@Nullable final User currentUser, final RecyclerView recyclerView, final View view, final Activity activity) {
        if ((currentUser != null ? currentUser.getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(currentUser.getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();

                        final User currentUser = new User(userSnapshot);
                        if (currentUser.getTeamUID().equals("")) {
                            populateCreateOrJoinTeamLayout(view, activity);
                        } else {
                            Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot teamSnapshot) {
                                    if (teamSnapshot.exists() && teamSnapshot.hasChildren()) {
                                        final Team currentTeam = new Team(teamSnapshot);
                                        if (actionBar != null) {
                                            actionBar.setTitle(Constants.Strings.Titles.TEAM + " " + currentTeam.getName());
                                        }
                                        populateTeamLayout(currentTeam, view, recyclerView, activity);
                                    } else {
                                        // Display Join & Create Teams
                                        populateCreateOrJoinTeamLayout(view, activity);
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
    private void populateCreateOrJoinTeamLayout(final View view, final Activity activity) {
        view.findViewById(R.id.team_layout).setVisibility(View.GONE);
        view.findViewById(R.id.button_create_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateTeamDialog(view, activity);
            }
        });
        view.findViewById(R.id.button_join_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JoinTeamDialog(view, activity);
            }
        });
        view.findViewById(R.id.create_or_join_team_layout).setVisibility(View.VISIBLE);
    }
    private void populateTeamLayout(final Team teamItem, final View view, final RecyclerView recyclerView, final Activity activity) {
        view.findViewById(R.id.create_or_join_team_layout).setVisibility(View.GONE);
        view.findViewById(R.id.team_layout).setVisibility(View.VISIBLE);
        final AppCompatButton leaveTeam = (AppCompatButton)view.findViewById(R.id.button_leave_team);
        final TextInputEditText teamNameEditText = (TextInputEditText) view.findViewById(R.id.input_team_name);
        final AppCompatButton inviteMembers = (AppCompatButton)view.findViewById(R.id.button_invite_member);
        final AppCompatButton updateButton = (AppCompatButton) view.findViewById(R.id.button_update_team_info);
        final TextInputEditText usernameEditText = (TextInputEditText) view.findViewById(R.id.input_team_username);

        teamNameEditText.setText(teamItem.getName());
        usernameEditText.setText(teamItem.getUsername());

        inviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InviteMemberDialog(teamItem,view, activity);
            }
        });

        leaveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LeaveTeamDialog(view, activity);
            }
        });

        if ((getCurrentUser() != null ? getCurrentUser().toString(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User(userSnapshot);
                        if ((currentUser.isInTeam(teamItem) && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN))
                                || (!currentUser.isInTeam(teamItem) && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR))) {
                            updateButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final ArrayMap<FirebaseEntity.EntityType,Team> teamArrayMap = Team.toClientAndHostTeamMap(dataSnapshot,currentUser.getTeamUID());
                                            final Team currentTeam = teamArrayMap.get(currentUser.getType());
                                            // If Updated Username Field
                                            handleUsernameUpdate(currentUser, usernameEditText, teamItem, teamArrayMap, activity);
                                            // If Updated Team Name Field
                                            handleTeamNameUpdate(currentUser, teamNameEditText, teamItem, teamArrayMap, activity);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            });

                            if (currentUser.getType() == FirebaseEntity.EntityType.HOST && teamItem.getType() != FirebaseEntity.EntityType.HOST) {
                                LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, 0, 0, (int) (40*activity.getResources().getDisplayMetrics().density));
                                inviteMembers.setLayoutParams(lp);
                            }
                        } else {
                            teamNameEditText.setEnabled(false);
                            usernameEditText.setEnabled(false);
                            updateButton.setVisibility(View.GONE);
                            inviteMembers.setEnabled(false);

                            if (currentUser.getType() == FirebaseEntity.EntityType.HOST && teamItem.getType() != FirebaseEntity.EntityType.HOST) {
                                LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, 0, 0, (int) (40*activity.getResources().getDisplayMetrics().density));

                                inviteMembers.setLayoutParams(lp);
                            }
                        }

                        if (!currentUser.isInTeam(teamItem)) {
                            leaveTeam.setEnabled(false);
                            leaveTeam.setVisibility(View.GONE);
                        } else if (!currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                            inviteMembers.setEnabled(false);
                            inviteMembers.setVisibility(View.GONE);
                        }

                        loadTeamMembers(currentUser,teamItem, usernameEditText, teamNameEditText, recyclerView, activity);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void handleTeamNameUpdate(final User currentUser, final TextInputEditText teamNameEditText, final Team modifyingTeamItem, final ArrayMap<FirebaseEntity.EntityType, Team> teamArrayMap, final Activity activity) {
        if (!modifyingTeamItem.getName().equals(teamNameEditText.getText().toString())) {
            final RecentActivity hostRecentActivity;
            final RecentActivity clientRecentActivity;
            if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                if (modifyingTeamItem.getType() == FirebaseEntity.EntityType.HOST) {
                    hostRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_TEAM_NAME,teamNameEditText.getText().toString());
                } else {
                    hostRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_TEAM_NAME,teamNameEditText.getText().toString());
                    clientRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_TEAM_NAME,teamNameEditText.getText().toString());
                    Backend.sendUpstreamNotification(clientRecentActivity,modifyingTeamItem.getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_NAME_UPDATED,activity, true);
                }
                Backend.sendUpstreamNotification(hostRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_NAME_UPDATED,activity, true);
            } else {
                hostRecentActivity = new RecentActivity(modifyingTeamItem,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_TEAM_NAME,teamNameEditText.getText().toString());
                clientRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_TEAM_NAME,teamNameEditText.getText().toString());
                Backend.sendUpstreamNotification(hostRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_NAME_UPDATED,activity, true);
                Backend.sendUpstreamNotification(clientRecentActivity,modifyingTeamItem.getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_NAME_UPDATED,activity, true);
            }
            modifyingTeamItem.setName(teamNameEditText.getText().toString().trim());
            update(modifyingTeamItem, activity);
        }
    }
    private void handleUsernameUpdate(final User currentUser, final TextInputEditText usernameEditText, final Team modifyingTeamItem, final ArrayMap<FirebaseEntity.EntityType, Team> teamArrayMap, final Activity activity) {
        if (!modifyingTeamItem.getUsername().equals(usernameEditText.getText().toString())) {
            final RecentActivity hostRecentActivity;
            final RecentActivity clientRecentActivity;
            if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                if (modifyingTeamItem.getType() == FirebaseEntity.EntityType.HOST) {
                    hostRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_USERNAME,usernameEditText.getText().toString());
                } else {
                    hostRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_USERNAME,usernameEditText.getText().toString());
                    clientRecentActivity = new RecentActivity(teamArrayMap.get(FirebaseEntity.EntityType.HOST),modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_USERNAME,usernameEditText.getText().toString());
                    Backend.sendUpstreamNotification(clientRecentActivity,modifyingTeamItem.getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_USERNAME_UPDATED,activity, true);
                }
                Backend.sendUpstreamNotification(hostRecentActivity,currentUser.getTeamUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_USERNAME_UPDATED,activity, true);
            } else {
                hostRecentActivity = new RecentActivity(modifyingTeamItem,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_USERNAME,usernameEditText.getText().toString());
                clientRecentActivity = new RecentActivity(currentUser,modifyingTeamItem, RecentActivity.ActivityVerbType.UPDATE_USERNAME,usernameEditText.getText().toString());
                Backend.sendUpstreamNotification(hostRecentActivity,teamArrayMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_USERNAME_UPDATED,activity, true);
                Backend.sendUpstreamNotification(clientRecentActivity,modifyingTeamItem.getUID(),currentUser.getUID(),Constants.Strings.Headers.TEAM_USERNAME_UPDATED,activity, true);
            }
            modifyingTeamItem.setUsername(usernameEditText.getText().toString().trim());
            update(modifyingTeamItem, activity);
        }
    }
    private void loadTeamMembers(final User currentUser, final Team team, final TextInputEditText usernameEditText, final TextInputEditText teamNameEditText, final RecyclerView recyclerView, final Activity activity) {
        if (team != null) {
            Backend.getReference(
                    R.string.firebase_teams_directory, activity
            ).child(team.getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot teamSnapshot) {
                    if (teamSnapshot.hasChildren()) {
                        final Team teamItem = new Team(teamSnapshot);
                        // Get all team members

                        Backend.getReference(R.string.firebase_users_directory, activity).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                if (userSnapshot.hasChildren()) {
                                    final ArrayList<User> teamMembers = User.toFilteredArrayList(userSnapshot, Constants.Strings.UIDs.TEAM_UID,teamItem.getUID());

                                    if (teamMembers.size() == 0) {
                                        recyclerView.setAdapter(null);
                                    } else {
                                        recyclerView.setAdapter(new TeamMembersAdapter(currentUser, teamMembers, usernameEditText, teamNameEditText, activity));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    } else {
                        recyclerView.setAdapter(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) { recyclerView.setAdapter(null);}
            });
        }
    }
}