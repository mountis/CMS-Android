package com.marionthefourth.augimas.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.DomainNameExtensionsAdapter;
import com.marionthefourth.augimas.adapters.SocialMediaPlatformsAdapter;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;

public class BrandingElementFragment extends android.support.v4.app.Fragment {

    public BrandingElementFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_branding_element, container, false);

        int recyclerViewId;
        RecyclerView recyclerView;
        LinearLayoutCompat layout;
        //getArguments().getString(Constants.Strings.BRANDING_ELEMENT_HEADER)
        switch ("Domain Name") {
            case "Domain Name":
                layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_domain_name_layout);
                if (layout != null) {
                    layout.setVisibility(View.VISIBLE);
                    recyclerViewId = R.id.domain_name_recycler_view;
                    recyclerView = (RecyclerView)view.findViewById(recyclerViewId);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                    if (PROTOTYPE_MODE) {
                        loadPrototypeDomainName(recyclerView);
                    } else {

                    }
                }

                break;
            case "Social Media Name":
                layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_social_media_name_layout);
                if (layout != null) {
                    layout.setVisibility(View.VISIBLE);
                    recyclerViewId = R.id.social_media_name_recycler_view;
                    recyclerView = (RecyclerView)view.findViewById(recyclerViewId);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                    if (PROTOTYPE_MODE) {
                        loadPrototypeSocialMediaName(recyclerView);
                    } else {

                    }
                }

                break;
            default:
                layout = (LinearLayoutCompat) view.findViewById(R.id.branding_element_mission_statement_layout);
                layout.setVisibility(View.VISIBLE);
                break;
        }

        return view;
    }

    private void loadPrototypeSocialMediaName(RecyclerView recyclerView) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setAdapter(new SocialMediaPlatformsAdapter(getContext(),socialMediaPlatforms));
        }
    }

    private void loadPrototypeDomainName(RecyclerView recyclerView) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setAdapter(new DomainNameExtensionsAdapter(getContext(),domainNameExtensions));
        }
    }
}
