package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;

import java.util.ArrayList;

public class DomainNameExtensionsAdapter extends RecyclerView.Adapter<DomainNameExtensionsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> domainNameExtensions = new ArrayList<>();

    public DomainNameExtensionsAdapter(Context context, ArrayList<String> domainNameExtensions) {
        this.context = context;
        this.domainNameExtensions = domainNameExtensions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_domain, parent, false);
        return new DomainNameExtensionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mDomainNameLabel.setText(domainNameExtensions.get(position));
    }

    @Override
    public int getItemCount() {
        return domainNameExtensions.size();
    }

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
