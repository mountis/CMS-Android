package com.marionthefourth.augimas.adapters;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;
import static com.marionthefourth.augimas.classes.objects.FirebaseEntity.EntityType.CLIENT;
import static com.marionthefourth.augimas.classes.objects.FirebaseEntity.EntityType.HOST;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.ViewHolder> {
    private User currentUser;
    private Activity activity;
    private ArrayList<User> members = new ArrayList<>();
    private boolean isDisabled = false;
    private AccessibilityService.SoftKeyboardController keyboard;
    RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            if (!isDisabled) {
                rv.findChildViewUnder(e.getX(), e.getY()).performClick();
                return true;
            }

            isDisabled = !isDisabled;
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    };

//    Adapter Constructor
    public TeamMembersAdapter(final Activity activity, final ArrayList<User> members, final User currentUser, TextInputEditText usernameEditText, TextInputEditText nameEditText) {
        this.members = members;
        this.activity = activity;
        this.currentUser = currentUser;

        final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Disable Recycler View Interaction
                    isDisabled = true;
                } else {
                    // Enable Recycler View Interaction
                    isDisabled = false;

                }
            }
        };

        nameEditText.setOnFocusChangeListener(focusChangeListener);
        usernameEditText.setOnFocusChangeListener(focusChangeListener);
    }
