package com.csc.jv.FileManager;

public class CustomListItem {

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private final int icon;

    public CustomListItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

}
