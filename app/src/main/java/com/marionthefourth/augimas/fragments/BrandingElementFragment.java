package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.BrandingNamesAdapter;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;

public class BrandingElementFragment extends android.support.v4.app.Fragment {

    public BrandingElementFragment(){}

    public static BrandingElementFragment newInstance(Bundle args) {
        BrandingElementFragment fragment = new BrandingElementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_branding_element, container, false);

        if (getArguments() != null ) {
            BrandingElement element = ((BrandingElement)getArguments().getSerializable(Constants.Strings.BRANDING_ELEMENT));
            if (element != null) {
                determineBrandingElementType(view,element.getType());
            }
        }

        return view;
    }

    private void determineBrandingElementType(final View view, BrandingElement.ElementType elementType) {
        final Activity activity = getActivity();

        switch (elementType) {
            case DOMAIN_NAME:
            case SOCIAL_MEDIA_NAME:
                loadBrandingNameView(elementType,view,activity);
                break;
            case MISSION_STATEMENT:
//                loadMissionStatementView(view);
                break;
            case TARGET_AUDIENCE:
//                loadTargetAudienceView(activity,view);
                break;
            case STYLE_GUIDE:
//                loadStyleGuideView(activity,view);
                break;
            case LOGO:
//                loadLogoView(activity,view);
                break;
            case PRODUCTS_SERVICES:
//                loadProductsAndServicesView(activity,view);
                break;
            case DEFAULT:
                break;
            default:
                break;
        }
    }

    private void loadBrandingNameView(BrandingElement.ElementType elementType, final View view, final Activity activity) {
        final LinearLayoutCompat layout;
        final RecyclerView recyclerView;
        if (elementType == BrandingElement.ElementType.DOMAIN_NAME) {
            layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_social_media_name_layout);
            recyclerView = (RecyclerView)view.findViewById(R.id.domain_name_recycler_view);
        } else {
            layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_domain_name_layout);
            recyclerView = (RecyclerView)view.findViewById(R.id.domain_name_recycler_view);
        }

        if (layout != null) {
            layout.setVisibility(View.VISIBLE);

            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, final int top, int right, final int bottom, int oldLeft, final int oldTop, int oldRight, int oldBottom) {
                    if ( bottom < oldBottom) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(oldTop);
                            }
                        }, 100);
                    }
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            if (!PROTOTYPE_MODE) {
                loadBrandingNameData(recyclerView,activity);
            }
        }
    }

    private void loadBrandingNameData(final RecyclerView recyclerView, final Activity activity) {
        final String brandingElementUID = getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
        if (brandingElementUID != null) {
            Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElementUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                        recyclerView.setAdapter(new BrandingNamesAdapter(activity,elementItem,getView()));
                        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, final int top, int right, final int bottom, int oldLeft, final int oldTop, int oldRight, final int oldBottom) {
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

}