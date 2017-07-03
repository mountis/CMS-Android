package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private final Channel channel;
    private final Context context;
    private final ArrayList<Message> messages;
    private Team team;
    private ArrayList<User> teamMembers;
    private ArrayList<Team> teams = new ArrayList<>();
    private OnMessageListFragmentInteractionListener mListener;
    private ArrayList<User> adminTeamMembers = new ArrayList<>();
    private ArrayList<User> clientTeamMembers = new ArrayList<>();

    public MessageListAdapter(final Context context, final Channel channel, final ArrayList<Message> messages, final ArrayList<User> adminTeamMembers, final ArrayList<User> clientTeamMembers, final ArrayList<Team> teams, final OnMessageListFragmentInteractionListener mListener) {
        this.channel = channel;
        this.teams = teams;
        this.context = context;
        this.messages = messages;
        this.mListener = mListener;
        this.clientTeamMembers = clientTeamMembers;
        this.adminTeamMembers = adminTeamMembers;
    }

    public MessageListAdapter(final Context context, final Channel channel, final ArrayList<Message> messages, final ArrayList<User> teamMembers, final Team team, final OnMessageListFragmentInteractionListener mListener) {
        this.channel = channel;
        this.team = team;
        this.context = context;
        this.messages = messages;
        this.mListener = mListener;
        this.teamMembers = teamMembers;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Set chat item
        holder.channelItem = channel;
        holder.messageItem = messages.get(position);

        if (adminTeamMembers.size() == 0) {
            handleSingleTeam(holder,position);
        } else {
            handleMultipleTeams(holder,position);

        }


    }

    private void handleSingleTeam(final ViewHolder holder, int position) {
        // Figure out who is sending the message
        holder.senderItem = User.getMessageSender(teamMembers,holder.messageItem);
        holder.mTimestampLabel.setVisibility(View.GONE);

        if (PROTOTYPE_MODE) {

        } else {
            if (holder.senderItem.getEmail().equals(FirebaseHelper.getCurrentUser().getEmail())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mMessageLabel.setBackgroundDrawable(context.getDrawable(R.drawable.your_circle));
                }

                LinearLayoutCompat messageViewLinearLayout = (LinearLayoutCompat) holder.mView.findViewById(R.id.item_linear_layout);
                holder.mMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.yourMessageTextColor));
                holder.mUsernameLabel.setVisibility(View.GONE);
                holder.mChatLetterMoniker.setVisibility(View.GONE);
                messageViewLinearLayout.setGravity(Gravity.RIGHT);
                messageViewLinearLayout.setRight(15);

                holder.mMessageLabel.setGravity(Gravity.RIGHT);

            } else {

                // Only Display Username if there are more than two users
                if (teamMembers.size() > 2) {
                    // Get sender username and set it to label
                    holder.mUsernameLabel.setText(holder.senderItem.getUsername());
                }

                if (position -1 >= 0) {
                    if (messages.get(position-1).getSenderUID().equals(holder.senderItem.getUID())) {
                        holder.mUsernameLabel.setVisibility(View.GONE);
                    }
                }

                // Only Display the Chat Letter on this message if the next one isn't by the same user
                if (position + 1 < messages.size()) {
                    if (messages.get(position+1).getSenderUID().equals(holder.senderItem.getUID())) {
                        holder.mChatLetterMoniker.setVisibility(View.INVISIBLE);
                    }
                }

                holder.mChatLetterMoniker.setText(holder.mUsernameLabel.getText().toString().substring(0,1));
            }
        }

        // Set message label
        holder.mMessageLabel.setText(messages.get(position).getText());

        // Set timestamp label
