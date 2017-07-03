package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.fragments.ChatListFragment;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Chat> chats;
    private final ArrayList<Team> teams;

    private final ChatListFragment.OnChatListFragmentInteractionListener mListener;

    public ChatListAdapter(Context context, ArrayList<Chat> chats, ArrayList<Team> teams, ChatListFragment.OnChatListFragmentInteractionListener mListener) {
        this.context = context;
        this.chats = chats;
        this.teams = teams;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Get Chat Item
        holder.chatItem = chats.get(position);
        holder.teamItem = teams.get(position);

        // Fill Chat Name with Team Name
        holder.mChatNameLabel.setText(holder.teamItem.getName());
        // Fill Chat Letter Moniker with first letter of Username
        holder.mChatLetterMoniker.setText(holder.mChatNameLabel.getText().toString().substring(0,1));

        // Get last sent message if there is one
//        getLastMessageSent(context,chats.get(position),teams,holder.mChatLastMessageSent,holder.mChatLastMessageSentTime);

        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChatListFragmentInteraction(context, holder.chatItem, holder.teamItem);
                }
            }
        });
    }

    private void getLastMessageSent(Context context, final Chat chatItem, final ArrayList<Team> teams, final AppCompatTextView mChatLastMessageSent, final AppCompatTextView mChatLastMessageSentTime) {
        FirebaseHelper.getReference(context,R.string.firebase_messages_directory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Message> messages = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    // Add Messages from Chat
                    for (DataSnapshot messageReference:dataSnapshot.getChildren()) {
                        Message message = new Message(messageReference);

                        if (message.isFromChat(chatItem)) {
                            messages.add(message);
                        }
                    }

                    if (messages.size() > 0) {
                        // Get Username that sent message
//                        getUsernameThatSentMessage(messages,teams,mChatLastMessageSent,mChatLastMessageSentTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Chat chatItem;
        public final AppCompatTextView mChatNameLabel;
        public final AppCompatButton mChatLetterMoniker;
        public final AppCompatTextView mChatLastMessageSent;
        public final AppCompatTextView mChatLastMessageSentTime;
        public Team teamItem = new Team();

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChatLetterMoniker = (AppCompatButton) view.findViewById(R.id.item_icon_letter_moniker);
            mChatNameLabel = (AppCompatTextView) view.findViewById(R.id.item_label_chat_display_name);
            mChatLastMessageSent = (AppCompatTextView) view.findViewById(R.id.item_label_last_message);
            mChatLastMessageSentTime = (AppCompatTextView) view.findViewById(R.id.item_label_last_message_sent_stamp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mChatNameLabel.getText() + "'";
        }
    }


}