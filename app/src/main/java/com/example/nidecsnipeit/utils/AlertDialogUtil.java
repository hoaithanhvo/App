package com.example.nidecsnipeit.utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.example.nidecsnipeit.R;

public class AlertDialogUtil {
    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        SpannableString spannableString = new SpannableString("OK");

        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.secondary)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
