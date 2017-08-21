package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
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
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.DeviceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

/**
 * Created on 8/18/17.
 */

public final class LogoAdapter extends RecyclerView.Adapter<LogoAdapter.ViewHolder> {
    private Activity activity;
    private View containingView;
    private BrandingElement brandingName;
    private Animation open,close,rotate_forward,rotate_back,bounceFasterAnimation;
    //    Adapter Constructor
    public LogoAdapter(final BrandingElement brandingName, final View containingView, final Activity activity) {
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
    public LogoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_logo_single, parent, false);
        return new LogoAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final LogoAdapter.ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
        setupView(holder,POSITION);
        holder.mView.startAnimation(bounceFasterAnimation);
        addOnClickListener(brandingName,holder,POSITION);
    }
    private void setupView(final LogoAdapter.ViewHolder holder, final int POSITION) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        if (getItemCount() == 1) {
                            holder.mView.setVisibility(View.GONE);
                            containingView.findViewById(R.id.branding_element_logo_layout).setVisibility(View.GONE);
                            containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                            // Set alternative View for display when there is no data.
                        } else {
                            if (POSITION > brandingName.getData().size()-1) {
                                holder.hideView();
                            } else {
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
                        if (getItemCount() == 1) {
                            containingView.findViewById(R.id.branding_element_logo_layout).setVisibility(View.VISIBLE);
                            containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                        }
                        if (POSITION > brandingName.getData().size()-1) {
                            holder.hideInput(false);
                        } else {
                            holder.editing = false;
                            holder.imageView.setImageBitmap(DeviceHelper.decodeBase64(brandingName.getData().get(POSITION)));
                            holder.turnButtonToDelete();
                            holder.revealImageView();
                            holder.inputHidden = false;
//                            holder.revealInputAndTurnButtonToDelete(false);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void addOnClickListener(final BrandingElement brandingName, final LogoAdapter.ViewHolder holder, final int POSITION) {
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
                    if (holder.buttonRotated) {
                        holder.editing = false;
                        holder.creating = false;
                        holder.hideButton();
                        holder.turnButtonToCreate();
                        holder.hideInput(true);
                        holder.revealButton();
                        if (POSITION < brandingName.getData().size()) {
                            holder.hideView();
                            brandingName.getData().remove(POSITION);
                            Backend.update(brandingName,activity);
                            sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.REMOVE);
                        }
                    }
                }
            }
        });
        View[] views = {holder.uploadButton,holder.itemView};
        for (final View view:views) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        JSONObject json  = new JSONObject();
                        json.put(Constants.Strings.Fields.SELECTED_INDEX,POSITION);
                        json.put(Constants.Strings.UIDs.BRANDING_ELEMENT_UID,brandingName.getUID());
                        json.put(Constants.Strings.Fields.BRANDING_ELEMENT_TYPE,BrandingElement.ElementType.LOGO.toString());
                        DeviceHelper.writeToJSONFile(json,activity);
                        photoPickerIntent.setType("image/*");
                        activity.startActivityForResult(photoPickerIntent, Constants.Ints.Results.RESULT_LOAD_IMG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void sendBrandingElementNotification(final BrandingElement brandingName, final RecentActivity.ActivityVerbType verbType) {
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
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType);
                                        }
                                        break;
                                    default:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType);
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
        public AppCompatImageView imageView;
        public AppCompatImageButton uploadButton;

        public LinearLayoutCompat content;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            uploadButton = (AppCompatImageButton) view.findViewById(R.id.upload_image_button);
            mCreateButton   = (AppCompatButton) view.findViewById(R.id.item_create_element);
            content         = (LinearLayoutCompat) view.findViewById(R.id.brand_name_content);
            imageView          = (AppCompatImageView) view.findViewById(R.id.uploaded_image_view);
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
        void hideImageView() {
            imageView.startAnimation(close);
            imageView.setVisibility(View.GONE);
        }
        void revealImageView() {
            imageView.startAnimation(open);
            imageView.setVisibility(View.VISIBLE);
        }
        void hideInput(boolean manual) {
            if (!inputHidden) {
                uploadButton.startAnimation(close);
                uploadButton.setVisibility(View.GONE);
                if (manual) {
                    editing = false;
                }
                inputHidden = true;
            }
        }
        void revealInput(boolean manual) {
            if (inputHidden) {
                inputHidden = false;
                uploadButton.startAnimation(open);
                uploadButton.setVisibility(View.VISIBLE);
                if (manual) {
                    editing = true;
                }
            }
        }
        void revealInputAndTurnButtonToDelete(final boolean manual) {
            revealInput(manual);
            turnButtonToDelete();
        }
        @Override
        public String toString() {
            return super.toString() + " '" + uploadButton.toString() + "'";
        }
    }
}