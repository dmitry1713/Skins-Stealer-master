package com.appscreat.project.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import com.appscreat.project.R;
import com.appscreat.project.ads.AdsInterstitial;
import com.appscreat.project.interfaces.InterfaceListener;
import com.google.firebase.crash.FirebaseCrash;

public class DialogManager {

    public static final String TAG = "DialogManager";

    private Context mContext;
    private boolean mShowAds;
    private String mTitle;
    private String mDescription;
    private String mPositiveButton;
    private String mNegativeButton;
    private String mNeutralButton;
    private InterfaceListener mIblPositive;
    private InterfaceListener mIblNegative;
    private InterfaceListener mIblNeutral;


    public DialogManager(Context context) {
        mContext = context;
    }

    public DialogManager(Context context, String title, String description, String positiveButton, boolean showAds) {
        this(context, title, description, positiveButton, null, showAds, null, null);
    }

    public DialogManager(Context context, int title, int description, int positiveButton, boolean showAds) {
        this(context, context.getResources().getString(title), context.getResources().getString(description), context.getResources().getString(positiveButton), null, showAds, null, null);
    }

    public DialogManager(Context context, int title, int description, int positiveButton, int negativeButton, boolean showAds, final InterfaceListener iblPositive, final InterfaceListener iblNegative) {
        this(context, context.getResources().getString(title), context.getResources().getString(description), context.getResources().getString(positiveButton), context.getResources().getString(negativeButton), showAds, iblPositive, iblNegative);
    }

    public DialogManager(Context context, int title, int description, int positiveButton, int negativeButton, int neutralButton, boolean showAds, final InterfaceListener iblPositive, final InterfaceListener iblNegative, final InterfaceListener iblNeutral) {
        this(context, context.getResources().getString(title), context.getResources().getString(description), context.getResources().getString(positiveButton), context.getResources().getString(negativeButton), context.getResources().getString(neutralButton), showAds, iblPositive, iblNegative, iblNeutral);
    }

    public DialogManager(Context context, String title, String description, String positiveButton, String negativeButton, boolean showAds, final InterfaceListener iblPositive, final InterfaceListener iblNegative) {
        this(context, title, description, positiveButton, negativeButton, null, showAds, iblPositive, iblNegative, null);
    }

    public DialogManager(Context context, String title, String description, String positiveButton, String negativeButton, String neutralButton, boolean showAds, final InterfaceListener iblPositive, final InterfaceListener iblNegative, final InterfaceListener iblNeutral) {
        this.mContext = context;
        this.mShowAds = showAds;
        this.mTitle = title;
        this.mDescription = description;
        this.mPositiveButton = positiveButton;
        this.mNegativeButton = negativeButton;
        this.mNeutralButton = neutralButton;
        this.mIblPositive = iblPositive;
        this.mIblNegative = iblNegative;
        this.mIblNeutral = iblNeutral;
        initDialogManager();
    }

    private void initDialogManager() {
        try {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setTitle(mTitle)
                    .setMessage(Helper.onVerificationString(mDescription))
                    .setCancelable(true)
                    .setPositiveButton(mPositiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            new AdsInterstitial(mContext).showInterstitialWithProgress(mShowAds, mIblPositive);
                        }
                    })
                    .setNeutralButton(mNeutralButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AdsInterstitial(mContext).showInterstitialWithProgress(mShowAds, mIblNeutral);
                        }
                    })
                    .setNegativeButton(mNegativeButton,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new AdsInterstitial(mContext).showInterstitialWithProgress(mShowAds, mIblNegative);
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            new AdsInterstitial(mContext).showInterstitialWithProgress(mShowAds, null);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

}
