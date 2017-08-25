package com.augimas.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.adapters.BrandStylesAdapter;
import com.augimas.android.adapters.BrandingNamesAdapter;
import com.augimas.android.adapters.LogoAdapter;
import com.augimas.android.adapters.TargetAudienceAdapter;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.entities.Team;

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
        final View view = inflater.inflate(R.layout.fragment_branding_element, container, false);
        if (getArguments() != null ) {
            final BrandingElement element = ((BrandingElement)getArguments().getSerializable(Constants.Strings.BRANDING_ELEMENT));
            if (element != null) {
                determineBrandingElementType(element.getType(), view);
                setActionBarName(element,(AppCompatActivity)getActivity());
            }
        }

        return view;
    }
    private void setActionBarName(final BrandingElement element, final AppCompatActivity activity) {
        if (element.getTeamUID() != null && !element.getTeamUID().equals("")) {
            Backend.getReference(R.string.firebase_teams_directory, activity).child(element.getTeamUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final Team teamItem = new Team(dataSnapshot);
                        final ActionBar actionBar = activity.getSupportActionBar();
                        if (actionBar != null) {
                            if (teamItem.getName().endsWith("s")) {
                                if (element.getType().toString().endsWith("s")) {
                                    actionBar.setTitle(teamItem.getName() +"' " + element.getType().toString());
                                } else {
                                    actionBar.setTitle(teamItem.getName() +"' " + element.getType().toString() + "s");
                                }
                            } else {
                                if (element.getType().toString().endsWith("s")) {
                                    actionBar.setTitle(teamItem.getName() +"'s " + element.getType().toString());
                                } else {
                                    actionBar.setTitle(teamItem.getName() +"'s " + element.getType().toString() + "s");
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void determineBrandingElementType(final BrandingElement.ElementType elementType, final View view) {
        final Activity activity = getActivity();
        switch (elementType) {
            case DOMAIN_NAME:
            case SOCIAL_MEDIA_NAME:
            case MISSION_STATEMENT:
            case PRODUCTS_SERVICES:
                loadBrandingNameView(elementType,view,activity);
                break;
            case TARGET_AUDIENCE:
                loadTargetAudienceView(view,activity);
                break;
            case BRAND_STYLE:
                loadBrandStyleView(view,activity);
                break;
            case LOGO:
                loadLogoView(view,activity);
                break;
            default:
                break;
        }
    }

    private void loadLogoView(View view, Activity activity) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_logo_layout);
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.logo_recycler_view);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            loadLogoData(recyclerView,activity);
        }
    }

    private void loadBrandStyleView(final View view, final Activity activity) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_brand_style_layout);
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.brand_style_recycler_view);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            loadBrandStyleData(recyclerView,activity);
        }
    }

    private void loadTargetAudienceView(final View view, final Activity activity) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_target_audience_layout);
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.target_audience_recycler_view);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            loadTargetAudienceData(recyclerView,activity);
        }
    }

    private void loadTargetAudienceData(final RecyclerView recyclerView, final Activity activity) {
        final String brandingElementUID = getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
        if (brandingElementUID != null) {
            Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElementUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                        recyclerView.setAdapter(new TargetAudienceAdapter(elementItem, getView(), activity));
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

    private void loadLogoData(final RecyclerView recyclerView, final Activity activity) {
        final String brandingElementUID = getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
        if (brandingElementUID != null) {
            Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElementUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                        recyclerView.setAdapter(new LogoAdapter(elementItem, getView(), activity));
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
    private void loadBrandStyleData(final RecyclerView recyclerView, final Activity activity) {
        final String brandingElementUID = getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
        if (brandingElementUID != null) {
            Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElementUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                        recyclerView.setAdapter(new BrandStylesAdapter(elementItem, getView(), activity));
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
    private void loadBrandingNameView(final BrandingElement.ElementType elementType, final View view, final Activity activity) {
        final LinearLayoutCompat layout;
        final RecyclerView recyclerView;
        if (elementType == BrandingElement.ElementType.DOMAIN_NAME) {
            layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_domain_name_layout);
            recyclerView = (RecyclerView)view.findViewById(R.id.domain_name_recycler_view);
        } else if (elementType == BrandingElement.ElementType.SOCIAL_MEDIA_NAME){
            layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_social_media_name_layout);
            recyclerView = (RecyclerView)view.findViewById(R.id.social_media_name_recycler_view);
        } else if (elementType == BrandingElement.ElementType.MISSION_STATEMENT){
            layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_mission_statement_layout);
            recyclerView = (RecyclerView)view.findViewById(R.id.mission_statement_recycler_view);
        } else {
            layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_product_service_layout);
            recyclerView = (RecyclerView)view.findViewById(R.id.product_service_recycler_view);
        }

        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            loadBrandingNameData(recyclerView,activity);
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
                        recyclerView.setAdapter(new BrandingNamesAdapter(elementItem, getView(), activity));
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