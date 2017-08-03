package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<Notification> notifications = new ArrayList<>();

    public NotificationsAdapter(Activity activity, ArrayList<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notification, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationsAdapter.ViewHolder holder, int position) {
        holder.notificationItem = notifications.get(position);
        holder.mNotificationText.setText(holder.notificationItem.getMessage());

        if (holder.notificationItem.getSubject() instanceof FirebaseEntity) {
            holder.mIconLetterMoniker.setText(((FirebaseEntity)holder.notificationItem.getSubject()).getName().substring(0,1));
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

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
