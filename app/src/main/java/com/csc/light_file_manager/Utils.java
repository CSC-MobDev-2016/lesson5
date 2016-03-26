package com.csc.light_file_manager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.List;

/**
 * Created by Филипп on 26.03.2016.
 */
public class Utils {
    public static String getMimeType(File file) {
        try {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}