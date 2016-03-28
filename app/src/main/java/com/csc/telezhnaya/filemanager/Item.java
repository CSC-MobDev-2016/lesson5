package com.csc.telezhnaya.filemanager;

import java.io.File;

public abstract class Item implements Comparable<Item> {
    protected final String name;
    protected final File file;

    public Item(File file) {
        this.name = file.getName();
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int compareTo(Item another) {
        return this.name.toLowerCase().compareTo(another.getName().toLowerCase());
    }

    public abstract int getImage();
}
