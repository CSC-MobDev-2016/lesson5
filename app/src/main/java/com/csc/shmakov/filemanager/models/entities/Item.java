package com.csc.shmakov.filemanager.models.entities;

import android.support.annotation.IntDef;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Pavel on 12/26/2015.
 */
public class Item {
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "png", "bmp");
    @IntDef({Type.FOLDER, Type.IMAGE, Type.OTHER})
    public @interface Type{
        int FOLDER = 0;
        int IMAGE = 1;
        int OTHER = 2;
    }

    public final @Type int type;
    public final String path;
    public final String name;
    public final String extension;
    public final File file;

    public Item(File file) {
        this.file = file;
        name = file.getName();
        path = file.getAbsolutePath();
        if (file.isDirectory()) {
            extension = "";
            type = Type.FOLDER;
        } else {
            extension = name.substring(name.lastIndexOf(".") + 1, name.length());
            if (IMAGE_EXTENSIONS.contains(extension)) {
                type = Type.IMAGE;
            } else {
                type = Type.OTHER;
            }
        }
    }




}
