package com.augimas.android.adapters;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.dialogs.ConfirmActionDialog;
import com.augimas.android.helpers.FragmentHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

/**
 * Created on 8/6/17.
 */

public class GeneralBrandingAdapter extends RecyclerView.Adapter<GeneralBrandingAdapter.ViewHolder> {
    private Activity activity;
    private View containingView;
    private BrandingElement brandingName;
    private Animation open,close,rotate_forward,rotate_back,bounceFasterAnimation;
    //    Adapter Constructor
    public GeneralBrandingAdapter(final BrandingElement brandingName, final View containingView, final Activity activity) {
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
        final int HINT_ID;
        boolean multiline = false;
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_general_brand_item, parent, false);
        switch (brandingName.getType()) {
            case DOMAIN_NAME:
                HINT_ID = R.string.domain_name_text;
                break;
            case SOCIAL_MEDIA_NAME:
                HINT_ID = R.string.social_media_name_text;
                break;
            case MISSION_STATEMENT:
                HINT_ID = R.string.mission_statement_text;
                multiline = true;
                break;
            case PRODUCTS_SERVICES:
                HINT_ID = R.string.product_service_text;
                multiline = true;
                break;
            default: return null;
        }
        return new GeneralBrandingAdapter.ViewHolder(view,HINT_ID,multiline);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
        holder.hideButtons();
        setupView(holder,POSITION);
        addTextChangedListener(holder,position);
        addOnEditorActionListener(holder,POSITION);
        addOnClickListener(brandingName,holder,POSITION);
        holder.mView.startAnimation(bounceFasterAnimation);
        addOnFocusChangeListener(brandingName,holder,POSITION);
    }
    private void addTextChangedListener(final ViewHolder holder, final int position) {
        holder.mBrandItemInputText.addTextChangedListener(new TextWatcher() {
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
            public void afterTextChanged(Editable s) {
                if (position > brandingName.getData().size()-1) {

                } else {
                    if (!s.toString().equals(brandingName.getData().get(position))) {
                        if (holder.buttonRotated) {
                            holder.turnButtonToCreate();
                        }
                    }
                }
                if (holder.buttonRotated) {

                }
            }
        });
    }
    private String getCharsPerLineString(String text){
        String charsPerLineString = "";
        int maxLength = 25;
        while (text.length() > maxLength) {

            String buffer = text.substring(0, maxLength);
            charsPerLineString = charsPerLineString + buffer + System.getProperty("line.separator");
            text = text.substring(maxLength);
        }
        charsPerLineString = charsPerLineString + text;
        return charsPerLineString;
    }
    private void setupView(final ViewHolder holder, final int POSITION) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        if (getItemCount() == 1) {
                            holder.mView.setVisibility(View.GONE);
                            containingView.findViewById(R.id.branding_element_general_layout).setVisibility(View.GONE);
                            containingView.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
                            // Set alternative View for display when there is no data.
                        } else {
                            if (POSITION > brandingName.getData().size()-1) {
                                holder.hideView();
                            } else {
                                holder.mBrandItemInputText.setText(getCharsPerLineString(brandingName.getData().get(POSITION)));
                                holder.editing = false;
                                holder.mBrandItemInputText.setEnabled(false);
                                holder.mBrandItemInputText.setClickable(false);
                                holder.revealInputAndTurnButtonToDelete(false);
                            }
                        }
                    } else {
                        if (getItemCount() == 1) {
                            containingView.findViewById(R.id.branding_element_general_layout).setVisibility(View.VISIBLE);
                            containingView.findViewById(R.id.no_content).setVisibility(View.GONE);
                        }
                        if (POSITION > brandingName.getData().size()-1) {
                            holder.hideInput(false);
                            holder.revealCreateButton();
                        } else {
                            holder.editing = false;
                            holder.revealInputAndTurnButtonToDelete(false);
//                            holder.mBrandItemInputText.setText(getCharsPerLineString(brandingName.getData().get(POSITION)));
                            holder.mBrandItemInputText.setText(brandingName.getData().get(POSITION));
                        }

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void handleInput(final ViewHolder holder, final int position) {
        if (BrandingElement.checkInput(holder.mBrandItemInputText.getText().toString().trim(), brandingName)) {
            if (brandingName.getType() == BrandingElement.ElementType.DOMAIN_NAME) {
                for(String domain:brandingName.getData()) {
                    if (holder.mBrandItemInputText.getText().toString().trim().equals(domain)) {
                        return;
                    }
                }
            }
            if (position == brandingName.getData().size()) {
                brandingName.getData().add(holder.mBrandItemInputText.getText().toString().trim());
                sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.ADD,holder.mBrandItemInputText.getText().toString().trim(), null);
            } else if (position < brandingName.getData().size()){
                if (!holder.mBrandItemInputText.getText().toString().trim().equals(brandingName.getData().get(position))) {
                    final String previousName = brandingName.getData().get(position).trim();
                    brandingName.getData().set(position,holder.mBrandItemInputText.getText().toString().trim());
                    sendBrandingElementNotification(brandingName, RecentActivity.ActivityVerbType.UPDATE,previousName, holder.mBrandItemInputText.getText().toString().trim());
                }
            }
            holder.editing = false;
            holder.creating = false;
        } else {
            // Improper Input
            holder.mBrandItemInputText.setText("");
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
        holder.mBrandItemInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                holder.hideCreateButton();
                holder.revealInputAndTurnButtonToDelete(true);
                holder.creating = true;
            }
        });
        holder.mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.buttonRotated) {
                    holder.editing = false;
                    holder.creating = false;
                    if (!holder.mBrandItemInputText.getText().toString().equals("")) {
                        final String previousName;
                        if (POSITION == brandingName.getData().size()) {
                            previousName = holder.mBrandItemInputText.getText().toString();
                        } else {
                            previousName = brandingName.getData().get(POSITION);
                        }
                        new ConfirmActionDialog(previousName,previousName,null, brandingName,activity);
                    } else {
                        holder.hideModifyButton();
                        holder.revealCreateButton();
                        holder.turnButtonToCreate();
                        holder.hideInput(true);
                    }
                } else {
                    if (!holder.mBrandItemInputText.getText().toString().equals("")) {
                        handleInput(holder,POSITION);
                    } else {
                        holder.creating = false;
                        holder.hideModifyButton();
                        holder.revealCreateButton();
                        holder.turnButtonToCreate();
                        holder.hideInput(true);
                    }
                }
            }
        });
    }
    private void addOnFocusChangeListener(final BrandingElement brandingName, final ViewHolder holder, final int POSITION) {
        holder.mBrandItemInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                        if (!holder.mBrandItemInputText.getText().toString().equals("")) {
//                            if (holder.editing) {
//                                handleInput(holder,POSITION);
//                            }
                        } else {
                            if (brandingName.getData().size() >= POSITION+1) {
                                holder.hideView();
                                String previousText = brandingName.getData().get(POSITION);
                                new ConfirmActionDialog(previousText,previousText,null, brandingName,activity);
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
        final AppCompatButton mCreateButton;
        final AppCompatButton mModifyButton;
        public final LinearLayoutCompat layout;
        public final LinearLayoutCompat content;
        final AppCompatEditText mBrandItemInputText;

        public ViewHolder(View view, int HINT_ID, boolean multiline) {
            super(view);
            mView = view;
            content         = view.findViewById(R.id.brand_name_content);
            mCreateButton   = view.findViewById(R.id.item_create_element);
            mModifyButton   = view.findViewById(R.id.item_modify_element);
            mBrandItemInputText = view.findViewById(R.id.input_brand_item);
            mBrandItemInputText.setHint(HINT_ID);
            if (multiline) {
                mBrandItemInputText.setSingleLine(false);
                mBrandItemInputText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
//                mBrandItemInputText.setLines(2);
//                mBrandItemInputText.setMaxLines(5);
                mBrandItemInputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
            layout          = view.findViewById(R.id.input_layout_brand_name);
        }
        void hideView() {
            hideButtons();
            mView.startAnimation(close);
        }
        void hideButtons(){
            hideCreateButton();
            hideModifyButton();
        }
        void hideModifyButton(){
            mModifyButton.setEnabled(false);
            mModifyButton.setClickable(false);
            mModifyButton.startAnimation(close);
            mModifyButton.setVisibility(View.GONE);
        }
        void hideCreateButton() {
            mCreateButton.setEnabled(false);
            mCreateButton.setClickable(false);
            mCreateButton.startAnimation(close);
            mCreateButton.setVisibility(View.GONE);
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
                    mBrandItemInputText.clearFocus();
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
                    mBrandItemInputText.requestFocus();
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
            return super.toString() + " '" + mBrandItemInputText.getText() + "'";
        }
    }
}