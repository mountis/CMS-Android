package com.marionthefourth.augimas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.QuestionnaireActivity;
import com.marionthefourth.augimas.activities.TeamManagementActivity;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.dialogs.AdminRequestDialog;
import com.marionthefourth.augimas.dialogs.ChangePasswordDialog;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class SettingsFragment extends PreferenceFragment {

    private int buildButtonTapped = 0;

    public SettingsFragment() { }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.backgroundMain));
        setupPreferences(view);
        return view;
    }

    private void setupPreferences(final View containingView) {

        Preference switchAccountPreference = findPreference(Constants.Strings.MANAGE_TEAM_KEY);
        switchAccountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                transitionToTeamManagementScreen();
                return true;
            }
        });

        Preference changePasswordPreference = findPreference(Constants.Strings.CHANGE_PASSWORD_KEY);
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
                FirebaseHelper.logout((AppCompatActivity) getActivity(),true);
                return true;
            }
        });

        Preference buildVersionPreference = findPreference(Constants.Strings.BUILD_VERSION_KEY);
        buildVersionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                buildButtonTapped++;

                if (buildButtonTapped == 5) {
                    new AdminRequestDialog(containingView);
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
                        Intent questionnaireHomeIntent = new Intent(getActivity(), QuestionnaireActivity.class);
                        startActivity(questionnaireHomeIntent);
                        return true;
                    } else {
                        display(containingView,TOAST,R.string.feature_unavailable);
                        return false;
                    }
                }
            });


    }

    private void transitionToTeamManagementScreen() {
        Intent teamManagementIntent = new Intent(getActivity().getApplicationContext(), TeamManagementActivity.class);
        getActivity().startActivity(teamManagementIntent);
    }

    private boolean fieldsArentFilled(ArrayList<EditText> inputs) {
        for (int i = 0; i < inputs.size();i++) {
            if (inputs.get(i).getText().toString().equals("")) {
                return true;
            }
        }
        return false;
    }
}