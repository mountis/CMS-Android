package com.marionthefourth.augimas.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
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
import com.marionthefourth.augimas.classes.Chat;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.Message;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.classes.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.Constants.Ints.SNACKBAR;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.save;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class ChatFragment extends Fragment implements MessageListAdapter.OnMessageListFragmentInteractionListener {


    public ChatFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.message_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        TextInputEditText inputField = (TextInputEditText)view.findViewById(R.id.input_message_text);
        AppCompatImageButton sendButton = (AppCompatImageButton)view.findViewById(R.id.button_send);

        if (PROTOTYPE_MODE) {
            Chat chat = new Chat("Chat");
            loadPrototypeMessages(recyclerView,chat);
        } else {
            if (getArguments() != null) {

                setupSendButtonClickListener(sendButton, inputField, getArguments());

                loadMessages(
                        recyclerView,
                        (Chat) getArguments().getSerializable(Constants.Strings.CHATS),
                        (ArrayList<User>) getArguments().getSerializable(Constants.Strings.USER)
                );
            }
        }


        return view;
    }

    private void loadPrototypeMessages(RecyclerView recyclerView, Chat chat) {
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

        messages.add(new Message(chat,users.get(3),"Guess what guys?"));
        messages.add(new Message(chat,users.get(2),"Whats?"));
        messages.add(new Message(chat,users.get(1),"Sup?"));
        messages.add(new Message(chat,users.get(0),"Doc"));
        messages.add(new Message(chat,users.get(3),"We're live!"));
        messages.add(new Message(chat,users.get(2),"Ain't that something special"));

        ArrayList<User> adminUsers = new ArrayList<>();
        adminUsers.add(new User("roliepolie"));
        adminUsers.get(0).setUID("21111");
        adminUsers.add(new User("karmapolice"));
        adminUsers.get(1).setUID("31111");

        messages.add(new Message(chat,adminUsers.get(0),"I think so :D"));
        messages.add(new Message(chat,adminUsers.get(1),"Everyone put your hands up! :)"));

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Augimas","51515",adminUsers));
        teams.add(new Team(chat.getNickname(),"51591",users));

        recyclerView.setAdapter(new MessageListAdapter(getContext(),chat,messages,adminUsers,users,teams,ChatFragment.this));

    }

    private void setupSendButtonClickListener(final AppCompatImageButton sendButton, final TextInputEditText inputField, final Bundle bundle) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputField.getText().toString().equals("")) {
                    Message message = new Message(
                            (Chat) bundle.getSerializable(Constants.Strings.CHATS),
                            FirebaseHelper.getCurrentUser(),
                            inputField.getText().toString()
                    );

                    // Save Message to Firebase
                    save(getContext(),FirebaseHelper.getCurrentUser(),message);

                    // Clear input text
                    inputField.setText("");
                } else {
                    display(getView(),SNACKBAR,R.string.required_field);
                }
            }
        });
    }

    private void loadMessages(final RecyclerView recyclerView, final Chat chat, final ArrayList<User> users) {
        // Load Messages from Firebase
        FirebaseHelper.getReference(getContext(),R.string.firebase_messages_directory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Message> messages = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot messageReference:dataSnapshot.getChildren()) {
                        Message messageItem = new Message(messageReference);

                        if (messageItem.isFromChat(chat)) {
                            messages.add(messageItem);
                        }
                    }

                    if (messages.size() > 0) {
//                        recyclerView.setAdapter(new MessageListAdapter(getContext(),chat,messages,users,ChatFragment.this));
                        return;
                    } else {
                        recyclerView.setAdapter(null);
                        return;
                    }
                } else {
                    recyclerView.setAdapter(null);
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    @Override
    public void onMessageListFragmentInteraction(View view, Message messageItem, User userItem) {

    }
}
