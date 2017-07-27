package com.marionthefourth.augimas.helpers;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.PROGRESS_DIALOG;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;

public final class FragmentHelper {

    public static ProgressDialog display(final View view, final int VIEW_TYPE, final int STRING_ID) {
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

    public static void display(final View view, final int VIEW_TYPE, final int STRING_ID, final String additionalText) {
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

    public static ProgressDialog build(View view, int stringID) {
        final ProgressDialog loadingProgress = new ProgressDialog(view.getContext());
        loadingProgress.setMessage(view.getContext().getString(stringID));
        loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
        loadingProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        loadingProgress.show();
        return loadingProgress;
    }

    public static boolean fieldsAreFilled(ArrayList<TextInputEditText> editTexts) {
        for (int i = 0; i < editTexts.size(); i++) {
            if (editTexts.get(i).getText().equals("")) {
                return false;
            }
        }

        return true;
    }

    public static boolean fieldsPassWhitelist(ArrayList<TextInputEditText> editTexts) {
        String[] whitelist = new String[] {
            "augimas","augimus","augeemas","augeemus"
        };
        for (int i = 0; i < editTexts.size(); i++) {
            for (int j = 0; j < whitelist.length; j++) {
                if (editTexts.get(i).getText().toString().toLowerCase().equals(whitelist[j])) {
                    return false;
                }
            }

        }

        return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static void handleNonSupportFragmentRemoval(final FragmentManager rManager) {
        if (rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS) != null) {
            rManager.beginTransaction().remove(rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS)).commit();
        }
    }
}