package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;

import java.util.ArrayList;

/**
 * Created by MGR4 on 5/26/17.
 */

public class SocialMediaPlatformsAdapter extends RecyclerView.Adapter<SocialMediaPlatformsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> socialMediaPlatforms = new ArrayList<>();

    public SocialMediaPlatformsAdapter(Context context, ArrayList<String> socialMediaPlatforms) {
        this.context = context;
        this.socialMediaPlatforms = socialMediaPlatforms;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_social_media, parent, false);
        return new SocialMediaPlatformsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSocialMediaNameLabel.setText(socialMediaPlatforms.get(position));

    }

    @Override
    public int getItemCount() {
        return socialMediaPlatforms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AppCompatTextView mSocialMediaNameLabel;
        public final AppCompatCheckBox mSocialMediaNameAvailableCheckBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSocialMediaNameAvailableCheckBox = (AppCompatCheckBox) view.findViewById(R.id.social_media_name_available_check_box);
            mSocialMediaNameLabel = (AppCompatTextView) view.findViewById(R.id.item_label_social_media);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSocialMediaNameLabel.getText() + "'";
        }
    }
}
