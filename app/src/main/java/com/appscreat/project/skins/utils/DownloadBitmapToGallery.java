package com.appscreat.project.skins.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadBitmapToGallery
{
    public static void downloaderSkin(final String s, final Context context, final Bitmap bitmap) {
        try {
            Toast.makeText(context, (CharSequence)"Downloaded!", Toast.LENGTH_SHORT).show();
            setNotificationDownload(bitmap, s, saveImageToExternal(s, bitmap, context), context);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static String saveImageToExternal(final String s, final Bitmap bitmap, final Context context) throws IOException {
        final File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory("PvP Skins for Minecraft");
        externalStoragePublicDirectory.mkdirs();
        final File file = new File(externalStoragePublicDirectory, s + ".png");
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream)fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MediaScannerConnection.scanFile(context, new String[] { file.getAbsolutePath() }, (String[])null, (MediaScannerConnection.OnScanCompletedListener)new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(final String s, final Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + s + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
            return file.getPath();
        }
        catch (Exception ex) {
            throw new IOException();
        }
    }
    
    static void setNotificationDownload(final Bitmap largeIcon, final String s, final String s2, final Context context) {
        final Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse("file://" + s2), "image/*");
        final PendingIntent activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        ((android.support.v4.app.NotificationCompat.Builder)builder).setContentIntent(activity).setSmallIcon(17301634).setLargeIcon(largeIcon).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle("Skin: " + s).setContentText("Downloaded.");
        notificationManager.notify(5213, ((android.support.v4.app.NotificationCompat.Builder)builder).build());
    }
}
