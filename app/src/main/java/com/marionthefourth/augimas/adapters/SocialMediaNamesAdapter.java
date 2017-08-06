package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;

import java.util.ArrayList;

/**
 * Created on 8/6/17.
 */

public class SocialMediaNamesAdapter extends RecyclerView.Adapter<SocialMediaNamesAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement social;
    private ArrayList<String> domainNameExtensions = new ArrayList<>();
    //    Adapter Constructor
    public SocialMediaNamesAdapter(final Activity activity, BrandingElement social) {
        this.activity = activity;
        this.social = social;
        this.domainNameExtensions = social.getContents();
        if (domainNameExtensions.size() >= 1) {
            this.domainNameExtensions.remove(0);
        }
    }
    //    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_social_media_name_single, parent, false);
        return new SocialMediaNamesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position > social.getData().size()-1) {
            holder.mSocialMediaName.setVisibility(View.GONE);
        } else {
            holder.mSocialMediaName.setVisibility(View.VISIBLE);
            holder.mSocialMediaName.setText(social.getData().get(position));

        }
        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create New Domain Name
                if (holder.mSocialMediaName.getVisibility() != View.VISIBLE) {
                    holder.mSocialMediaName.setVisibility(View.VISIBLE);
                } else {
                    // save data
                    if (!holder.mSocialMediaName.getText().toString().equals("")) {
                        if (position == social.getData().size()) {
                            social.getData().add(holder.mSocialMediaName.getText().toString());
                        } else {
                            social.getData().set(position, holder.mSocialMediaName.getText().toString());
                        }

                        Backend.update(social, activity);
//                        notifyDataSetChanged();
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return social.getData().size()+1;
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AppCompatEditText mSocialMediaName;
        public AppCompatButton mCreateButton;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSocialMediaName = (AppCompatEditText) view.findViewById(R.id.input_social_media_name);
            mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSocialMediaName.getText() + "'";
        }
    }
}