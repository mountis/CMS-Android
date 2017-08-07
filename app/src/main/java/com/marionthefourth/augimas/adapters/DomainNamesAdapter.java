package com.marionthefourth.augimas.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

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
            holder.hideInputLayout();
        } else {
            holder.revealAndTurnOn();
            holder.mDomainEditText.setText(domainName.getData().get(position));
            holder.mDomainEditText.setHint("");
        }

        holder.mDomainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    holder.turnOffDeleteButton();
                } else {
                    holder.turnOnDeleteButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.mDomainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (BrandingElement.checkInput(holder.mDomainEditText.getText().toString(), BrandingElement.ElementType.DOMAIN_NAME)) {
                        if (position == domainName.getData().size()) {
                            domainName.getData().add(holder.mDomainEditText.getText().toString());
                        }
                        Backend.update(domainName, activity);
                    } else {
                        // Inproper Input
                        holder.mDomainEditText.setText("");
                        FragmentHelper.display(TOAST,R.string.tld_not_valid,holder.mView.getRootView());
                    }
                }
                return false;
            }
        });

        holder.mDomainEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (holder.rotated) {
//                        holder.turnOffDeleteButton();
                        if (position == domainName.getData().size()+1) {

                        } else {
                            if (holder.mDomainEditText.getText().toString().equals("") && holder.rotated) {
//                                holder.turnOffDeleteButton();
                            }
                        }
                    } else {

                    }
                } else {
                    holder.creating = false;
                    if (holder.hidden) {
                        holder.layout.startAnimation(open);
                        holder.hidden = false;
                        if (position < getItemCount() && !holder.rotated) {
                            holder.turnOnDeleteButton();
                        }
                    }

                    if (!holder.mDomainEditText.getText().toString().equals("")) {
                        if (BrandingElement.checkInput(holder.mDomainEditText.getText().toString(), BrandingElement.ElementType.DOMAIN_NAME)) {
                            if (position == domainName.getData().size()+1) {
                                domainName.getData().add(holder.mDomainEditText.getText().toString());
                            } else {
                                domainName.getData().set(position, holder.mDomainEditText.getText().toString());
                            }

                            Backend.update(domainName, activity);
                        } else {
                            // Inproper Input
                            holder.mDomainEditText.setText("");
                            FragmentHelper.display(TOAST,R.string.tld_not_valid,holder.mView.getRootView());
                        }

                    } else {
                        if (domainName.getData().size() >= position+1) {
                            domainName.getData().remove(position);
                            holder.hideAndTurnOff();
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
                    holder.revealAndTurnOn();
                    holder.creating = true;
                } else {
                    // delete data if there
                    if (holder.creating) {
                        if (!holder.mDomainEditText.getText().toString().equals("")) {
                            if (BrandingElement.checkInput(holder.mDomainEditText.getText().toString(), BrandingElement.ElementType.DOMAIN_NAME)) {
                                if (position == domainName.getData().size()) {
                                    domainName.getData().add(holder.mDomainEditText.getText().toString());
                                }
                                Backend.update(domainName, activity);
                            } else {
                                // Inproper Input
                                holder.mDomainEditText.setText("");
                                FragmentHelper.display(TOAST,R.string.tld_not_valid,holder.mView.getRootView());
                            }
                        } else {
                            holder.creating = false;
                            holder.hideAndTurnOff();
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

                            holder.hideAndTurnOff();
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
        public boolean hasText = false;

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

        public void hideAndTurnOff() {
            hideInputLayout();
            turnOffDeleteButton();
        }

        public void revealAndTurnOn() {
            revealInputLayout();
            turnOnDeleteButton();
        }

        public void hideInputLayout() {
            if (!hidden) {
                layout.setVisibility(View.GONE);
                layout.startAnimation(close);
                mDomainEditText.clearFocus();
                hidden = true;
            }
        }

        public void revealInputLayout() {
            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                layout.startAnimation(open);
                hidden = false;
            }
        }

        public void turnOnDeleteButton() {
            if (!rotated) {
                mCreateButton.startAnimation(rotate_forward);
                rotated = true;
            }
        }

        public void turnOffDeleteButton() {
            if (rotated) {
                mCreateButton.startAnimation(rotate_back);
                rotated = false;
            }
        }
    }
}