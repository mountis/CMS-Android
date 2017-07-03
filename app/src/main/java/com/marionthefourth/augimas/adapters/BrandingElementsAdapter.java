package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.dialogs.ElementStatusDialog;

import java.util.ArrayList;

public final class BrandingElementsAdapter extends RecyclerView.Adapter<BrandingElementsAdapter.ViewHolder> {

    private Team team;
    private Context context;
    private ArrayList<BrandingElement> elements;
    private BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener mListener;

    public BrandingElementsAdapter(Context context, ArrayList<BrandingElement> elements, Team team, BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener mListener) {
        this.context = context;
        this.elements = elements;
        this.team = team;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_branding_element, parent, false);
        return new BrandingElementsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.elementItem = elements.get(position);

        holder.mBrandingElementNameLabel.setText(holder.elementItem.getHeader());
//        holder.mBrandingElementStatus.setBackground(holder.elementItem.getStatus().toDrawable(context));
        holder.mBrandingElementStatus.setBackgroundDrawable(holder.elementItem.getStatus().toDrawable(context));

        // Set view click listener
        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.OnBrandingElementsFragmentInteractionListener(context,holder.elementItem,team);
                }
            }
        });

        holder.mBrandingElementStatus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new ElementStatusDialog(holder.mView,holder);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public interface OnBrandingElementsFragmentInteractionListener {
        void OnBrandingElementsFragmentInteractionListener(Context context, BrandingElement elementItem, Team teamItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public BrandingElement elementItem;
        public final AppCompatTextView mBrandingElementNameLabel;
        public final AppCompatButton mBrandingElementStatus;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBrandingElementStatus = (AppCompatButton) view.findViewById(R.id.item_branding_element_status);
            mBrandingElementNameLabel = (AppCompatTextView) view.findViewById(R.id.item_label_branding_element_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBrandingElementNameLabel.getText() + "'";
        }
    }
}