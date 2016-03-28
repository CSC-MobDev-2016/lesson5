package com.csc.malinovsky239.filemanager;

/**
 * Created by malinovsky239 on 28.03.2016.
 */
public class FileSystemItem {
    private String title;
    private boolean isFolder;

    FileSystemItem(String title, boolean isFolder) {
        this.title = title;
        this.isFolder = isFolder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
