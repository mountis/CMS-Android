package com.augimas.android.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.dialogs.ConfirmActionDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.augimas.android.backend.Backend.getCurrentUser;

/**
 * Created on 8/18/17.
 */

public final class BrandStylesAdapter extends RecyclerView.Adapter<BrandStylesAdapter.ViewHolder> {
    private Activity activity;
    private View containingView;
    private BrandingElement brandingName;
    private Animation open,close,rotate_forward,rotate_back,bounceFasterAnimation;
    //    Adapter Constructor
    public BrandStylesAdapter(final BrandingElement brandingName, final View containingView, final Activity activity) {
        this.activity = activity;
        this.brandingName = brandingName;
        this.containingView = containingView;
        open = AnimationUtils.loadAnimation(activity, R.anim.open);
        close = AnimationUtils.loadAnimation(activity,R.anim.close);
        rotate_back = AnimationUtils.loadAnimation(activity,R.anim.rotate_back);
        rotate_forward = AnimationUtils.loadAnimation(activity,R.anim.rotate_forward);
        bounceFasterAnimation = AnimationUtils.loadAnimation(activity, R.anim.bounce_faster);
    }
    //    Adapter Methods
    @Override
    public BrandStylesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_brand_style_brand_item, parent, false);
        return new BrandStylesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final BrandStylesAdapter.ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
        holder.hideButtons();
        setupView(holder,POSITION);
        holder.mView.startAnimation(bounceFasterAnimation);
        addOnClickListener(brandingName,holder,POSITION);
    }
    private void setupView(final BrandStylesAdapter.ViewHolder holder, final int POSITION) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        if (getItemCount() == 1) {
                            holder.mView.setVisibility(View.GONE);
                            containingView.findViewById(R.id.branding_element_brand_style_layout).setVisibility(View.GONE);
                            containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                            // Set alternative View for display when there is no data.
                        } else {
                            if (POSITION > brandingName.getData().size()-1) {
                                holder.hideView();
                            } else {
                                holder.mColorsRecyclerView.setAdapter(new ColorsAdapter(brandingName,containingView,activity,POSITION));
                                holder.editing = false;
                                holder.revealInputAndTurnButtonToDelete(false);
                            }
                        }
                    } else {
                        containingView.findViewById(R.id.branding_element_brand_style_layout).setVisibility(View.VISIBLE);
                        containingView.findViewById(R.id.no_content).setVisibility(View.GONE);

                        if (POSITION > brandingName.getData().size()-1) {
                            holder.hideInput(false);
                            holder.revealCreateButton();
                            holder.mColorsRecyclerView.setAdapter(new ColorsAdapter(brandingName,containingView,activity,POSITION));
                        } else {
                            holder.editing = false;
                            holder.revealInputAndTurnButtonToDelete(false);
                            holder.mColorsRecyclerView.setAdapter(new ColorsAdapter(brandingName,containingView,activity,POSITION));
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void addOnClickListener(final BrandingElement brandingName, final BrandStylesAdapter.ViewHolder holder, final int POSITION) {
        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.revealInputAndTurnButtonToDelete(true);
                holder.hideCreateButton();
                holder.creating = true;
            }
        });
        holder.mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.creating = false;
                Backend.getReference(R.string.firebase_branding_elements_directory,activity).child(brandingName.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final BrandingElement element = new BrandingElement(dataSnapshot);
                            if (element.getData().size() != 0) {
                                if (POSITION <= element.getData().size()-1) {
                                    String previousText = element.getData().get(POSITION);
                                    new ConfirmActionDialog(previousText,"style","", element,activity);
                                } else {
                                    holder.hideInput(true);
                                    holder.hideModifyButton();
                                    holder.revealCreateButton();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return brandingName.getData().size()+1;
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        boolean editing = false;
        boolean creating = false;
        boolean inputHidden = true;
        boolean buttonRotated = false;
        final AppCompatButton mCreateButton;
        final AppCompatButton mModifyButton;
        final RecyclerView mColorsRecyclerView;
        public final LinearLayoutCompat layout;
        public final LinearLayoutCompat content;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mColorsRecyclerView     = view.findViewById(R.id.brand_style_recycler_view);
            mCreateButton           = view.findViewById(R.id.item_create_element);
            mModifyButton           = view.findViewById(R.id.item_modify_element);
            content                 = view.findViewById(R.id.brand_style_content);
            layout                  = view.findViewById(R.id.input_layout_brand_style);
        }

        void hideView() {
            hideButtons();
            mView.startAnimation(close);

        }
        void hideButtons() {
            hideCreateButton();
            hideModifyButton();
        }

        void hideModifyButton(){
            mModifyButton.setEnabled(false);
            mModifyButton.setClickable(false);
            mModifyButton.startAnimation(close);
            mModifyButton.post(new Runnable() {
                @Override
                public void run() {
                    mModifyButton.setVisibility(View.GONE);
                }
            });
        }
        void hideCreateButton() {
            mCreateButton.setEnabled(false);
            mCreateButton.setClickable(false);
            mCreateButton.startAnimation(close);
            mCreateButton.post(new Runnable() {
                @Override
                public void run() {
                    mCreateButton.setVisibility(View.GONE);
                }
            });
        }
        void revealModifyButton(){
            mModifyButton.setEnabled(true);
            mModifyButton.setClickable(true);
            mModifyButton.setVisibility(View.VISIBLE);
            mModifyButton.startAnimation(open);

        }
        void revealCreateButton() {
            mCreateButton.setEnabled(true);
            mCreateButton.setClickable(true);
            mCreateButton.setVisibility(View.VISIBLE);
            mCreateButton.startAnimation(open);
        }
        void turnButtonToDelete() {
            if (!buttonRotated) {
                buttonRotated = true;
                mModifyButton.startAnimation(rotate_forward);
            }
        }
        void turnButtonToCreate() {
            if (buttonRotated) {
                buttonRotated = false;
                mModifyButton.startAnimation(rotate_back);
            }
        }
        void hideInput(boolean manual) {
            if (!inputHidden) {
                layout.startAnimation(close);
                layout.setVisibility(View.GONE);
                if (manual) {
                    editing = false;
//                    mBrandItemInputText.clearFocus();
                }
                inputHidden = true;
            }
        }
        void revealInput(boolean manual) {
            if (inputHidden) {
                inputHidden = false;
                layout.startAnimation(open);
                layout.setVisibility(View.VISIBLE);
                if (manual) {
                    editing = true;
//                    mBrandItemInputText.requestFocus();
                }
            }
        }
        void revealInputAndTurnButtonToDelete(final boolean manual) {
            revealInput(manual);
            revealModifyButton();
            turnButtonToDelete();
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mColorsRecyclerView.toString()+ "'";
        }
    }
}