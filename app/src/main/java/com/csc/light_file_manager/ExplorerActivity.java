package com.csc.light_file_manager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.csc.light_file_manager.items.FileItem;
import com.csc.light_file_manager.items.FolderItem;
import com.csc.light_file_manager.items.Item;
import com.csc.light_file_manager.items.ParentItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplorerActivity extends AppCompatActivity {



    RVAdapter adapter;
    RecyclerView recyclerView;

    private final String root = "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explorer);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RVAdapter(getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ExplorerActivity.this.onMyItemClick(view, position);
//                Item item = adapter.getItem(position);
//                if (item.getFile().isDirectory()) {
//                    recyclerView.getLayoutManager().scrollToPosition(0);
//                    showDir(item.getFile());
//                    //maybe scroll back when back
//                }
//                else {
//                    try {
//                        String mimeType = Utils.getMimeType(item.getFile());
//                        if (mimeType == null) { throw new NullPointerException();}
//                        final Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(item.getFile()), mimeType);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        getApplicationContext().startActivity(intent);
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
            }
        }));

        File f = new File(root);
        showDir(f);
    }

    public void onMyItemClick(View view, int position) {
        Item item = adapter.getItem(position);
        if (item.getFile().isDirectory()) {
            recyclerView.getLayoutManager().scrollToPosition(0);
            showDir(item.getFile());
            //maybe scroll back when back
        }
        else {
            try {
                String mimeType = Utils.getMimeType(item.getFile());
                if (mimeType == null) { throw new NullPointerException();}
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(item.getFile()), mimeType);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }
        }


    }


    protected void showDir(File dir) {
        this.setTitle(dir.getName());
        new ShowDir().execute(dir);
    }

    class ShowDir extends AsyncTask<File, Void, List<Item>> {
        @Override
        protected List<Item> doInBackground(File... params) {
            File f = new File(params[0].getPath());
            File[] files = f.listFiles();


            List<Item> dirs = new ArrayList<>();
            List<Item> fls = new ArrayList<>();

            if (f.getParent() != null) {
                dirs.add(new ParentItem(f.getParentFile()));
            }

            try {
                for(File d: files) {
                    if (d.isDirectory()) {
                        dirs.add(new FolderItem(d));
                    }
                    else {
                        fls.add(new FileItem(d));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Collections.sort(dirs);
            Collections.sort(fls);

            dirs.addAll(fls);
            return Collections.unmodifiableList(dirs);
        }

        @Override
        protected void onPostExecute(List<Item> list) {
            super.onPostExecute(list);
            adapter.updateItems(list);
        }
    }

}
