package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;
import com.marionthefourth.augimas.fragments.BrandingElementFragment;
import com.marionthefourth.augimas.fragments.ChatFragment;
import com.marionthefourth.augimas.fragments.TeamManagementFragment;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

public class RecentActivitiesAdapter extends RecyclerView.Adapter<RecentActivitiesAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<Notification> notifications = new ArrayList<>();
//    Adapter Constructor
    public RecentActivitiesAdapter(Activity activity, ArrayList<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
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
        holder.notificationItem = notifications.get(position);
        holder.mIconLetterMoniker.setText(holder.notificationItem.getMessageText().substring(0,1));

        holder.mNotificationText.setText(holder.notificationItem.getMessageText());

        pullItemsData(holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                    Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User currentUser = new User(dataSnapshot);

                            final BottomNavigationView navigation = (BottomNavigationView) activity.findViewById(R.id.navigation);
                            final String entityTeamUID;
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
                                    Backend.getReference(R.string.firebase_branding_elements_directory,activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final BrandingElement brandingElement = new BrandingElement(dataSnapshot);
                                            navigation.setSelectedItemId(R.id.navigation_dashboard);

                                            navigation.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable(Constants.Strings.BRANDING_ELEMENT,brandingElement);
                                                    bundle.putSerializable(Constants.Strings.UIDs.BRANDING_ELEMENT_UID,brandingElement.getUID());
                                                    ((HomeActivity)activity)
                                                            .getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                            .replace(R.id.container, BrandingElementFragment.newInstance(bundle))
                                                            .commitAllowingStateLoss();
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                case MESSAGE:
                                    Backend.getReference(R.string.firebase_messages_directory,activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final Message messageItem = new Message(dataSnapshot);

                                            if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                                navigation.setSelectedItemId(R.id.navigation_dashboard);

                                            } else {
                                                navigation.setSelectedItemId(R.id.navigation_chat);

                                            }

                                            navigation.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((AppCompatActivity)activity)
                                                            .getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                            .replace(R.id.container, ChatFragment.newInstance(messageItem.getChannelUID()))
                                                            .commit();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                    break;
                                case MEMBER:
                                    Backend.getReference(R.string.firebase_users_directory,activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final User userElement = new User(dataSnapshot);

                                            Backend.getReference(R.string.firebase_teams_directory,activity).child(userElement.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    final Team teamElement = new Team(dataSnapshot);
                                                    if (currentUser.getTeamUID().equals(entityTeamUID)) {
                                                        navigation.setSelectedItemId(R.id.navigation_dashboard);
                                                    } else {
                                                        navigation.setSelectedItemId(R.id.navigation_settings);
                                                    }

                                                    navigation.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ((AppCompatActivity)activity)
                                                                    .getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                                    .replace(R.id.container, TeamManagementFragment.newInstance(teamElement))
                                                                    .commit();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                    break;
                                case TEAM:
                                    Backend.getReference(R.string.firebase_teams_directory,activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final Team teamElement = new Team(dataSnapshot);

                                            if (currentUser.getTeamUID().equals(entityTeamUID)) {
                                                navigation.setSelectedItemId(R.id.navigation_dashboard);
                                            } else {
                                                navigation.setSelectedItemId(R.id.navigation_settings);
                                            }

                                            navigation.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((AppCompatActivity)activity)
                                                            .getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                            .replace(R.id.container, TeamManagementFragment.newInstance(teamElement))
                                                            .commit();
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                    break;
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                break;
            case MESSAGE:
                Backend.getReference(R.string.firebase_chats_directory, activity).child(holder.notificationItem.getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Chat chatItem = new Chat(dataSnapshot);
                            if (chatItem != null) {
                                holder.notificationItem.setObject(chatItem);
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
                            holder.notificationItem.setObject(userItem);
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
