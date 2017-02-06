package com.appscreat.project;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends Application {

    public static FirebaseAnalytics mFirebaseAnalytics;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        App.context = getApplicationContext();
        initGoogleAnalytics();
    }

    public static Context getAppContext() {
        return App.context;
    }

    private synchronized void initGoogleAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        Tracker tracker = analytics.newTracker(Variables.ANALYTICS_ID);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.setScreenName(getClass().getName());
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getPackageName())
                .setAction("Launch")
                .build());
    }
}
