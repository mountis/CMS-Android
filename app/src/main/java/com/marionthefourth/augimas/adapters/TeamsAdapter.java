package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.dialogs.TeamAccessDialog;

import java.util.ArrayList;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<Team> teams = new ArrayList<>();
    private Animation bounceFasterAnimation;
//    Adapter Constructor
    public TeamsAdapter(final ArrayList<Team> teams, final Activity activity) {
        this.teams = teams;
        this.activity = activity;
        bounceFasterAnimation = AnimationUtils.loadAnimation(activity, R.anim.bounce_faster);
    }
//    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_minimal_team, parent, false));
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.teamItem = teams.get(position);
        holder.mDisplayLabel.setText(holder.teamItem.getName());
        holder.mView.startAnimation(bounceFasterAnimation);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TeamAccessDialog(holder.teamItem, activity);
            }
        });
    }
    @Override
    public int getItemCount() {
        return teams.size();
    }
//    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public Team teamItem;
        public final View mView;
        final AppCompatTextView mDisplayLabel;

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