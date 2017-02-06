package com.appscreat.project.ads;

import android.app.Activity;
import android.content.Context;

import com.appscreat.project.Variables;
import com.appscreat.project.interfaces.InterfaceListener;
import com.appscreat.project.util.ProgressDialogManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crash.FirebaseCrash;

import static com.appscreat.project.Variables.DEVICE_ID;
import static com.appscreat.project.Variables.DEVICE_ID_2;
import static com.appscreat.project.Variables.DEVICE_ID_3;
import static com.appscreat.project.Variables.DEVICE_ID_4;
import static com.appscreat.project.Variables.INTERSTITIAL_ID;

public class AdsInterstitial {

    private static final String TAG = "AdsInterstitial";
    private Context context;
    private InterstitialAd interstitialAd;
    private AdRequest interstitialRequest;
    private ProgressDialogManager progressDialog;

    public AdsInterstitial(Context context) {
        this.context = context;

        if (!INTERSTITIAL_ID.equals("")) {
            MobileAds.initialize(context, Variables.ADMOB_APP_ID);
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(INTERSTITIAL_ID);
            interstitialRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(DEVICE_ID)
                    .addTestDevice(DEVICE_ID_2)
                    .addTestDevice(DEVICE_ID_3)
                    .addTestDevice(DEVICE_ID_4)
                    .build();
        }
    }

    public void showInterstitialWithProgress(boolean localVariable, final InterfaceListener interstitialBackListener) {
        if (interstitialAd != null && !Variables.isPremium && !interstitialAd.isLoading() && localVariable) {
            loadInterstitial();
            progressDialog = new ProgressDialogManager(context);
            progressDialog.showProgressDialog();
            interstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    progressDialog.dismissProgressDialog();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    progressDialog.dismissProgressDialog();
                }

                @Override
                public void onAdLoaded() {
                    onShowInterstitial();
                    progressDialog.dismissProgressDialog();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    progressDialog.dismissProgressDialog();
                }

                @Override
                public void onAdClosed() {
                    progressDialog.dismissProgressDialog();
                    if (interstitialBackListener != null)
                        try {
                            interstitialBackListener.onCallMethod();
                        } catch (Exception e) {
                            FirebaseCrash.report(e);
                        }
                }
            });
        } else {
            if (interstitialBackListener != null)
                try {
                    interstitialBackListener.onCallMethod();
                } catch (Exception e) {
                    FirebaseCrash.report(e);
                }
        }
    }

    public void showInterstitial(boolean localVariable, final InterfaceListener interstitialBackListener) {
        if (interstitialAd != null && !Variables.isPremium && localVariable) {
            interstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    onShowInterstitial();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    if (interstitialBackListener != null)
                        interstitialBackListener.onCallMethod();
                }

                @Override
                public void onAdClosed() {
                    if (interstitialBackListener != null)
                        interstitialBackListener.onCallMethod();
                }

                @Override
                public void onAdOpened() {
                    if (interstitialBackListener != null)
                        interstitialBackListener.onCallMethod();
                }
            });
        } else {
            if (interstitialBackListener != null)
                interstitialBackListener.onCallMethod();
        }
    }

    private void loadInterstitial() {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                if (interstitialAd != null && !INTERSTITIAL_ID.equals("")) {
                    interstitialAd.loadAd(interstitialRequest);
                }
            }
        });
    }

    private void onShowInterstitial() {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });
    }
}
