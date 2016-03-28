package com.csc.telezhnaya.filemanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManagerActivity extends AppCompatActivity {
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    private static final String ROOT = "/";
    private static final String KEY_PATH = "KEY_PATH";
    private String currentPath = ROOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager_activity);
        adapter = new RecyclerViewAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new ItemClickListener(getApplicationContext(),
                new ItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FileManagerActivity.this.onItemClick(position);
                    }
                }));

        String path = savedInstanceState == null ? ROOT : savedInstanceState.getString(KEY_PATH);
        showDirectory(new File(path));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PATH, currentPath);
    }

    public void onItemClick(int position) {
        Item item = adapter.getItem(position);
        if (item.getFile().isDirectory()) {
            recyclerView.getLayoutManager().scrollToPosition(0);
            showDirectory(item.getFile());
        } else {
            // Я честно попробовала взять код из вашего совета в issue, но я так и не победила
            // свой эмулятор (больше не на чем тестить)
            // И мне не удалось проверить, работает ли это, так как у меня, видимо, нет
            // ни одного нужного приложения :-(
            File file = item.getFile();
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(file.getName()));
            Context context = this.getApplicationContext();
            PackageManager manager = context.getPackageManager();

            final ResolveInfo resInfo = manager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resInfo != null) {
                final ActivityInfo activity = resInfo.activityInfo;
                if (!activity.name.equals("com.android.internal.app.ResolverActivity")) {
                    intent.setClassName(activity.applicationInfo.packageName, activity.name);
                    context.startActivity(intent);
                }
            } else {
                final List<ResolveInfo> resList = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!resList.isEmpty()) {
                    intent.setClassName(resList.get(0).activityInfo.packageName, resList.get(0).activityInfo.name);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "We will support it at next version!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void showDirectory(File directory) {
        this.setTitle(directory.getName());
        currentPath = directory.getPath();
        new AsyncTask<File, Void, List<Item>>() {
            @Override
            protected List<Item> doInBackground(File... params) {
                File curDirectory = new File(params[0].getPath());
                File[] files = curDirectory.listFiles();

                List<Item> items = new ArrayList<>();

                if (curDirectory.getParent() != null) {
                    items.add(new ReturnItem(curDirectory.getParentFile()));
                }

                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            items.add(new DirectoryItem(file));
                        } else {
                            items.add(new FileItem(file));
                        }
                    }
                }
                Collections.sort(items);
                return items;
            }

            @Override
            protected void onPostExecute(List<Item> list) {
                super.onPostExecute(list);
                adapter.setItems(list);
            }
        }.execute(directory);
    }
}
