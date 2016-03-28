package com.csc.kharitonov.filemanager;

import android.graphics.drawable.Drawable;

public class DirItem implements Comparable<DirItem> {
    private String name;
    private String date;
    private String size;
    private Drawable icon;
    private boolean isDir;

    public DirItem(String name, String date, String size, Drawable icon, boolean isDir) {
        this.name = name;
        this.icon = icon;
        this.isDir = isDir;
        this.size = size;
        this.date = date;
    }

    public boolean isDir() {
        return isDir;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getSize() {
        return size;
    }

    public Drawable getIcon() {
        return icon;
    }

    @Override
    public int compareTo(DirItem another) {
        if (isDir && (!another.isDir() || name == Utils.VOID_STRING))
            return -1;
        else if (another.isDir() && (!isDir || another.name == Utils.VOID_STRING)) {
            return 1;
        }
        return name.toLowerCase().compareTo(another.getName().toLowerCase());
    }
}
