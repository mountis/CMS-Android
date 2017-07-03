package com.marionthefourth.augimas.fragments;

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
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.update;

public class BrandingElementFragment extends android.support.v4.app.Fragment {

    public BrandingElementFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_branding_element, container, false);

        //getArguments().getString(Constants.Strings.BRANDING_ELEMENT_HEADER)

        if (getArguments() != null ) {
            BrandingElement element = ((BrandingElement)getArguments().getSerializable(Constants.Strings.BRANDING_ELEMENT));
            if (element != null) {
                determineBrandingElementType(view,element.getType());
            }
        }

        return view;
    }

    private void determineBrandingElementType(final View view, BrandingElement.ElementType elementType) {
        final Team team = new Team();
        switch (elementType) {
            case DOMAIN_NAME:
                loadDomainNameView(view,team);
                break;
            case SOCIAL_MEDIA_NAME:
                loadSocialMediaNameView(view,team);
                break;
            case MISSION_STATEMENT:
                loadMissionStatementView(view,team);
                break;
            case TARGET_AUDIENCE:
                loadTargetAudienceView(view,team);
                break;
            case STYLE_GUIDE:
                loadStyleGuideView(view,team);
                break;
            case LOGO:
                loadLogoView(view,team);
                break;
            case PRODUCTS_SERVICES:
                loadProductsAndServicesView(view,team);
                break;
            default:
                break;
        }
    }

    private void loadProductsAndServicesView(final View view, Team team) {
    }

    private void loadLogoView(final View view, Team team) {
    }

    private void loadStyleGuideView(final View view, Team team) {
    }

    private void loadTargetAudienceView(final View view, Team team) {
    }

    private void loadMissionStatementView(final View view, Team team) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_mission_statement_layout);
        layout.setVisibility(View.VISIBLE);
    }

    private void loadSocialMediaNameView(final View view, Team team) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_social_media_name_layout);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.social_media_name_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            if (PROTOTYPE_MODE) {
                loadPrototypeSocialMediaName(recyclerView);
            } else {
                loadSocialMediaNameData(view, recyclerView);
            }

            final AppCompatButton updateButton = (AppCompatButton)view.findViewById(R.id.branding_social_media_update_button);
            final TextInputEditText socialMediaInput = (TextInputEditText) view.findViewById(R.id.input_social_media_name);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getReference(getContext(),R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                                elementItem.getContents().set(0,socialMediaInput.getText().toString());
                                update(getContext(),elementItem);
                                // TODO - Send Notification to Both Teams
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
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

    private void loadSocialMediaNameData(final View view, final RecyclerView recyclerView) {
        // Load Available Services
        FirebaseHelper.getReference(getContext(),R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                    final TextInputEditText socialMediaInput = (TextInputEditText) view.findViewById(R.id.input_social_media_name);
                    socialMediaInput.setText(elementItem.getContents().get(0));
                    recyclerView.setAdapter(new SocialMediaPlatformsAdapter(getContext(),elementItem));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadDomainNameView(final View view, final Team team) {
        final LinearLayoutCompat layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_domain_name_layout);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.domain_name_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            if (PROTOTYPE_MODE) {
                loadPrototypeDomainName(recyclerView);
            } else {
                loadDomainNameData(team,view,recyclerView);
            }

            final AppCompatButton updateButton = (AppCompatButton)view.findViewById(R.id.branding_domain_update_button);
            final TextInputEditText domainNameInput = (TextInputEditText) view.findViewById(R.id.input_domain_name);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getReference(getContext(),R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                                elementItem.getContents().set(0,domainNameInput.getText().toString());
                                update(getContext(),elementItem);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            FirebaseHelper.getReference(getContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
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

    private void loadDomainNameData(final Team team, final View view, final RecyclerView recyclerView) {
        // Load Domain Name
        FirebaseHelper.getReference(getContext(),R.string.firebase_branding_elements_directory).child(getArguments().getString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final BrandingElement elementItem = new BrandingElement(dataSnapshot);
                    final TextInputEditText domainNameInput = (TextInputEditText) view.findViewById(R.id.input_domain_name);
                    domainNameInput.setText(elementItem.getContents().get(0));
                    recyclerView.setAdapter(new TLDAdapter(getContext(),elementItem));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadPrototypeSocialMediaName(final RecyclerView recyclerView) {
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

        recyclerView.setAdapter(new SocialMediaPlatformsAdapter(getContext(),new BrandingElement(BrandingElement.ElementType.SOCIAL_MEDIA_NAME)));

    }

    private void loadPrototypeDomainName(final RecyclerView recyclerView) {
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

        recyclerView.setAdapter(new TLDAdapter(getContext(),new BrandingElement(BrandingElement.ElementType.DOMAIN_NAME)));
    }
}
