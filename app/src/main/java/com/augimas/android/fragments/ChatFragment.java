package com.augimas.android.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.augimas.android.R;
import com.augimas.android.activities.SignInActivity;
import com.augimas.android.adapters.MessageListAdapter;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.communication.Channel;
import com.augimas.android.classes.objects.communication.Chat;
import com.augimas.android.classes.objects.communication.Message;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.helpers.DeviceHelper;

import java.util.ArrayList;
import java.util.Date;

import static com.augimas.android.backend.Backend.create;
import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.augimas.android.helpers.FragmentHelper.display;

public final class ChatFragment extends Fragment implements MessageListAdapter.OnMessageListFragmentInteractionListener {
    public ChatFragment() {}
    public static ChatFragment newInstance(final String channelUID) {
        final Bundle args = new Bundle();
        args.putString(Constants.Strings.UIDs.CHANNEL_UID,channelUID);

        final ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.message_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final TextInputEditText inputField = (TextInputEditText)view.findViewById(R.id.input_message_text);
        final AppCompatImageButton sendButton = (AppCompatImageButton)view.findViewById(R.id.button_send);
        final Activity activity = getActivity();
        determineToDisplayChatInputSection(view, activity);
        if (getArguments() != null) {
            setupSendButtonClickListener(sendButton, inputField, view, activity);
            loadMessages(activity,recyclerView,view);
        }
        return view;
    }
    private void determineToDisplayChatInputSection(final View view, final Activity activity) {
        final LinearLayoutCompat chatInputSection = (LinearLayoutCompat)view.findViewById(R.id.chat_input_section);
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User (userSnapshot);
                        if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
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
    }
    private void setupSendButtonClickListener(final AppCompatImageButton sendButton, final TextInputEditText inputField, final View containingView, final Activity activity) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnapshot) {
                        if (userSnapshot.exists()) {
                            final User currentUser = new User(userSnapshot);
                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
                                if (!inputField.getText().toString().equals("")) {
                                    // Get Channel UID
                                    final Message message = new Message(
                                            getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID),
                                            currentUser.getUID(),
                                            inputField.getText().toString().trim()
                                    );

                                    message.setTimestamp(new Date().toString());
                                    // Save Message to Firebase
                                    create(message, activity);
                                    final String channelUID = getArguments().getString(Constants.Strings.UIDs.CHANNEL_UID);
                                    if (channelUID != null) {
                                        Backend.getReference(R.string.firebase_channels_directory,activity).child(channelUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot channelSnapshot) {
                                                final Channel channelItem = new Channel(channelSnapshot);
                                                Backend.getReference(R.string.firebase_chats_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot chatReference) {
                                                        if (chatReference.hasChildren()) {
                                                            for(final Chat chatItem:Chat.toArrayList(chatReference)) {
                                                                if (channelItem.getChatUID().equals(chatItem.getUID())) {
                                                                    Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot teamSnapshot) {
                                                                            if (teamSnapshot.hasChildren()) {

                                                                                sendNotification(chatItem,channelItem,teamSnapshot,currentUser,message,activity);

                                                                                DeviceHelper.dismissKeyboard(containingView);
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
                                    }

                                    // Clear input text
                                    inputField.setText("");
                                } else {
                                    display(SNACKBAR, R.string.required_field, containingView);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            }
        });
    }

    private void sendNotification(final Chat chatItem, final Channel channelItem, final DataSnapshot teamSnapshot, final User currentUser, final Message message, final Activity activity) {
        Team otherTeam = null;
        Team yourTeam = null;

        for(final Team teamItem:Team.toFilteredArrayList(Team.toArrayList(teamSnapshot),Constants.Strings.UIDs.CHAT_UID,chatItem.getTeamUIDs())) {
            if (currentUser.isInTeam(teamItem)) {
                yourTeam = teamItem;
            } else {
                otherTeam = teamItem;
            }
        }


        final RecentActivity hostRecentActivity;
        final RecentActivity clientRecentActivity;

        if (channelItem.getName().equals("")) {
            // Client And Host RecentActivity
            hostRecentActivity = new RecentActivity(currentUser,message, RecentActivity.ActivityVerbType.CHAT,otherTeam.getName(),Constants.Ints.Sender.TO);
            clientRecentActivity = new RecentActivity(currentUser,message, RecentActivity.ActivityVerbType.CHAT,yourTeam.getName(),Constants.Ints.Sender.FROM);
            Backend.sendUpstreamNotification(hostRecentActivity,yourTeam.getUID(), currentUser.getUID(),Constants.Strings.Headers.NEW_MESSAGE, activity, true);
            Backend.sendUpstreamNotification(clientRecentActivity,otherTeam.getUID(), currentUser.getUID(),Constants.Strings.Headers.NEW_MESSAGE, activity, true);
        } else {
            // Your Team RecentActivity
            hostRecentActivity = new RecentActivity(currentUser,message, RecentActivity.ActivityVerbType.CHAT);
            Backend.sendUpstreamNotification(hostRecentActivity,currentUser.getTeamUID(), currentUser.getUID(),Constants.Strings.Headers.NEW_MESSAGE, activity, true);
        }
    }

    private void loadMessages(final Activity activity, final RecyclerView recyclerView, final View containingView) {
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
                                        getBothTeamUIDs(activity,recyclerView,currentChannel,messages,containingView);
                                    } else {
                                        // Only Get Current Team UID
                                        getOneTeamUID(activity,recyclerView,currentChannel,messages,containingView);
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
                    if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                        Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
                                    containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                                    containingView.findViewById(R.id.no_content_chat).setVisibility(View.VISIBLE);
                                } else {
                                    containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                                    containingView.findViewById(R.id.no_content_chat).setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        startActivity(new Intent(activity, SignInActivity.class));
                    }


                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    public void getOneTeamUID(final Activity activity, final RecyclerView recyclerView, final Channel currentChannel, final ArrayList<Message> messages, final View containingView) {
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (!currentUser.getTeamUID().equals("")) {
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
                                                        containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                                                        containingView.findViewById(R.id.no_content_chat).setVisibility(View.GONE);
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
                                                        if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
                                                            containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                                                            containingView.findViewById(R.id.no_content_chat).setVisibility(View.VISIBLE);
                                                        } else {
                                                            containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                                                            containingView.findViewById(R.id.no_content_chat).setVisibility(View.GONE);
                                                        }

                                                        return;
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
    private void getBothTeamUIDs(final Activity activity, final RecyclerView recyclerView, final Channel currentChannel, final ArrayList<Message> messages, final View containingView) {
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

                                                    containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                                                    containingView.findViewById(R.id.no_content_chat).setVisibility(View.GONE);
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

                                                    if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                                                        Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.CHATTER)) {
                                                                    containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                                                                    containingView.findViewById(R.id.no_content_chat).setVisibility(View.VISIBLE);
                                                                } else {
                                                                    containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                                                                    containingView.findViewById(R.id.no_content_chat).setVisibility(View.GONE);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }


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
}