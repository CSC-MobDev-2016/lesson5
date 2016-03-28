package com.csc.lpaina.lesson5;

import android.webkit.MimeTypeMap;

import java.io.File;

public class FileWrapper {
    private File file;
    private String extension;
    private String mimeType;

    public FileWrapper(File file) {
        this.file = file;
        extension = file.getPath().substring(file.getPath().lastIndexOf('.') + 1);
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public String getPath() {
        return file.getPath();
    }

    public File getFile() {
        return file;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }
}
