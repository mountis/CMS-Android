package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.classes.User;

import java.util.ArrayList;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.ViewHolder> {

    private Context context;
    private Team team;
    private ArrayList<User> members = new ArrayList<>();

    public TeamMembersAdapter(Context context, Team team, ArrayList<User> members) {
        this.context = context;
        this.team = team;
        this.members = members;
    }

    @Override
    public TeamMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team_member, parent, false);
        return new TeamMembersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamMembersAdapter.ViewHolder holder, int position) {
        holder.userItem = members.get(position);
        holder.mTeamMemberNameLabel.setText(holder.userItem.getFullname());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,User.getAllMemberRoles());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.mTeamMemberRoleSpinner.setAdapter(adapter);

        for (int i = 0; i < User.getAllMemberRoles().size();i++) {
            if (User.getMemberRoleString(holder.userItem.getMemberRole()).equals(User.getAllMemberRoles().get(i))) {
                holder.mTeamMemberRoleSpinner.setSelection(i);
                if (i == 0) {
                    holder.mTeamMemberRoleSpinner.setEnabled(false);
                }
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public User userItem;
        public final AppCompatTextView mTeamMemberNameLabel;
        public final AppCompatSpinner mTeamMemberRoleSpinner;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTeamMemberRoleSpinner = (AppCompatSpinner) view.findViewById(R.id.list_item_team_member_role);
            mTeamMemberNameLabel = (AppCompatTextView) view.findViewById(R.id.list_item_label_team_member_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTeamMemberNameLabel.getText() + "'";
        }
    }

}
