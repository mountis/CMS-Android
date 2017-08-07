package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;

import java.util.ArrayList;

/**
 * Created on 8/6/17.
 */

public class DomainNamesAdapter extends RecyclerView.Adapter<DomainNamesAdapter.ViewHolder> {
    private Activity activity;
    private BrandingElement domainName;
    private ArrayList<String> domainNameExtensions = new ArrayList<>();
    private Animation open,close,rotate_forward,rotate_back;

    //    Adapter Constructor
    public DomainNamesAdapter(final Activity activity, BrandingElement domainName) {
        this.activity = activity;
        this.domainName = domainName;
        this.domainNameExtensions = domainName.getContents();
        if (domainNameExtensions.size() >= 1) {
            this.domainNameExtensions.remove(0);
        }
        open = AnimationUtils.loadAnimation(activity, R.anim.open);
        close = AnimationUtils.loadAnimation(activity,R.anim.close);
        rotate_forward = AnimationUtils.loadAnimation(activity,R.anim.rotate_forward);
        rotate_back = AnimationUtils.loadAnimation(activity,R.anim.rotate_back);
    }
    //    Adapter Methods
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_domain_name_single, parent, false);
        return new DomainNamesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position > domainName.getData().size()-1) {
            holder.hidden = true;
            holder.layout.startAnimation(close);
        } else {
            holder.rotated = true;
            holder.mCreateButton.startAnimation(rotate_forward);
            holder.hidden = false;
            holder.layout.startAnimation(open);
            holder.mDomainEditText.setText(domainName.getData().get(position));
            holder.mDomainEditText.setHint("");
        }

        holder.mDomainEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (position == domainName.getData().size()+1) {

                    } else {
                        if (holder.mDomainEditText.getText().toString().equals("") && holder.rotated) {
                            holder.mCreateButton.startAnimation(rotate_back);
                            holder.rotated = false;
                        }
                    }

                } else {
                    holder.creating = false;
                    if (holder.hidden) {
                        holder.layout.startAnimation(open);
                        if (position < getItemCount() && !holder.rotated) {
                            holder.mCreateButton.startAnimation(rotate_forward);
                            holder.rotated = true;
                        }
                    }

                    if (!holder.mDomainEditText.getText().toString().equals("")) {
                        if (position == domainName.getData().size()+1) {
                            domainName.getData().add(holder.mDomainEditText.getText().toString());
                        } else {
                            domainName.getData().set(position, holder.mDomainEditText.getText().toString());
                        }

                        Backend.update(domainName, activity);
                    } else {
                        if (domainName.getData().size() >= position+1) {
                            domainName.getData().remove(position);
                            holder.layout.startAnimation(close);
                            holder.hidden = false;
                            holder.mCreateButton.startAnimation(rotate_back);
                            holder.rotated = false;
                            Backend.update(domainName, activity);
                        }
                    }
                }
            }
        });

        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create New Domain Name
                if (holder.hidden) {
                    holder.layout.startAnimation(open);
                    holder.hidden = false;
                    holder.mCreateButton.startAnimation(rotate_forward);
                    holder.rotated = true;
                    holder.creating = true;
                } else {
                    // delete data if there
                    if (holder.creating) {
                        if (!holder.mDomainEditText.getText().toString().equals("")) {
                            if (position == domainName.getData().size()) {
                                domainName.getData().add(holder.mDomainEditText.getText().toString());
                            }
                            Backend.update(domainName, activity);
                        }
                    } else {
                        if (holder.rotated) {
                            if (!holder.mDomainEditText.getText().toString().equals("")) {
                                if (position == domainName.getData().size()) {
                                    domainName.getData().remove(holder.mDomainEditText.getText().toString());
                                } else {
                                    domainName.getData().remove(position);
                                }

                                Backend.update(domainName, activity);
                            }

                            holder.layout.startAnimation(close);
                            holder.hidden = true;
                            holder.mCreateButton.startAnimation(rotate_back);
                            holder.rotated = false;
                        }
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return domainName.getData().size()+1;
    }
    //    View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public AppCompatEditText mDomainEditText;
        public AppCompatButton mCreateButton;
        public LinearLayoutCompat layout;
        public LinearLayoutCompat content;
        public boolean rotated = false;
        public boolean hidden = true;
        public boolean creating = false;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            content = (LinearLayoutCompat) view.findViewById(R.id.domain_name_content);
            mDomainEditText = (AppCompatEditText) view.findViewById(R.id.input_domain_name);
            mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
            layout = (LinearLayoutCompat) view.findViewById(R.id.input_layout_domain_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDomainEditText.getText() + "'";
        }
    }
}