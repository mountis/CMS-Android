package com.marionthefourth.augimas.dialogs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class ChangePasswordDialog extends AlertDialog.Builder {

    public ChangePasswordDialog(final View containingView) {
        super(containingView.getContext());
        setupDialog(containingView);

    }

    private void setupDialog(final View containingView) {
        // Get FirebaseUser

        final User user = getCurrentUser();

        if (user != null || PROTOTYPE_MODE) {
            // Setting Dialog Title
            setTitle(getContext().getString(R.string.title_change_password));

            // Setting Icon to Dialog
            setIcon(R.drawable.ic_lock_open);

            // Create LinearLayout to add TextInputLayouts with EditTexts
            final ArrayList<TextInputEditText> inputs = new ArrayList<>();
            final ArrayList<TextInputLayout> layouts = new ArrayList<>();

            setupDialogLayouts(layouts,inputs);

            // Setting Positive "Update" Button
            setupPositiveButton(containingView,inputs, user);

            show();
        }

    }

    private void setupDialogLayouts(final ArrayList<TextInputLayout> layouts, final ArrayList<TextInputEditText> inputs) {
        final LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);
        layout.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

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

            layouts.add(new TextInputLayout(getContext()));
            inputs.add(new TextInputEditText(getContext()));
            inputs.get(i).setTransformationMethod(PasswordTransformationMethod.getInstance());
            layouts.get(i).addView(inputs.get(i), 0, lp);
            layout.addView(layouts.get(i));
            inputs.get(i).setHint(getContext().getString(id));
        }

        setView(layout);
    }

    private void setupPositiveButton(final View containingView, final ArrayList<TextInputEditText> inputs, final User user) {
        setPositiveButton(getContext().getString(R.string.update_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (Constants.Bools.FeaturesAvailable.CHANGE_PASSWORD) {
                    // Ensure Fields are filled
                    if (!FragmentHelper.fieldsAreFilled(inputs)) {
                        display(containingView,SNACKBAR,R.string.error_field_required);
                        dialog.dismiss();
                        return;
                    }
                    // Ensure Passwords are the Same
                    if (passwordsDontMatch(inputs)) {
                        display(containingView,SNACKBAR,R.string.error_password_mismatch);
                        dialog.dismiss();
                        return;
                    }
                    // Ensure Password Length is Greater than 5
                    if (passwordIsNotCorrectLength(inputs)) {
                        display(containingView,SNACKBAR,R.string.error_invalid_password_length);
                        dialog.dismiss();
                        return;
                    }

                    if (Constants.Bools.FeaturesAvailable.CHANGE_PASSWORD) {
                        final ProgressDialog loadingProgress = new ProgressDialog(getContext());
                        setupProgressDialog(containingView,loadingProgress);
                        reauthenticateUser(containingView, dialog,inputs,user, loadingProgress);
                    } else {
                        display(containingView,SNACKBAR,R.string.feature_unavailable);
                    }
                }
            }
        });
    }

    private void reauthenticateUser(final View containingView, final DialogInterface dialog, final ArrayList<TextInputEditText> inputs, User user, final ProgressDialog loadingProgress) {
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
                                        display(containingView,SNACKBAR,R.string.success_password_updated);
                                        dialog.dismiss();
                                    } else {
                                        display(containingView,SNACKBAR,R.string.error_password_updated,task.getResult().toString());
                                        dialog.dismiss();
                                    }
                                }
                            });
                } else {
                    loadingProgress.dismiss();
                    display(containingView,SNACKBAR,R.string.error_incorrect_password);
                    dialog.dismiss();
                }
            }
        });
    }

    private void setupProgressDialog(final View view, final ProgressDialog loadingProgress) {
        loadingProgress.setMessage(view.getContext().getString(R.string.updating_password_text));
        loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
        loadingProgress.setCancelable(false);
        // disable dismiss by tapping outside of the dialog
        loadingProgress.show();
    }

    private boolean passwordIsNotCorrectLength(final ArrayList<TextInputEditText> inputs) {
        return inputs.get(1).getText().toString().length() < 6;
    }

    private boolean passwordsDontMatch(final ArrayList<TextInputEditText> inputs) {
        return !inputs.get(1).getText().toString().equals(inputs.get(2).getText().toString());
    }

}
