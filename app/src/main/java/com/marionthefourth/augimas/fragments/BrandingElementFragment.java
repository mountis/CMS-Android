package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
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
import com.marionthefourth.augimas.adapters.SocialMediaPlatformsAdapter;
import com.marionthefourth.augimas.adapters.TLDAdapter;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.update;

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
                loadDomainNameView(activity,view);
                break;
            case SOCIAL_MEDIA_NAME:
                loadSocialMediaNameView(activity,view);
                break;
            case MISSION_STATEMENT:
                loadMissionStatementView(activity,view);
                break;
            case TARGET_AUDIENCE:
                loadTargetAudienceView(activity,view);
                break;
            case STYLE_GUIDE:
                loadStyleGuideView(activity,view);
                break;
            case LOGO:
                loadLogoView(activity,view);
                break;
            case PRODUCTS_SERVICES:
                loadProductsAndServicesView(activity,view);
                break;
            case DEFAULT:
                break;
            default:
                break;
        }
    }

    private void loadProductsAndServicesView(final Activity activity, final View view) {
    }

    private void loadLogoView(final Activity activity, final View view) {
    }

    private void loadStyleGuideView(final Activity activity, final View view) {
    }

    private void loadTargetAudienceView(final Activity activity, final View view) {
    }

    private void loadMissionStatementView(final Activity activity, final View view) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_mission_statement_layout);
        layout.setVisibility(View.VISIBLE);
    }

    private void loadSocialMediaNameView(final Activity activity, final View view) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_social_media_name_layout);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.social_media_name_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            if (PROTOTYPE_MODE) {
                loadPrototypeSocialMediaName(activity, recyclerView);
            } else {
                loadSocialMediaNameData(activity, view, recyclerView);
            }

            final AppCompatButton updateButton = (AppCompatButton)view.findViewById(R.id.branding_social_media_update_button);
            final TextInputEditText socialMediaInput = (TextInputEditText) view.findViewById(R.id.input_social_media_name);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getReference(activity,R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                                elementItem.getContents().set(0,socialMediaInput.getText().toString());
                                update(activity,elementItem);
                                // TODO - Send Notification to Both Teams
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User currentUser = new User(dataSnapshot);
                        if (currentUser == null || !currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.ADMIN)) {
                            updateButton.setEnabled(false);
                            updateButton.setVisibility(View.GONE);
                            socialMediaInput.setEnabled(false);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadSocialMediaNameData(final Activity activity,final View view, final RecyclerView recyclerView) {
        // Load Available Services
        FirebaseHelper.getReference(activity,R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                    final TextInputEditText socialMediaInput = (TextInputEditText) view.findViewById(R.id.input_social_media_name);
                    if (elementItem.getContents().size() >= 1) {
                        socialMediaInput.setText(elementItem.getContents().get(0));
                    }
                    recyclerView.setAdapter(new SocialMediaPlatformsAdapter(activity,elementItem));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadDomainNameView(final Activity activity, final View view) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_domain_name_layout);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.domain_name_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            if (PROTOTYPE_MODE) {
                loadPrototypeDomainName(activity,recyclerView);
            } else {
                loadDomainNameData(activity,view,recyclerView);
            }

            final TextInputEditText domainNameInput = (TextInputEditText) view.findViewById(R.id.input_domain_name);
            final AppCompatButton updateButton = (AppCompatButton)view.findViewById(R.id.branding_domain_update_button);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getReference(activity,R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                                elementItem.getContents().set(0,domainNameInput.getText().toString());
                                update(activity,elementItem);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            FirebaseHelper.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final User currentUser = new User(dataSnapshot);
                        if (currentUser == null || !currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                            updateButton.setEnabled(false);
                            updateButton.setVisibility(View.GONE);
                            domainNameInput.setEnabled(false);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    private void loadDomainNameData(final Activity activity, final View view, final RecyclerView recyclerView) {
        // Load Domain Name
        FirebaseHelper.getReference(activity,R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final BrandingElement elementItem = new BrandingElement(dataSnapshot);

                    // Add Items that aren't there yet
                    final TextInputEditText domainNameInput = (TextInputEditText) view.findViewById(R.id.input_domain_name);
                    if (elementItem.getContents().size() >= 1) {
                        domainNameInput.setText(elementItem.getContents().get(0));
                    }
                    recyclerView.setAdapter(new TLDAdapter(activity,elementItem));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void loadPrototypeSocialMediaName(final Activity activity, final RecyclerView recyclerView) {
        ArrayList<String> socialMediaPlatforms = new ArrayList<>();

        socialMediaPlatforms.add("Facebook");
        socialMediaPlatforms.add("Snapchat");
        socialMediaPlatforms.add("Instagram");
        socialMediaPlatforms.add("Twitter");
        socialMediaPlatforms.add("Reddit");
        socialMediaPlatforms.add("Blogger");
        socialMediaPlatforms.add("GooglePlus");
        socialMediaPlatforms.add("Twitch");
        socialMediaPlatforms.add("Ebay");
        socialMediaPlatforms.add("Wordpress");
        socialMediaPlatforms.add("Pinterest");
        socialMediaPlatforms.add("Blogger");

        recyclerView.setAdapter(new SocialMediaPlatformsAdapter(activity,new BrandingElement(BrandingElement.ElementType.SOCIAL_MEDIA_NAME)));

    }

    private void loadPrototypeDomainName(final Activity activity, final RecyclerView recyclerView) {
        ArrayList<String> domainNameExtensions = new ArrayList<>();

        domainNameExtensions.add(".com");
        domainNameExtensions.add(".net");
        domainNameExtensions.add(".org");
        domainNameExtensions.add(".co");
        domainNameExtensions.add(".biz");
        domainNameExtensions.add(".io");
        domainNameExtensions.add(".ly");
        domainNameExtensions.add(".us");
        domainNameExtensions.add(".me");
        domainNameExtensions.add(".info");
        domainNameExtensions.add(".xyz");
        domainNameExtensions.add(".ca");

        recyclerView.setAdapter(new TLDAdapter(activity,new BrandingElement(BrandingElement.ElementType.DOMAIN_NAME)));
    }
}