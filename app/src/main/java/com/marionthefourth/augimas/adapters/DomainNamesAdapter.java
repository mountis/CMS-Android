package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    //    Adapter Constructor
    public DomainNamesAdapter(final Activity activity, BrandingElement domainName) {
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
                .inflate(R.layout.list_item_domain_name_single, parent, false);
        return new DomainNamesAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position > domainName.getData().size()-1) {
            holder.mDomainEditText.setVisibility(View.GONE);
        } else {
            holder.mDomainEditText.setVisibility(View.VISIBLE);
            holder.mDomainEditText.setText(domainName.getData().get(position));

        }
        holder.mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create New Domain Name
                if (holder.mDomainEditText.getVisibility() != View.VISIBLE) {
                    holder.mDomainEditText.setVisibility(View.VISIBLE);
                } else {
                    // save data
                    if (!holder.mDomainEditText.getText().toString().equals("")) {
                        if (position == domainName.getData().size()) {
                            domainName.getData().add(holder.mDomainEditText.getText().toString());
                        } else {
                            domainName.getData().set(position, holder.mDomainEditText.getText().toString());
                        }

                        Backend.update(domainName, activity);
//                        notifyDataSetChanged();
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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDomainEditText = (AppCompatEditText) view.findViewById(R.id.input_domain_name);
            mCreateButton = (AppCompatButton) view.findViewById(R.id.item_create_element);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDomainEditText.getText() + "'";
        }
    }
}