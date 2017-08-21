package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

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

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

/**
 * Created on 8/18/17.
 */

public final class TargetAudienceAdapter extends RecyclerView.Adapter<TargetAudienceAdapter.ViewHolder>  {
    private Activity activity;
    private View containingView;
    private BrandingElement brandingName;
    private Animation open,close,rotate_forward,rotate_back,bounceFasterAnimation;
    //    Adapter Constructor
    public TargetAudienceAdapter(final BrandingElement brandingName, final View containingView, final Activity activity) {
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
    public TargetAudienceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_target_audience_single, parent, false);
        return new TargetAudienceAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final TargetAudienceAdapter.ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
        setupView(holder,POSITION);
        holder.mView.startAnimation(bounceFasterAnimation);
        addTextChangedListener(holder);
        addItemAdapters(brandingName,holder,POSITION,null);
        addOnClickListener(brandingName,holder,POSITION);
    }
    private void addItemAdapters(final BrandingElement brandingName, final ViewHolder holder, final int position, final String data) {
        final ArrayList<String> educationalLevels = new ArrayList<>();
        educationalLevels.add("Select your Education Level");
        educationalLevels.add("Less than high school");
        educationalLevels.add("High school diploma or equivalent");
        educationalLevels.add("Some college, no degree");
        educationalLevels.add("Postsecondary non-degree award");
        educationalLevels.add("Associate’s degree");
        educationalLevels.add("Bachelor’s degree");
        educationalLevels.add("Master’s degree");
        educationalLevels.add("Doctoral or professional degree");
        final ArrayList<String> incomeLevels = new ArrayList<>();
        incomeLevels.add("Select your Income Level");
        incomeLevels.add("No income.");
        incomeLevels.add("$0 to $15k");
        incomeLevels.add("$15k to $25k");
        incomeLevels.add("$25k to $50k");
        incomeLevels.add("$50k to $75k");
        incomeLevels.add("$75k to $100k");
        incomeLevels.add("$100k to $150k");
        incomeLevels.add("$150k to $200k");
        incomeLevels.add("$200k+");
        final ArrayList<String> sexes = new ArrayList<>();
        sexes.add("Select your Sex");
        sexes.add("Male");
        sexes.add("Female");
        holder.mSexSpinner.setAdapter(new ArrayAdapter<>(activity,R.layout.spinner_layout,R.id.spinner_text_item,sexes));
        holder.mIncomeSpinner.setAdapter(new ArrayAdapter<>(activity,R.layout.spinner_layout,R.id.spinner_text_item, incomeLevels));
        holder.mEducationSpinner.setAdapter(new ArrayAdapter<>(activity,R.layout.spinner_layout,R.id.spinner_text_item,educationalLevels));

        if (data != null && !data.equals("")) {
            holder.mSexSpinner.setSelection(Integer.valueOf(getViewData(data,0)));
            holder.mIncomeSpinner.setSelection(Integer.valueOf(getViewData(data,1)));
            holder.mEducationSpinner.setSelection(Integer.valueOf(getViewData(data,2)));
        } else {
            holder.mIncomeSpinner.setSelection(0);
            holder.mSexSpinner.setSelection(0);
            holder.mEducationSpinner.setSelection(0);
        }
    }
    private String getViewData(final String data, final int position) {
        String viewData = data;
        switch(position) {
            case 0:
                if (data.contains("sex")) {
                    viewData = data.substring(data.indexOf("sex:")+4);
                }
                break;
            case 1:
                if (data.contains("income")) {
                    viewData = data.substring(data.indexOf("income:")+7);
                }
                break;
            case 2:
                if (data.contains("education")) {
                    viewData = data.substring(data.indexOf("education:")+10);
                }
                break;
            case 3:
                if (data.contains("age")) {
                    viewData = data.substring(data.indexOf("age:")+4);
                }
                break;
            case 4:
                if (data.contains("occupation")) {
                    viewData = data.substring(data.indexOf("occupation:")+11);
                }
                break;
        }
        if (!viewData.equals(data)) {
            viewData = viewData.substring(0,viewData.indexOf("&"));
            return viewData;
        }
        return "";
    }
    private void addTextChangedListener(final TargetAudienceAdapter.ViewHolder holder) {
        AppCompatEditText[] inputs = {holder.mAgeEditText,holder.mOccupationEditText};
        for (AppCompatEditText input:inputs) {
            input.addTextChangedListener(new TextWatcher() {
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
    }
    private void setupView(final TargetAudienceAdapter.ViewHolder holder, final int POSITION) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        if (getItemCount() == 1) {
                            holder.mView.setVisibility(View.GONE);
                            containingView.findViewById(R.id.brand_name_content).setVisibility(View.GONE);
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
                                holder.setEnabled(false);
                                holder.setClickable(false);
                                holder.revealInputAndTurnButtonToDelete(false);
                            }
                        }
                    } else {
                        if (getItemCount() == 1) {
                            containingView.findViewById(R.id.brand_name_content).setVisibility(View.VISIBLE);
                            containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                        }
                        if (POSITION > brandingName.getData().size()-1) {
                            holder.hideInput(false);
                        } else {
                            holder.editing = false;
                            holder.revealInputAndTurnButtonToDelete(false);
                            holder.mAgeEditText.setText(getViewData(brandingName.getData().get(POSITION),3));
                            holder.mOccupationEditText.setText(getViewData(brandingName.getData().get(POSITION),4));
                            addItemAdapters(brandingName,holder,POSITION,brandingName.getData().get(POSITION));
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void handleInput(final TargetAudienceAdapter.ViewHolder holder, final int position) {
        if (BrandingElement.checkInput(holder.mOccupationEditText.getText().toString().trim(), brandingName.getType())) {
            if (position == brandingName.getData().size()) {
                brandingName.getData().add(holder.mOccupationEditText.getText().toString().trim());
                sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.ADD,holder.mOccupationEditText.getText().toString().trim(), null);
            } else if (position < brandingName.getData().size()){
                if (!holder.getText().equals(brandingName.getData().get(position))) {
                    final String previousName = brandingName.getData().get(position).trim();
                    brandingName.getData().set(position,holder.getText());
                    sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.UPDATE,previousName, holder.getText());
                }
            }
            holder.editing = false;
            holder.creating = false;
        } else {
            // Improper Input
            switch (brandingName.getType()) {
                case DOMAIN_NAME:
                    FragmentHelper.display(TOAST,R.string.tld_not_valid,holder.mView.getRootView());
                    break;
                default:
                    break;
            }
        }
    }
    private void addOnClickListener(final BrandingElement brandingName, final TargetAudienceAdapter.ViewHolder holder, final int POSITION) {
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
                        if (holder.inputsFilled()) {
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
                            if (holder.inputsFilled()) {
                                if (POSITION == brandingName.getData().size()) {
                                    brandingName.getData().remove(holder.getText());
                                    sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.REMOVE, holder.getText(), null);
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
    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        boolean editing = false;
        boolean creating = false;
        boolean inputHidden = true;
        boolean buttonRotated = false;
        public LinearLayoutCompat content;
        final AppCompatSpinner mSexSpinner;
        final AppCompatButton mCreateButton;
        final AppCompatEditText mAgeEditText;
        final LinearLayoutCompat layoutPart1;
        final LinearLayoutCompat layoutPart2;
        final AppCompatSpinner mIncomeSpinner;
        final AppCompatSpinner mEducationSpinner;
        final AppCompatEditText mOccupationEditText;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mAgeEditText = (AppCompatEditText) view.findViewById(R.id.input_age);
            mSexSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_sex);
            content = (LinearLayoutCompat) view.findViewById(R.id.brand_name_content);
            mIncomeSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_income);
            mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
            mEducationSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_education);
            mOccupationEditText = (AppCompatEditText) view.findViewById(R.id.input_occupation);
            layoutPart1 = (LinearLayoutCompat) view.findViewById(R.id.input_layout_brand_name_1);
            layoutPart2 = (LinearLayoutCompat) view.findViewById(R.id.input_layout_brand_name_2);
        }

        boolean inputsFilled() {
            return !mOccupationEditText.getText().toString().equals("") &&
                    !mAgeEditText.getText().toString().equals("") &&
                    mSexSpinner.getSelectedItemPosition() != 0 &&
                    mEducationSpinner.getSelectedItemPosition() != 0 &&
                    mIncomeSpinner.getSelectedItemPosition() != 0;
        }

        String getText() {
            return "sex:" + mSexSpinner.getSelectedItemPosition() +
                    "&income:" + mIncomeSpinner.getSelectedItemPosition()  +
                    "&education:" + mEducationSpinner.getSelectedItemPosition()  +
                    "&occupation:" + mOccupationEditText.getText().toString().trim() +
                    "&age:" + mAgeEditText.getText().toString().trim() + "&";
        }

        void setEnabled(boolean enabled) {
            AppCompatEditText[] inputs = {mAgeEditText,mOccupationEditText};
            for (AppCompatEditText input:inputs) {
                input.setEnabled(enabled);
            }
        }

        void setClickable(boolean clickable) {
            AppCompatEditText[] inputs = {mAgeEditText,mOccupationEditText};
            for (AppCompatEditText input:inputs) {
                input.setClickable(clickable);
            }
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
                inputHidden = true;
                layoutPart1.startAnimation(close);
                layoutPart1.setVisibility(View.GONE);
                layoutPart2.startAnimation(close);
                layoutPart2.setVisibility(View.GONE);
                if (manual) {
                    editing = false;
//                    mNameEditText.clearFocus();
                }
            }
        }

        void revealInput(boolean manual) {
            if (inputHidden) {
                inputHidden = false;
                layoutPart1.startAnimation(open);
                layoutPart1.setVisibility(View.VISIBLE);
                layoutPart2.startAnimation(open);
                layoutPart2.setVisibility(View.VISIBLE);
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
            return super.toString() + " '" + getText() + "'";
        }
    }
}