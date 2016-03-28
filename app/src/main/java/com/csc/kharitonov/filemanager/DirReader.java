package com.csc.kharitonov.filemanager;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirReader extends AsyncTask<String, Void, List<DirItem>> {
    ExplorerAdapter adapter;
    IconHolder iconHolder;
    TextView currentPathTextView;
    MainActivity activity;
    String currentPathText;
    boolean isRestoring;

    public DirReader(ExplorerAdapter adapter, TextView currentPath, MainActivity activity,
                     IconHolder iconHolder, boolean isRestoring) {
        this.adapter = adapter;
        this.iconHolder = iconHolder;
        this.currentPathTextView = currentPath;
        this.activity = activity;
        this.isRestoring = isRestoring;
    }

    @Override
    protected List<DirItem> doInBackground(String... params) {
        currentPathText = params[0];

        List<DirItem> res = new ArrayList<>();
        if (!params[0].equals(Utils.ROOT_PATH)) {
            res.add(new DirItem(
                    Utils.LEVEL_UP,             // Text for level up
                    Utils.VOID_STRING,          // Date
                    Utils.VOID_STRING,          // Size
                    iconHolder.getBackIcon(),   // Icon
                    true));                     // IsDir
        }

        try {
            File[] files = new File(params[0]).listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    res.add(new DirItem(
                            f.getName(),
                            DateFormat.getDateTimeInstance().format(f.lastModified()),
                            Utils.VOID_STRING,
                            iconHolder.getDirIcon(),
                            true));
                } else {
                    res.add(new DirItem(
                            f.getName(),
                            DateFormat.getDateTimeInstance().format(f.lastModified()),
                            Utils.sizeToString(f.length()),
                            iconHolder.getFileIcon(),
                            false));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Collections.sort(res);
        return res;
    }

    @Override
    protected void onPostExecute(List<DirItem> items) {
        super.onPostExecute(items);
        if (items == null) {
            Toast.makeText(activity, activity.getText(R.string.read_dir_error) + currentPathText,
                   Toast.LENGTH_SHORT).show();
            if (isRestoring) {
                activity.readDir(Utils.ROOT_PATH, false);
            }
            return;
        }
        adapter.update(items);
        currentPathTextView.setText(currentPathText);
        activity.scrollUp();
    }
}


