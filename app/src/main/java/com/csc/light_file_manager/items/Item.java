package com.csc.light_file_manager.items;

import java.io.File;

/**
 * Created by Филипп on 26.03.2016.
 */
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


    @Override
    public int compareTo(Item another) {
        if (this.name != null) {
            return this.name.toLowerCase().compareTo(another.getName().toLowerCase());
        }
        else {
            throw new IllegalStateException("Item has null name");
        }
    }

    public abstract int getImage();

    public File getFile() {
        return file;
    }

}
