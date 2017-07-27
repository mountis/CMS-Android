package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
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
import com.marionthefourth.augimas.adapters.MessageListAdapter;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.save;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class ChatFragment extends Fragment implements MessageListAdapter.OnMessageListFragmentInteractionListener {

    public static ChatFragment newInstance(final String channelUID) {
        final Bundle args = new Bundle();
        args.putString(Constants.Strings.UIDs.CHANNEL_UID,channelUID);

        final ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ChatFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.message_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final TextInputEditText inputField = (TextInputEditText)view.findViewById(R.id.input_message_text);
        final AppCompatImageButton sendButton = (AppCompatImageButton)view.findViewById(R.id.button_send);

        final Activity activity = getActivity();
        determineToDisplayChatInputSection(activity,view);

        if (PROTOTYPE_MODE) {
            Channel channel = new Channel();
            loadPrototypeMessages(activity,recyclerView,channel);
        } else {
            if (getArguments() != null) {
                setupSendButtonClickListener(activity,sendButton, inputField);
                loadMessages(activity,recyclerView);
            }
        }

        return view;
    }

    private void determineToDisplayChatInputSection(final Activity activity, final View view) {
        final LinearLayoutCompat chatInputSection = (LinearLayoutCompat)view.findViewById(R.id.chat_input_section);
        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User (dataSnapshot);
                    if (currentUser != null && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
                        chatInputSection.setVisibility(View.VISIBLE);
                    } else {
                        chatInputSection.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                chatInputSection.setVisibility(View.GONE);
            }
        });

    }

    private void loadPrototypeMessages(final Activity activity, final RecyclerView recyclerView, final Channel channel) {
        ArrayList<Message> messages = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("zaynewaynes"));
        users.get(0).setUID("11111");
        users.add(new User("timwallace"));
        users.get(1).setUID("11112");
        users.add(new User("englishmuffin"));
        users.get(2).setUID("11113");
        users.add(new User("probablytheowner"));
        users.get(3).setUID("11114");

        messages.add(new Message(channel,users.get(3),"Guess what guys?"));
        messages.add(new Message(channel,users.get(2),"Whats?"));
        messages.add(new Message(channel,users.get(1),"Sup?"));
        messages.add(new Message(channel,users.get(0),"Doc"));
        messages.add(new Message(channel,users.get(3),"We're live!"));
        messages.add(new Message(channel,users.get(2),"Ain't that something special"));

        ArrayList<User> adminUsers = new ArrayList<>();
        adminUsers.add(new User("roliepolie"));
        adminUsers.get(0).setUID("21111");
        adminUsers.add(new User("karmapolice"));
        adminUsers.get(1).setUID("31111");

        messages.add(new Message(channel,adminUsers.get(0),"I think so :D"));
        messages.add(new Message(channel,adminUsers.get(1),"Everyone put your hands up! :)"));

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Augimas","51515",adminUsers));

        recyclerView.setAdapter(new MessageListAdapter(activity,channel,messages,adminUsers,users,teams,ChatFragment.this));

    }

    private void setupSendButtonClickListener(final Activity activity, final AppCompatImageButton sendButton, final TextInputEditText inputField) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUser = new User(dataSnapshot);
                            if (currentUser != null && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
                                if (!inputField.getText().toString().equals("")) {
                                    // Get Channel UID
                                    Message message = new Message(
                                            getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID),
                                            currentUser.getUID(),
                                            inputField.getText().toString()
                                    );

                                    // Save Message to Firebase
                                    save(activity,message);

                                    // Clear input text
                                    inputField.setText("");
                                } else {
                                    display(getView(),SNACKBAR,R.string.required_field);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void loadMessages(final Activity activity, final RecyclerView recyclerView) {
        // Load Messages from Firebase
        FirebaseHelper.getReference(activity,R.string.firebase_messages_directory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<Message> messages = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot messageReference:dataSnapshot.getChildren()) {
                        Message messageItem = new Message(messageReference);

                        if (messageItem.getChannelUID().equals(getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID))) {
                            messages.add(messageItem);
                        }
                    }

                    FirebaseHelper.getReference(activity,R.string.firebase_channels_directory).child(getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final Channel currentChannel = new Channel(dataSnapshot);
                                if (currentChannel != null) {
                                    // Read Name of Channel
                                    if (currentChannel.getName().equals("")) {
                                        // Get Both Team UIDs
                                        getBothTeamUIDs(activity,recyclerView,currentChannel,messages);
                                    } else {
                                        // Only Get Current Team UID
                                        getOneTeamUID(activity,recyclerView,currentChannel,messages);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    recyclerView.setAdapter(null);
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void getOneTeamUID(final Activity activity, final RecyclerView recyclerView, final Channel currentChannel, final ArrayList<Message> messages) {
        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        FirebaseHelper.getReference(activity,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                    final ArrayList<User> teamMembers = new ArrayList<>();
                                                    for(DataSnapshot userReference:dataSnapshot.getChildren()) {
                                                        final User userItem = new User(userReference);
                                                        if (userItem.getTeamUID().equals(currentTeam.getUID())) {
                                                            teamMembers.add(userItem);
                                                        }

                                                    }

                                                    if (messages.size() > 0) {
                                                        final ArrayList<Team> sortedTeams = new ArrayList<>();
                                                        final ArrayList<ArrayList<User>> sortedTeamUsers = new ArrayList<>();

                                                        recyclerView.setAdapter(new MessageListAdapter(activity,currentChannel,messages,teamMembers,currentTeam,ChatFragment.this));
                                                        return;
                                                    } else {
                                                        recyclerView.setAdapter(null);
                                                        return;
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getBothTeamUIDs(final Activity activity, final RecyclerView recyclerView, final Channel currentChannel, final ArrayList<Message> messages) {
        FirebaseHelper.getReference(activity,R.string.firebase_chats_directory).child(currentChannel.getChatUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Chat currentChat = new Chat(dataSnapshot);
                    if (currentChat != null) {
                        final ArrayList<ArrayList<User>> teamUsers = new ArrayList<>();
                        teamUsers.add(new ArrayList<User>());
                        teamUsers.add(new ArrayList<User>());
                        final ArrayList<Team> teams = new ArrayList<>();
                        FirebaseHelper.getReference(activity,R.string.firebase_users_directory).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    for (DataSnapshot userReference:dataSnapshot.getChildren()) {
                                        final User userItem = new User(userReference);
                                        for (int i = 0; i < currentChat.getTeamUIDs().size(); i++) {
                                            if (userItem != null && userItem.getTeamUID().equals(currentChat.getTeamUIDs().get(i))) {
                                                teamUsers.get(i).add(userItem);
                                            }
                                        }

                                    }

                                    FirebaseHelper.getReference(activity,R.string.firebase_teams_directory).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                                    final Team teamItem = new Team(teamReference);
                                                    for (int i = 0; i < currentChat.getTeamUIDs().size(); i++) {
                                                        if (teamItem.getUID().equals(currentChat.getTeamUIDs().get(i))) {
                                                            teams.add(teamItem);
                                                        }
                                                    }
                                                }

                                                if (messages.size() > 0) {
                                                    final ArrayList<Team> sortedTeams = new ArrayList<>();
                                                    final ArrayList<ArrayList<User>> sortedTeamUsers = new ArrayList<>();

                                                    if (teams.get(0).getType().equals(FirebaseEntity.EntityType.US)) {
                                                        for (int i = 0; i < teams.size(); i++) {
                                                            sortedTeams.add(teams.get(i));
                                                            if (teamUsers.size() > i) {
                                                                sortedTeamUsers.add(teamUsers.get(i));
                                                            }
                                                        }
                                                    } else {
                                                        for (int i = teams.size()-1; i > -1; i--) {
                                                            sortedTeams.add(teams.get(i));
                                                            if (teamUsers.size() > i) {
                                                                sortedTeamUsers.add(teamUsers.get(i));
                                                            }                                                        }
                                                    }
                                                    recyclerView.setAdapter(new MessageListAdapter(activity,currentChannel,messages,sortedTeamUsers.get(0),sortedTeamUsers.get(1),sortedTeams,ChatFragment.this));
                                                    return;
                                                } else {
                                                    recyclerView.setAdapter(null);
                                                    return;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
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

    @Override
    public void onMessageListFragmentInteraction(View view, Message messageItem, User userItem) {

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}