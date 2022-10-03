package com.ochs.app.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.ochs.app.R;

public class ProgressDialog extends AlertDialog
{
    public ProgressDialog(Context context)
    {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
    }

    private static AlertDialog dialog;

    public static void showDialog(Context context, boolean cancelable)
    {
        cancelDialog();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(cancelable);
        dialog.show();
    }

    public static void cancelDialog()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static ProgressDialog getProgressDialog(Context context, boolean cancelable)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(cancelable);
        return dialog;
    }
}
