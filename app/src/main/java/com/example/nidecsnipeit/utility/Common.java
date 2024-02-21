package com.example.nidecsnipeit.utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.example.nidecsnipeit.model.SpinnerItemModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public static boolean isHardScanButtonPressed = false;
    public static final int KEYCODE_SCAN = 10036;

    private static final Handler handler = new Handler();

    public static void setHardScanButtonPressed() {
        isHardScanButtonPressed = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isHardScanButtonPressed = false;
            }
        }, 1000); // 500 milliseconds = 0.5 seconds
    }

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

        // set color to progress
        Drawable drawable = new ProgressBar(context).getIndeterminateDrawable();
        drawable.setColorFilter(ContextCompat.getColor(context, R.color.secondary), PorterDuff.Mode.SRC_IN);

        progressDialog.setIndeterminateDrawable(drawable);

        progressDialog.show();
    }


    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // custom alert dialog
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


    // custom snack bar
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
                    callback.onSnackbar();
                }
            }
        }, 2000);

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

    public static List<SpinnerItemModel> convertArrayJsonToListIdName(JSONArray jsonArray) {
        List<SpinnerItemModel> myList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                SpinnerItemModel myObject = new SpinnerItemModel(jsonObject.getString("id"), jsonObject.getString("name"));
                myList.add(myObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return myList;
    }

    public static void focusCursorToEnd(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                if (!editText.getText().toString().isEmpty()) {
                    editText.setSelection(editText.getText().length());
                }
            }
        });
    }
}
