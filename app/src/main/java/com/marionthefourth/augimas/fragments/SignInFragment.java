package com.marionthefourth.augimas.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.SignUpActivity;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.FirebaseObject;
import com.marionthefourth.augimas.classes.User;
import com.marionthefourth.augimas.helpers.DeviceHelper;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.Constants.Ints.FIREBASE_USER;
import static com.marionthefourth.augimas.classes.Constants.Ints.FORGOT_PASSWORD_BUTTON;
import static com.marionthefourth.augimas.classes.Constants.Ints.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.Constants.Ints.SIGN_IN_BUTTON;
import static com.marionthefourth.augimas.classes.Constants.Ints.SIGN_UP_TEXT_BUTTON;
import static com.marionthefourth.augimas.classes.Constants.Ints.SNACKBAR;
import static com.marionthefourth.augimas.classes.Constants.Ints.TOAST;
import static com.marionthefourth.augimas.helpers.DeviceHelper.dismissKeyboard;
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
        ArrayList<Button> buttons = getAllButtonViews(view);

        // Add all Inputs to an ArrayList
        final ArrayList<TextInputEditText> inputs = getAllEditTextViews(view);

        // Add all Layouts to an ArrayList
        final ArrayList<TextInputLayout> layouts = getAllTextInputLayoutViews(view);

        User user = new User();
        // Check if user was already logged in
//        checkIfUserIsLoggedIn(view,user);

        // Set OnFocusChangeListener to all Inputs & Add TextChangedListener to all Inputs
        setupTextInputsWithFocusAndTextChangedListeners(layouts,inputs);

        // Sign In Button (Connect to Firebase & Transition to Home)
        setupSignInButtonsOnClickListener(layouts,inputs,view,buttons.get(SIGN_IN_BUTTON),user);

        // Sign Up Button (Transition to Sign Up)
        setupSignUpButtonsOnClickListener(inputs,view,buttons.get(SIGN_UP_TEXT_BUTTON),user);

        // Forgot Password Button (Transition to Forgot Password Section)
        setupForgotPasswordButtonsOnClickListener(view,buttons.get(FORGOT_PASSWORD_BUTTON));

        //            initializeAdminSDK();

        return view;
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
    private void setupSignUpButtonsOnClickListener(final ArrayList<TextInputEditText> inputs, final View view, final Button button, final User user) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(view.getContext(), SignUpActivity.class);
