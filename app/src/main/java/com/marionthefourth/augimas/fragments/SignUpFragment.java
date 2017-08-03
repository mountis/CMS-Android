package com.marionthefourth.augimas.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseObject;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.DeviceHelper;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.CONFIRM_PASSWORD;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.EMAIL;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.PASSWORD;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.USERNAME;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.MINIMUM_PASSWORD_COUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Buttons.Indices.SIGN_IN_TEXT_BUTTON;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Buttons.Indices.SIGN_UP_BUTTON;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.PROGRESS_DIALOG;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.helpers.DeviceHelper.dismissKeyboard;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.getReference;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class SignUpFragment extends Fragment {

    public SignUpFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return setupView(inflater.inflate(R.layout.fragment_sign_up, container, false));
    }

    private View setupView(final View view) {
        // All all Buttons to an ArrayList
        final ArrayList<Button> buttons = getAllButtonViews(view);
        // Add all Inputs to an ArrayList
        final ArrayList<TextInputEditText> inputs = getAllEditTextViews(view);
        // Add all Layouts to an ArrayList
        final ArrayList<TextInputLayout> layouts = getAllTextInputLayoutViews(view);

        final Activity activity = getActivity();

        // Read Data from Intent and fill Appropriate Fields
        populateTextInputsWithDataFromIntent(activity,inputs);
        // Set OnFocusChangeListener to all Inputs & Add TextChangedListener to all Inputs
        setupTextInputsWithFocusAndTextChangedListeners(layouts,inputs);
        // Sign In Button (Connect to Firebase & Transition to Home)
        setupSignUpButtonsOnClickListener(activity,layouts,inputs,view,buttons.get(SIGN_UP_BUTTON));
        // Sign Up Button (Transition to Sign In)
        setupSignInButtonsOnClickListener(activity,buttons.get(SIGN_IN_TEXT_BUTTON));

        return view;
    }

    private ArrayList<Button> getAllButtonViews(View view) {
        // Button IDs
        final int BUTTON_VIEW_IDS[] = {
                R.id.button_sign_up,
                R.id.text_button_sign_in
        };

        final ArrayList<Button> buttons = new ArrayList<>();
        for (int i = 0; i < BUTTON_VIEW_IDS.length; i++) {
            buttons.add((Button)view.findViewById(BUTTON_VIEW_IDS[i]));
        }

        return buttons;
    }

    private ArrayList<TextInputEditText> getAllEditTextViews(final View view) {
        // TextInputEditText View IDs
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username,
                R.id.input_password,
                R.id.input_confirm_password,
                R.id.input_email,
                R.id.input_full_name
        };

        final ArrayList<TextInputEditText> inputs = new ArrayList<>();
        for (int i = 0; i < INPUT_VIEW_IDS.length; i++) {
            inputs.add((TextInputEditText)view.findViewById(INPUT_VIEW_IDS[i]));
        }
        return inputs;
    }

    private ArrayList<TextInputLayout> getAllTextInputLayoutViews(final View view) {
        // Text Input Layout IDs
        final int TEXT_INPUT_LAYOUT_IDS[] = {
                R.id.input_layout_username,
                R.id.input_layout_password,
                R.id.input_layout_confirm_password,
                R.id.input_layout_email,
                R.id.input_layout_full_name
        };

        final ArrayList<TextInputLayout> layouts = new ArrayList<>();
        for (int i = 0; i < TEXT_INPUT_LAYOUT_IDS.length; i++) {
            layouts.add((TextInputLayout)view.findViewById(TEXT_INPUT_LAYOUT_IDS[i]));
        }

        return layouts;
    }

    private void populateTextInputsWithDataFromIntent(final Activity activity, final ArrayList<TextInputEditText> inputs) {
        if (activity.getIntent() != null) {
            final String passwordText = activity.getIntent().getStringExtra(Constants.Strings.Fields.PASSWORD);
            final String usernameOrEmailText = activity.getIntent().getStringExtra(Constants.Strings.Fields.USERNAME_OR_EMAIL);
            // Check if text from intent is Username or Email and set it to the appropriate field
            if (isValidEmail(usernameOrEmailText)) {
                inputs.get(EMAIL).setText(usernameOrEmailText);
            } else {
                inputs.get(USERNAME).setText(usernameOrEmailText);
            }

            // Fill the Password Field
            inputs.get(PASSWORD).setText(passwordText);
        }
    }

    private void setupTextInputsWithFocusAndTextChangedListeners(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs) {
        for (int i = 0; i < layouts.size();i++) {
            final int finalI = i;
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

            //
            inputs.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Clear error on Layout if there is one
                    if (layouts.get(finalI).getError() != null) {
                        layouts.get(finalI).setError(null);
                    }
                }
            });
        }
    }

    private void setupSignInButtonsOnClickListener(final Activity activity, final Button signInButton) {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    private void setupSignUpButtonsOnClickListener(final Activity activity, final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs, final View view, Button signUpButton) {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PROTOTYPE_MODE) {
                    FirebaseHelper.signin(activity, view,null);
                } else {
                    if (Constants.Bools.FeaturesAvailable.SIGN_UP) {
                        if (allTextFieldsAreFilled(view,layouts,inputs)) {
                            if (passwordsMatch(view,layouts,inputs)) {
                                if (passwordLengthIsAppropriate(view,layouts,inputs)) {
                                    if (isAValidEmail(view,layouts,inputs)) {
                                        // Connect to Firebase & Attempt to Sign up
                                        dismissKeyboard(view);
                                        createUser(activity,view, (User) getFirebaseContentFromFields(Constants.Ints.FIREBASE_USER));
                                    }
                                }
                            }
                        }
                    } else {
                        display(view,SNACKBAR,R.string.feature_unavailable);
                    }

                }
            }
        });
    }

    private boolean isAValidEmail(View view, ArrayList<TextInputLayout> layouts, ArrayList<TextInputEditText> inputs) {
        if (!isValidEmail(inputs.get(EMAIL).getText().toString())) {
            // Vibrate Phone
            DeviceHelper.vibrateDevice(view.getContext());

            layouts.get(EMAIL).setError(view.getContext().getString(R.string.error_invalid_email));

            // Set Focus to Incorrect Field
            inputs.get(EMAIL).requestFocus();
            return false;
        }

        return true;
    }

    private boolean passwordsMatch(View view, ArrayList<TextInputLayout> layouts, ArrayList<TextInputEditText> inputs) {
        // Check that passwords match
        if (!inputs.get(PASSWORD).getText().toString().equals(inputs.get(CONFIRM_PASSWORD).getText().toString())) {
            // Vibrate Phone
            DeviceHelper.vibrateDevice(view.getContext());

            layouts.get(CONFIRM_PASSWORD).setError(view.getContext().getString(R.string.error_password_mismatch));

            // Set Focus to Incorrect Field
            inputs.get(CONFIRM_PASSWORD).requestFocus();

            // Clear errors for all other fields
            for (int i = 0; i < layouts.size();i++) {
                if (i != CONFIRM_PASSWORD)
                    if (layouts.get(i).getError() != null) {
                        layouts.get(i).setError(null);
                    }
            }

            return false;
        } else {
            // Clear error for field if filled
            if (layouts.get(CONFIRM_PASSWORD).getError() != null) {
                layouts.get(CONFIRM_PASSWORD).setError(null);
            }
        }

        return true;
    }

    private boolean passwordLengthIsAppropriate(View view, ArrayList<TextInputLayout> layouts, ArrayList<TextInputEditText> inputs) {
        // Check that password is the correct length
        if (inputs.get(PASSWORD).getText().toString().length() < MINIMUM_PASSWORD_COUNT) {

            // Vibrate Phone
            DeviceHelper.vibrateDevice(view.getContext());

            // Set Error To Password Layout
            layouts.get(PASSWORD).setError(view.getContext().getString(R.string.error_invalid_password_length));

            // Set Focus to Password Field
            inputs.get(PASSWORD).requestFocus();

            // Clear errors for all other fields
            for (int i = 0; i < layouts.size();i++) {
                if (i != PASSWORD)
                    layouts.get(i).setError(null);
            }

            return false;

        } else {
            // Clear error for field if filled
            layouts.get(PASSWORD).setError(null);
        }

        return true;
    }

    private boolean allTextFieldsAreFilled(View view, ArrayList<TextInputLayout> layouts, ArrayList<TextInputEditText> inputs) {
        // Check That Fields Are Filled
        for (int i = 0; i < inputs.size();i++) {
            if (inputs.get(i).getText().toString().equals("")) {

                // Vibrate Phone
                DeviceHelper.vibrateDevice(view.getContext());

                // Set Error To Layout w/o Text
                layouts.get(i).setError(view.getContext().getString(R.string.error_field_required));
                // Set Focus to Unfilled Field
                inputs.get(i).requestFocus();

                // Clear errors for all other fields
                for (int j = i+1; j < layouts.size();j++) {
                    layouts.get(j).setError(null);
                }

                return false;
            } else {
                // Clear error for field if filled
                layouts.get(i).setError(null);
            }
        }

        return true;
    }

    private void createUser(final Activity activity, final View view, final User user) {
        final Context context = view.getContext();
        final ProgressDialog loadingProgress = display(view,PROGRESS_DIALOG,R.string.progress_signing_up);
        // Ensure that the User is not null
        if (user != null) {
            // Get a reference to the users location of the database
            final DatabaseReference usersRef = getReference(activity,R.string.firebase_users_directory);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot userReference : dataSnapshot.getChildren()) {
                            // Get the User instance from the user reference
                            final User newUser = new User(userReference);
                            // Check to see if the User instance's Session ID matches the current Session ID
                            if (newUser != null) {
                                // Make sure usernames aren't the same
                                if (newUser.getUsername().equals(user.getUsername())) {
                                    // Display Snackbar to user letting them know that they need
                                    // to select another username
                                    loadingProgress.show();
                                    display(view,SNACKBAR,R.string.error_username_duplicate);
                                    return;
                                }
                            }
                        }

                        firebaseCreateUser(activity,view,loadingProgress,user,usersRef);
                    } else {
                        firebaseCreateUser(activity,view,loadingProgress,user,usersRef);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingProgress.show();

                }

            });
        }
    }

    private void firebaseCreateUser(final Activity activity, final View view, final ProgressDialog loadingProgress,final User user, final DatabaseReference usersRef) {
        final Context context = view.getContext();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener((AppCompatActivity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    // Display Snackbar letting the user know they failed to signed up
                    loadingProgress.show();
                    display(view,SNACKBAR,R.string.error_incorrect_signup_general);
                } else {
                    // Get UID from Firebase's result
                    final String resultUID = task.getResult().getUser().getUid();
                    // Make new child reference from users reference with given resultUID
                    final DatabaseReference newUserReference = usersRef.child(resultUID);
                    // Set the given firebase user's UID property to the resultUID value
                    user.setUID(resultUID);
                    // Set the new user reference's value to the firebase user's value
                    newUserReference.setValue(user.toMap());
                    // Display Snackbar letting the user know they sucessfully signed up
                    loadingProgress.show();
                    display(view,SNACKBAR,R.string.success_signup);
                    // Sign in the new user
                    FirebaseHelper.signin(activity,view,user);
                }
            }


        });
    }

    private FirebaseObject getFirebaseContentFromFields(int FIREBASE_CONTENT) {
        // Edit Text View IDs
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username,
                R.id.input_password,
                R.id.input_confirm_password,
                R.id.input_email,
                R.id.input_full_name
        };

        final ArrayList<String> fields = new ArrayList<>();
        for (int i = 0; i < INPUT_VIEW_IDS.length; i++) {
            fields.add(((EditText)getView().findViewById(INPUT_VIEW_IDS[i])).getText().toString());
        }

        return FirebaseObject.getFirebaseObjectFromFields(fields,FIREBASE_CONTENT);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
