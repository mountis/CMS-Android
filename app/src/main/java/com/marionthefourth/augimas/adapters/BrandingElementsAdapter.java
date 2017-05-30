package com.marionthefourth.augimas.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.BrandingElement;
import com.marionthefourth.augimas.classes.Team;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Ints.GENERAL_PADDING_AMOUNT;

public final class BrandingElementsAdapter extends RecyclerView.Adapter<BrandingElementsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BrandingElement> elements;
    private Team team;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.getElementStatusImage(context,holder.elementItem.getStatus()));
        }

        // Set view click listener
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.OnBrandingElementsFragmentInteractionListener(context,holder.elementItem,team);
                    return true;
                }

                return false;
            }
        });

        holder.mBrandingElementStatus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayElementStatusDialog(holder.mView,holder);
                return true;
            }
        });
    }

    private void displayElementStatusDialog(View view, ViewHolder holder) {
        Context context = view.getContext();
        // Creating alert Dialog with one Button
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(context.getString(R.string.title_element_status_updater));

        // Add Button Fields
        ArrayList<AppCompatButton> buttons = new ArrayList<>();
        buttons.add(new AppCompatButton(context));
        buttons.add(new AppCompatButton(context));
        buttons.add(new AppCompatButton(context));
        buttons.add(new AppCompatButton(context));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupButtons(alertDialog,buttons,holder);
        }

        setupElementStatusDialogLayout(alertDialog,view,buttons);

        // Showing Alert Message
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupButtons(final AlertDialog.Builder alertDialog, ArrayList<AppCompatButton> buttons, final ViewHolder holder) {
        for (int i = 0; i < buttons.size();i++) {
            switch (i) {
                case 0:
                    buttons.get(i).setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.GOOD));
                    break;
                case 1:
                    buttons.get(i).setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.BAD));
                    break;
                case 2:
                    buttons.get(i).setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.CHECK));
                    break;
                case 3:
                    buttons.get(i).setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.NOT_STARTED));
                    break;
                default:
                    break;
            }
            final int finalI = i;
            buttons.get(i).setWidth(5);
            buttons.get(i).setHeight(5);

            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.GOOD));
                            break;
                        case 1:
                            holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.BAD));
                            break;
                        case 2:
                            holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.CHECK));
                            break;
                        case 3:
                            holder.mBrandingElementStatus.setBackgroundDrawable(BrandingElement.getElementStatusImage(context, BrandingElement.ElementStatus.NOT_STARTED));
                            break;
                        default:
                            break;
                    }
                }
            });
        }

    }

    private void setupElementStatusDialogLayout(AlertDialog.Builder alertDialog, View view, ArrayList<AppCompatButton> buttons) {
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        for (int i = 0; i < buttons.size();i++) {
            layout.addView(buttons.get(i));
        }
        alertDialog.setView(layout);
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