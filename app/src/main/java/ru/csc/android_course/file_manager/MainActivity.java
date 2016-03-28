package ru.csc.android_course.file_manager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
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
        Collections.addAll(files, new File(directory).listFiles());

        recyclerView.setAdapter(new FileAdapter(files));
    }
}
