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

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

/**
 * Created on 8/6/17.
 */

public class BrandingNamesAdapter extends RecyclerView.Adapter<BrandingNamesAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement name;
    private ArrayList<String> nameExtensions = new ArrayList<>();
    private Animation open,close,rotate_forward,rotate_back;

    //    Adapter Constructor
    public BrandingNamesAdapter(final Activity activity, BrandingElement brandingName) {
        this.activity = activity;
        this.name = brandingName;
        this.nameExtensions = brandingName.getContents();
        if (nameExtensions.size() >= 1) {
            this.nameExtensions.remove(0);
        }
        open = AnimationUtils.loadAnimation(activity, R.anim.open);
        close = AnimationUtils.loadAnimation(activity,R.anim.close);
        rotate_forward = AnimationUtils.loadAnimation(activity,R.anim.rotate_forward);
        rotate_back = AnimationUtils.loadAnimation(activity,R.anim.rotate_back);
    }
    //    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (name.getType()) {

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position > name.getData().size()-1) {
            holder.hideInputLayout();
        } else {
            holder.revealAndTurnOn();
            holder.mNameEditText.setText(name.getData().get(position));
            holder.mNameEditText.setHint("");
        }

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
                    handleInput(holder,position,activity);
                }
                return false;
            }
        });

        holder.mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (holder.rotated) {
                        if (position == name.getData().size()+1) {

                        } else {
                            if (holder.mNameEditText.getText().toString().equals("") && holder.rotated) {

                            }
                        }
                    } else {

                    }
                } else {
                    holder.creating = false;
                    if (holder.hidden) {
                        holder.layout.startAnimation(open);
                        holder.hidden = false;
                        if (position < getItemCount() && !holder.rotated) {
                            holder.turnOnDeleteButton();
                        }
                    } else {
                        if (!holder.mNameEditText.getText().toString().equals("")) {
                            handleInput(holder,position,activity);
                        } else {
                            if (name.getData().size() >= position+1) {
                                name.getData().remove(position);
                                holder.hideAndTurnOff();
                                Backend.update(name, activity);
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
                            handleInput(holder,position,activity);
                        } else {
                            holder.creating = false;
                            holder.hideAndTurnOff();
                        }
                    } else {
                        if (holder.rotated) {
                            if (!holder.mNameEditText.getText().toString().equals("")) {
                                if (position == name.getData().size()) {
                                    name.getData().remove(holder.mNameEditText.getText().toString());
                                } else {
                                    name.getData().remove(position);
                                }

                                Backend.update(name, activity);
                            }

                            holder.hideAndTurnOff();
                        }
                    }
                }
            }
        });
    }

    private void handleInput(ViewHolder holder, int position, Activity activity) {
        if (BrandingElement.checkInput(holder.mNameEditText.getText().toString(), name.getType())) {
            if (position == name.getData().size()) {
                name.getData().add(holder.mNameEditText.getText().toString());
            }
            Backend.update(name, activity);
        } else {
            // Inproper Input
            holder.mNameEditText.setText("");
            switch (name.getType()) {
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
        return name.getData().size()+1;
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AppCompatEditText mNameEditText;
        public AppCompatButton mCreateButton;
        public LinearLayoutCompat layout;
        public LinearLayoutCompat content;
        public boolean rotated = false;
        public boolean hidden = true;
        public boolean creating = false;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            switch (name.getType()) {
                case DOMAIN_NAME:
                    content = (LinearLayoutCompat) view.findViewById(R.id.domain_name_content);
                    mNameEditText = (AppCompatEditText) view.findViewById(R.id.input_domain_name);
                    mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
                    layout = (LinearLayoutCompat) view.findViewById(R.id.input_layout_domain_name);
                    break;
                case SOCIAL_MEDIA_NAME:
                    content = (LinearLayoutCompat) view.findViewById(R.id.social_media_name_content);
                    mNameEditText = (AppCompatEditText) view.findViewById(R.id.input_social_media_name);
                    mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
                    layout = (LinearLayoutCompat) view.findViewById(R.id.input_layout_social_media_name);
                    break;
                default:
                    break;
            }

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameEditText.getText() + "'";
        }

        public void hideAndTurnOff() {
            hideInputLayout();
            turnOffDeleteButton();
        }

        public void revealAndTurnOn() {
            revealInputLayout();
            turnOnDeleteButton();
        }

        public void hideInputLayout() {
            if (!hidden) {
                layout.setVisibility(View.GONE);
                layout.startAnimation(close);
                mNameEditText.clearFocus();
                hidden = true;
            }
        }

        public void revealInputLayout() {
            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                layout.startAnimation(open);
                mNameEditText.requestFocus();

                hidden = false;
            }
        }

        public void turnOnDeleteButton() {
            if (!rotated) {
                mCreateButton.startAnimation(rotate_forward);
                rotated = true;
            }
        }

        public void turnOffDeleteButton() {
            if (rotated) {
                mCreateButton.startAnimation(rotate_back);
                rotated = false;
            }
        }
    }
}