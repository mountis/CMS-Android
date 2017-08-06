package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;

import java.util.ArrayList;

/**
 * Created by MGR4 on 8/6/17.
 */

public final class BrandingElementContentAdapter extends RecyclerView.Adapter<BrandingElementContentAdapter.ViewHolder> {

    public BrandingElementContentAdapter(Activity activity, ArrayList<BrandingElement> elements, Team team, BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener mListener) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_branding_element_index, parent, false);
        return new BrandingElementContentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public BrandingElement elementItem;
        public final AppCompatButton mBrandingAddItemButton;
        public final AppCompatButton getmBrandingCloseItemsButton;
        public final AppCompatTextView mBrandingElementNameLabel;
        public final AppCompatImageButton mBrandingElementStatus;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBrandingAddItemButton = (AppCompatButton) view.findViewById(R.id.item_button_add_item);
            getmBrandingCloseItemsButton = (AppCompatButton) view.findViewById(R.id.item_button_close_list);
            mBrandingElementStatus = (AppCompatImageButton) view.findViewById(R.id.item_branding_element_status);
            mBrandingElementNameLabel = (AppCompatTextView) view.findViewById(R.id.item_label_branding_element_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBrandingElementNameLabel.getText() + "'";
        }
    }
}
