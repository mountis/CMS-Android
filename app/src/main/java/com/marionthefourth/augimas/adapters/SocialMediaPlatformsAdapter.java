package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.content.branding_elements.Branding;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.backend.Backend;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Strings.NO_VALUE;
import static com.marionthefourth.augimas.classes.constants.Constants.Strings.YES_VALUE;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;

public class SocialMediaPlatformsAdapter extends RecyclerView.Adapter<SocialMediaPlatformsAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement socialMediaElement;
    private ArrayList<String> socialMediaPlatforms = new ArrayList<>();
//    Adapter Constructor
    public SocialMediaPlatformsAdapter(Activity activity, BrandingElement socialMediaElement) {
        this.activity = activity;
        this.socialMediaElement = socialMediaElement;
        this.socialMediaPlatforms = socialMediaElement.getContents();
        if (socialMediaPlatforms.size() >= 1) {
            socialMediaPlatforms.remove(0);
        }
    }
//    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_social_media, parent, false);
        return new SocialMediaPlatformsAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (socialMediaPlatforms.get(position).equals(YES_VALUE)) {
            holder.mSocialMediaNameAvailableCheckBox.setChecked(true);
        }

        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser == null || !currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        holder.mSocialMediaNameAvailableCheckBox.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mSocialMediaNameAvailableCheckBox.setEnabled(true);

        holder.mSocialMediaNameLabel.setText(Branding.Service.getService(position).toString());

        holder.mSocialMediaNameAvailableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = YES_VALUE;
                if (!isChecked) {
                    value = NO_VALUE;
                }

                final String VALUE = value;

                Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(socialMediaElement.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final BrandingElement elementItem = new BrandingElement(dataSnapshot);

                            elementItem.getContents().set(position+1,VALUE);

                            update(elementItem, activity);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }
    @Override
    public int getItemCount() {
        return socialMediaPlatforms.size();
    }
//    View Holder Class
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
