package com.csc.lpaina.lesson5;

import java.io.File;

public class FileWrapper {
    private File file;

    public FileWrapper(File file) {
        this.file = file;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public String getPath() {
        return file.getPath();
    }

    public void setFile(File file) {
        this.file = file;
    }
}
