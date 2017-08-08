package com.marionthefourth.augimas.helpers;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.PROGRESS_DIALOG;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

public final class FragmentHelper {
//    Widget Building Methods
    public static ProgressDialog buildProgressDialog(int stringID, View view) {
        final ProgressDialog loadingProgress = new ProgressDialog(view.getContext());
        loadingProgress.setMessage(view.getContext().getString(stringID));
        loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
        loadingProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        loadingProgress.show();
        return loadingProgress;
    }
    public static ProgressDialog display(final int VIEW_TYPE, final int STRING_ID, final View view) {
        switch (VIEW_TYPE) {
            case SNACKBAR:
                Snackbar.make(view, STRING_ID, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case TOAST:
                Toast.makeText(view.getContext(), view.getContext().getString(STRING_ID), Toast.LENGTH_SHORT).show();
                break;
            case PROGRESS_DIALOG:
                final ProgressDialog loadingProgress = new ProgressDialog(view.getContext());
                loadingProgress.setMessage(view.getContext().getString(STRING_ID));
                loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
                loadingProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                loadingProgress.show();
                return loadingProgress;
            default:
                return null;
        }
        return null;
    }
    public static void display(final int VIEW_TYPE, final int STRING_ID, final String additionalText, final View view) {
        switch (VIEW_TYPE) {
            case SNACKBAR:
                Snackbar.make(view, STRING_ID + " " + additionalText + ".", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case TOAST:
                Toast.makeText(view.getContext(), view.getContext().getString(STRING_ID) + " " + additionalText + ".", Toast.LENGTH_SHORT).show();
                break;
        }
    }
//    Input Verification Methods
    public final static boolean isValidEmail(String email) {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
}
    public final static boolean isValidEmail(CharSequence target) {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
}
    public static boolean fieldsAreFilled(ArrayList<TextInputEditText> editTexts) {
        for (TextInputEditText input:editTexts) {
            if (input.getText().equals("")) return false;
        }
        return true;
    }
    public static boolean fieldsPassWhitelist(ArrayList<TextInputEditText> inputs) {
        String[] whitelist = {
            "augimas","augimus","augeemas","augeemus"
        };

        for (TextInputEditText input:inputs) {
            for (String filter:whitelist) {
                if (input.getText().toString().toLowerCase().equals(filter)) return false;
            }
        }

        return true;
    }
//    Fragment Transition Methods
    public final static void handleNonSupportFragmentRemoval(final FragmentManager rManager) {
        if (rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS) != null) {
            rManager.beginTransaction().setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS)).commit();
        }
    }
}