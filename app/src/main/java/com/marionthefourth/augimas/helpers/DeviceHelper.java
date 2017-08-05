package com.marionthefourth.augimas.helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class DeviceHelper {
    public static void dismissKeyboard(final View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void vibrateDevice(final Context context) {
        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 1 seconds
        v.vibrate(1000);
    }
    public static void copyToClipboard(final Context context, final String label, final String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}