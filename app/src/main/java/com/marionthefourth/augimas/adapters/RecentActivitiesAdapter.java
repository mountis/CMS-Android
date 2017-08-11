package com.marionthefourth.augimas.adapters;

import android.app.Activity;
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
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;

import java.util.ArrayList;

public class RecentActivitiesAdapter extends RecyclerView.Adapter<RecentActivitiesAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<RecentActivity> recentActivities = new ArrayList<>();
//    Adapter Constructor
    public RecentActivitiesAdapter(Activity activity, ArrayList<RecentActivity> recentActivities) {
        this.activity = activity;
        this.recentActivities = recentActivities;
    }
//    Adapter Methods
    @Override
    public RecentActivitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notification, parent, false);
        return new RecentActivitiesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final RecentActivitiesAdapter.ViewHolder holder, int position) {
        holder.recentActivityItem = recentActivities.get(position);
        if (holder.recentActivityItem.getMessageText() != null &&
                !holder.recentActivityItem.getMessageText().equals("")) {
            holder.mIconLetterMoniker.setText(holder.recentActivityItem.getMessageText().substring(0,1));

            holder.mNotificationText.setText(holder.recentActivityItem.getMessageText());

            pullItemsData(holder);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.recentActivityItem.navigate(activity);
                }
            });
        } else {
            holder.mView.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return recentActivities.size();
    }
//    Backend Pulling Methods
    private void pullItemsData(final ViewHolder holder) {
        switch (holder.recentActivityItem.getSubjectType()) {
            case MEMBER:
                Backend.getReference(R.string.firebase_users_directory, activity).child(holder.recentActivityItem.getSubjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User teamMember = new User(dataSnapshot);
                            if (teamMember != null) {
                                holder.recentActivityItem.setSubject(teamMember);
                                pullObjectItemData(holder);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case TEAM:
                Backend.getReference(R.string.firebase_teams_directory, activity).child(holder.recentActivityItem.getSubjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);
                            if (teamItem != null) {
                                holder.recentActivityItem.setSubject(teamItem);

                                pullObjectItemData(holder);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case DEFAULT:
                break;
        }
    }
    private void pullObjectItemData(final ViewHolder holder) {
        switch (holder.recentActivityItem.getObjectType()) {
            case BRANDING_ELEMENT:
                Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(holder.recentActivityItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                            if (elementItem != null) {
                                holder.recentActivityItem.setObject(elementItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case MESSAGE:
                Backend.getReference(R.string.firebase_chats_directory, activity).child(holder.recentActivityItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Chat chatItem = new Chat(dataSnapshot);
                            if (chatItem != null) {
                                holder.recentActivityItem.setObject(chatItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case MEMBER:
                Backend.getReference(R.string.firebase_users_directory, activity).child(holder.recentActivityItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User userItem = new User(dataSnapshot);
                            holder.recentActivityItem.setObject(userItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case TEAM:
                Backend.getReference(R.string.firebase_teams_directory, activity).child(holder.recentActivityItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);
                            if (teamItem != null) {
                                holder.recentActivityItem.setObject(teamItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case DEFAULT:
                break;
        }
    }
//    View Holder Methods
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public RecentActivity recentActivityItem;
        public final AppCompatTextView mNotificationText;
        public final AppCompatButton mIconLetterMoniker;

        public ViewHolder(View view) {
            super(view);
            mView = view.findViewById(R.id.notification_view_id);
            mNotificationText = (AppCompatTextView) mView.findViewById(R.id.item_label_notification_text);
            mIconLetterMoniker = (AppCompatButton) mView.findViewById(R.id.item_team_icon_letter_moniker);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNotificationText.getText() + "'";
        }
    }
}
