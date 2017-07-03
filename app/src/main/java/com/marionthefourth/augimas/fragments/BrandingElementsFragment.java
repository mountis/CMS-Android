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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.BrandingElementActivity;
import com.marionthefourth.augimas.adapters.BrandingElementsAdapter;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.io.Serializable;
import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.save;

public final class BrandingElementsFragment extends Fragment implements BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener {

    public BrandingElementsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_branding_elements, container, false);

        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.branding_elements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if (PROTOTYPE_MODE) {
            Team team = new Team("Google","google");
            loadPrototypeBrandingElements(recyclerView,team);
        } else {
            if (getArguments() != null) {
                FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).child(getArguments().getString(Constants.Strings.UIDs.TEAM_UID)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);
                            if (teamItem != null) {
                                loadBrandingElements(recyclerView,teamItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        return view;
    }

    private void loadBrandingElements(final RecyclerView recyclerView, final Team team) {
        FirebaseHelper.getReference(getContext(),R.string.firebase_branding_elements_directory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final ArrayList<BrandingElement> elements = new ArrayList<>();
                    for (DataSnapshot brandingElementReference:dataSnapshot.getChildren()) {
                        final BrandingElement elementItem = new BrandingElement(brandingElementReference);
                        if (elementItem.getTeamUID().equals(team.getUID())) {
                            elements.add(new BrandingElement(brandingElementReference));
                        }
                    }

                    if (elements.size() < BrandingElement.ElementType.getNumberOfElementTypes()) {
                        // Create Missing Elements
                        createMissingElements(recyclerView,elements,team);
                    } else {
                        recyclerView.setAdapter(new BrandingElementsAdapter(getContext(),elements,team,BrandingElementsFragment.this));
                    }
                } else {
                    createMissingElements(recyclerView,new ArrayList<BrandingElement>(),team);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createMissingElements(final RecyclerView recyclerView, final ArrayList<BrandingElement> elements, final Team team) {
        // Find each element type that you are missing
        final ArrayList<Boolean> typesMade = new ArrayList<>(BrandingElement.ElementType.getNumberOfElementTypes());

        for (int i = 0; i < BrandingElement.ElementType.getNumberOfElementTypes(); i++) {
            for (int j = 0; j < elements.size(); j++) {
                if (elements.get(j).getType().equals(BrandingElement.ElementType.getType(i))) {
                    typesMade.add(true);
                }
            }

            if (typesMade.size() == i) {
                typesMade.add(false);
                elements.add(new BrandingElement(BrandingElement.ElementType.getType(i)));
                elements.get(elements.size()-1).setTeamUID(team.getUID());
                save(getContext(),elements.get(elements.size()-1));
            }
        }

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
        brandingElementIntent.putExtra(Constants.Strings.Fields.BRANDING_ELEMENT_HEADER,elementItem.getHeader());
        brandingElementIntent.putExtra(Constants.Strings.UIDs.BRANDING_ELEMENT_UID,elementItem.getUID());
        brandingElementIntent.putExtra(Constants.Strings.TEAM, (Serializable) teamItem);
        brandingElementIntent.putExtra(Constants.Strings.BRANDING_ELEMENT, (Serializable) elementItem);
        brandingElementIntent.putExtra(Constants.Strings.UIDs.TEAM_UID, teamItem.getUID());

        context.startActivity(brandingElementIntent);
    }
}