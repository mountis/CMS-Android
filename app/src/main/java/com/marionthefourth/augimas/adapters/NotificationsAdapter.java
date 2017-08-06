package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.content.Intent;
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
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.backend.Backend;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<Notification> notifications = new ArrayList<>();
//    Adapter Constructor
    public NotificationsAdapter(Activity activity, ArrayList<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
    }
//    Adapter Methods
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notification, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final NotificationsAdapter.ViewHolder holder, int position) {
        holder.notificationItem = notifications.get(position);
        pullItemsData(holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String entityTeamUID;
                final Intent intent = new Intent(activity, HomeActivity.class);
                switch(holder.notificationItem.getSubjectType()) {
                    case MEMBER:
                        entityTeamUID = ((User) holder.notificationItem.getSubject()).getTeamUID();
                        break;
                    case TEAM:
                    default:
                        entityTeamUID = (holder.notificationItem.getSubject()).getUID();
                        break;

                }
                switch (holder.notificationItem.getObjectType()) {
                    case BRANDING_ELEMENT:
                        intent.putExtra(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.BRANDING_ELEMENTS);
                        break;
                    case CHAT:
                        intent.putExtra(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.CHAT);
                        break;
                    case MEMBER:
                    case TEAM:
                        if (holder.notificationItem.getVerbType() == Notification.NotificationVerbType.CHAT) {
                            intent.putExtra(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.CHAT);
                            if (holder.notificationItem.getObject() != null) {
                                intent.putExtra(Constants.Strings.UIDs.TEAM_UID,holder.notificationItem.getObject().getUID());
                            }
                        } else {
                            intent.putExtra(Constants.Strings.Fields.FRAGMENT,Constants.Strings.Fragments.TEAM_MANAGEMENT);
                            if (holder.notificationItem.getObject() != null) {
                                intent.putExtra(Constants.Strings.UIDs.TEAM_UID,holder.notificationItem.getObject().getUID());
                            }
                        }

                        break;
                }

                if (entityTeamUID == null || entityTeamUID.equals("")) {
                    intent.putExtra(Constants.Strings.UIDs.TEAM_UID,entityTeamUID);
                    activity.startActivity(intent);
                }


            }
        });
    }
    @Override
    public int getItemCount() {
        return notifications.size();
    }
//    Backend Pulling Methods
    private void pullItemsData(final ViewHolder holder) {
        switch (holder.notificationItem.getSubjectType()) {
            case MEMBER:
                Backend.getReference(R.string.firebase_users_directory, activity).child(holder.notificationItem.getSubjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User teamMember = new User(dataSnapshot);
                            if (teamMember != null) {
                                holder.mIconLetterMoniker.setText(teamMember.getName().substring(0,1));
                                holder.notificationItem.setSubject(teamMember);
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
                Backend.getReference(R.string.firebase_teams_directory, activity).child(holder.notificationItem.getSubjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);
                            if (teamItem != null) {
                                holder.mIconLetterMoniker.setText(teamItem.getName().substring(0,1));
                                holder.notificationItem.setSubject(teamItem);

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
        switch (holder.notificationItem.getObjectType()) {

            case BRANDING_ELEMENT:
                Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                            if (elementItem != null) {
                                holder.notificationItem.setObject(elementItem);
                                holder.mNotificationText.setText(holder.notificationItem.getMessage());
                                if (holder.mNotificationText.getText().equals("notification text")) {
                                    holder.itemView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case CHAT:
                Backend.getReference(R.string.firebase_chats_directory, activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Chat chatItem = new Chat(dataSnapshot);
                            if (chatItem != null) {
                                holder.notificationItem.setObject(chatItem);
                                holder.mNotificationText.setText(holder.notificationItem.getMessage());
                                if (holder.mNotificationText.getText().equals("notification text")) {
                                    holder.itemView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case MEMBER:
                Backend.getReference(R.string.firebase_users_directory, activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User userItem = new User(dataSnapshot);
                            if (userItem != null) {
                                holder.notificationItem.setObject(userItem);
                                holder.mNotificationText.setText(holder.notificationItem.getMessage());
                                if (holder.mNotificationText.getText().equals("notification text")) {
                                    holder.itemView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case TEAM:
                Backend.getReference(R.string.firebase_teams_directory, activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);
                            if (teamItem != null) {
                                holder.notificationItem.setObject(teamItem);
                                holder.mNotificationText.setText(holder.notificationItem.getMessage());

                                if (holder.mNotificationText.getText().equals("notification text")) {
                                    holder.itemView.setVisibility(View.INVISIBLE);
                                }
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
        public Notification notificationItem;
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
