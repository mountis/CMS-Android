package com.augimas.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.adapters.BrandingElementsAdapter;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.entities.Team;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.create;

public final class BrandingElementsFragment extends Fragment implements BrandingElementsAdapter.OnBrandingElementsFragmentInteractionListener {
    public BrandingElementsFragment(){}
    public static BrandingElementsFragment newInstance(String teamUID) {
        Bundle args = new Bundle();
        args.putString(Constants.Strings.UIDs.TEAM_UID,teamUID);
        BrandingElementsFragment fragment = new BrandingElementsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_branding_elements, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.branding_elements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final Activity activity = getActivity();
        if (getArguments() != null) {
            // Load Branding Elements && Setup Action Bar Name
            final String teamUID = getArguments().getString(Constants.Strings.UIDs.TEAM_UID);
            if (teamUID != null) {
                Backend.getReference(R.string.firebase_teams_directory, activity).child(teamUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);
                            final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                            if (actionBar != null) {
                                if (teamItem.getName().endsWith("s")) {
                                    actionBar.setTitle(teamItem.getName() +"' " + Constants.Strings.Titles.BRANDING_ELEMENTS);
                                } else {
                                    actionBar.setTitle(teamItem.getName() +"'s " + Constants.Strings.Titles.BRANDING_ELEMENTS);
                                }
                            }
                            loadBrandingElements(activity,recyclerView,teamItem);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }
        return view;
    }
    private void loadBrandingElements(final Activity activity, final RecyclerView recyclerView, final Team team) {
        Backend.getReference(R.string.firebase_branding_elements_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<BrandingElement> elements = new ArrayList<>();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot brandingElementReference:dataSnapshot.getChildren()) {
                        final BrandingElement elementItem = new BrandingElement(brandingElementReference);
                        if (elementItem.getTeamUID().equals(team.getUID())) {
                            elements.add(new BrandingElement(brandingElementReference));
                        }
                    }
                    if (elements.size() != BrandingElement.ElementType.getNumberOfElementTypes()) {
                        // Create Missing Elements
                        createMissingElements(activity,elements,team);
                    }
                } else {
                    createMissingElements(activity,elements,team);
                }

                recyclerView.setAdapter(new BrandingElementsAdapter(activity,elements,team,BrandingElementsFragment.this));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void createMissingElements(final Activity activity, final ArrayList<BrandingElement> elements, final Team team) {
        // Find each element type that you are missing
        for (int i = 0; i < BrandingElement.ElementType.getNumberOfElementTypes(); i++) {
            if (elements.size() < i+1) {
                elements.add(new BrandingElement(BrandingElement.ElementType.getType(i)));
                elements.get(i).setTeamUID(team.getUID());
                create(elements.get(i), activity);
            }

            if (elements.size() == BrandingElement.ElementType.getNumberOfElementTypes()) {
                return;
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                final Activity activity = getActivity();
                final Intent homeIntent = new Intent(activity,HomeActivity.class);
                activity.startActivity(homeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void OnBrandingElementInteractionListener(final BrandingElement elementItem, final Team teamItem, final Context context) {
        Bundle brandingElementBundle = new Bundle();
        brandingElementBundle.putSerializable(Constants.Strings.TEAM, teamItem);
        brandingElementBundle.putString(Constants.Strings.UIDs.TEAM_UID, teamItem.getUID());
        brandingElementBundle.putSerializable(Constants.Strings.BRANDING_ELEMENT, elementItem);
        brandingElementBundle.putString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID,elementItem.getUID());
        brandingElementBundle.putString(Constants.Strings.Fields.BRANDING_ELEMENT_HEADER,elementItem.getHeader());
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementFragment.newInstance(brandingElementBundle),Constants.Strings.Fragments.BRANDING_ELEMENT).addToBackStack(Constants.Strings.Fragments.BRANDING_ELEMENTS).commit();
    }
}