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
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.adapters.ChatListAdapter;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.backend.Backend;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

public class ChatListFragment extends Fragment {

    private OnChatListFragmentInteractionListener mListener;

    public ChatListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            final Activity activity = getActivity();
            if (PROTOTYPE_MODE) {
                loadPrototypeChats(activity,recyclerView);
            } else {
                loadChats(activity, recyclerView);
            }
        }
        return view;
    }

    private void loadPrototypeChats(final Activity activity,final RecyclerView recyclerView) {
        final ArrayList<Chat> chats = new ArrayList<>();

        final ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Google","google"));
        teams.add(new Team("AOL","aol") );
        teams.add(new Team("Walmart","walmart"));

        recyclerView.setAdapter(new ChatListAdapter(activity,chats,teams,mListener));
    }

    private void loadChats(final Activity activity,final RecyclerView recyclerView) {
        Backend.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        Backend.getReference(activity,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        Backend.getReference(activity,R.string.firebase_chats_directory).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                    final ArrayList<Chat> chats = new ArrayList<>();
                                                    for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                                                        final Chat chatItem = new Chat(chatReference);
                                                        if (chatItem.hasTeam(currentTeam.getUID())) {
                                                            chats.add(chatItem);
                                                        }

                                                    }

                                                    if (chats.size() == 0) {
                                                        recyclerView.setAdapter(null);
                                                    } else {
                                                        // Get User Information for each Contact
                                                        getCorrespondingTeamsForChats(activity, recyclerView, currentTeam, chats);
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
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void getCorrespondingTeamsForChats(final Activity activity, final RecyclerView recyclerView, final Team currentTeam, final ArrayList<Chat> chats) {
        Backend.getReference(activity,R.string.firebase_teams_directory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    final ArrayList<Team> teams = new ArrayList<>();
                    for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                        final Team teamItem = new Team(teamReference);
                        if (!teamItem.getUID().equals(currentTeam.getUID())) {
                            // check if Team is In Chat
                            addTeamToList(teams,teamItem,chats);
                        }
                    }

                    recyclerView.setAdapter(new ChatListAdapter(activity,chats,teams,(HomeActivity)activity));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void addTeamToList(ArrayList<Team> teams, Team teamItem, ArrayList<Chat> chats) {
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).hasTeam(teamItem.getUID())) {
                teams.add(teamItem);
                return;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatListFragmentInteractionListener) {
            mListener = (OnChatListFragmentInteractionListener) context;
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

    public interface OnChatListFragmentInteractionListener {
        void onChatListFragmentInteraction(Context context, Chat chatItem, Team teamItem);
    }

}