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
import com.marionthefourth.augimas.adapters.ChatListAdapter;
import com.marionthefourth.augimas.classes.Chat;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.classes.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;

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
                loadChats(view, recyclerView, FirebaseHelper.getCurrentUser());
            }
        }
        return view;
    }

    private void loadPrototypeChats(RecyclerView recyclerView) {
        final ArrayList<Chat> chats = new ArrayList<>();
        chats.add(new Chat("Google"));
        chats.add(new Chat("AOL") );
        chats.add(new Chat("Walmart"));

        final ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Google","google"));
        teams.add(new Team("AOL","aol") );
        teams.add(new Team("Walmart","walmart"));

        recyclerView.setAdapter(new ChatListAdapter(getContext(),chats,teams,mListener));
    }

    private void loadChats(final View view, final RecyclerView recyclerView, final User user) {
        if (user != null) {
            FirebaseHelper.getReference(
                    view.getContext(),
                    R.string.firebase_chats_directory
            ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        final ArrayList<Chat> chatItems = new ArrayList<>();
                        // Get chats that the user belong to
                        for (DataSnapshot chatReference : dataSnapshot.getChildren()) {
                            Chat chatItem = new Chat(chatReference);
                            if (user.isInChat(chatItem)) {
                                chatItems.add(new Chat(chatReference));
                            }
                        }

                        if (chatItems.size() == 0) {
                            recyclerView.setAdapter(null);
                        } else {
                            // Get User Information for each Contact
//                            getCorrespondingUsersForChats(view, recyclerView, chatItems);
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