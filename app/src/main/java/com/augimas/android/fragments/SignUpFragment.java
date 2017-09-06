package com.augimas.android.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.augimas.android.helpers.NetworkConnectionHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseObject;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.helpers.DeviceHelper;
import com.augimas.android.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.getReference;
import static com.augimas.android.classes.constants.Constants.Ints.Fields.CONFIRM_PASSWORD;
import static com.augimas.android.classes.constants.Constants.Ints.Fields.EMAIL;
import static com.augimas.android.classes.constants.Constants.Ints.Fields.PASSWORD;
import static com.augimas.android.classes.constants.Constants.Ints.Fields.USERNAME;
import static com.augimas.android.classes.constants.Constants.Ints.SignificantNumbers.MINIMUM_PASSWORD_COUNT;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Buttons.Indices.SIGN_IN_TEXT_BUTTON;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Buttons.Indices.SIGN_UP_BUTTON;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.PROGRESS_DIALOG;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.augimas.android.helpers.DeviceHelper.dismissKeyboard;
import static com.augimas.android.helpers.FragmentHelper.display;

public final class SignUpFragment extends Fragment {
//    Fragment Constructor
    public SignUpFragment() {}
    //    Fragment Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setupView(inflater.inflate(R.layout.fragment_sign_up, container, false));
    }
//    View Setup Methods
    private View setupView(final View view) {
        final ArrayList<AppCompatButton> buttons = getAllButtonViews(view);
        final ArrayList<TextInputEditText> inputs = getAllEditTextViews(view);
        final ArrayList<TextInputLayout> layouts = getAllTextInputLayoutViews(view);

        final Activity activity = getActivity();

        // Read Data from Intent and fill Appropriate Fields
        populateTextInputsWithDataFromIntent(activity.getIntent().getExtras(),inputs);
        // Setup Input & Layout Listeners to get User's attention for improper inputs.
        setupTextInputsWithFocusAndTextChangedListeners(layouts,inputs);
        // Sign In Button (Connect to Firebase & Transition to Home)
        setupSignUpButtonsOnClickListener(buttons.get(SIGN_UP_BUTTON), layouts, inputs, view, activity);
        // Sign Up Button (Transition back to Sign In)
        setupSignInButtonsOnClickListener(buttons.get(SIGN_IN_TEXT_BUTTON), activity);

        return view;
    }
//    private ArrayMap<String,ArrayList<View>> getAllVisualElementViews(final View view) {
////        TODO - Try to combine all three Visual Element Arrays Into 1
//        final ArrayMap<String,ArrayList<View>> visualElements = new ArrayMap<>();
////        visualElements.put(Constants.Strings.Elements.BUTTONS,(ArrayList<View>)getAllButtonViews(view));
//        return visualElements;
//    }

    private ArrayList<AppCompatButton> getAllButtonViews(final View view) {
        final int BUTTON_VIEW_IDS[] = {
                R.id.button_sign_up,
                R.id.text_button_sign_in
        };

        final ArrayList<AppCompatButton> buttons = new ArrayList<>();
        for(int ID:BUTTON_VIEW_IDS) {
            buttons.add((AppCompatButton)view.findViewById(ID));
        }

        return buttons;
    }
    private ArrayList<TextInputEditText> getAllEditTextViews(final View view) {
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username,
                R.id.input_password,
                R.id.input_confirm_password,
                R.id.input_email,
                R.id.input_full_name
        };

        final ArrayList<TextInputEditText> inputs = new ArrayList<>();
        for(int ID:INPUT_VIEW_IDS) {
            inputs.add((TextInputEditText)view.findViewById(ID));
        }

        return inputs;
    }
    private ArrayList<TextInputLayout> getAllTextInputLayoutViews(final View view) {
        final int TEXT_INPUT_LAYOUT_IDS[] = {
                R.id.input_layout_username,
                R.id.input_layout_password,
                R.id.input_layout_confirm_password,
                R.id.input_layout_email,
                R.id.input_layout_full_name
        };

        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        for(int ID:TEXT_INPUT_LAYOUT_IDS) {
            layouts.add((TextInputLayout)view.findViewById(ID));

        }

        return layouts;
    }
    private void populateTextInputsWithDataFromIntent(final Bundle bundle, final ArrayList<TextInputEditText> inputs) {
        if (bundle != null) {
            final String passwordText = bundle.getString(Constants.Strings.Fields.PASSWORD);
            final String usernameOrEmailText = bundle.getString(Constants.Strings.Fields.USERNAME_OR_EMAIL);

            // User is able to input either their username/password, this statement sees which it is
            if (FragmentHelper.isValidEmail(usernameOrEmailText)) {
                inputs.get(EMAIL).setText(usernameOrEmailText);
            } else {
                inputs.get(USERNAME).setText(usernameOrEmailText);
            }

            inputs.get(PASSWORD).setText(passwordText);
        }
    }

    private void setupTextInputsWithFocusAndTextChangedListeners(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs) {
        for (int i = 0; i < layouts.size();i++) {
            final int finalI = i;
            inputs.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) layouts.get(finalI).setError(null);
                }
            });

            inputs.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    layouts.get(finalI).setError(null);
                }
            });
        }
    }
    private void setupSignInButtonsOnClickListener(final AppCompatButton signInButton, final Activity activity) {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
    private void setupSignUpButtonsOnClickListener(final AppCompatButton signUpButton, final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs, final View view, final Activity activity) {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.Bools.FeaturesAvailable.SIGN_UP) {
                    if (allTextFieldsAreFilled(view,layouts,inputs) &&
                            passwordsMatch(inputs, layouts, view) &&
                            passwordIsAppropriateLength(inputs, layouts, view) &&
                            emailIsValid(inputs, layouts, view)) {
                        dismissKeyboard(view);

                        if (NetworkConnectionHelper.getState(getActivity()) != TelephonyManager.DATA_DISCONNECTED) {
                            attemptToCreateNewUser((User) createObjectFromFields(Constants.Ints.FIREBASE_USER,view), view, activity);
                        } else {
                            FragmentHelper.display(TOAST,R.string.message_network_connection_lost,getActivity());
                        }
                    }
                } else {
                    display(SNACKBAR, R.string.feature_unavailable, view);
                }
            }
        });
    }
