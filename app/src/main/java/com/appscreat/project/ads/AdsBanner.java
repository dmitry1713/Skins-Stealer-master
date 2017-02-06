package com.appscreat.project.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.appscreat.project.BuildConfig;
import com.appscreat.project.Variables;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AdsBanner {

    private static final String TAG = "AdsBanner";
    private AdView adView;
    private Context context;
    private String banner_id;
    private String[] device_id;

    public AdsBanner(Context context, String banner_id, String[] device_id) {
        this.context = context;
        this.banner_id = banner_id;
        this.device_id = device_id;
    }

    public AdsBanner(Context context, String banner_id) {
        this.context = context;
        this.banner_id = banner_id;
        this.device_id = new String[0];
    }

    public AdView getAdView() {
        initAdView();
        initAdRequest();
        adView.setAdListener(getAdListener());
        return adView;
    }

    private void initAdView() {
        MobileAds.initialize(context, Variables.ADMOB_APP_ID);
        adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(banner_id);
    }

    @NonNull
    private AdListener getAdListener() {
        return new AdListener() {

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad");
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
            }
        };
    }

    private void initAdRequest() {
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            for (String s : device_id) {
                adRequestBuilder.addTestDevice(s);
            }
        }
        AdRequest adRequest = adRequestBuilder.build();
        adView.loadAd(adRequest);
    }

}
