package ru.ppzh.filemanager;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

public class Item implements Comparable<Item> {
    public static final String TAG = "Item";

    private String name;
    private String date;
    private String path;
    private Bitmap preview;
    private boolean isDirectory;

    public Item(String name, String date, String path, Bitmap preview, boolean isDirectory) {
        this.name = name;
        this.date = date;
        this.path = path;
        this.preview = preview;
        this.isDirectory = isDirectory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public int compareTo(@NonNull Item o) {
        if (this.name != null)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item item = (Item) o;

        if (isDirectory() != item.isDirectory()) return false;
        if (!getName().equals(item.getName())) return false;
        if (!getDate().equals(item.getDate())) return false;
        if (!getPath().equals(item.getPath())) return false;
        return getPreview().equals(item.getPreview());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDate().hashCode();
        result = 31 * result + getPath().hashCode();
        result = 31 * result + getPreview().hashCode();
        result = 31 * result + (isDirectory() ? 1 : 0);
        return result;
    }
}