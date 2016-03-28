package com.csc.malinovsky239.filemanager;

/**
 * Created by malinovsky239 on 28.03.2016.
 */
public interface OnItemClickListener {
    void onItemClick(FileSystemItem item);

    void onItemLongClick(FileSystemItem item);
}