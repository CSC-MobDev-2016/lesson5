package ru.csc.android_course.file_manager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by qurbonzoda on 28.03.16.
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String directory = "/";
    static final String DIRECTORY = "DIRECTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            directory = savedInstanceState.getString(DIRECTORY);
        } else if (getIntent().hasExtra(DIRECTORY)) {
            directory = getIntent().getStringExtra(DIRECTORY);
        }

        ArrayList<File> files = new ArrayList<>();

        File file = new File(directory);
        if (file.listFiles() != null) {
            Collections.addAll(files, file.listFiles());
        }

        recyclerView.setAdapter(new FileAdapter(files, this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DIRECTORY, directory);
    }

    public void onFolderClick(File file) {
        Intent intent = new Intent(this, MainActivity.class);
        try {
            intent.putExtra(DIRECTORY, file.getCanonicalPath());
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this, "Sorry, we couldn't open the folder.", Toast.LENGTH_LONG).show();
        }
    }

    public void onFileClick(File file) {
        try {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(fileExt(file.getAbsolutePath()).substring(1));
            newIntent.setDataAndType(Uri.fromFile(file), mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        } catch (ActivityNotFoundException | NullPointerException e) {
            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    private String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

}
