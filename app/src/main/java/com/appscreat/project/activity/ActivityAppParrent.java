package com.appscreat.project.activity;


import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appscreat.project.R;
import com.appscreat.project.Variables;
import com.appscreat.project.ads.AdsBanner;
import com.appscreat.project.interfaces.FragmentInterface;
import com.google.firebase.crash.FirebaseCrash;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import static com.appscreat.project.Variables.DEVICE_ID;
import static com.appscreat.project.Variables.DEVICE_ID_2;
import static com.appscreat.project.Variables.DEVICE_ID_3;
import static com.appscreat.project.Variables.DEVICE_ID_4;

public class ActivityAppParrent extends AppCompatActivity implements FragmentInterface {

    public static final String TAG = "ActivityAppParrent";
    public Toolbar toolbar;
    public AdsBanner adsBanner;
    public boolean doubleBackToExitPressedOnce = false;

    //Permission WRITE_EXTERNAL_STORAGE
    public final int REQUEST_EXTERNAL_STORAGE = 200;
    public String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public String[] device_id = {DEVICE_ID, DEVICE_ID_2, DEVICE_ID_3, DEVICE_ID_4};

    public void initHideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void initBanner(LinearLayout linearLayout) {
        adsBanner = new AdsBanner(this, Variables.BANNER_ID, device_id);
        linearLayout.removeAllViews();
        linearLayout.addView(adsBanner.getAdView());
    }

    public boolean shouldAskPermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    public boolean onSdkMoreThanLollipopMR1() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_EXTERNAL_STORAGE:
                try {
                    if (permissions.length != 0) {
                        boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        if (writeAccepted) {
                            onPermissionSuccessResult();
                        } else {
                            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Resources.NotFoundException e) {
                    FirebaseCrash.report(e);
                }
                break;
        }
    }

    @Override
    public void initBannerFragment(LinearLayout linearLayout) {
        initBanner(linearLayout);
    }

    public void onPermissionSuccessResult() {

    }
}
