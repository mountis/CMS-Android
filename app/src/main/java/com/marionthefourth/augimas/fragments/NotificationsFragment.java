package com.marionthefourth.augimas.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.NotificationsAdapter;
import com.marionthefourth.augimas.classes.Notification;
import com.marionthefourth.augimas.classes.Team;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;

public final class NotificationsFragment extends Fragment {

    public NotificationsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            if (PROTOTYPE_MODE) {
                loadPrototypeNotifications(recyclerView);
            } else {
                if (getArguments() != null) {

//
                }
            }
        }





        return view;
    }

    private void loadPrototypeNotifications(RecyclerView recyclerView) {
        Team google = new Team("Google","google");
        Team walmart = new Team("Walmart","walmart");
        Team aol = new Team("AOL","aol");

        // Display a Requesting Approval Notification
        // Display a Updated Mission Statement Notification
        // Display a Client Approved Element Notification

        ArrayList<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("needs approval.",google));
        notifications.add(new Notification("updated their Mission Statement.",walmart));
        notifications.add(new Notification("approved their Domain Name.",aol));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setAdapter(new NotificationsAdapter(getContext(),notifications));
        }

    }

}
