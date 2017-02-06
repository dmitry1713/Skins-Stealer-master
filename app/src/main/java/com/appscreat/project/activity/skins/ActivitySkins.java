package com.appscreat.project.activity.skins;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appscreat.project.R;
import com.appscreat.project.activity.ActivityAppParrent;
import com.appscreat.project.interfaces.DownloadInterface;
import com.appscreat.project.interfaces.InterfaceListener;
import com.appscreat.project.skins.render.MinecraftSkinRenderer;
import com.appscreat.project.skins.render.SkinGLSurfaceView;
import com.appscreat.project.skins.render.SkinRender;
import com.appscreat.project.util.DialogManager;
import com.appscreat.project.util.Helper;
import com.appscreat.project.util.ScreenSize;
import com.appscreat.project.util.files.FileManager;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ActivitySkins extends ActivityAppParrent {

    public SkinGLSurfaceView glSurfaceView;
    public MinecraftSkinRenderer mRenderer;
    public ProgressBar progressBar;
    public String skinLinkFile;

    public FloatingActionButton saveButton;

    public static final String TAG = "ActivitySkins";

    public void initFAB(final Context context) {
        saveButton = (FloatingActionButton) findViewById(R.id.fab_save);
        saveButton.setOnClickListener(getOnClickListener(this));
    }

    private void onSaveToGallery(final Context context) {
        new FileManager.DownloadFileAsync(context, "/Download/Skins/", new DownloadInterface() {
            @Override
            public void downloadComlete(File file, boolean result) {
                if (result) {
                    new DialogManager(context, R.string.file_saved, R.string.skin_saved_to_gallery, R.string.ok, true);
                } else {
                    new DialogManager(context, R.string.file_not_saved, R.string.error_download_description, R.string.ok, true);
                }
            }
        }).execute(skinLinkFile);
    }

    private void onSaveToMinecraft(final Context context) {
        if (!Helper.checkAppInDevice(context, getString(R.string.minecraft_pe))) {
            new DialogManager(context, R.string.minecraft_not_installed_title, R.string.minecraft_not_installed_description, R.string.ok, true);
        } else {
            new FileManager.DownloadFileAsync(context, getString(R.string.file_skin_custom), getString(R.string.path_folder_minecraft), new DownloadInterface() {
                @Override
                public void downloadComlete(File file, boolean result) {
                    if (result) {
                        File fileOptions = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/options.txt");
                        if (fileOptions.exists()) {
                            ActivitySkins.changeOptionsFile(context.getString(R.string.path_folder_minecraft), "options.txt", "game_skintypefull", "game_skintypefull:Standard_Custom");
                            new DialogManager(context, R.string.file_saved, R.string.skin_dialog_description, R.string.open, R.string.cancel, true, new InterfaceListener() {
                                @Override
                                public void onCallMethod() {
                                    Helper.openApp(context, context.getString(R.string.minecraft_pe));
                                }
                            }, null);
                        } else {
                            new DialogManager(context, R.string.error, R.string.skin_not_installed, R.string.ok, true);
                        }
                    } else {
                        new DialogManager(context, R.string.file_not_saved, R.string.error_download_description, R.string.ok, true);
                    }
                }
            }).execute(skinLinkFile);
        }
    }

    public void initGLSurfaceView() {
        //Skin 3D
        try {
            this.glSurfaceView = (SkinGLSurfaceView) this.findViewById(R.id.skins);
            this.mRenderer = new MinecraftSkinRenderer(this, R.raw.nullchar, false);
            this.glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            this.glSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
            this.glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            this.glSurfaceView.setZOrderOnTop(false);
            this.glSurfaceView.setRenderer(this.mRenderer, this.getResources().getDisplayMetrics().density);
            this.glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            this.mRenderer.mCharacter.SetRunning(true);
            this.progressBar = (ProgressBar) this.findViewById(R.id.progress);

            int height = (int) Math.round(ScreenSize.getHeightPX(this) * 0.52);
            int width = (int) Math.round(ScreenSize.getWidthPX(this) * 0.85);
            if (height > width) {
                width = height;
            }
            Log.d(TAG, "GLSurfaceView: Full height " + ScreenSize.getHeightPX(this));
            Log.d(TAG, "GLSurfaceView: Full width " + ScreenSize.getWidthPX(this));
            Log.d(TAG, "GLSurfaceView: height " + height);
            Log.d(TAG, "GLSurfaceView: width " + width);
            final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(14, 14);
            layoutParams.setMargins(0, (int) Math.round(height * 0.15), 0, 0);
            final RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.lnrr);
            relativeLayout.setLayoutParams(layoutParams);

//            this.getSkinsFromSite(skinLinkFile);

            Log.d(TAG, "initGLSurfaceView: glSurfaceView");
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSkinBitmap(Bitmap bitmap) {
        try {
            if (SkinRender.isOldSkin(bitmap)) {
                Log.d(TAG, "setSkinBitmap: isOldSkin");
                bitmap = SkinRender.convertSkin(bitmap);
            }
            this.mRenderer.updateTexture(bitmap);
            this.progressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSkinInv() {
        this.mRenderer.updateTexture(BitmapFactory.decodeStream(this.getResources().openRawResource(R.raw.nullchar)));
        this.progressBar.setVisibility(View.VISIBLE);
    }

    public void getSkinsFromSite(String url) {
        new BitmapFromSite(this).execute(url);
        Log.i("BitmapLoader", "getSkin");
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        Log.d(TAG, "onPause: glSurfaceView.onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        Log.d(TAG, "onResume: glSurfaceView.onResume()");
//        this.getSkinsFromSite(skinLinkFile);
    }

    public static void changeOptionsFile(String path, String fileName, String readLine, String writeLine) {
        List<String> stringList = new ArrayList<>();
        File file;
        File sdPath;

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
        }

        try {
            sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + path);

            // формируем объект File, который содержит путь к файлу
            file = new File(sdPath, fileName);
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(TAG, "readStringsFile: " + str);
                if (str.contains(readLine)) {
                    str = str.replace(str, writeLine);
                }
                stringList.add(str);
            }

            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // пишем данные
            for (String s : stringList) {
                bw.write(s); //записываем в файл строку
                bw.write("\n"); // переходим на след строку
                Log.d(TAG, "writeStringsFile: " + s);
            }
            // закрываем поток
            bw.close();
        } catch (IOException e) {
            FirebaseCrash.report(e);
        }
    }


    public class BitmapFromSite extends AsyncTask<String, Void, Integer> {
        private Bitmap bmp;
        private int code;
        private Context context;

        public BitmapFromSite(final Context context) {
            this.context = context;
        }

        protected Integer doInBackground(final String... array) {
            try {
                final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(array[0]).openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                this.code = httpURLConnection.getResponseCode();
                this.bmp = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                return this.code;
            } catch (FileNotFoundException e) {
                return this.code;
            } catch (IOException e) {
                FirebaseCrash.report(e);
                return this.code;
            }
        }

        protected void onPostExecute(final Integer n) {
            super.onPostExecute(n);
            if (n == 404) {
                Toast.makeText(this.context, "404 Not Found", Toast.LENGTH_SHORT).show();
                return;
            }
            if (this.bmp != null) {
                setSkinBitmap(this.bmp);
                return;
            }
            Toast.makeText(this.context, "Error!", Toast.LENGTH_SHORT).show();
        }

        protected void onPreExecute() {
            setSkinInv();
        }
    }

    public View.OnClickListener getOnClickListener(final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSdkMoreThanLollipopMR1()) {
                    if (shouldAskPermission(activity)) {
                        onPermissionSuccessResult();
                    }
                } else {
                    onPermissionSuccessResult();
                }
            }
        };
    }

    @Override
    public void onPermissionSuccessResult() {
        initDialogList();
    }

    public void initDialogList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setTextColor(Color.BLACK);
                return view;
            }
        };
        adapter.add(getString(R.string.save_to_gallery));
        adapter.add(getString(R.string.save_to_minecraft));

        try {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: //Save to Gallery
                            onSaveToGallery(ActivitySkins.this);
                            break;
                        case 1: //Save to Minecraft
                            onSaveToMinecraft(ActivitySkins.this);
                            break;
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
    }
}
