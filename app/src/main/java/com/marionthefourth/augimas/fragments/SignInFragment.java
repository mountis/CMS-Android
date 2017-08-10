package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.activities.SignUpActivity;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseObject;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.dialogs.RecoverPasswordDialog;
import com.marionthefourth.augimas.helpers.DeviceHelper;
import com.onesignal.OneSignal;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.FIREBASE_USER;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Buttons.Indices.FORGOT_PASSWORD_BUTTON;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Buttons.Indices.SIGN_IN_BUTTON;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Buttons.Indices.SIGN_UP_TEXT_BUTTON;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.helpers.DeviceHelper.dismissKeyboard;
import static com.marionthefourth.augimas.helpers.FragmentHelper.buildProgressDialog;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class SignInFragment extends Fragment {

    public SignInFragment() { /* Required empty public constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return setupView(inflater.inflate(R.layout.fragment_sign_in, container, false));
    }

    private View setupView(final View view) {
        // Establish all View Elements
        final ArrayList<Button> buttons = getAllButtonViews(view);

        // Add all Inputs to an ArrayList
        final ArrayList<TextInputEditText> inputs = getAllEditTextViews(view);

        // Add all Layouts to an ArrayList
        final ArrayList<TextInputLayout> layouts = getAllTextInputLayoutViews(view);

        final User user = new User();
        final Activity activity = getActivity();
        // Check if user was already logged in
//        checkIfUserIsLoggedIn(view,user);

        // Set OnFocusChangeListener to all Inputs & Add TextChangedListener to all Inputs
        setupTextInputsWithFocusAndTextChangedListeners(layouts,inputs);

        // Sign In Button (Connect to Firebase & Transition to Home)
        setupSignInButtonsOnClickListener(activity,layouts,inputs,view,buttons.get(SIGN_IN_BUTTON),user);

        // Sign Up Button (Transition to Sign Up)
        setupSignUpButtonsOnClickListener(activity,view,buttons.get(SIGN_UP_TEXT_BUTTON));

        // Forgot Password Button (Transition to Forgot Password Section)
        setupForgotPasswordButtonsOnClickListener(activity,view,buttons.get(FORGOT_PASSWORD_BUTTON));

        //            initializeAdminSDK();

        checkIfUserIsLoggedIn(activity,view,getCurrentUser());

        return view;
    }

    private void checkIfUserIsLoggedIn(final Activity activity, final View view, final User user) {
        // Attempt to get the current user if null, they aren't logged in
        if (user != null) {
            final ProgressDialog loadingProgress = buildProgressDialog(R.string.progress_signing_in, view);
            // Display Toast to the User, welcoming them back
            Backend.getReference(
                    R.string.firebase_users_directory, activity
            ).child(user.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadingProgress.dismiss();
                    final User currentUser = new User(dataSnapshot);
                    if (!currentUser.getTeamUID().equals("")) {
                        OneSignal.sendTag(Constants.Strings.UIDs.TEAM_UID,user.getTeamUID());
                    }
                    OneSignal.syncHashedEmail(user.getEmail());
                    OneSignal.sendTag(Constants.Strings.UIDs.USER_UID,user.getUID());

                    display(Constants.Ints.Views.Widgets.IDs.TOAST, R.string.welcome_back_text, currentUser.getUsername(), view);
                    final Intent homeIntent = new Intent(activity,HomeActivity.class);

                    final Bundle bundle = activity.getIntent().getExtras();
                    if (bundle != null) {
                        homeIntent.putExtras(bundle);
                    }
                    startActivity(homeIntent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingProgress.dismiss();
                }
            });
        }
    }

    private void setupTextInputsWithFocusAndTextChangedListeners(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs) {
        for (int i = 0; i < inputs.size();i++) {
            final int finalI = i;
            // Set OnFocusChangeListeners to all Inputs
            inputs.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (layouts.get(finalI).getError() != null) {
                            layouts.get(finalI).setError(null);
                        }
                    }
                }
            });

            // Add TextChangedListener to all Inputs
            inputs.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (layouts.get(finalI).getError() != null) {
                        layouts.get(finalI).setError(null);
                    }
                }
            });
        }
    }
    private void setupSignUpButtonsOnClickListener(final Activity activity, final View view, final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(view.getContext(), SignUpActivity.class);
                activity.startActivity(signUpIntent);
            }
        });
    }
    private ArrayList<Button> getAllButtonViews(final View view) {
        // Button IDs
        final int BUTTON_VIEW_IDS[] = {
                R.id.button_sign_in,
                R.id.text_button_forgot_password,
                R.id.text_button_sign_up
        };

        final ArrayList<Button> buttons = new ArrayList<>();
        for (int i = 0; i < BUTTON_VIEW_IDS.length; i++) {
            buttons.add((Button)view.findViewById(BUTTON_VIEW_IDS[i]));
        }

        return buttons;
    }
    private void setupSignInButtonsOnClickListener(final Activity activity, final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs, final View view, final Button button, final User user) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allTextFieldsAreFilled(view,layouts,inputs) || PROTOTYPE_MODE) {
                    // Connect to Firebase & Attempt to Log In
                    dismissKeyboard(view);

                    if (Constants.Bools.FeaturesAvailable.SIGN_IN) {
                        final User newUser = (User) getFirebaseContentFromFields(FIREBASE_USER);
                        user.setPassword(newUser.getPassword());
                        user.setUsername(newUser.getUsername());
                        user.setEmail(newUser.getEmail());
                        user.setName(newUser.getName());
                        user.setRole(newUser.getRole());
                        user.setTeamUID(newUser.getTeamUID());
                    } else  {
                        display(SNACKBAR, R.string.feature_unavailable, view);
                    }

                    Backend.signIn(user, view, activity);
                }
            }
        });
    }
    private boolean allTextFieldsAreFilled(final View view, final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs) {
        //Errors
        final int REQUIRED_IDS[] = {
                R.string.required_username,
                R.string.required_password
        };

        // Check That Fields Are Filled
        for (int i = 0; i < inputs.size();i++) {
            if (inputs.get(i).getText().toString().equals("")) {
                // Vibrate Phone
                DeviceHelper.vibrateDevice(view.getContext());

                // Display error if it isn't already displayed
                layouts.get(i).setError(view.getContext().getResources().getString(REQUIRED_IDS[i]));

                // Set Focus to Unfilled Field
                inputs.get(i).requestFocus();

                // Clear errors for all other fields
                for (int j = i+1; j < layouts.size();j++) {
                    layouts.get(j).setError(null);
                }
                return false;
            } else {
                // Clear error for field
                layouts.get(i).setError(null);
            }
        }

        return true;
    }
    private ArrayList<TextInputLayout> getAllTextInputLayoutViews(final View view) {
        // Text Input Layout IDs
        final int TEXT_INPUT_LAYOUT_IDS[] = {
                R.id.input_layout_username_or_email,
                R.id.input_layout_password
        };

        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        for (int i = 0; i < TEXT_INPUT_LAYOUT_IDS.length; i++) {
            layouts.add((TextInputLayout)view.findViewById(TEXT_INPUT_LAYOUT_IDS[i]));
        }

        return layouts;
    }
    private ArrayList<TextInputEditText> getAllEditTextViews(final View view) {
        // TextInputEditText View IDs
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username_or_email,
                R.id.input_password
        };

        final ArrayList<TextInputEditText> inputs = new ArrayList<>();
        for (int i = 0; i < INPUT_VIEW_IDS.length; i++) {
            inputs.add((TextInputEditText)view.findViewById(INPUT_VIEW_IDS[i]));
        }
        return inputs;
    }
    private void setupForgotPasswordButtonsOnClickListener(final Activity activity, final View view, final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RecoverPasswordDialog(view, activity);
            }
        });
    }
    private FirebaseObject getFirebaseContentFromFields(int FIREBASE_CONTENT) {
        // Edit Text View IDs
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username_or_email,
                R.id.input_password
        };

        final ArrayList<String> fields = new ArrayList<>();
        for (int i = 0; i < INPUT_VIEW_IDS.length; i++) {
            fields.add(((EditText)getView().findViewById(INPUT_VIEW_IDS[i])).getText().toString());
        }

        return FirebaseObject.getFromFields(fields,FIREBASE_CONTENT);
    }

}