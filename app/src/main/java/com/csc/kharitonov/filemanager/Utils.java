package com.csc.kharitonov.filemanager;

public abstract class Utils {
    public static final String ROOT_PATH = "/";
    public static final String LEVEL_UP = "..";
    public static final String VOID_STRING = "";
    public static final String PATH_IN_BUNDLE = "current_path";

    public static final int KB = 1024;
    public static final int MB = KB * 1024;
    public static final int GB = MB * 1024;

    public static String sizeToString(long size) {
        StringBuilder builder = new StringBuilder();
        if (size >= GB) {
            long v = size / GB;
            builder.append(String.valueOf(v))
                    .append(".")
                    .append(String.valueOf(size - v * GB).substring(0, 1))
                    .append(" Gb");
        } else if (size >= MB) {
            long v = size / MB;
            builder.append(String.valueOf(v))
                    .append(".")
                    .append(String.valueOf(size - v * MB).substring(0, 1))
                    .append(" Mb");
        }
        if (size >= KB) {
            long v = size / KB;
            builder.append(String.valueOf(v))
                    .append(".")
                    .append(String.valueOf(size - v * KB).substring(0, 1))
                    .append(" Kb");
        } else {
            builder.append(String.valueOf(size)).append(" bytes");
        }
        return builder.toString();
    }

    public static String getExtention(String fileName) {
        String extension = Utils.VOID_STRING;
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            extension = fileName.substring(i + 1);
        return extension;
    }
}
