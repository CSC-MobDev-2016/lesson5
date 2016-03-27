package com.csc.light_file_manager.items;

import com.csc.light_file_manager.R;

import java.io.File;

/**
 * Created by Филипп on 26.03.2016.
 */
public class ParentItem extends Item {
    public static String name = "../";
    public ParentItem(File file) {
        super(file);
    }

    @Override
    public String getName() {
        return name;
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
