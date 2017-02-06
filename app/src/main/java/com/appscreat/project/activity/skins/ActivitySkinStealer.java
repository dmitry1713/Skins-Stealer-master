package com.appscreat.project.activity.skins;

import android.app.SearchManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appscreat.project.R;
import com.appscreat.project.util.KeyboardUtil;
import com.google.firebase.crash.FirebaseCrash;

public class ActivitySkinStealer extends ActivitySkins implements SearchView.OnQueryTextListener {

    private static final int LAYOUT = R.layout.activity_skin_stealer;
    public static final String TAG = "ActivitySkinStealer";
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initHideStatusBar();
            setContentView(LAYOUT);
            initBanner((LinearLayout) findViewById(R.id.bannerLayout));
            initThreadPolicy();
            skinLinkFile = "http://skins.minecraft.net/MinecraftSkins/herobrine.png";
            initGLSurfaceView();
            getSkinsFromSite(skinLinkFile);
            initFAB(this);
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getSkinsFromSite(skinLinkFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.stealer_menu, menu);

            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);

            MenuItem searchMenuItem = menu.findItem(R.id.search);
            searchMenuItem.expandActionView();

            searchView.setQuery("herobrine", false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            setSkinOnSearch(newText);
        }
        return true;
    }

    private void setSkinOnSearch(String query) {
        Log.d(TAG, "onQueryTextSubmit: " + query);
        skinLinkFile = "http://skins.minecraft.net/MinecraftSkins/" + query + ".png";
        getSkinsFromSite(skinLinkFile);
    }

    private void initThreadPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onBackPressed() {
        try {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.back_pressed, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        } catch (Resources.NotFoundException e) {
            FirebaseCrash.report(e);
        }
    }


}
