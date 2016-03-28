package com.csc.telezhnaya.filemanager;

import java.io.File;

public class DirectoryItem extends Item {

    public DirectoryItem(File file) {
        super(file);
    }

    @Override
    public int compareTo(Item another) {
        if (another instanceof FileItem) return -1;
        return super.compareTo(another);
    }

    @Override
    public int getImage() {
        return R.drawable.folder;
    }
}
