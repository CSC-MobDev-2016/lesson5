package com.csc.telezhnaya.filemanager;

import java.io.File;

public class FileItem extends Item {

    public FileItem(File file) {
        super(file);
    }

    @Override
    public int compareTo(Item another) {
        if (another instanceof DirectoryItem) return 1;
        return super.compareTo(another);
    }

    @Override
    public int getImage() {
        return R.drawable.file;
    }
}
