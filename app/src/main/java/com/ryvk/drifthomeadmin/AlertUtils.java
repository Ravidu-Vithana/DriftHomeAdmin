package com.ryvk.drifthomeadmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertUtils {
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    public static void showConfirmDialog(Context context, String title, String message,
                                         DialogInterface.OnClickListener yesListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", yesListener)
                .setNegativeButton("No", null)
                .show();
    }
    public static AlertDialog showExitConfirmationDialog(Context context) {
        if (context instanceof Activity) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Exit Confirmation")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> ((Activity) context).finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.show();
            return alertDialog;
        }
        return null;
    }
}
