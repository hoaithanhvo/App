package com.example.nidecsnipeit.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.google.android.material.snackbar.Snackbar;

import javax.security.auth.callback.Callback;

public class Common {
    public static int convertDpToPixel(int dp, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void showCustomAlertDialog(Context context, String title, String message, boolean showCancelButton, final AlertDialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        SpannableString okSpannableString = new SpannableString("OK");
        okSpannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.secondary)), 0, okSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(okSpannableString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.onPositiveButtonClick();
                        }
                    }
                });

        if (showCancelButton) {
            SpannableString cancelSpannableString = new SpannableString("Cancel");
            cancelSpannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.secondary)), 0, cancelSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setNegativeButton(cancelSpannableString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onNegativeButtonClick();
                    }
                }
            });
        }

        builder.show();
    }


    public static void showCustomSnackBar(final View view, String message, SnackBarType type, final SnackbarCallback callback) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        int backgroundColor = R.color.success;

        if (type == SnackBarType.ERROR) {
            backgroundColor = R.color.error;
        }
        snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), backgroundColor));
        int animationDuration = 200; // milliseconds
        float translationY = snackbarView.getHeight();

        // Hide the snackbar initially
        snackbarView.setTranslationY(translationY);
        // Animate the snackbar to appear with translationY = 0
        snackbarView.animate()
                .translationY(0)
                .setDuration(animationDuration)
                .start();

        // Set callback to run after 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSnackbarDismissed(snackbar);
                }
            }
        }, 1000);

        snackbar.show();
    }

    public enum SnackBarType {
        ERROR,
        SUCCESS
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
