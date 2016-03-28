package com.csc.kharitonov.filemanager;

import android.graphics.drawable.Drawable;

/**
 * Created by chainic-vina on 27.03.16.
 */
public class IconHolder {
    private final Drawable dirIcon;
    private final Drawable backIcon;
    private final Drawable fileIcon;

    public IconHolder(Drawable dirIcon, Drawable fileIcon, Drawable backIcon) {
        this.dirIcon = dirIcon;
        this.fileIcon = fileIcon;
        this.backIcon = backIcon;
    }

    public Drawable getBackIcon() {
        return backIcon;
    }

    public Drawable getDirIcon() {
        return dirIcon;
    }

    public Drawable getFileIcon() {
        return fileIcon;
    }
}
