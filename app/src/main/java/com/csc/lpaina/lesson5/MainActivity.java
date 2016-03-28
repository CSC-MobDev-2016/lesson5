package com.csc.lpaina.lesson5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String PATH = "PATH";
    private static final String TAG = "MainActivity";
    private File defaultDirectory = Environment.getExternalStorageDirectory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra(PATH);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<FileWrapper> files = new ArrayList<>();
        File directory;
        if (path == null) {
            directory = defaultDirectory;
        } else {
            directory = new File(path);
            if (!directory.exists() || directory.listFiles() == null) {
                Log.e(TAG, "onCreate: Directory " + path + "doesn't exists");
                directory = defaultDirectory;
            }
        }

        for (File file : directory.listFiles()) {
            files.add(new FileWrapper(file));
        }

        recyclerView.setAdapter(new RVAdapter(files));

        TextView textView = (TextView) findViewById(R.id.working_directory);
        textView.setText(directory.getAbsolutePath());
    }
}
