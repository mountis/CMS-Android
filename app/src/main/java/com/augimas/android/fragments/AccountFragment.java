package com.augimas.android.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.dialogs.ChangePasswordDialog;
import com.augimas.android.helpers.FragmentHelper;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.backend.Backend.getReference;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.augimas.android.helpers.FragmentHelper.display;

/**
 * Created on 7/23/17.
 */

public final class AccountFragment extends Fragment {
    public static AccountFragment newInstance(String userUID) {
        Bundle args = new Bundle();
        AccountFragment fragment = new AccountFragment();
        args.putString(Constants.Strings.UIDs.USER_UID,userUID);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setupView(inflater.inflate(R.layout.fragment_account,container,false),getActivity());
    }

    private View setupView(final View view, final Activity activity) {
        setupBackButton(view,activity);
        final TextInputEditText emailInput = view.findViewById(R.id.input_email);
        final TextInputEditText usernameInput = view.findViewById(R.id.input_username);
        final TextInputEditText fullNameInput = view.findViewById(R.id.input_full_name);

        fillInputFields(emailInput,usernameInput,fullNameInput,activity);
        setupEmailSection(emailInput,view,activity);
        setupUsernameSection(usernameInput,view,activity);
        setupFullNameSection(fullNameInput,view,activity);

        view.findViewById(R.id.change_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangePasswordDialog(view);
            }
        });

        return view;
    }

    private void fillInputFields(final TextInputEditText emailInput, final TextInputEditText usernameInput, final TextInputEditText fullNameInput, final Activity activity) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User(userSnapshot);
                        emailInput.setText(currentUser.getEmail());
                        usernameInput.setText(currentUser.getUsername());
                        fullNameInput.setText(currentUser.getName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    private void setupEmailSection(final TextInputEditText emailInput, final View view, final Activity activity) {
        final AppCompatButton emailButton = view.findViewById(R.id.change_email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailButton.getText().toString().toLowerCase().equals("change email")) {
                    // unlock email
                    emailInput.setEnabled(true);
                    emailInput.setClickable(true);
                    emailButton.setText("Update Email");
                } else {
                    emailInput.setEnabled(false);
                    emailInput.setClickable(false);

                    if (!emailInput.getText().toString().equals("") && FragmentHelper.isValidEmail(emailInput.getText().toString())) {
                        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        final User currentUser = new User(userSnapshot);
                                        if (!currentUser.getEmail().equals(emailInput.getText().toString())) {
                                            // Update
//                                            currentUser.setEmail(emailInput.getText().toString());
                                            Backend.updateEmail(emailInput.getText().toString(),view,activity);
//                                          Backend.update(currentUser,activity);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }

                    emailButton.setText("Change Email");
                }
            }
        });
    }
    private void setupFullNameSection(final TextInputEditText fullNameInput, final View view, final Activity activity) {
        final AppCompatButton fullNameButton = (AppCompatButton)view.findViewById(R.id.update_full_name_button);
        fullNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullNameButton.getText().toString().toLowerCase().equals("change full name")) {
                    // unlock email
                    fullNameInput.setEnabled(true);
                    fullNameInput.setClickable(true);
                    fullNameButton.setText("Update Full Name");
                } else {
                    fullNameInput.setEnabled(false);
                    fullNameInput.setClickable(false);

                    if (!fullNameInput.getText().toString().equals("")) {
                        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        final User currentUser = new User(userSnapshot);
                                        if (!currentUser.getName().equals(fullNameInput.getText().toString())) {
                                            // Update
                                            currentUser.setName(fullNameInput.getText().toString());
                                            Backend.update(currentUser,activity);                                            FragmentHelper.display(SNACKBAR,R.string.error_invalid_name,view);
                                            FragmentHelper.display(SNACKBAR,R.string.name_updated,view);
                                        } else {
                                            FragmentHelper.display(SNACKBAR,R.string.error_invalid_name,view);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }

                    fullNameButton.setText("Change Full Name");
                }
            }
        });
    }
    private void setupUsernameSection(final TextInputEditText usernameInput, final View view, final Activity activity) {
        final AppCompatButton usernameButton = view.findViewById(R.id.update_username_button);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameButton.getText().toString().toLowerCase().equals("change username")) {
                    // unlock email
                    usernameInput.setEnabled(true);
                    usernameInput.setClickable(true);
                    usernameButton.setText("Update Username");
                } else {
                    usernameInput.setEnabled(false);
                    usernameInput.setClickable(false);

                    if (!usernameInput.getText().toString().equals("")) {
                        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
                            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        final User currentUser = new User(userSnapshot);
                                        if (!currentUser.getUsername().equals(usernameInput.getText().toString())) {

                                            getReference(R.string.firebase_users_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChildren()) {
                                                        for (DataSnapshot userReference : dataSnapshot.getChildren()) {
                                                            final User userItem = new User(userReference);
                                                            if (userItem.getUsername().equals(usernameInput.getText().toString())) {
                                                                display(SNACKBAR, R.string.error_username_duplicate, view);
                                                                return;
                                                            }
                                                        }
                                                        currentUser.setUsername(usernameInput.getText().toString());
                                                        Backend.update(currentUser,activity);
                                                        FragmentHelper.display(SNACKBAR,R.string.username_updated,view);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {}
                                            });
                                        } else {
                                            FragmentHelper.display(SNACKBAR,R.string.error_invalid_username,view);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }

                    usernameButton.setText("Change Username");
                }
            }
        });
    }
    private void setupBackButton(final View view, final Activity activity) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    final FragmentManager rManager = activity.getFragmentManager();
                    final android.support.v4.app.FragmentManager manager = ((AppCompatActivity) activity).getSupportFragmentManager();
                    handleTransitionToSettings(manager,rManager);
                }
                return false;
            }
        });
    }
    private void handleTransitionToSettings(final android.support.v4.app.FragmentManager manager, final FragmentManager rManager) {
        manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).remove(this).commit();
        rManager.beginTransaction().setTransition(
                android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,
                new SettingsFragment(),
                Constants.Strings.Fragments.SETTINGS).commit();
    }
}