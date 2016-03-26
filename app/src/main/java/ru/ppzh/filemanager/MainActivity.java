package ru.ppzh.filemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private static final int parentImageResId = R.drawable.ic_folder_parent_black_48dp;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private File currentFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Log.i(TAG, Environment.getExternalStorageState());

        if (isExternalStorageAccessable()) {
            currentFolder = Environment.getExternalStorageDirectory();
        } else {
            currentFolder = new File("", getString(R.string.no_storage));
        }

        mAdapter = new RecyclerAdapter(getFiles(currentFolder));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Item item = mAdapter.getItem(position);

                                if (item.isDirectory()) {
                                    currentFolder = new File(item.getPath());
                                    mAdapter.setData(getFiles(currentFolder));
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    Intent i = new Intent();
                                    i.setAction(Intent.ACTION_VIEW);
                                    String mime =
                                            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                                    item.getExtension()
                                            );
                                    i.setDataAndType(
                                            Uri.fromFile(
                                                    new File(item.getPath())
                                            ),
                                            mime);
                                    startActivity(i);
                                }
                            }
                        }
                )
        );
    }

    public boolean isExternalStorageAccessable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private List<Item> getFiles(File file) {
        File[] files = file.listFiles();
        List<Item> dirs = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();

        this.setTitle(file.getName());

        String filename, modificationDate, path;
        int imageResource;
        for (File f: files) {
            filename = f.getName();
            modificationDate = DateFormat.getDateTimeInstance().format(
                    new Date(f.lastModified())
            );
            path = f.getAbsolutePath();

            if (f.isDirectory()) {
                imageResource = R.drawable.ic_folder_black_48dp;
                dirs.add(
                        new Item(filename, modificationDate, path, imageResource, true)
                );
            } else {
                imageResource = R.drawable.ic_file_black_48dp;
                fls.add(
                        new Item(filename, modificationDate, path, imageResource, false)
                );
            }
        }
        Collections.sort(dirs);
        Collections.sort(fls);
        dirs.addAll(fls);

        if (!(file.getAbsolutePath()
                .equals(Environment.getExternalStorageDirectory()
                        .getAbsolutePath()))) {
            dirs.add(0,
                    new Item("..", "",
                            file.getParentFile().getAbsolutePath(),
                            parentImageResId,
                            true)
            );
        }

        return dirs;
    }
}
