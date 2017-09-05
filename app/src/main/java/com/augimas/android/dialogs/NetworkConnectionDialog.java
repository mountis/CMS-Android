package com.augimas.android.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.augimas.android.R;

/**
 * Created  on 9/2/17.
 */

public final class NetworkConnectionDialog extends AlertDialog.Builder {
    public NetworkConnectionDialog(Context context) {
        super(context);
        setupDialog();
    }
    //    Dialog Setup Methods
    private void setupDialog() {
        setTitle(R.string.title_network_connection_lost);
        setMessage(R.string.message_network_connection_lost);
        setupPositiveButton();
        show();
    }
    private void setupPositiveButton() {
        setPositiveButton(getContext().getString(R.string.okay_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