//                User newUser = (User) getFirebaseContentFromFields(User);
//                signUpIntent.putExtra(Constants.Strings.USERNAME_OR_EMAIL,inputs.get(Constants.Ints.USERNAME).getText().toString());
//                signUpIntent.putExtra(Constants.Strings.PASSWORD,inputs.get(Constants.Ints.PASSWORD).getText().toString());
//                signUpIntent.putExtra(Constants.Strings.JOINT_UID,newUser.getJointUID());
                getActivity().startActivity(signUpIntent);
            }
        });
    }
    private ArrayList<Button> getAllButtonViews(final View view) {
        // Button IDS
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
    private void setupSignInButtonsOnClickListener(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs, final View view, final Button button, final User user) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allTextFieldsAreFilled(view,layouts,inputs) || PROTOTYPE_MODE) {
                    // Connect to Firebase & Attempt to Log In
                    dismissKeyboard(view);

                    if (Constants.FeaturesAvailable.SIGN_IN) {
                        User newUser = (User) getFirebaseContentFromFields(FIREBASE_USER);
                        user.setPassword(newUser.getPassword());
                        user.setUsername(newUser.getUsername());
                        user.setEmail(newUser.getEmail());
                        user.setFullname(newUser.getFullname());
                    } else  {
                        display(view,SNACKBAR,R.string.feature_unavailable);
                    }

                    FirebaseHelper.signin(view,user);
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
        // Text Input Layout IDS
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
        // TextInputEditText View IDS
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
    private void setupForgotPasswordButtonsOnClickListener(final View view, final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAndDisplayForgotPasswordDialog(view);
            }
        });
    }
    private void setupAndDisplayForgotPasswordDialog(final View view) {
        // Creating alert Dialog with one Button
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());

        // Setting Dialog Title
        alertDialog.setTitle(view.getContext().getString(R.string.title_recover_password));

        // Add Username or Email Field
        final TextInputLayout usernameLayout = new TextInputLayout(view.getContext());
        final TextInputEditText usernameOrEmail = new TextInputEditText(view.getContext());
        setupForgotPasswordDialogLayout(alertDialog,view,usernameLayout,usernameOrEmail);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_lock_open);

        // Set Positive "Email Me" Button
        setupForgotPasswordDialogPositiveButton(alertDialog, view, usernameOrEmail);

        // Showing Alert Message
        alertDialog.show();
    }
    private void setupForgotPasswordDialogLayout(AlertDialog.Builder alertDialog, View view, TextInputLayout usernameLayout, TextInputEditText usernameOrEmail) {
        // Create LinearLayout to add TextInputLayout with Edit Text
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        usernameOrEmail.setLayoutParams(layoutParams);
        usernameOrEmail.setEnabled(true);
        usernameOrEmail.setHint(view.getContext().getString(R.string.username_or_email_text));
        usernameLayout.addView(usernameOrEmail,0,layoutParams);
        layout.addView(usernameLayout);
        alertDialog.setView(layout);
    }
    private void setupForgotPasswordDialogPositiveButton(final AlertDialog.Builder alertDialog, final View view, final TextInputEditText usernameOrEmail) {
        alertDialog.setPositiveButton(view.getContext().getString(R.string.dialog_email_me_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        if (Constants.FeaturesAvailable.RECOVER_PASSWORD) {
                            // Ensure Field was filled
                            if (!usernameOrEmail.getText().toString().equals("")) {
                                // Search For Username Or Email
                                searchForUser(view,dialog,usernameOrEmail);
                            } else {
                                // Show Message, User Needs to Fill Field
                                display(view,SNACKBAR,R.string.enter_username_or_email_text);
                            }
                        } else {
                            display(view,SNACKBAR,R.string.feature_unavailable);
                        }

                    }
                });
    }
    private void searchForUser(final View view, final DialogInterface dialog, final TextInputEditText usernameOrEmail) {
        FirebaseHelper.getReference(
                view.getContext(),
                R.string.firebase_users_directory
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userItem;
                if (dataSnapshot.hasChildren()) {
                    for (final DataSnapshot userReference : dataSnapshot.getChildren()) {
                        // userItem is holding the current userReference
                        userItem = new User(userReference);
                        // check to make sure input is referring to the userItem
                        if (userItem.getUsername().equals(usernameOrEmail.getText().toString())
                                || userItem.getEmail().equals(usernameOrEmail.getText().toString())) {

                            // Send Email
                            sendPasswordResetEmail(view,userItem.getEmail());
                            return;
                        }
                    }
                    // Display Message that Email or Username didn't match
                    display(view,SNACKBAR,R.string.error_user_not_found);
                } else {
                    // Throw Error, Should should be at least 1 User
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
    private FirebaseObject getFirebaseContentFromFields(int FIREBASE_CONTENT) {
        // Edit Text View IDS
        final int INPUT_VIEW_IDS[] = {
                R.id.input_username_or_email,
                R.id.input_password
        };

        ArrayList<String> fields = new ArrayList<>();
        for (int i = 0; i < INPUT_VIEW_IDS.length; i++) {
            fields.add(((EditText)getView().findViewById(INPUT_VIEW_IDS[i])).getText().toString());
        }

        return FirebaseObject.getFirebaseObjectFromFields(fields,FIREBASE_CONTENT);
    }
    private void sendPasswordResetEmail(final View view, String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            display(view,TOAST,R.string.success_email_sent);
                        }
                    }
                });
    }
}