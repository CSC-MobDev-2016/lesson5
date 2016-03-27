package com.csc.my_file_manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by anastasia on 26.03.16.
 */
public class FileItem implements Comparable<FileItem>{

    File f;
    String name;
    private String[] imgExt = {"jpg", "jpeg", "png", "bmp", "gif"};

    FileItem(File file){
        this.name = file.getName();
        this.f = file;
    }
    public int compareTo(FileItem o) {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(o.name.toLowerCase());
        else
            throw new IllegalArgumentException();
    }
    public boolean isImg() {
        return Arrays.asList(imgExt).contains(f.getName().substring(f.getName().lastIndexOf('.') + 1));
    }
}
