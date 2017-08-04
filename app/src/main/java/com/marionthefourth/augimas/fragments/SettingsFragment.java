package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
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
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.dialogs.AdminRequestDialog;
import com.marionthefourth.augimas.dialogs.ChangePasswordDialog;
import com.marionthefourth.augimas.backend.Backend;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;
import static com.marionthefourth.augimas.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

public final class SettingsFragment extends PreferenceFragment {

    private int buildButtonTapped = 0;

    public SettingsFragment() { }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);

        final Activity activity = getActivity();
        Backend.getReference(activity,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                        Backend.getReference(activity,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team teamItem = new Team(dataSnapshot);
                                    if (teamItem != null) {
                                        final ActionBar actionBar = ((HomeActivity)activity).getSupportActionBar();
                                        if (actionBar != null) {
                                            actionBar.setTitle(teamItem.getName());
                                        }
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        final Activity activity = getActivity();
        view.setBackgroundColor(ContextCompat.getColor(activity, R.color.backgroundMain));
        setupPreferences(activity,view);
        return view;
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

        Preference changePasswordPreference = findPreference(Constants.Strings.UPDATE_ACCOUNT_INFO_KEY);
        changePasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
                Backend.logout((AppCompatActivity) activity,true);
                return true;
            }
        });

        Preference buildVersionPreference = findPreference(Constants.Strings.BUILD_VERSION_KEY);
        buildVersionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                buildButtonTapped++;

                if (buildButtonTapped == 5) {
                    new AdminRequestDialog(activity,containingView);
                    buildButtonTapped = 0;
                }

                return true;
            }
        });

        Preference questionnairePreference = findPreference(Constants.Strings.QUESTIONNAIRE_KEY);
            questionnairePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (Constants.Bools.FeaturesAvailable.DISPLAY_QUESTIONNAIRE) {
//                        Intent questionnaireHomeIntent = new Intent(activity, QuestionnaireActivity.class);
//                        startActivity(questionnaireHomeIntent);
                        return true;
                    } else {
                        display(containingView,TOAST,R.string.feature_unavailable);
                        return false;
                    }
                }
            });


    }

    private void transitionToTeamManagementScreen(final Activity activity) {
        handleNonSupportFragmentRemoval(getFragmentManager());
        ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.container, new TeamManagementFragment()).commit();
    }

}