package com.csc.my_file_manager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final File startFile = new File("/");
    private ArrayList<FileItem> files = new ArrayList<>();
    private RecycleViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main_activity);

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        itemClick(view, position);
                    }
                })
        );
        adapter = new RecycleViewAdapter(files, this);
        recyclerView.setAdapter(adapter);
        getFiles(startFile);
        super.onCreate(savedInstanceState);
    }
    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    void itemClick(View view, int position) {
        File f = adapter.getItem(position).f;
        if (!f.isDirectory()) {
            Uri file = Uri.fromFile(f);
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, file);
            viewIntent.setDataAndType(file, getMimeType(f.getAbsolutePath()));
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(viewIntent, 0);
            boolean isIntentSafe = activities.size() > 0;

            if (isIntentSafe) {
                startActivity(viewIntent);
            }
        }else {
            getFiles(f);
        }
    }

    void getFiles(File f)
    {
        this.setTitle(f.getAbsolutePath());
        recyclerView.smoothScrollToPosition(0);

        new AsyncTask<File, Void, ArrayList<FileItem>>() {
        @Override
        protected void onPostExecute(ArrayList<FileItem> result) {
            super.onPostExecute(result);
            files = result;
            adapter.setItems(files);

        }
        @Override
        protected ArrayList<FileItem> doInBackground(File... params) {
            File f = params[0];
            File[] files = f.listFiles();
            ArrayList<FileItem> dir = new ArrayList<>();
            ArrayList<FileItem> fls = new ArrayList<>();
            try{
                for(File ff: files)
                {
                    if(ff.isDirectory()){
                        dir.add(new FileItem(ff));
                    } else {
                        fls.add(new FileItem(ff));
                    }
                }
            }catch(Exception e) {}
            finally {//just i can
            }

            Collections.sort(dir);
            Collections.sort(fls);
            dir.addAll(fls);
            if(!f.getAbsolutePath().equalsIgnoreCase("/")) {
                dir.add(0, new FileItem(new File(f.getParent())));
            } else {
                dir.add(0, new FileItem(f));
            }
            return dir;
        }
    }.execute(f);

    }
}
