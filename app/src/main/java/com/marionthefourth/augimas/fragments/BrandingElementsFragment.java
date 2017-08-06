package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.adapters.BrandingElementsAdapter;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.backend.Backend;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.backend.Backend.create;

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

        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.branding_elements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final Activity activity = getActivity();
        if (PROTOTYPE_MODE) {
            Team team = new Team("Google","google");
            loadPrototypeBrandingElements(activity,recyclerView,team);
        } else {
            if (getArguments() != null) {
                Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final User currentUser = new User(dataSnapshot);
                            if (currentUser != null && currentUser.getType().equals(FirebaseEntity.EntityType.HOST)) {
//                                ((AppCompatActivity)activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                if (view != null) {
                                    view.setFocusableInTouchMode(true);
                                    view.requestFocus();
                                    view.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                                            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                                                final Activity activity = getActivity();
                                                final Intent homeIntent = new Intent(activity,HomeActivity.class);
                                                activity.startActivity(homeIntent);
                                                return true;
                                            }
                                            return false;
                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                // Load Branding Elements && Setup Action Bar Name
                Backend.getReference(R.string.firebase_teams_directory, activity).child(getArguments().getString(Constants.Strings.UIDs.TEAM_UID)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Team teamItem = new Team(dataSnapshot);

                            if (teamItem != null) {
                                final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                                if (actionBar != null) {
                                    actionBar.setTitle(teamItem.getName());
                                }
                                loadBrandingElements(activity,recyclerView,teamItem);
                            }
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
        Backend.getReference(R.string.firebase_branding_elements_directory, activity).addValueEventListener(new ValueEventListener() {
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
                        createMissingElements(activity,elements,team);
                    } else {
                        recyclerView.setAdapter(new BrandingElementsAdapter(activity,elements,team,BrandingElementsFragment.this));
                    }
                } else {
                    createMissingElements(activity,new ArrayList<BrandingElement>(),team);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createMissingElements(final Activity activity, final ArrayList<BrandingElement> elements, final Team team) {
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
                create(activity,elements.get(elements.size()-1));
            }
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                final Activity activity = getActivity();
                final Intent homeIntent = new Intent(activity,HomeActivity.class);
                activity.startActivity(homeIntent);
//                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadPrototypeBrandingElements(final Activity activity, RecyclerView recyclerView, Team team) {
        ArrayList<BrandingElement> elements = new ArrayList<>();
        elements.add(new BrandingElement(BrandingElement.ElementType.DOMAIN_NAME));
        elements.add(new BrandingElement(BrandingElement.ElementType.SOCIAL_MEDIA_NAME));
        elements.add(new BrandingElement(BrandingElement.ElementType.MISSION_STATEMENT));
        recyclerView.setAdapter(new BrandingElementsAdapter(activity,elements,team,BrandingElementsFragment.this));
    }

    @Override
    public void OnBrandingElementsFragmentInteractionListener(Context context, BrandingElement elementItem, Team teamItem) {
        Bundle brandingElementBundle = new Bundle();
        brandingElementBundle.putSerializable(Constants.Strings.TEAM, teamItem);
        brandingElementBundle.putString(Constants.Strings.UIDs.TEAM_UID, teamItem.getUID());
        brandingElementBundle.putSerializable(Constants.Strings.BRANDING_ELEMENT, elementItem);
        brandingElementBundle.putString(Constants.Strings.UIDs.BRANDING_ELEMENT_UID,elementItem.getUID());
        brandingElementBundle.putString(Constants.Strings.Fields.BRANDING_ELEMENT_HEADER,elementItem.getHeader());
        getFragmentManager().beginTransaction().replace(R.id.container, new BrandingElementFragment().newInstance(brandingElementBundle)).commit();
    }
}