//    Adapter Methods
    @Override
    public TeamMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team_member, parent, false);
        return new TeamMembersAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final TeamMembersAdapter.ViewHolder holder, int index) {
        final int POSITION = holder.getAdapterPosition();
        holder.userItem = members.get(POSITION);
        holder.mTeamMemberNameLabel.setText(holder.userItem.getName());

        final Animation bounceFasterAnimation = AnimationUtils.loadAnimation(activity, R.anim.bounce_faster);
        holder.mView.startAnimation(bounceFasterAnimation);

        setupSpinner(holder);
    }

    private void setupSpinner(final ViewHolder holder) {
        final ArrayList<FirebaseEntity.EntityRole> roles = FirebaseEntity.EntityRole.getAllRoles();

        roles.remove(FirebaseEntity.EntityRole.DEFAULT);
        final ArrayAdapter<FirebaseEntity.EntityRole> adapter = new ArrayAdapter<FirebaseEntity.EntityRole>(activity,R.layout.spinner_item_team_member,R.id.team_member_role, roles) {
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public View getDropDownView(final int position, final View convertView, @NonNull ViewGroup parent){
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }

                final TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);

                tv.setText(FirebaseEntity.EntityRole.getRole(position).toString());

                final View finalV = v;
                Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUserItem = new User(dataSnapshot);

                            for(FirebaseEntity.EntityRole role: FirebaseEntity.EntityRole.getAllRoles()) {
                                if (!currentUserItem.hasInclusiveAccess(role)) {
                                    if (position == role.toInt(false)) {
                                        tv.setTextColor(Color.RED);
                                        finalV.setEnabled(false);
                                        finalV.setClickable(false);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });


                return finalV;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        holder.mTeamMemberRoleSpinner.setAdapter(adapter);
        holder.mTeamMemberRoleSpinner.setSelection(holder.userItem.getRole().toInt(false)-1);
        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUserItem = new User(dataSnapshot);
                    if (currentUserItem.getType().equals(HOST)) {
                        if (!currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                            holder.mTeamMemberRoleSpinner.setEnabled(false);
                        }
                        if (holder.userItem.getUID().equals(currentUser.getUID())) {
                            holder.mTeamMemberRoleSpinner.setEnabled(false);
                        }
                    } else {
                        if (!currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                            if (!currentUserItem.hasExclusiveAccess(holder.userItem.getRole()) || currentUserItem.getUID().equals(holder.userItem.getUID())) {
                                holder.mTeamMemberRoleSpinner.setEnabled(false);
                            }
                        } else if (currentUserItem.getUID().equals(holder.userItem.getUID())) {
                            holder.mTeamMemberRoleSpinner.setEnabled(false);
                        } else if (currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                            if (!currentUserItem.hasExclusiveAccess(holder.userItem.getRole()) || currentUserItem.getUID().equals(holder.userItem.getUID())) {
                                holder.mTeamMemberRoleSpinner.setEnabled(false);
                            }
                        }
                        if (!currentUserItem.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                            holder.mTeamMemberRoleSpinner.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mTeamMemberRoleSpinner.post(new Runnable() {
            public void run() {
                holder.mTeamMemberRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, long id) {
                        Backend.getReference(R.string.firebase_users_directory, activity).child(holder.userItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final User modifyingUserItem = new User(dataSnapshot);
                                    final FirebaseEntity.EntityRole selectedRole = (FirebaseEntity.EntityRole)parent.getSelectedItem();

                                    final boolean userIsQualified;
                                    final boolean userIsModifyingProperTeam;
                                    final boolean userIsAtLeastAnEditor = currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR);
                                    final boolean userIsNotModifyingSelf = !modifyingUserItem.getUID().equals(currentUser.getUID());
                                    final boolean userNotIsRestricted = !modifyingUserItem.getUsername().equals("marionthefourth");
                                    final boolean userHasExclusiveAccess = currentUser.hasExclusiveAccess(modifyingUserItem.getRole());
                                    final boolean userHasProperRank = currentUser.hasInclusiveAccess(selectedRole);
                                    if (currentUser.getType() == HOST) {
                                        if (modifyingUserItem.getType() == HOST) {
                                            userIsQualified = userIsAtLeastAnEditor && userHasExclusiveAccess && userHasProperRank;
                                        } else {
                                            userIsQualified = userIsAtLeastAnEditor;
                                        }

                                        userIsModifyingProperTeam = true;
                                    } else {
                                        userIsModifyingProperTeam = modifyingUserItem.getType() != HOST;
                                        userIsQualified = userIsAtLeastAnEditor && userHasExclusiveAccess && userHasProperRank;
                                    }

                                    if (userIsNotModifyingSelf && userNotIsRestricted && userIsQualified && userIsModifyingProperTeam) {
                                        parent.setEnabled(true);
                                        view.setEnabled(true);

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

                                        update(modifyingUserItem, activity);

                                        Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (!isDisabled) {
                                                    final ArrayMap<FirebaseEntity.EntityType,Team> teamArrayMap = Team.toClientAndHostTeamMap(dataSnapshot,modifyingUserItem.getTeamUID());
                                                    final Notification hostNotification;
                                                    final Notification clientNotification;
                                                    if (currentUser.getType() == HOST) {
                                                        hostNotification = new Notification(currentUser,modifyingUserItem, Notification.NotificationVerbType.UPDATE_ROLE,selectedRole,null);

                                                        if (modifyingUserItem.getType() == HOST) {
                                                            Backend.sendUpstreamNotification(hostNotification,modifyingUserItem.getTeamUID(),currentUser.getUID(), Constants.Strings.Headers.USER_ROLE_CHANGED,activity, true);
                                                        } else {
                                                            clientNotification = new Notification(teamArrayMap.get(HOST),modifyingUserItem, Notification.NotificationVerbType.UPDATE_ROLE,selectedRole,null);

                                                            Backend.sendUpstreamNotification(hostNotification,currentUser.getTeamUID(),currentUser.getUID(), Constants.Strings.Headers.USER_ROLE_CHANGED,activity, true);
                                                            Backend.sendUpstreamNotification(clientNotification,modifyingUserItem.getTeamUID(),currentUser.getUID(), Constants.Strings.Headers.USER_ROLE_CHANGED,activity, true);
                                                        }
                                                    } else {
                                                        hostNotification = new Notification(teamArrayMap.get(CLIENT),modifyingUserItem, Notification.NotificationVerbType.UPDATE_ROLE,selectedRole,null);
                                                        clientNotification = new Notification(currentUser,modifyingUserItem, Notification.NotificationVerbType.UPDATE_ROLE,selectedRole,null);

                                                        Backend.sendUpstreamNotification(hostNotification,teamArrayMap.get(HOST).getUID(),currentUser.getUID(), Constants.Strings.Headers.USER_ROLE_CHANGED,activity, true);
                                                        Backend.sendUpstreamNotification(clientNotification,modifyingUserItem.getTeamUID(),currentUser.getUID(), Constants.Strings.Headers.USER_ROLE_CHANGED,activity, true);
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    } else {
                                        holder.mTeamMemberRoleSpinner.setSelection(modifyingUserItem.getRole().toInt(false)-1);
                                        parent.setEnabled(false);
                                        view.setEnabled(false);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return members.size();
    }
//    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        User userItem;
        final AppCompatTextView mTeamMemberNameLabel;
        final AppCompatSpinner mTeamMemberRoleSpinner;

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