//    Input Verification Methods
    private boolean emailIsValid(final ArrayList<TextInputEditText> inputs, final ArrayList<TextInputLayout> layouts, final View view) {
        if (!FragmentHelper.isValidEmail(inputs.get(EMAIL).getText().toString())) {
            DeviceHelper.vibrateDevice(view.getContext());
            layouts.get(EMAIL).setError(view.getContext().getString(R.string.error_invalid_email));
            // Set Focus to Incorrect Field
            inputs.get(EMAIL).requestFocus();
            return false;
        }
        return true;
    }
    private boolean passwordsMatch(final ArrayList<TextInputEditText> inputs, final ArrayList<TextInputLayout> layouts, final View view) {
        if (!inputs.get(PASSWORD).getText().toString().equals(inputs.get(CONFIRM_PASSWORD).getText().toString())) {
            DeviceHelper.vibrateDevice(view.getContext());

            inputs.get(CONFIRM_PASSWORD).requestFocus();
            layouts.get(CONFIRM_PASSWORD).setError(view.getContext().getString(R.string.error_password_mismatch));
            // Clear errors for all other fields
            for (int i = 0; i < layouts.size();i++) {
                if (i != CONFIRM_PASSWORD) layouts.get(i).setError(null);
            }
            return false;
        } else {
            layouts.get(CONFIRM_PASSWORD).setError(null);
        }
        return true;
    }
    private boolean passwordIsAppropriateLength(final ArrayList<TextInputEditText> inputs, final ArrayList<TextInputLayout> layouts, final View view) {
        if (inputs.get(PASSWORD).getText().toString().length() < MINIMUM_PASSWORD_COUNT) {
            DeviceHelper.vibrateDevice(view.getContext());
            inputs.get(PASSWORD).requestFocus();
            layouts.get(PASSWORD).setError(view.getContext().getString(R.string.error_invalid_password_length));
            // Clear errors for all other fields
            for (int i = 0; i < layouts.size();i++) {
                if (i != PASSWORD) layouts.get(i).setError(null);
            }
            return false;
        } else {
            layouts.get(PASSWORD).setError(null);
        }
        return true;
    }
    private boolean allTextFieldsAreFilled(final View view, final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs) {
        for (int i = 0; i < inputs.size();i++) {
            if (inputs.get(i).getText().toString().equals("")) {
                DeviceHelper.vibrateDevice(view.getContext());

                inputs.get(i).requestFocus();
                layouts.get(i).setError(view.getContext().getString(R.string.error_field_required));
                // Clear errors for all other fields
                for (int j = i+1; j < layouts.size();j++) {
                    layouts.get(j).setError(null);
                }
                return false;
            } else {
                layouts.get(i).setError(null);
            }
        }
        return true;
    }
//    Other Functional Methods
    private void attemptToCreateNewUser(@Nullable final User user, final View view, final Activity activity) {
        if (user != null) {
            final ProgressDialog loadingProgress = display(PROGRESS_DIALOG, R.string.progress_signing_up, view);
            getReference(R.string.firebase_users_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot userReference : dataSnapshot.getChildren()) {
                            final User newUser = new User(userReference);
                            if (newUser.getUsername().equals(user.getUsername())) {
                                assert loadingProgress != null;
                                loadingProgress.dismiss();
                                display(SNACKBAR, R.string.error_username_duplicate, view);
                                return;
                            }
                        }
                    }
                    Backend.signUp(user, view, loadingProgress, activity);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    assert loadingProgress != null;
                    loadingProgress.dismiss();
                }
            });
        }
    }
    private FirebaseObject createObjectFromFields(final int OBJECT_TYPE, final View view) {
        // Edit Text View IDs
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username,
                R.id.input_password,
                R.id.input_confirm_password,
                R.id.input_email,
                R.id.input_full_name
        };

        final ArrayList<String> fields = new ArrayList<>();
        for(int ID:INPUT_VIEW_IDS) {
            fields.add(((TextInputEditText)view.findViewById(ID)).getText().toString().trim());
        }

        return FirebaseObject.getFromFields(fields,OBJECT_TYPE);
    }
}