package com.csc.lpaina.lesson5;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<FileWrapper> files = new ArrayList<>();
        File defaultDirectory = Environment.getExternalStorageDirectory();
        for (File file : defaultDirectory.listFiles()) {
            files.add(new FileWrapper(file));
        }

        recyclerView.setAdapter(new RVAdapter(files));

        TextView textView = (TextView) findViewById(R.id.working_directory);
        textView.setText(defaultDirectory.getAbsolutePath());
    }
}
