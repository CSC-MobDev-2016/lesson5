package com.csc.light_file_manager.items;

import com.csc.light_file_manager.R;

import java.io.File;

/**
 * Created by Филипп on 26.03.2016.
 */
public class FolderItem extends Item {

    public FolderItem(File file) {
        super(file);
    }

    @Override
    public int getImage() {
        return R.drawable.folder;
    }

}
