package com.csc.shmakov.filemanager.models;

import com.csc.shmakov.filemanager.models.entities.Item;
import com.csc.shmakov.filemanager.models.entities.OpenedFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel on 12/31/2015.
 */
public class SlideshowModel {
    private final List<Item> imageItems = new ArrayList<>();
    private final OpenedFolder folder;
    private static SlideshowModel currentInstance;

    public static SlideshowModel getInstance(OpenedFolder folder) {
        if (currentInstance == null || folder != currentInstance.folder) {
            currentInstance = new SlideshowModel(folder);
        }
        return currentInstance;
    }

    public static SlideshowModel getInstance() {
        return currentInstance;
    }

    private SlideshowModel(OpenedFolder folder) {
        this.folder = folder;
        for (Item item: folder.content) {
            if (item.type == Item.Type.IMAGE) {
                imageItems.add(item);
            }
        }
    }

    public String getImageName(int position) {
        return imageItems.get(position).name;
    }

    public File getImageFileAtSlideshowPosition(int position) {
        return imageItems.get(position).file;
    }

    public int getSlideshowPositionForFolderPosition(int position) {
        Item item = folder.content.get(position);
        for (int i = 0; i < imageItems.size(); i++) {
            if (imageItems.get(i) == item) {
                return i;
            }
        }
        return 0;
    }

    public int getNumberOfImages() {
        return imageItems.size();
    }
}
