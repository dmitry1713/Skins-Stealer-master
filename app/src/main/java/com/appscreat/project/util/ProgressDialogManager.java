package com.appscreat.project.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.appscreat.project.R;
import com.google.firebase.crash.FirebaseCrash;

public class ProgressDialogManager {

    private ProgressDialog progressDialog;

    public ProgressDialogManager(Context context) {
        if (progressDialog == null) {
            initProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    public ProgressDialogManager(Context context, int style) {
        if (progressDialog == null) {
            initProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.setProgressStyle(style);
        }
    }

    public ProgressDialogManager(Context context, int style, String message) {
        if (progressDialog == null) {
            initProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(style);
        }
    }

    public ProgressDialogManager(Context context, int style, String message, boolean cancelable) {
        if (progressDialog == null) {
            initProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(style);
            progressDialog.setCancelable(cancelable);
        }
    }

    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
        } finally {
            progressDialog = null;
        }
    }

    public void showProgressDialog() {
        try {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    public void setProgressDialog(int value){
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setProgress(value);
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    private void initProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
//        progressDialog.setCancelable(true);
//        progressDialog.setIndeterminate(true);
    }
}
