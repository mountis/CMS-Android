package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.BrandingNamesAdapter;
import com.marionthefourth.augimas.adapters.SocialMediaPlatformsAdapter;
import com.marionthefourth.augimas.adapters.TLDAdapter;
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

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    final AppCompatActivity activity = (AppCompatActivity) getActivity();
                    final FragmentManager manager = activity.getSupportFragmentManager();

                    manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, BrandingElementsFragment.newInstance(getArguments().getString(Constants.Strings.UIDs.TEAM_UID)),Constants.Strings.Fragments.BRANDING_ELEMENTS).commit();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void determineBrandingElementType(final View view, BrandingElement.ElementType elementType) {
        final Activity activity = getActivity();

        switch (elementType) {
            case DOMAIN_NAME:
                loadDomainNameView(view, activity);
                break;
            case SOCIAL_MEDIA_NAME:
                loadSocialMediaNameView(view, activity);
                break;
            case MISSION_STATEMENT:
                loadMissionStatementView(view);
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

    private void loadMissionStatementView(final View view) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_mission_statement_layout);
        layout.setVisibility(View.VISIBLE);
    }

    private void loadSocialMediaNameView(final View view, final Activity activity) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_social_media_name_layout);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.social_media_name_recycler_view);

            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, final int bottom, int oldLeft, final int oldTop, int oldRight, int oldBottom) {
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

            if (PROTOTYPE_MODE) {
                loadPrototypeSocialMediaName(activity, recyclerView);
            } else {
                loadSocialMediaNameData(activity, recyclerView);
            }
        }
    }

    private void loadSocialMediaNameData(final Activity activity, final RecyclerView recyclerView) {
        // Load Available Services
        final String brandingElementUID = getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
        if (brandingElementUID != null) {
            Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElementUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                        recyclerView.setAdapter(new BrandingNamesAdapter(activity,elementItem));
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

    private void loadDomainNameView(final View view, final Activity activity) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_domain_name_layout);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.domain_name_recycler_view);

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

            if (PROTOTYPE_MODE) {
                loadPrototypeDomainName(recyclerView, activity);
            } else {
                loadDomainNameData(recyclerView, activity);
            }
        }
    }

    private void loadDomainNameData(final RecyclerView recyclerView, final Activity activity) {
        // Load Domain Name
        final String brandingElementUID = getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID);
        if (brandingElementUID != null) {
            Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElementUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                        recyclerView.setAdapter(new BrandingNamesAdapter(activity,elementItem));
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

    private void loadPrototypeSocialMediaName(final Activity activity, final RecyclerView recyclerView) {
        recyclerView.setAdapter(new SocialMediaPlatformsAdapter(activity,new BrandingElement(BrandingElement.ElementType.SOCIAL_MEDIA_NAME)));
    }

    private void loadPrototypeDomainName(final RecyclerView recyclerView, final Activity activity) {
        recyclerView.setAdapter(new TLDAdapter(activity,new BrandingElement(BrandingElement.ElementType.DOMAIN_NAME)));
    }
}