package com.augimas.android.adapters;

import android.app.Activity;
import android.graphics.Color;
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
import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.dialogs.ColorDialog;

import static com.augimas.android.backend.Backend.getCurrentUser;

/**
 * Created on 8/18/17.
 */

final class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {
    private int index;
    private Activity activity;
    private View containingView;
    private BrandingElement brandingName;
    private Animation open;
    //    Adapter Constructor
    ColorsAdapter(final BrandingElement brandingName, final View containingView, final Activity activity, final int index) {
        this.index = index;
        this.activity = activity;
        this.brandingName = brandingName;
        this.containingView = containingView;
        open = AnimationUtils.loadAnimation(activity, R.anim.open);
    }
    //    Adapter Methods
    @Override
    public ColorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_color_single, parent, false);
        return new ColorsAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ColorsAdapter.ViewHolder holder, int position) {
        final int POSITION = holder.getAdapterPosition();
        setupView(holder,POSITION);
    }
    private void setupView(final ColorsAdapter.ViewHolder holder, final int POSITION) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID(): null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (new User(dataSnapshot).hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        if (POSITION > numberOfColors()-1) {
                            holder.displayView();
                            holder.revealButton();
                        } else {
                            if (brandingName.getData().size() > index) {
                                holder.mCreateButton.setBackgroundColor(Color.parseColor(getViewData(brandingName.getData().get(index),POSITION)));
                            }
                            holder.mCreateButton.setText("");
                        }
                        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (holder.mCreateButton.getText().equals(activity.getString(R.string.plus_sign))) {
                                    new ColorDialog(index,POSITION,null,brandingName,containingView,activity);
                                } else {
                                    new ColorDialog(index,POSITION,getViewData(brandingName.getData().get(index),POSITION),brandingName,containingView,activity);
                                }
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private String getViewData(String data, int position) {
        return data.split(",")[position];
    }
    @Override
    public int getItemCount() {
        return numberOfColors()+1;
    }
    int numberOfColors() {
        String data = "";
        if (brandingName.getData().size() > 0) {
            if (brandingName.getData().size()-1 >= index) {
                data = brandingName.getData().get(index);
            }
        }
        int number = data.split(",").length;
        if (number > 1 || !data.split(",")[0].equals("")) {
            return number;
        } else {
            return 0;
        }
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final AppCompatButton mCreateButton;
        public final LinearLayoutCompat content;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCreateButton           = (AppCompatButton) view.findViewById(R.id.button_color);
            content                 = (LinearLayoutCompat) view.findViewById(R.id.color_item_content);
        }
        void displayView() {
            revealButton();
            mView.startAnimation(open);
        }
        void revealButton() {
            mCreateButton.setEnabled(true);
            mCreateButton.setClickable(true);
            mCreateButton.startAnimation(open);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mCreateButton.toString()+ "'";
        }
    }
}