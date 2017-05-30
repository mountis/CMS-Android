package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.TeamManagementActivity;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.Constants.Ints.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.Constants.Ints.SNACKBAR;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class SettingsFragment extends PreferenceFragment {

    private int buildButtonTapped = 0;

    public SettingsFragment() {
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);
        setupPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.backgroundMain));
        return view;
    }

    private void setupPreferences() {

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
                displayChangePasswordDialog();
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
                    displayAdminRequestDialog();
                    buildButtonTapped = 0;
                }

                return true;
            }
        });
    }

    private void displayAdminRequestDialog() {
        // Creating alert Dialog with one Button
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle(getActivity().getString(R.string.title_admin_request));

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_person);

        // Create LinearLayout to add TextInputLayouts with EditTexts
        final ArrayList<EditText> inputs = new ArrayList<>();
        final ArrayList<TextInputLayout> layouts = new ArrayList<>();

        setupAdminRequestDialogLayouts(getActivity(),alertDialog,layouts,inputs);

        // Setting Positive "Request" Button
        setupAdminRequestPositiveButton(getView(),alertDialog,inputs);

        alertDialog.show();
    }

    private void setupAdminRequestPositiveButton(View view, AlertDialog.Builder alertDialog, ArrayList<EditText> inputs) {
        alertDialog.setPositiveButton(R.string.request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Constants.FeaturesAvailable.REQUEST_ADMIN_ROLE) {
                    // Connect to Firebase
                }
            }

        });

    }

    private void setupAdminRequestDialogLayouts(Activity activity, AlertDialog.Builder alertDialog, ArrayList<TextInputLayout> layouts, ArrayList<EditText> inputs) {
        final LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layouts.add(new TextInputLayout(activity));
        inputs.add(new EditText(activity));
        layouts.get(0).addView(inputs.get(0), 0, lp);
        layout.addView(layouts.get(0));
        inputs.get(0).setHint(getActivity().getString(R.string.request_code));

        alertDialog.setView(layout);

    }

    private void transitionToTeamManagementScreen() {
        Intent teamManagementIntent = new Intent(getActivity().getApplicationContext(), TeamManagementActivity.class);
        getActivity().startActivity(teamManagementIntent);
    }

    private void displayChangePasswordDialog() {
        // Get FirebaseUser
        final User user = FirebaseHelper.getCurrentUser();

        if (user != null || PROTOTYPE_MODE) {
            // Creating alert Dialog with one Button
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

            // Setting Dialog Title
            alertDialog.setTitle(getActivity().getString(R.string.title_change_password));

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_lock_open);

            // Create LinearLayout to add TextInputLayouts with EditTexts
            final ArrayList<EditText> inputs = new ArrayList<>();
            final ArrayList<TextInputLayout> layouts = new ArrayList<>();

            setupChangePasswordDialogLayouts(getActivity(),alertDialog,layouts,inputs);

            // Setting Positive "Update" Button
            setupChangePasswordPositiveButton(getView(),alertDialog,inputs, user);

            alertDialog.show();
        }

    }

    private void setupChangePasswordPositiveButton(final View view, AlertDialog.Builder alertDialog, final ArrayList<EditText> inputs, final User user) {
        alertDialog.setPositiveButton(getActivity().getString(R.string.update_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!PROTOTYPE_MODE) {
                    // Ensure Fields are filled
                    if (fieldsArentFilled(inputs)) {
                        display(getView(),SNACKBAR,R.string.error_field_required);
                        dialog.dismiss();
                        return;
                    }
                    // Ensure Passwords are the Same
                    if (passwordsDontMatch(inputs)) {
                        display(getView(),SNACKBAR,R.string.error_password_mismatch);
                        dialog.dismiss();
                        return;
                    }
                    // Ensure Password Length is Greater than 5
                    if (passwordIsNotCorrectLength(inputs)) {
                        display(getView(),SNACKBAR,R.string.error_invalid_password_length);
                        dialog.dismiss();
                        return;
                    }

                    if (Constants.FeaturesAvailable.CHANGE_PASSWORD) {
                        final ProgressDialog loadingProgress = new ProgressDialog(getView().getContext());
                        setupChangePasswordProgressDialog(view,loadingProgress);
                        reauthenticateUser(view, dialog,inputs,user, loadingProgress);
                    } else {
                        display(getView(),SNACKBAR,R.string.feature_unavailable);
                    }
                }
            }
        });
    }

    private void reauthenticateUser(final View view, final DialogInterface dialog, final ArrayList<EditText> inputs, User user, final ProgressDialog loadingProgress) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), inputs.get(0).getText().toString());

        // Reauthenticate User
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Update Password
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(inputs.get(1).getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingProgress.dismiss();

                                    if (task.isSuccessful()) {
                                        display(view,SNACKBAR,R.string.success_password_updated);
                                        dialog.dismiss();
                                    } else {
                                        display(view,SNACKBAR,R.string.error_password_updated,task.getResult().toString());
                                        dialog.dismiss();
                                    }
                                }
                            });
                } else {
                    loadingProgress.dismiss();
                    display(view,SNACKBAR,R.string.error_incorrect_password);
                    dialog.dismiss();
                }
            }
        });
    }

    private void setupChangePasswordProgressDialog(View view, ProgressDialog loadingProgress) {
        loadingProgress.setMessage(view.getContext().getString(R.string.updating_password_text));
        loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
        loadingProgress.setCancelable(false);
        // disable dismiss by tapping outside of the dialog
        loadingProgress.show();
    }

    private boolean passwordIsNotCorrectLength(ArrayList<EditText> inputs) {
        return inputs.get(1).getText().toString().length() < 6;
    }

    private boolean passwordsDontMatch(ArrayList<EditText> inputs) {
        return !inputs.get(1).getText().toString().equals(inputs.get(2).getText().toString());
    }

    private boolean fieldsArentFilled(ArrayList<EditText> inputs) {
        for (int i = 0; i < inputs.size();i++) {
            if (inputs.get(i).getText().toString().equals("")) {
                return true;
            }
        }
        return false;
    }

    private void setupChangePasswordDialogLayouts(Activity activity, AlertDialog.Builder alertDialog, ArrayList<TextInputLayout> layouts, ArrayList<EditText> inputs) {
        final LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final int size = 3;

        // Add Current, New & Confirm New Password Field respectively
        for (int i = 0; i < size; i++) {
            int id;
            switch (i) {
                case 0:
                    id = R.string.current_password_text;
                    break;
                case 1:
                    id = R.string.new_password_text;
                    break;
                case 2:
                    id = R.string.confirm_password_text;
                    break;
                default:
                    id = 0;
                    break;
            }

            layouts.add(new TextInputLayout(activity));
            inputs.add(new EditText(activity));
            inputs.get(i).setTransformationMethod(PasswordTransformationMethod.getInstance());
            layouts.get(i).addView(inputs.get(i), 0, lp);
            layout.addView(layouts.get(i));
            inputs.get(i).setHint(getActivity().getString(id));
        }

        alertDialog.setView(layout);
    }
}