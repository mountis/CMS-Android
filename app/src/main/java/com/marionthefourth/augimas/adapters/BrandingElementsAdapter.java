package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.dialogs.BrandingElementStatusDialog;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class BrandingElementsAdapter extends RecyclerView.Adapter<BrandingElementsAdapter.ViewHolder> {
    private Team team;
    private Activity activity;
    private ArrayList<BrandingElement> elements;
    private BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener mListener;
//    Adapter Constructor
    public BrandingElementsAdapter(Activity activity, ArrayList<BrandingElement> elements, Team team, BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener mListener) {
        this.activity = activity;
        this.elements = elements;
        this.team = team;
        this.mListener = mListener;
    }
//    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_full_branding_element, parent, false);
        return new BrandingElementsAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.elementItem = elements.get(position);
        switch(BrandingElement.ElementType.getType(position)) {
            case DOMAIN_NAME:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_domain));
                }
                holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader() + "s");
                break;
            case SOCIAL_MEDIA_NAME:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_social));
                }
                holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader() + "s");
                break;
            case MISSION_STATEMENT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_mission_statement));
                }
                holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader() + "s");
                break;
            case TARGET_AUDIENCE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_target_audience));
                }
                holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader());
                break;
            case BRAND_STYLE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_palette));
                }
                holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader() + "s");
                break;
            case LOGO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_logo));
                }
                holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader() + "s");
                break;
            case PRODUCTS_SERVICES:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mBrandingElementStatus.setBackgroundDrawable(activity.getDrawable(R.drawable.icon_product_service));
                }
                holder.mBrandingElementNameLabel.setText("Products and Services");
                break;
            case DEFAULT:
                break;
        }

        final Animation bounceAnimation = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        holder.mBrandingElementStatus.startAnimation(bounceAnimation);

        final Animation bounceFasterAnimation = AnimationUtils.loadAnimation(activity, R.anim.bounce_faster);
        holder.mView.startAnimation(bounceFasterAnimation);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    handleNonSupportFragmentRemoval(activity.getFragmentManager());
                    mListener.OnBrandingElementInteractionListener(holder.elementItem, team, activity);
                }
            }
        });
        holder.mBrandingElementStatus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                    Backend.getReference(R.string.firebase_users_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userSnapshot) {
                            if (userSnapshot.exists()) {
                                final User currentUser = new User(userSnapshot);
                                if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                                    new BrandingElementStatusDialog(holder, holder.mView, activity);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                return true;
            }
        });
    }
    @Override
    public int getItemCount() {
        return elements.size();
    }
//    Listener Methods
    public interface OnBrandingElementsFragmentInteractionListener {
        void OnBrandingElementInteractionListener(BrandingElement elementItem, Team teamItem, Context context);
    }
//    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        public BrandingElement elementItem;
        final AppCompatTextView mBrandingElementNameLabel;
        public final AppCompatImageButton mBrandingElementStatus;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBrandingElementStatus = (AppCompatImageButton) view.findViewById(R.id.item_branding_element_status);
            mBrandingElementNameLabel = (AppCompatTextView) view.findViewById(R.id.item_label_branding_element_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBrandingElementNameLabel.getText() + "'";
        }
    }
}