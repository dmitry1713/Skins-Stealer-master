package com.appscreat.project.util.files;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.appscreat.project.R;
import com.appscreat.project.interfaces.DownloadInterface;
import com.appscreat.project.util.ProgressDialogManager;
import com.google.firebase.crash.FirebaseCrash;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class FileManager {

    private final static String TAG = "FileManager";

    /**
     * Класс загружает асинхронно файл из удаленного источника по url
     */
    public static class DownloadFileAsync extends AsyncTask<String, String, Boolean> {

        private Context context;
        private File file;
        private File dir;
        private File sdPath;
        private String fileExt;
        private String fileName = null;
        private String newName = null;
        private String as[] = null;
        private String pathSave;
        private OutputStream output;
        private InputStream input;
        private DownloadInterface downloadInterface;
        private ProgressDialogManager progressDialog;

        public DownloadFileAsync(Context context, String newName, String pathSave, DownloadInterface downloadInterface) {
            this.context = context;
            this.newName = newName;
            this.pathSave = pathSave;
            this.downloadInterface = downloadInterface;
        }

        public DownloadFileAsync(Context context, String pathSave, DownloadInterface downloadInterface) {
            this.context = context;
            this.pathSave = pathSave;
            this.downloadInterface = downloadInterface;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Создаем диалог загрузки
            progressDialog = new ProgressDialogManager(context, ProgressDialog.STYLE_HORIZONTAL, context.getString(R.string.download_file));
            progressDialog.showProgressDialog();
            progressDialog.getProgressDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true); //При отмене ProgressDialog отменяется загрузка
                    if (downloadInterface != null) {
                        downloadInterface.downloadComlete(null, false);
                    }
                }
            });
        }

        @Override
        protected Boolean doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                Log.d(TAG, "doInBackground: URL " + url);

                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();

                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                input = new BufferedInputStream(url.openStream());
                String path = url.getPath();

                //Расширение файла
                fileExt = path.substring(path.lastIndexOf('.') + 1);
                Log.d(TAG, "doInBackground: File Extension " + fileExt);

                //Полное имя файла
                fileName = path.substring(path.lastIndexOf('/') + 1);

                //Изменить имя сохраняемого файла
                if (newName != null && !newName.isEmpty()) {
                    fileName = FileNameUtil.changeFullNameFile(fileName, newName);
                }

                Log.d(TAG, "doInBackground: File Name " + fileName);

                if (isExternalStorageWritable()) {
                    //Корневая директория внешней карты мобильного устройства
                    sdPath = Environment.getExternalStorageDirectory();
                    Log.d(TAG, "doInBackground: ExternalStorage is available");
                } else {
                    Log.d(TAG, "doInBackground: ExternalStorage is not available");
                    return false;
                }

                //Указываем директорию для сохранения
                dir = new File(sdPath.getAbsolutePath() + pathSave);

                //Если директория не существует, создаем ее
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                //Создаем файл
                file = new File(dir, fileName);

                //Записываем файл
                output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                if (file != null) {
                    as = new String[1];
                    as[0] = file.toString();

                    //Обновляем файловый сканер
                    MediaScannerConnection.scanFile(context, as, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String s1, Uri uri) {
                        }
                    });
                }
            } catch (Exception e) {
                Log.d(TAG, "ApplicationException: " + e);
                FirebaseCrash.report(e);
                return false;
            }
            return true;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            progressDialog.setProgressDialog(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Boolean result) {

            //Если не отменен, продолжить выполнение
            if (!isCancelled()) {
                //Разархивируем файл
                unzip(result);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "onCancelled");
        }

        private void unzip(Boolean result) {

            try {
                //Расширение файла не равно null и оно не пустое
                if (fileExt != null && !fileExt.isEmpty()) {
                    //Расширение фала равно zip
                    if (fileExt.equals("zip")) {
                        //Файл не равен null и он существует
                        if (file != null && file.exists()) {
                            ZipFile zipFile = new ZipFile(file);
                            //Директория сохранения не равна null, она существует, является папкой
                            if (dir != null && dir.exists() && dir.isDirectory()) {
                                //Распаковать файл
                                zipFile.extractAll(dir.toString());
                            }
                        }
                    }
                }
            } catch (ZipException e) {
                FirebaseCrash.report(e);
            } finally {
                downloadComlete(result);
            }
        }

        private void downloadComlete(Boolean result) {
            //Убираем диалог загрузки
            progressDialog.dismissProgressDialog();

            if (downloadInterface != null) {
                downloadInterface.downloadComlete(file, result);
            }
        }

        //Проверить доступно ли внешнее хранилише
        private boolean isExternalStorageWritable() {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        }
    }

    public static void createNewFile(File file, String string) {
        try {
            //Создаем файл
            FileOutputStream fileOutput = new FileOutputStream(file);

            //Открываем поток для записи
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutput);

            //Записываем строку в файл
            streamWriter.write(string);

            //Проверяем, что все действительно записалось и закрываем файл */
            streamWriter.flush();
            streamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
