package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.content.branding_elements.Branding;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.backend.Backend;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Strings.NO_VALUE;
import static com.marionthefourth.augimas.classes.constants.Constants.Strings.YES_VALUE;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.update;

public class TLDAdapter extends RecyclerView.Adapter<TLDAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement domainName;
    private ArrayList<String> domainNameExtensions = new ArrayList<>();
//    Adapter Constructor
    public TLDAdapter(final Activity activity, BrandingElement domainName) {
        this.activity = activity;
        this.domainName = domainName;
        this.domainNameExtensions = domainName.getContents();
        if (domainNameExtensions.size() >= 1) {
            this.domainNameExtensions.remove(0);
        }
    }
//    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_domain, parent, false);
        return new TLDAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (domainNameExtensions.get(position).equals(YES_VALUE)) {
            holder.mDomainNameAvailableCheckBox.setChecked(true);
        }

        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser == null || !currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                        holder.mDomainNameAvailableCheckBox.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        holder.mDomainNameLabel.setText(Branding.TLD.getTLD(position).toString());

        holder.mDomainNameAvailableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = YES_VALUE;
                if (!isChecked) {
                    value = NO_VALUE;
                }

                final String VALUE = value;
                Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(domainName.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                            elementItem.getContents().set(position+1,VALUE);
                            update(elementItem, activity);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return domainNameExtensions.size();
    }
//    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AppCompatTextView mDomainNameLabel;
        public AppCompatCheckBox mDomainNameAvailableCheckBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDomainNameAvailableCheckBox = (AppCompatCheckBox) view.findViewById(R.id.domain_name_available_check_box);
            mDomainNameLabel = (AppCompatTextView) view.findViewById(R.id.item_label_domain);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDomainNameLabel.getText() + "'";
        }
    }
}
