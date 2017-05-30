package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.fragments.TeamsFragment;

import java.util.ArrayList;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.ViewHolder> {

    private ArrayList<Team> teams;
    private Context context;
    private final TeamsFragment.OnTeamsFragmentInteractionListener mListener;

    public TeamsAdapter(Context context, ArrayList<Team> teams, TeamsFragment.OnTeamsFragmentInteractionListener mListener) {
        this.teams = teams;
        this.mListener = mListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_team, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.teamItem = teams.get(position);

        // Fill ViewHolder
        holder.mDisplayLabel.setText(holder.teamItem.getName());
        holder.mIconLetter.setText(holder.mDisplayLabel.getText().toString().substring(0,1));
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onTeamsFragmentInteraction(context, holder.teamItem);
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return teams.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Team teamItem;
        public final AppCompatButton mIconLetter;
        public final AppCompatTextView mDisplayLabel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDisplayLabel = (AppCompatTextView) view.findViewById(R.id.item_label_team_display_name);
            mIconLetter = (AppCompatButton) view.findViewById(R.id.item_team_icon_letter_moniker);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDisplayLabel.getText() + "'";
        }
    }
}