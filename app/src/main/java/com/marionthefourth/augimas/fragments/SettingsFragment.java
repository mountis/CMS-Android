package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.dialogs.ChangePasswordDialog;
import com.marionthefourth.augimas.dialogs.HostRequestDialog;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class SettingsFragment extends PreferenceFragment {
    private int buildButtonTapped = 0;
//    Fragment Constructor
    public SettingsFragment() { }
//    Fragment Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);
        setupActionBarName(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final Activity activity = getActivity();
        assert view != null;
        view.setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundMain));
        setupPreferences(activity,view);
        return view;
    }
//    Functional Methods
    private void setupActionBarName(final Activity activity) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User(userSnapshot);
                        if (!currentUser.getTeamUID().equals("")) {
                            Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot teamSnapshot) {
                                    if (teamSnapshot.exists()) {
                                        final Team teamItem = new Team(teamSnapshot);
                                        final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                                        if (actionBar != null) {
                                            actionBar.setTitle(teamItem.getName());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void setupPreferences(final Activity activity, final View containingView) {
        Preference switchAccountPreference = findPreference(Constants.Strings.MANAGE_TEAM_KEY);
        switchAccountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                transitionToTeamManagementScreen(activity);
                return true;
            }
        });
        Preference updateAccountInfoPreference = findPreference(Constants.Strings.UPDATE_ACCOUNT_INFO_KEY);
        updateAccountInfoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ChangePasswordDialog(containingView);
                return true;
            }
        });
        Preference signOutPreference = findPreference(Constants.Strings.SIGN_OUT_KEY);
        signOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Backend.signOut(activity,true);
                return true;
            }
        });
        Preference buildVersionPreference = findPreference(Constants.Strings.BUILD_VERSION_KEY);
        buildVersionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                buildButtonTapped++;
                if (buildButtonTapped == 5) {
                    new HostRequestDialog(containingView, activity);
                    buildButtonTapped = 0;
                }
                return true;
            }
        });
//
    }
//    Transitional Methods
    private void transitionToTeamManagementScreen(final Activity activity) {
        ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,new TeamManagementFragment(),Constants.Strings.Fragments.TEAM_MANAGEMENT).addToBackStack(Constants.Strings.Fragments.SETTINGS).commit();
        handleNonSupportFragmentRemoval(getFragmentManager());
    }
}