package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;

import java.util.ArrayList;

/**
 * Created on 8/6/17.
 */

public class SocialMediaNamesAdapter extends RecyclerView.Adapter<SocialMediaNamesAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement socialMediaNames;
    private ArrayList<String> socialMediaNamesExtensions = new ArrayList<>();
    private Animation open,close,rotate_forward,rotate_back;

    //    Adapter Constructor
    public SocialMediaNamesAdapter(final Activity activity, BrandingElement socialMediaNames) {
        this.activity = activity;
        this.socialMediaNames = socialMediaNames;
        this.socialMediaNamesExtensions = socialMediaNames.getContents();
        if (socialMediaNamesExtensions.size() >= 1) {
            this.socialMediaNamesExtensions.remove(0);
        }
        open = AnimationUtils.loadAnimation(activity, R.anim.open);
        close = AnimationUtils.loadAnimation(activity,R.anim.close);
        rotate_forward = AnimationUtils.loadAnimation(activity,R.anim.rotate_forward);
        rotate_back = AnimationUtils.loadAnimation(activity,R.anim.rotate_back);
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
        if (position > socialMediaNames.getData().size()-1) {
            holder.hidden = true;
            holder.layout.startAnimation(close);
        } else {
            holder.rotated = true;
            holder.mCreateButton.startAnimation(rotate_forward);
            holder.hidden = false;
            holder.layout.startAnimation(open);
            holder.mSocialMediaEditText.setText(socialMediaNames.getData().get(position));
            holder.mSocialMediaEditText.setHint("");
        }

        holder.mSocialMediaEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (position == socialMediaNames.getData().size()+1) {

                    } else {
                        if (holder.mSocialMediaEditText.getText().toString().equals("") && holder.rotated) {
                            holder.mCreateButton.startAnimation(rotate_back);
                            holder.rotated = false;
                        }
                    }

                } else {
                    holder.creating = false;
                    if (holder.hidden) {
                        holder.layout.startAnimation(open);
                        if (position < getItemCount() && !holder.rotated) {
                            holder.mCreateButton.startAnimation(rotate_forward);
                            holder.rotated = true;
                        }
                    }

                    if (!holder.mSocialMediaEditText.getText().toString().equals("")) {
                        if (position == socialMediaNames.getData().size()+1) {
                            socialMediaNames.getData().add(holder.mSocialMediaEditText.getText().toString());
                        } else {
                            socialMediaNames.getData().set(position, holder.mSocialMediaEditText.getText().toString());
                        }

                        Backend.update(socialMediaNames, activity);
                    } else {
                        if (socialMediaNames.getData().size() >= position+1) {
                            socialMediaNames.getData().remove(position);
                            holder.layout.startAnimation(close);
                            holder.hidden = false;
                            holder.mCreateButton.startAnimation(rotate_back);
                            holder.rotated = false;
                            Backend.update(socialMediaNames, activity);
                        }
                    }
                }
            }
        });

        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create New SocialMedia Name
                if (holder.hidden) {
                    holder.layout.startAnimation(open);
                    holder.hidden = false;
                    holder.mCreateButton.startAnimation(rotate_forward);
                    holder.rotated = true;
                    holder.creating = true;
                } else {
                    // delete data if there
                    if (holder.creating) {
                        if (!holder.mSocialMediaEditText.getText().toString().equals("")) {
                            if (position == socialMediaNames.getData().size()) {
                                socialMediaNames.getData().add(holder.mSocialMediaEditText.getText().toString());
                            }
                            Backend.update(socialMediaNames, activity);
                        }
                    } else {
                        if (holder.rotated) {
                            if (!holder.mSocialMediaEditText.getText().toString().equals("")) {
                                if (position == socialMediaNames.getData().size()) {
                                    socialMediaNames.getData().remove(holder.mSocialMediaEditText.getText().toString());
                                } else {
                                    socialMediaNames.getData().remove(position);
                                }

                                Backend.update(socialMediaNames, activity);
                            }

                            holder.layout.startAnimation(close);
                            holder.hidden = true;
                            holder.mCreateButton.startAnimation(rotate_back);
                            holder.rotated = false;
                        }
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return socialMediaNames.getData().size()+1;
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AppCompatEditText mSocialMediaEditText;
        public AppCompatButton mCreateButton;
        public LinearLayoutCompat layout;
        public LinearLayoutCompat content;
        public boolean rotated = false;
        public boolean hidden = true;
        public boolean creating = false;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            content = (LinearLayoutCompat) view.findViewById(R.id.social_media_name_content);
            mSocialMediaEditText = (AppCompatEditText) view.findViewById(R.id.input_social_media_name);
            mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
            layout = (LinearLayoutCompat) view.findViewById(R.id.input_layout_social_media_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSocialMediaEditText.getText() + "'";
        }
    }
}