package com.marionthefourth.augimas.dialogs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.LinearLayout;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class ChangePasswordDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public ChangePasswordDialog(final View containingView) {
        super(containingView.getContext());
        setupDialog(containingView);

    }
//    Dialog Setup Methods
    private void setupDialog(final View containingView) {
        final User user = getCurrentUser();

        if (user != null || PROTOTYPE_MODE) {
            setTitle(getContext().getString(R.string.title_change_password));

            setIcon(R.drawable.ic_lock_open);

            final ArrayList<TextInputEditText> inputs = new ArrayList<>();

            setupDialogLayouts(inputs, new ArrayList<TextInputLayout>());
            setupPositiveButton(user, inputs, containingView);

            show();
        }

    }
    private void setupDialogLayouts(final ArrayList<TextInputEditText> inputs, final ArrayList<TextInputLayout> layouts) {
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
    private void setupPositiveButton(final User user, final ArrayList<TextInputEditText> inputs, final View containingView) {
        setPositiveButton(getContext().getString(R.string.update_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (Constants.Bools.FeaturesAvailable.CHANGE_PASSWORD) {
                    if (!FragmentHelper.fieldsAreFilled(inputs)) {
                        display(SNACKBAR, R.string.error_field_required, containingView);
                        dialog.dismiss();
                        return;
                    }
                    if (passwordsDoNotMatch(inputs)) {
                        display(SNACKBAR, R.string.error_password_mismatch, containingView);
                        dialog.dismiss();
                        return;
                    }
                    if (passwordIsNotCorrectLength(inputs)) {
                        display(SNACKBAR, R.string.error_invalid_password_length, containingView);
                        dialog.dismiss();
                        return;
                    }

                    final ProgressDialog loadingProgress = FragmentHelper.buildProgressDialog(R.string.updating_password_text,containingView);
                    Backend.reAuthenticate(user,inputs.get(0).getText().toString(),inputs.get(1).getText().toString(),loadingProgress,dialog,containingView);
                } else {
                    display(SNACKBAR, R.string.feature_unavailable, containingView);
                }
            }
        });
    }
//    Input Verification Methods
    private boolean passwordsDoNotMatch(final ArrayList<TextInputEditText> inputs) {
        return !inputs.get(1).getText().toString().equals(inputs.get(2).getText().toString());
    }
    private boolean passwordIsNotCorrectLength(final ArrayList<TextInputEditText> inputs) {
        return inputs.get(1).getText().toString().length() < 6;
    }
}
