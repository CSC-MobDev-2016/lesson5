package ru.ppzh.filemanager;


import android.support.annotation.NonNull;
import android.util.Log;

public class Item implements Comparable<Item>{
    public static final String TAG = "Item";

    private String name;
    private String date;
    private String path;
    private int imageResource;
    private boolean isDirectory;

    public Item(String name, String date, String path, int imageResource, boolean isDirectory) {
        this.name = name;
        this.date = date;
        this.path = path;
        this.imageResource = imageResource;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public int getImageResource() {
        return imageResource;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public int compareTo(@NonNull Item o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }

    public String getExtension() {
        int index = name.lastIndexOf('.');
        String extension = name.substring(index);

        Log.i(TAG, extension);

        return extension;
    }
}