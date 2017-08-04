package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.backend.Backend;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class RecoverPasswordDialog extends AlertDialog.Builder {

    public RecoverPasswordDialog(final Activity activity, final View containingView) {
        super(containingView.getContext());
        setupDialog(activity, containingView);
    }

    private void setupDialog(final Activity activity, final View containingView) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_recover_password));

        // Add Username or Email Field
        final TextInputLayout usernameLayout = new TextInputLayout(getContext());
        final TextInputEditText usernameOrEmail = new TextInputEditText(getContext());
        setupLayout(usernameLayout,usernameOrEmail);

        // Setting Icon to Dialog
        setIcon(R.drawable.ic_lock_open);

        // Set Positive "Email Me" Button
        setupPositiveButton(activity, containingView, usernameOrEmail);

        // Showing Alert Message
        show();
    }

    private void setupLayout(final TextInputLayout usernameLayout, final TextInputEditText usernameOrEmail) {
        // Create LinearLayout to add TextInputLayout with Edit Text
        final LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        usernameOrEmail.setLayoutParams(layoutParams);
        usernameOrEmail.setMaxLines(1);
        usernameOrEmail.setHint(getContext().getString(R.string.username_or_email_text));
        usernameLayout.addView(usernameOrEmail,0,layoutParams);
        layout.addView(usernameLayout);
        setView(layout);
    }

    private void setupPositiveButton(final Activity activity, final View containingView, final TextInputEditText usernameOrEmail) {
        setPositiveButton(getContext().getString(R.string.dialog_email_me_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        if (Constants.Bools.FeaturesAvailable.RECOVER_PASSWORD) {
                            // Ensure Field was filled
                            if (!usernameOrEmail.getText().toString().equals("")) {
                                // Search For Username Or Email
                                searchForUser(activity, containingView, usernameOrEmail);
                            } else {
                                // Show Message, User Needs to Fill Field
                                display(containingView,SNACKBAR,R.string.enter_username_or_email_text);
                            }
                        } else {
                            display(containingView,SNACKBAR,R.string.feature_unavailable);
                        }

                    }
                });
    }

    private void searchForUser(final Activity activity, final View containingView, final TextInputEditText usernameOrEmail) {
        Backend.getReference(
                activity,
                R.string.firebase_users_directory
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (final DataSnapshot userReference : dataSnapshot.getChildren()) {
                        // userItem is holding the current userReference
                        final User userItem = new User(userReference);
                        // check to make sure input is referring to the userItem
                        if (userItem.getUsername().equals(usernameOrEmail.getText().toString())
                                || userItem.getEmail().equals(usernameOrEmail.getText().toString())) {

                            // Send Email

                            sendPasswordResetEmail(containingView,userItem.getEmail());
                            return;
                        }
                    }
                    // Display Message that Email or Username didn't match
                    display(containingView,SNACKBAR,R.string.error_user_not_found);
                } else {
                    // Throw Error, Should should be at least 1 User
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
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