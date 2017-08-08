package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

/**
 * Created on 8/6/17.
 */

public class BrandingNamesAdapter extends RecyclerView.Adapter<BrandingNamesAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement brandingName;
    private ArrayList<String> nameExtensions = new ArrayList<>();
    private Animation open,close,rotate_forward,rotate_back;
    private View containingView;

    //    Adapter Constructor
    public BrandingNamesAdapter(final Activity activity, final BrandingElement brandingName, final View containingView) {
        this.activity = activity;
        this.brandingName = brandingName;
        this.nameExtensions = brandingName.getContents();
        if (nameExtensions.size() >= 1) {
            this.nameExtensions.remove(0);
        }
        this.containingView = containingView;
        open = AnimationUtils.loadAnimation(activity, R.anim.open);
        close = AnimationUtils.loadAnimation(activity,R.anim.close);
        rotate_forward = AnimationUtils.loadAnimation(activity,R.anim.rotate_forward);
        rotate_back = AnimationUtils.loadAnimation(activity,R.anim.rotate_back);
    }
    //    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (brandingName.getType()) {

            case DOMAIN_NAME: view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_domain_name_single, parent, false);
                break;
            case SOCIAL_MEDIA_NAME: view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_social_media_name_single, parent, false);
                break;
            default: return null;
        }

        return new BrandingNamesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (getItemCount() == 1) {
            if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
                Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                            holder.mView.setVisibility(View.GONE);

                            if (brandingName.getType() == BrandingElement.ElementType.DOMAIN_NAME) {
                                containingView.findViewById(R.id.branding_element_domain_name_layout).setVisibility(View.GONE);
                            } else {
                                containingView.findViewById(R.id.branding_element_social_media_name_layout).setVisibility(View.GONE);

                            }

                            containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                            // Set alternative View for display when there is no data.
                        } else {
                            if (brandingName.getType() == BrandingElement.ElementType.DOMAIN_NAME) {
                                containingView.findViewById(R.id.branding_element_domain_name_layout).setVisibility(View.VISIBLE);
                            } else {
                                containingView.findViewById(R.id.branding_element_social_media_name_layout).setVisibility(View.VISIBLE);

                            }

                            containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        final int POSITION = holder.getAdapterPosition();
        if (POSITION > brandingName.getData().size()-1) {
            holder.hideInputLayout();
        } else {
            holder.revealAndTurnOn();
            holder.mNameEditText.setText(brandingName.getData().get(position));
            holder.mNameEditText.setHint("");
        }

        final Animation bounceFasterAnimation = AnimationUtils.loadAnimation(activity, R.anim.bounce_faster);
        holder.mView.startAnimation(bounceFasterAnimation);

        holder.mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    holder.turnOffDeleteButton();
                } else {
                    holder.turnOnDeleteButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.mNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleInput(holder,POSITION,activity);
                }
                return false;
            }
        });

        holder.mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    holder.creating = false;
                    if (holder.hidden) {
                        holder.layout.startAnimation(open);
                        holder.hidden = false;
                        if (POSITION < getItemCount() && !holder.rotated) {
                            holder.turnOnDeleteButton();
                        }
                    } else {
                        if (!holder.mNameEditText.getText().toString().equals("")) {
                            handleInput(holder,POSITION,activity);
                        } else {
                            if (brandingName.getData().size() >= POSITION+1) {
                                brandingName.getData().remove(POSITION);
                                holder.hideAndTurnOff();
                                Backend.update(brandingName, activity);
                            }
                        }
                    }
                }
            }
        });

        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.hidden) {
                    holder.revealAndTurnOn();
                    holder.creating = true;
                } else {
                    // delete data if there
                    if (holder.creating) {
                        if (!holder.mNameEditText.getText().toString().equals("")) {
                            handleInput(holder,POSITION,activity);
                        } else {
                            holder.creating = false;
                            holder.hideAndTurnOff();
                        }
                    } else {
                        if (holder.rotated) {
                            if (!holder.mNameEditText.getText().toString().equals("")) {
                                if (POSITION == brandingName.getData().size()) {
                                    brandingName.getData().remove(holder.mNameEditText.getText().toString());
                                } else {
                                    brandingName.getData().remove(POSITION);
                                }

                                Backend.update(brandingName, activity);
                            }

                            holder.hideAndTurnOff();
                        }
                    }
                }
            }
        });
    }

    private void handleInput(ViewHolder holder, int position, Activity activity) {
        if (BrandingElement.checkInput(holder.mNameEditText.getText().toString(), brandingName.getType())) {
            if (position == brandingName.getData().size()) {
                brandingName.getData().add(holder.mNameEditText.getText().toString());
            }
            Backend.update(brandingName, activity);
        } else {
            // Inproper Input
            holder.mNameEditText.setText("");
            switch (brandingName.getType()) {
                case DOMAIN_NAME:
                    FragmentHelper.display(TOAST,R.string.tld_not_valid,holder.mView.getRootView());
                    break;
                case SOCIAL_MEDIA_NAME:
                    break;
                case MISSION_STATEMENT:
                    break;
                case TARGET_AUDIENCE:
                    break;
                case STYLE_GUIDE:
                    break;
                case LOGO:
                    break;
                case PRODUCTS_SERVICES:
                    break;
                case DEFAULT:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return brandingName.getData().size()+1;
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        AppCompatEditText mNameEditText;
        AppCompatButton mCreateButton;
        public LinearLayoutCompat layout;
        public LinearLayoutCompat content;
        boolean rotated = false;
        boolean hidden = true;
        boolean creating = false;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            switch (brandingName.getType()) {
                case DOMAIN_NAME:
                    content = (LinearLayoutCompat) view.findViewById(R.id.brand_name_content); break;
                case SOCIAL_MEDIA_NAME:
                    content = (LinearLayoutCompat) view.findViewById(R.id.social_media_name_content); break;
                default: break;
            }

            mNameEditText = (AppCompatEditText) view.findViewById(R.id.input_brand_name);
            mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
            layout = (LinearLayoutCompat) view.findViewById(R.id.input_layout_brand_name);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameEditText.getText() + "'";
        }

        private void hideAndTurnOff() {
            hideInputLayout();
            turnOffDeleteButton();
        }

        void revealAndTurnOn() {
            revealInputLayout();
            turnOnDeleteButton();
        }

        void hideInputLayout() {
            if (!hidden) {
                layout.setVisibility(View.GONE);
                layout.startAnimation(close);
                mNameEditText.clearFocus();
                hidden = true;
            }
        }

        void revealInputLayout() {
            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                layout.startAnimation(open);
                mNameEditText.requestFocus();

                hidden = false;
            }
        }

        void turnOnDeleteButton() {
            if (!rotated) {
                mCreateButton.startAnimation(rotate_forward);
                rotated = true;
            }
        }

        void turnOffDeleteButton() {
            if (rotated) {
                mCreateButton.startAnimation(rotate_back);
                rotated = false;
            }
        }
    }
}