//        holder.mTimestampLabel.setText(messages.get(position).getTimestamp());
//
        holder.mMessageLabel.setPadding(25,25,25,25);

        // Set view click listener
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageListFragmentInteraction(holder.mView,holder.messageItem,holder.senderItem);
                    return true;
                }

                return false;
            }
        });
    }

    private void handleMultipleTeams(final ViewHolder holder, int position) {
        ArrayList<User> users = new ArrayList<>();
        users.addAll(adminTeamMembers);
        users.addAll(clientTeamMembers);

        // Figure out who is sending the message
        holder.senderItem = User.getMessageSender(users,holder.messageItem);


        holder.mTimestampLabel.setVisibility(View.GONE);

        // Check if sender matches your user for labeling purposes
        if (PROTOTYPE_MODE) {
            if (holder.senderItem.getUID().equals("31111")) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mMessageLabel.setBackgroundDrawable(context.getDrawable(R.drawable.your_circle));
                }


                LinearLayoutCompat messageViewLinearLayout = (LinearLayoutCompat) holder.mView.findViewById(R.id.item_linear_layout);
                holder.mMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.yourMessageTextColor));
                holder.mUsernameLabel.setVisibility(View.GONE);
                holder.mChatLetterMoniker.setVisibility(View.GONE);
                messageViewLinearLayout.setGravity(Gravity.RIGHT);
                messageViewLinearLayout.setRight(15);

                holder.mMessageLabel.setGravity(Gravity.RIGHT);
            } else {

                // Only Display Username if there are more than two users
                if (users.size() > 2) {
                    // Get sender username and set it to label
                    holder.mUsernameLabel.setText(holder.senderItem.getUsername());
                }

                if (position -1 >= 0) {
                    if (messages.get(position-1).getSenderUID().equals(holder.senderItem.getUID())) {
                        holder.mUsernameLabel.setVisibility(View.GONE);
                    }
                }

                // Only Display the Chat Letter on this message if the next one isn't by the same user
                if (position + 1 < messages.size()) {
                    if (messages.get(position+1).getSenderUID().equals(holder.senderItem.getUID())) {
                        holder.mChatLetterMoniker.setVisibility(View.INVISIBLE);
                    }
                }

                holder.mChatLetterMoniker.setText(holder.mUsernameLabel.getText().toString().substring(0,1));
            }
        } else {
            if (holder.senderItem.getEmail().equals(FirebaseHelper.getCurrentUser().getEmail())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mMessageLabel.setBackgroundDrawable(context.getDrawable(R.drawable.your_circle));
                }

                LinearLayoutCompat messageViewLinearLayout = (LinearLayoutCompat) holder.mView.findViewById(R.id.item_linear_layout);
                holder.mMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.yourMessageTextColor));
                holder.mUsernameLabel.setVisibility(View.GONE);
                holder.mChatLetterMoniker.setVisibility(View.GONE);
                messageViewLinearLayout.setGravity(Gravity.RIGHT);
                messageViewLinearLayout.setRight(15);

                holder.mMessageLabel.setGravity(Gravity.RIGHT);

            } else {

                // Only Display Username if there are more than two users
                if (users.size() > 2) {
                    // Get sender username and set it to label
                    holder.mUsernameLabel.setText(holder.senderItem.getUsername());
                }

                if (position -1 >= 0) {
                    if (messages.get(position-1).getSenderUID().equals(holder.senderItem.getUID())) {
                        holder.mUsernameLabel.setVisibility(View.GONE);
                    }
                }

                // Only Display the Chat Letter on this message if the next one isn't by the same user
                if (position + 1 < messages.size()) {
                    if (messages.get(position+1).getSenderUID().equals(holder.senderItem.getUID())) {
                        holder.mChatLetterMoniker.setVisibility(View.INVISIBLE);
                    }
                }

                holder.mChatLetterMoniker.setText(holder.mUsernameLabel.getText().toString().substring(0,1));
            }
        }

        // Set message label
        holder.mMessageLabel.setText(messages.get(position).getText());

        // Set timestamp label
//        holder.mTimestampLabel.setText(messages.get(position).getTimestamp());
//
        holder.mMessageLabel.setPadding(25,25,25,25);

        // Set view click listener
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageListFragmentInteraction(holder.mView,holder.messageItem,holder.senderItem);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Channel channelItem;
        public Message messageItem;
        public User senderItem;
        public final AppCompatTextView mMessageLabel;
        public final AppCompatTextView mUsernameLabel;
        public final AppCompatTextView mTimestampLabel;
        public final AppCompatButton mChatLetterMoniker;


        public ViewHolder(View view) {
            super(view);
            mView = view.findViewById(R.id.message_view_id);
            mMessageLabel = (AppCompatTextView) mView.findViewById(R.id.item_label_message_text);
            mTimestampLabel = (AppCompatTextView) mView.findViewById(R.id.item_label_last_message_sent_stamp);
            mUsernameLabel = (AppCompatTextView) mView.findViewById(R.id.item_label_username_or_email);
            mChatLetterMoniker = (AppCompatButton) mView.findViewById(R.id.item_icon_letter_moniker);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageLabel.getText() + "'";
        }
    }

    public interface OnMessageListFragmentInteractionListener {
        void onMessageListFragmentInteraction(View view, Message messageItem, User userItem);
    }
}