package com.csc.fedorov.file_manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FilesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private File currentFile;
    private final ArrayList<File> filesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        currentFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        recyclerView = (RecyclerView) findViewById(R.id.files_recycler_view);
        final File[] filesArray = currentFile.listFiles();
        Collections.addAll(filesList, filesArray);
        adapter = new FilesAdapter(filesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new FilesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                File clickedFile = filesList.get(position);
                if (clickedFile.isDirectory()) {
                    File directoryToOpen = clickedFile.getAbsoluteFile();
                    ArrayList<File> insideDirectoryList = new ArrayList<>();
                    Collections.addAll(insideDirectoryList, directoryToOpen.listFiles());
                    filesList.clear();
                    filesList.addAll(insideDirectoryList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter.setOnItemLongClickListener(new FilesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.choose_action).setItems(R.array.actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 2) {
                            if (currentFile.delete()) {
                                Toast.makeText(MainActivity.this, R.string.delete_ok, Toast.LENGTH_LONG).show();
                                adapter.notifyItemRemoved(position);
                            } else {
                                Toast.makeText(MainActivity.this, R.string.delete_not_ok, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        File parentFile = currentFile.getParentFile();
        ArrayList<File> parentDirectoryList = new ArrayList<>();
        Collections.addAll(parentDirectoryList, parentFile.listFiles());
        filesList.clear();
        filesList.addAll(parentDirectoryList);
        adapter.notifyDataSetChanged();
    }
}
