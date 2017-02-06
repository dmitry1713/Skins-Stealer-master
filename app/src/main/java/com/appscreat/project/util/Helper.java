package com.appscreat.project.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appscreat.project.App;
import com.appscreat.project.R;
import com.appscreat.project.Variables;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

public class Helper {

    private static final String TAG = "Helper";

    public static String onVerificationUrl(String url) {
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    public static String onVerificationString(String string) {
        return string.replace("${tabulate}", "\t")
                .replace("${newline}", "\n")
                .replace("${return}", "\r")
                .replace("${doublequote}", "\'");
    }

    public static void openActivity(Context context, Class className) {
        try {
            Intent intent = new Intent(context, className);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    public static void openActivityWithIntent(Context context, Class className, String extra) {
        try {
            Intent intent = new Intent(context, className);
            intent.putExtra("EXTRA", extra);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    public static void initShareApplication(Context context) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Get in on Google Play https://play.google.com/store/apps/details?id=" + context.getPackageName());
            sendIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_choise)));
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    public static void initRateApplication(final Context context) {
        // Saving for preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.appreciated_pref), true);
        editor.apply();
        Log.d(TAG, "isAppreciatedPref: " + prefs.getBoolean(context.getString(R.string.appreciated_pref), false));

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.like_app, context.getString(R.string.app_name)))
                .setMessage(R.string.rate_us_on_google)
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String packageName = context.getPackageName();
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.protocol_market) + packageName)));
                        } catch (ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.protocol_http_google_play) + packageName)));
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, R.string.thank_feedback, Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    public static void sendEventFirebase(Context context, String categoty, String name) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, categoty);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public static void sendEventAnalytics(Context context, String category, String action, String label) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        analytics.setLocalDispatchPeriod(1800);
        Tracker tracker = analytics.newTracker(Variables.ANALYTICS_ID);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public static void openApp(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent == null) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + packageName));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.report(e);
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    public void openDeveloperPage(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:Appscreat"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean checkAppInDevice(Context context, String packageName) {
        boolean b = false;
        try {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                b = true;
            } catch (PackageManager.NameNotFoundException e) {
                b = false;
            }
            Log.d(TAG, "checkAppInDevice: " + packageName + " " + b);
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
        return b;
    }

    public static SimpleImageLoadingListener getSimpleImageLoadingListener(final ProgressBar progressBar) {
        return new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static void setBackgroundButton(Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable background = button.getBackground();
            background.mutate();
            background.setColorFilter(Color.parseColor(Variables.MAIN_COLOR_STYLE), PorterDuff.Mode.SRC);
            button.setBackground(background);
        }
    }

    public static void setBackgroundSuccessButton(Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable background = button.getBackground();
            background.mutate();
            background.setColorFilter(Color.parseColor(Variables.BUTTON_SUCCESS_COLOR_STYLE), PorterDuff.Mode.SRC);
            button.setBackground(background);
        }
    }

    public static void sendMail(Context context, String subject, String body, String filelocation) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.support_gmail)});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);

        if (filelocation != null) {
            File file = new File(filelocation);
            Uri path = Uri.fromFile(file);
            i.putExtra(Intent.EXTRA_STREAM, path);
        }
        try {
            context.startActivity(Intent.createChooser(i, context.getString(R.string.send)));
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.report(e);
            Toast.makeText(context, R.string.not_email_client, Toast.LENGTH_SHORT).show();
        }
    }

    public static String checkLocaleJsonFile(String fileName) {
        String locale = Locale.getDefault().getLanguage();
        try {
            if (Arrays.asList(App.getAppContext().getResources().getAssets().list("")).contains(fileName + "-" + locale + ".json")) {
                return fileName + "-" + locale + ".json";
            } else {
                return fileName + ".json";
            }
        } catch (IOException e) {
            FirebaseCrash.report(e);
        }
        return fileName + ".json";
    }

    public static String checkLocaleJsonString(JSONObject jsonObject, String string) {
        String locale = Locale.getDefault().getLanguage();
        if (!jsonObject.isNull(string + "_" + locale)) {
            return string + "_" + locale;
        } else {
            return string;
        }
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            FirebaseCrash.report(e);
        }

        return bitmap;
    }

}
