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
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.helpers.DeviceHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.create;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.sendNotification;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
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
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
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

        recyclerView.setAdapter(new MessageListAdapter(activity,channel,messages,adminUsers,users, ChatFragment.this));

    }

    private void setupSendButtonClickListener(final Activity activity, final AppCompatImageButton sendButton, final TextInputEditText inputField) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    create(activity,message);

                                    Backend.getReference(R.string.firebase_channels_directory,activity).child(getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final Channel channelItem = new Channel(dataSnapshot);

                                            Backend.getReference(R.string.firebase_chats_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChildren()) {
                                                        for(final Chat chatItem:Chat.toArrayList(dataSnapshot)) {
                                                            if (channelItem.getChatUID().equals(chatItem.getUID())) {
                                                                Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.hasChildren()) {
                                                                            Team otherTeam = null;
                                                                            Team yourTeam = null;
                                                                            for(final Team teamItem:Team.toFilteredArrayList(Team.toArrayList(dataSnapshot),Constants.Strings.UIDs.CHAT_UID,chatItem.getTeamUIDs())) {
                                                                                if (currentUser.isInTeam(teamItem)) {
                                                                                    yourTeam = teamItem;
                                                                                } else {
                                                                                    otherTeam = teamItem;
                                                                                }
                                                                            }

                                                                            if (channelItem.getName().equals("")) {
                                                                                Backend.sendUpstreamNotification(sendNotification(otherTeam,currentUser, Notification.NotificationVerbType.CHAT,activity),otherTeam.getUID());
                                                                                Backend.sendUpstreamNotification(sendNotification(otherTeam,currentUser, Notification.NotificationVerbType.CHAT,activity),currentUser.getTeamUID());
                                                                            } else {
                                                                                Backend.sendUpstreamNotification(sendNotification(yourTeam,currentUser, Notification.NotificationVerbType.CHAT,activity),currentUser.getTeamUID());
                                                                            }

                                                                            DeviceHelper.dismissKeyboard(getView());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {}
                                                                });
                                                            }


                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    // Clear input text
                                    inputField.setText("");
                                } else {
                                    display(SNACKBAR, R.string.required_field, getView());
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
        Backend.getReference(R.string.firebase_messages_directory, activity).addValueEventListener(new ValueEventListener() {
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

                    Backend.getReference(R.string.firebase_channels_directory, activity).child(getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID)).addValueEventListener(new ValueEventListener() {
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
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        Backend.getReference(R.string.firebase_users_directory, activity).addValueEventListener(new ValueEventListener() {
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

                                                        recyclerView.setAdapter(new MessageListAdapter(activity,currentChannel,messages,teamMembers, ChatFragment.this));
                                                        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                                            @Override
                                                            public void onLayoutChange(View v, int left, final int top, int right, final int bottom, int oldLeft, final int oldTop, int oldRight, final int oldBottom) {
                                                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                                                            }
                                                        });
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
        Backend.getReference(R.string.firebase_chats_directory, activity).child(currentChannel.getChatUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Chat currentChat = new Chat(dataSnapshot);
                    if (currentChat != null) {
                        final ArrayList<ArrayList<User>> teamUsers = new ArrayList<>();
                        teamUsers.add(new ArrayList<User>());
                        teamUsers.add(new ArrayList<User>());
                        final ArrayList<Team> teams = new ArrayList<>();
                        Backend.getReference(R.string.firebase_users_directory, activity).addValueEventListener(new ValueEventListener() {
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

                                    Backend.getReference(R.string.firebase_teams_directory, activity).addValueEventListener(new ValueEventListener() {
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

                                                    if (teams.get(0).getType().equals(FirebaseEntity.EntityType.HOST)) {
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
                                                    recyclerView.setAdapter(new MessageListAdapter(activity,currentChannel,messages,sortedTeamUsers.get(0),sortedTeamUsers.get(1), ChatFragment.this));
                                                    recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                                        @Override
                                                        public void onLayoutChange(View v, int left, final int top, int right, final int bottom, int oldLeft, final int oldTop, int oldRight, final int oldBottom) {
                                                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                                                        }
                                                    });
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