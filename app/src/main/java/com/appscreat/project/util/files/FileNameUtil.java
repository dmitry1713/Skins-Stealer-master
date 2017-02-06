package com.appscreat.project.util.files;


public class FileNameUtil {

    private final static String TAG = "FileNameUtil";

    public static String getExtensionFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static String changeFullNameFile(String fileOrigName, String fileNewName) {
        return fileNewName;
    }
}
