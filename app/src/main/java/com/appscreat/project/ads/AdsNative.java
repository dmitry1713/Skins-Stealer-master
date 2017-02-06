package com.appscreat.project.ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.appscreat.project.Variables;
import com.appscreat.project.util.Helper;
import com.appscreat.project.util.ScreenSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import static com.appscreat.project.Variables.DEVICE_ID;
import static com.appscreat.project.Variables.DEVICE_ID_2;
import static com.appscreat.project.Variables.DEVICE_ID_3;
import static com.appscreat.project.Variables.DEVICE_ID_4;
import static com.appscreat.project.Variables.NATIVE_LARGE_ID;

public class AdsNative {

    public static final int NATIVE_MIDDLE_HEIGHT = 132;
    public static final int NATIVE_MIDDLE_HEIGHT_TABLET = 236;
    public static final int NATIVE_LARGE_HEIGHT = 320;
    public static final int NATIVE_LARGE_HEIGHT_TABLET = 600;
    public static final int TABLET_WIDTH = 600;
    private static final String TAG = "NativeAdmob";
    private NativeExpressAdView adView;
    private Context context;
    private int height;

    public AdsNative(Context context) {
        this.context = context;

        Log.d(TAG, "AdsNative: ScreenWidthDP " + ScreenSize.getWidthDP(context));
        if (ScreenSize.getWidthDP(context) >= TABLET_WIDTH) {
            height = NATIVE_LARGE_HEIGHT_TABLET;
        } else {
            height = NATIVE_LARGE_HEIGHT;
        }

        if (!NATIVE_LARGE_ID.isEmpty()) {
            MobileAds.initialize(context, Variables.ADMOB_APP_ID);
            adView = new NativeExpressAdView(context);
            adView.setAdSize(new AdSize(getFullWidth(), height));
            adView.setAdUnitId(NATIVE_LARGE_ID);
        }
    }

    public AdsNative(Context context, String adUnitId, int adHeightPhone, int adHeightTablet) {
        this.context = context;

        Log.d(TAG, "AdsNative: ScreenWidthDP " + ScreenSize.getWidthDP(context));
        if (ScreenSize.getWidthDP(context) >= TABLET_WIDTH) {
            height = adHeightTablet;
        } else {
            height = adHeightPhone;
        }

        MobileAds.initialize(context, Variables.ADMOB_APP_ID);
        adView = new NativeExpressAdView(context);
        adView.setAdSize(new AdSize(getFullWidth(), height));
        adView.setAdUnitId(adUnitId);
    }

    public View getNativeAdView() {
        if (adView != null && !Variables.isPremium) {
            AdRequest adRequestBuilder = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(DEVICE_ID)
                    .addTestDevice(DEVICE_ID_2)
                    .addTestDevice(DEVICE_ID_3)
                    .addTestDevice(DEVICE_ID_4)
                    .build();

            adView.loadAd(adRequestBuilder);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.d(TAG, "onAdFailedToLoad: Native Ads");
                    adView.setVisibility(View.GONE);
                    Helper.sendEventAnalytics(context, "NativeAdsFailed", "SizeAds", String.valueOf("Width: " + getFullWidth() + " Height: " + height));
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d(TAG, "onAdLoaded: Native Ads");
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(7, 7, 7, 7);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            adView.setLayoutParams(layoutParams);
        }
        return adView;
    }

    public NativeExpressAdView getAdView() {
        return adView;
    }

    private int getFullWidth() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = context.getResources().getDisplayMetrics().density;
        float dpHeight = (outMetrics.heightPixels / density);
        float dpWidth = (outMetrics.widthPixels / density);
        int inWidth = (int) (dpWidth) - (30);
        Log.d(TAG, "buildNativeAdmobFragment: " + dpWidth + " " + inWidth);
        return inWidth;
    }
}