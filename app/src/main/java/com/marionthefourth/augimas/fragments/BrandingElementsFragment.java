package com.marionthefourth.augimas.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.BrandingElementActivity;
import com.marionthefourth.augimas.adapters.BrandingElementsAdapter;
import com.marionthefourth.augimas.classes.BrandingElement;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.Team;

import java.io.Serializable;
import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;

public final class BrandingElementsFragment extends Fragment implements BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener {

    public BrandingElementsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_branding_elements, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.branding_elements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if (PROTOTYPE_MODE) {
            Team team = new Team("Google","google");
            loadPrototypeBrandingElements(recyclerView,team);
        } else {
            if (getArguments() != null) {

//                loadBrandingElements(
//                        recyclerView,
//                        (Chat) getArguments().getSerializable(Constants.Strings.CHATS),
//                        (ArrayList<User>) getArguments().getSerializable(Constants.Strings.USER)
//                );
            }
        }


        return view;
    }

    private void loadPrototypeBrandingElements(RecyclerView recyclerView, Team team) {
        ArrayList<BrandingElement> elements = new ArrayList<>();

        elements.add(new BrandingElement(BrandingElement.ElementType.DOMAIN_NAME));
        elements.add(new BrandingElement(BrandingElement.ElementType.SOCIAL_MEDIA_NAME));
        elements.add(new BrandingElement(BrandingElement.ElementType.MISSION_STATEMENT));

        recyclerView.setAdapter(new BrandingElementsAdapter(getContext(),elements,team,BrandingElementsFragment.this));
    }

    @Override
    public void OnBrandingElementsFragmentInteractionListener(Context context, BrandingElement elementItem, Team teamItem) {
        Intent brandingElementIntent = new Intent(context, BrandingElementActivity.class);
        brandingElementIntent.putExtra(Constants.Strings.BRANDING_ELEMENT_HEADER,elementItem.getHeader());
        brandingElementIntent.putExtra(Constants.Strings.TEAM, (Serializable) teamItem);
        context.startActivity(brandingElementIntent);
    }
}