package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
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
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

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
                .inflate(R.layout.list_item_brand_style_single, parent, false);
        return new BrandStylesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final BrandStylesAdapter.ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
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
                                holder.mCreateButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.hideButton();
                                    }
                                });
                                holder.editing = false;
                                holder.revealInputAndTurnButtonToDelete(false);
                            }
                        }
                    } else {
                        containingView.findViewById(R.id.branding_element_brand_style_layout).setVisibility(View.VISIBLE);

                        if (POSITION > brandingName.getData().size()-1) {
                            holder.hideInput(false);
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
                if (holder.inputHidden) {
                    holder.hideButton();
                    holder.revealInputAndTurnButtonToDelete(true);
                    holder.revealButton();
                    holder.creating = true;
                    holder.mCreateButton.startAnimation(rotate_forward);
                } else {
                    if (holder.creating) {
                        holder.creating = false;
                        holder.turnButtonToCreate();
                        holder.hideButton();
                        holder.hideInput(true);
                        holder.revealButton();
                    }

                    Backend.getReference(R.string.firebase_branding_elements_directory,activity).child(brandingName.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final BrandingElement element = new BrandingElement(dataSnapshot);
                                if (element.getData().size() != 0) {
                                    if (POSITION <= element.getData().size()-1) {
                                        element.getData().remove(POSITION);
                                        Backend.update(element,activity);
                                        sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.REMOVE,"style","");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
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
        final AppCompatButton mCreateButton;
        final RecyclerView mColorsRecyclerView;
        public final LinearLayoutCompat layout;
        public final LinearLayoutCompat content;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mColorsRecyclerView     = (RecyclerView) view.findViewById(R.id.brand_style_recycler_view);
            mCreateButton           = (AppCompatButton) view.findViewById(R.id.item_create_element);
            content                 = (LinearLayoutCompat) view.findViewById(R.id.brand_style_content);
            layout                  = (LinearLayoutCompat) view.findViewById(R.id.input_layout_brand_style);
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
//                    mNameEditText.clearFocus();
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
//                    mNameEditText.requestFocus();
                }
            }
        }
        void revealInputAndTurnButtonToDelete(final boolean manual) {
            revealInput(manual);
            turnButtonToDelete();
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mColorsRecyclerView.toString()+ "'";
        }
    }
}