package com.marionthefourth.augimas.helpers;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.marionthefourth.augimas.R;

import static com.marionthefourth.augimas.classes.Constants.Ints.PROGRESS_DIALOG;
import static com.marionthefourth.augimas.classes.Constants.Ints.SNACKBAR;
import static com.marionthefourth.augimas.classes.Constants.Ints.TOAST;

public final class FragmentHelper {

    public static ProgressDialog display(final View view, final int VIEW_TYPE, final int STRING_ID) {
        switch (VIEW_TYPE) {
            case SNACKBAR:
                Snackbar.make(view, STRING_ID, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}