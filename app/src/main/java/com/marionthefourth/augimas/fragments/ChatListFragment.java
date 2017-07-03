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
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.adapters.ChatListAdapter;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

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

            if (PROTOTYPE_MODE) {
                loadPrototypeChats(recyclerView);
            } else {
                loadChats(view, recyclerView);
            }
        }
        return view;
    }

    private void loadPrototypeChats(RecyclerView recyclerView) {
        final ArrayList<Chat> chats = new ArrayList<>();

        final ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Google","google"));
        teams.add(new Team("AOL","aol") );
        teams.add(new Team("Walmart","walmart"));

        recyclerView.setAdapter(new ChatListAdapter(getContext(),chats,teams,mListener));
    }

    private void loadChats(final View view, final RecyclerView recyclerView) {
        FirebaseHelper.getReference(getActivity().getApplicationContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        FirebaseHelper.getReference(getActivity().getApplicationContext(),R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        FirebaseHelper.getReference(getActivity().getApplicationContext(),R.string.firebase_chats_directory).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                    final ArrayList<Chat> chats = new ArrayList<>();
                                                    for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                                                        final Chat chatItem = new Chat(chatReference);
                                                        for (int i = 0; i < chatItem.getTeamUIDs().size(); i++) {
                                                            if (chatItem.getTeamUIDs().get(i).equals(currentTeam.getUID())) {
                                                                chats.add(chatItem);
                                                            }
                                                        }

                                                    }

                                                    if (chats.size() == 0) {
                                                        recyclerView.setAdapter(null);
                                                    } else {
                                                        // Get User Information for each Contact
                                                        getCorrespondingTeamsForChats(view, recyclerView, currentTeam, chats);
                                                    }

//                                                    // Create Augimas Chat with Itself
//                                                    Chat adminChat = new Chat();
//
//                                                    recyclerView.setAdapter(null);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCorrespondingTeamsForChats(View view, final RecyclerView recyclerView, final Team currentTeam, final ArrayList<Chat> chats) {
        FirebaseHelper.getReference(getActivity().getApplicationContext(),R.string.firebase_teams_directory).addValueEventListener(new ValueEventListener() {
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

                    recyclerView.setAdapter(new ChatListAdapter(getContext(),chats,teams,(HomeActivity)getActivity()));


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addTeamToList(ArrayList<Team> teams, Team teamItem, ArrayList<Chat> chats) {
        for (int i = 0; i < chats.size(); i++) {
            for (int j = 0; j < chats.get(i).getTeamUIDs().size(); j++) {
                if (chats.get(i).getTeamUIDs().get(j).equals(teamItem.getUID())) {
                    teams.add(teamItem);
                    return;
                }
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