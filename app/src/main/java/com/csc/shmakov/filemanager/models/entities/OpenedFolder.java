package com.csc.shmakov.filemanager.models.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Pavel on 12/20/2015.
 */
public class OpenedFolder {
    public final String name;
    public final String parentPath;
    public final String path;
    public final List<Item> content;

    public OpenedFolder(File file) {
        name = file.getName();
        path = file.getAbsolutePath();
        parentPath = file.getParent();

        content = new ArrayList<>();
        File[] listFiles = file.listFiles();
        for (File f : listFiles) {
            content.add(new Item(f));
        }
        Collections.sort(content, itemComparator);
    }

    private static final Comparator<Item> itemComparator = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            if (lhs.type == Item.Type.FOLDER && rhs.type != Item.Type.FOLDER) {
                return -1;
            } else if (lhs.type != Item.Type.FOLDER && rhs.type == Item.Type.FOLDER) {
                return 1;
            } else {
                return lhs.name.compareTo(rhs.name);
            }
        }
    };
}
