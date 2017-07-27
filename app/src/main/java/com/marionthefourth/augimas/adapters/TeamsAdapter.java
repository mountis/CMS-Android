package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.dialogs.TeamAccessDialog;
import com.marionthefourth.augimas.fragments.ChatListFragment;
import com.marionthefourth.augimas.fragments.TeamsFragment;

import java.util.ArrayList;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.ViewHolder> {

    private ArrayList<Team> teams;
    private Activity activity;
    private final TeamsFragment.OnTeamsFragmentInteractionListener teamListener;
    private final ChatListFragment.OnChatListFragmentInteractionListener chatListener;


    public TeamsAdapter(Activity activity, ArrayList<Team> teams, TeamsFragment.OnTeamsFragmentInteractionListener tListener, ChatListFragment.OnChatListFragmentInteractionListener chatListener) {
        this.teams = teams;
        this.teamListener = tListener;
        this.chatListener = chatListener;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_minimal_team, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.teamItem = teams.get(position);

        // Fill ViewHolder
        holder.mDisplayLabel.setText(holder.teamItem.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TeamAccessDialog(activity,holder.teamItem);
            }
        });

//        holder.dashBoardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                teamListener.onTeamsFragmentInteraction(activity, holder.teamItem);
//            }
//        });
//
//        holder.chatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseHelper.getReference(activity,R.string.firebase_chats_directory).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
//                            for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
//                                final Chat chatItem = new Chat(chatReference);
//                                if (chatItem != null) {
//                                    if (chatItem.hasTeam(holder.teamItem.getUID())) {
//                                        chatListener.onChatListFragmentInteraction(activity,chatItem,holder.teamItem);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//
//        holder.statusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new TeamStatusDialog(activity,holder.mView,holder);
//
//            }
//        });
    }
    @Override
    public int getItemCount() {
        return teams.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Team teamItem;
        public final AppCompatTextView mDisplayLabel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDisplayLabel = (AppCompatTextView) view.findViewById(R.id.item_label_team_display_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDisplayLabel.getText() + "'";
        }
    }
}