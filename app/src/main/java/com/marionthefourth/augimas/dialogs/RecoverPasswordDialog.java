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
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.entities.User;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class RecoverPasswordDialog extends AlertDialog.Builder {
//    Dialog Constructor
    public RecoverPasswordDialog(final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(containingView, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final View containingView, final Activity activity) {
        setTitle(getContext().getString(R.string.title_recover_password));

        final TextInputEditText usernameOrEmail = new TextInputEditText(getContext());
        setupLayout(usernameOrEmail, new TextInputLayout(getContext()));

        setIcon(R.drawable.ic_lock_open);

        setupPositiveButton(usernameOrEmail, containingView, activity);

        show();
    }
    private void setupLayout(final TextInputEditText usernameOrEmail, final TextInputLayout usernameLayout) {
        final LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        usernameOrEmail.setMaxLines(1);
        usernameOrEmail.setLayoutParams(layoutParams);
        usernameOrEmail.setHint(getContext().getString(R.string.username_or_email_text));
        usernameLayout.addView(usernameOrEmail,0,layoutParams);
        layout.addView(usernameLayout);
        setView(layout);
    }
    private void setupPositiveButton(final TextInputEditText usernameOrEmail, final View containingView, final Activity activity) {
        setPositiveButton(getContext().getString(R.string.dialog_email_me_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        if (Constants.Bools.FeaturesAvailable.RECOVER_PASSWORD) {
                            if (!usernameOrEmail.getText().toString().equals("")) {
                                searchForUser(usernameOrEmail, containingView, activity);
                            } else {
                                display(SNACKBAR, R.string.enter_username_or_email_text, containingView);
                            }
                        } else {
                            display(SNACKBAR, R.string.feature_unavailable, containingView);
                        }
                    }
                });
    }
//    Functional Methods
    private void sendPasswordResetEmail(String email, final View view) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) display(TOAST, R.string.success_email_sent, view);
                }
            });
}
    private void searchForUser(final TextInputEditText usernameOrEmail, final View containingView, final Activity activity) {
    Backend.getReference(R.string.firebase_users_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChildren()) {
                for (final User userItem: User.toArrayList(dataSnapshot)) {
                    if (userItem.usernameOrEmailMatches(usernameOrEmail.getText().toString())){
                        sendPasswordResetEmail(userItem.getEmail(), containingView);
                        return;
                    }
                }
                display(SNACKBAR, R.string.error_user_not_found, containingView);
            }
        }
        @Override
        public void onCancelled(DatabaseError error) {}
    });
}
}