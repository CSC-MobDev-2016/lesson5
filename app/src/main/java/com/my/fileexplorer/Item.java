package com.my.fileexplorer;

public class Item implements Comparable<Item> {
    private String name;
    private String data;
    private String date;
    private String path;
    private ExtensionsImages image;

    public Item(String n, String d, String dt, String p, ExtensionsImages img) {
        name = n;
        data = d;
        date = dt;
        path = p;
        image = img;
    }

    public String getName() { return name; }

    public String getData() { return data; }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public ExtensionsImages getImage() {
        return image;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }

    public int compareTo(Item o) {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
