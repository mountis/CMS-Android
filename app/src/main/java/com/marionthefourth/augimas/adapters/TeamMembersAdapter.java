package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.update;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.ViewHolder> {

    private Team team;
    private Context context;
    private User currentUser;
    private ArrayList<User> members = new ArrayList<>();

    public TeamMembersAdapter(Context context, Team team, ArrayList<User> members, User currentUser) {
        this.team = team;
        this.context = context;
        this.members = members;
        this.currentUser = currentUser;
    }

    @Override
    public TeamMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team_member, parent, false);
        return new TeamMembersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TeamMembersAdapter.ViewHolder holder, int position) {
        holder.userItem = members.get(position);
        holder.mTeamMemberNameLabel.setText(holder.userItem.getName());

        final ArrayList<FirebaseEntity.EntityRole> roles = FirebaseEntity.EntityRole.getAllRoles();

        roles.remove(FirebaseEntity.EntityRole.DEFAULT);

        final ArrayAdapter<FirebaseEntity.EntityRole> adapter = new ArrayAdapter<FirebaseEntity.EntityRole>(context,R.layout.spinner_item_team_member,R.id.team_member_role, roles) {
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(final int position, final View convertView, ViewGroup parent){
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }

                final TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
//                tv.setText(testarray.get(position));

                final View finalV = v;
                FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUserItem = new User(dataSnapshot);
                            if (currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                                if (position == FirebaseEntity.EntityRole.OWNER.toInt(false)) {
                                    tv.setTextColor(Color.GRAY);
                                    finalV.setEnabled(false);
                                }
                            }

                            if (currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                                if (position == FirebaseEntity.EntityRole.ADMIN.toInt(false)) {
                                    tv.setTextColor(Color.GRAY);
                                    finalV.setEnabled(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                return finalV;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        holder.mTeamMemberRoleSpinner.setAdapter(adapter);

        holder.mTeamMemberRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                FirebaseHelper.getReference(context,R.string.firebase_users_directory).child(holder.userItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User modifyingUserItem = new User(dataSnapshot);
                            final FirebaseEntity.EntityRole selectedRole = (FirebaseEntity.EntityRole)parent.getSelectedItem();

                            modifyingUserItem.setRole(selectedRole);

                            switch (selectedRole) {
                                case OWNER:
                                case ADMIN:
                                case EDITOR:
                                case CHATTER:
                                case VIEWER:
                                    modifyingUserItem.setStatus(FirebaseEntity.EntityStatus.APPROVED);
                                    break;
                                case NONE:
                                    modifyingUserItem.setStatus(FirebaseEntity.EntityStatus.AWAITING);
                                    break;
                                case DEFAULT:
                                    break;
                            }

                            update(context,modifyingUserItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i = 0; i < FirebaseEntity.EntityRole.getAllRoles().size(); i++) {
            if (holder.userItem.getRole().equals(FirebaseEntity.EntityRole.getRole(i))) {
                holder.mTeamMemberRoleSpinner.setSelection(i);

                FirebaseHelper.getReference(context,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUserItem = new User(dataSnapshot);
                            if (!currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                                if (!currentUserItem.hasInclusiveAccess(holder.userItem.getRole()) || currentUserItem.getUID().equals(holder.userItem.getUID())) {
                                    holder.mTeamMemberRoleSpinner.setEnabled(false);
                                }
                            } else {
//                                if (!currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.OWNER)) {
//                                    roles.remove(FirebaseEntity.EntityRole.OWNER);
//                                }
//
//                                holder.mTeamMemberRoleSpinner.getAdapter().
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
