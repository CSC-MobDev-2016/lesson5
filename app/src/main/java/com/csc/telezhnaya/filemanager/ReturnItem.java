package com.csc.telezhnaya.filemanager;

import java.io.File;


public class ReturnItem extends Item {
    public static final String PATH = "../";

    public ReturnItem(File file) {
        super(file);
    }

    @Override
    public String getName() {
        return PATH;
    }

    @Override
    public int compareTo(Item another) {
        return -1;
    }

    @Override
    public int getImage() {
        return R.drawable.previous;
    }
}
