package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
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
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

/**
 * Created on 8/6/17.
 */

public class BrandingNamesAdapter extends RecyclerView.Adapter<BrandingNamesAdapter.ViewHolder> {
    private Activity activity;
    private View containingView;
    private BrandingElement brandingName;
    private Animation open,close,rotate_forward,rotate_back,bounceFasterAnimation;
    //    Adapter Constructor
    public BrandingNamesAdapter(final BrandingElement brandingName, final View containingView, final Activity activity) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (brandingName.getType()) {
            case DOMAIN_NAME: view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_domain_name_single, parent, false);
                break;
            case SOCIAL_MEDIA_NAME: view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_social_media_name_single, parent, false);
                break;
            case MISSION_STATEMENT: view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_mission_statements_single, parent, false);
                break;
            case PRODUCTS_SERVICES: view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_product_service_single, parent, false);
                break;
            default: return null;
        }
        return new BrandingNamesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
        setupView(holder,POSITION);
        holder.mView.startAnimation(bounceFasterAnimation);
        addTextChangedListener(holder);
        addOnEditorActionListener(holder,POSITION);
        addOnClickListener(brandingName,holder,POSITION);
        addOnFocusChangeListener(brandingName,holder,POSITION);
    }
    private void addTextChangedListener(final ViewHolder holder) {
        holder.mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.editing) {
                    if (s.length() != 0) {
                        holder.turnButtonToCreate();
                    } else {
                        holder.turnButtonToDelete();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void setupView(final ViewHolder holder, final int POSITION) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        if (getItemCount() == 1) {
                            holder.mView.setVisibility(View.GONE);
                            if (brandingName.getType() == BrandingElement.ElementType.DOMAIN_NAME) {
                                containingView.findViewById(R.id.branding_element_domain_name_layout).setVisibility(View.GONE);
                            } else if (brandingName.getType() == BrandingElement.ElementType.SOCIAL_MEDIA_NAME) {
                                containingView.findViewById(R.id.branding_element_social_media_name_layout).setVisibility(View.GONE);
                            } else if (brandingName.getType() == BrandingElement.ElementType.MISSION_STATEMENT) {
                                containingView.findViewById(R.id.branding_element_mission_statement_layout).setVisibility(View.GONE);
                            } else {
                                containingView.findViewById(R.id.branding_element_product_service_layout).setVisibility(View.GONE);
                            }
                            containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                            // Set alternative View for display when there is no data.
                        } else {
                            if (POSITION > brandingName.getData().size()-1) {
                                holder.hideView();
                            } else {
                                holder.mNameEditText.setText(brandingName.getData().get(POSITION));
                                holder.mCreateButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.hideButton();
                                    }
                                });
                                holder.editing = false;
                                holder.mNameEditText.setEnabled(false);
                                holder.mNameEditText.setClickable(false);
                                holder.revealInputAndTurnButtonToDelete(false);
                            }
                        }
                    } else {
                        if (getItemCount() == 1) {
                            if (brandingName.getType() == BrandingElement.ElementType.DOMAIN_NAME) {
                                containingView.findViewById(R.id.branding_element_domain_name_layout).setVisibility(View.VISIBLE);
                            } else if (brandingName.getType() == BrandingElement.ElementType.SOCIAL_MEDIA_NAME) {
                                containingView.findViewById(R.id.branding_element_social_media_name_layout).setVisibility(View.VISIBLE);
                            } else if (brandingName.getType() == BrandingElement.ElementType.MISSION_STATEMENT) {
                                containingView.findViewById(R.id.branding_element_mission_statement_layout).setVisibility(View.VISIBLE);
                            } else {
                                containingView.findViewById(R.id.branding_element_product_service_layout).setVisibility(View.VISIBLE);
                            }
                            containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                        }
                        if (POSITION > brandingName.getData().size()-1) {
                            holder.hideInput(false);
                        } else {
                            holder.editing = false;
                            holder.revealInputAndTurnButtonToDelete(false);
                            holder.mNameEditText.setText(brandingName.getData().get(POSITION));
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void handleInput(final ViewHolder holder, final int position) {
        if (BrandingElement.checkInput(holder.mNameEditText.getText().toString().trim(), brandingName.getType())) {
            if (position == brandingName.getData().size()) {
                brandingName.getData().add(holder.mNameEditText.getText().toString().trim());
                sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.ADD,holder.mNameEditText.getText().toString().trim(), null);
            } else if (position < brandingName.getData().size()){
                if (!holder.mNameEditText.getText().toString().trim().equals(brandingName.getData().get(position))) {
                    final String previousName = brandingName.getData().get(position).trim();
                    brandingName.getData().set(position,holder.mNameEditText.getText().toString().trim());
                    sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.UPDATE,previousName, holder.mNameEditText.getText().toString().trim());
                }
            }
            holder.editing = false;
            holder.creating = false;
        } else {
            // Improper Input
            holder.mNameEditText.setText("");
            switch (brandingName.getType()) {
                case DOMAIN_NAME:
                    FragmentHelper.display(TOAST,R.string.tld_not_valid,holder.mView.getRootView());
                    break;
                default:
                    break;
            }
        }
    }
    private void addOnEditorActionListener(final ViewHolder holder,final int POSITION) {
        holder.mNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleInput(holder,POSITION);
                }
                return false;
            }
        });
    }
    private void addOnClickListener(final BrandingElement brandingName, final ViewHolder holder, final int POSITION) {
        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.inputHidden) {
                    holder.hideButton();
                    holder.revealInputAndTurnButtonToDelete(true);
                    holder.revealButton();
                    holder.creating = true;
                    holder.mCreateButton.startAnimation(rotate_forward);
                } else {
                    if (holder.creating) {
                        if (!holder.mNameEditText.getText().toString().equals("")) {
                            handleInput(holder,POSITION);
                        } else {
                            holder.creating = false;
                            holder.turnButtonToCreate();
                            holder.hideButton();
                            holder.hideInput(true);
                            holder.revealButton();
                        }
                    } else {
                        if (holder.buttonRotated) {
                            holder.editing = false;
                            holder.creating = false;
                            holder.hideView();
                            if (!holder.mNameEditText.getText().toString().equals("")) {
                                if (POSITION == brandingName.getData().size()) {
                                    brandingName.getData().remove(holder.mNameEditText.getText().toString());
                                    sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.REMOVE, holder.mNameEditText.getText().toString(), null);
                                } else {
                                    final String previousName = brandingName.getData().get(POSITION);
                                    brandingName.getData().remove(POSITION);
                                    sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.REMOVE,previousName, null);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    private void addOnFocusChangeListener(final BrandingElement brandingName, final ViewHolder holder, final int POSITION) {
        holder.mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    holder.editing = true;
                    holder.creating = true;
                    if (holder.inputHidden) {
                        holder.revealInput(true);
                        if (POSITION < getItemCount() && !holder.buttonRotated) {
                            holder.turnButtonToDelete();
                        }
                    } else {
                        if (!holder.mNameEditText.getText().toString().equals("")) {
                            handleInput(holder,POSITION);
                        } else {
                            if (brandingName.getData().size() >= POSITION+1) {
                                holder.hideView();
                                brandingName.getData().remove(POSITION);
                                sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.REMOVE, holder.mNameEditText.getText().toString(), null);
                            }
                        }
                    }
                }
            }
        });
    }
    private void sendBrandingElementNotification(final BrandingElement brandingName, final RecentActivity.ActivityVerbType verbType, final String extraString, final String extraString2) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayMap<FirebaseEntity.EntityType,Team> teamMap = Team.toClientAndHostTeamMap(dataSnapshot,brandingName.getTeamUID());
                    Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User currentUser = new User(dataSnapshot);

                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                                final RecentActivity hostRecentActivity;
                                final RecentActivity clientRecentActivity;

                                switch (verbType) {
                                    case UPDATE:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName(), extraString,extraString2);
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType, "", extraString, extraString2);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType, "",extraString,extraString2);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType, "",extraString,extraString2);
                                        }
                                        break;
                                    default:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName(), extraString);
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType, "",extraString);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType, "",extraString);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType,"", extraString);
                                        }
                                        break;
                                }

                                Backend.sendUpstreamNotification(hostRecentActivity,teamMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),brandingName.getType().toString(),activity, true);
                                Backend.sendUpstreamNotification(clientRecentActivity,teamMap.get(FirebaseEntity.EntityType.CLIENT).getUID(),currentUser.getUID(),brandingName.getType().toString(),activity, true);

                                Backend.update(brandingName, activity);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
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
        AppCompatButton mCreateButton;
        AppCompatEditText mNameEditText;
        public LinearLayoutCompat layout;
        public LinearLayoutCompat content;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameEditText   = (AppCompatEditText) view.findViewById(R.id.input_brand_name);
            mCreateButton   = (AppCompatButton) view.findViewById(R.id.item_create_element);
            content         = (LinearLayoutCompat) view.findViewById(R.id.brand_name_content);
            layout          = (LinearLayoutCompat) view.findViewById(R.id.input_layout_brand_name);
        }

        void hideView() {
            hideButton();
            mView.startAnimation(close);

        }
        void hideButton() {
            mCreateButton.setEnabled(false);
            mCreateButton.setClickable(false);
            mCreateButton.startAnimation(close);
        }
        void revealButton() {
            mCreateButton.setEnabled(true);
            mCreateButton.setClickable(true);
            mCreateButton.startAnimation(open);
        }
        void turnButtonToDelete() {
            if (!buttonRotated) {
                buttonRotated = true;
                mCreateButton.startAnimation(rotate_forward);
            }
        }
        void turnButtonToCreate() {
            if (buttonRotated) {
                buttonRotated = false;
                mCreateButton.startAnimation(rotate_back);
            }
        }
        void hideInput(boolean manual) {
            if (!inputHidden) {
                layout.startAnimation(close);
                layout.setVisibility(View.GONE);
                if (manual) {
                    editing = false;
                    mNameEditText.clearFocus();
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
                    mNameEditText.requestFocus();
                }
            }
        }
        void revealInputAndTurnButtonToDelete(final boolean manual) {
            revealInput(manual);
            turnButtonToDelete();
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mNameEditText.getText() + "'";
        }
    }
}