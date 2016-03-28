package com.csc.sfilemanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnPathChange {

    RecyclerView rv;
    TextView tvPath;
    ImageView back;
    private final static String KEY_PATH = "PATH_FOLDER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        rv = (RecyclerView)findViewById(R.id.recycler_list_fs);
        tvPath = (TextView)findViewById(R.id.path);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String path = intent.getStringExtra(KEY_PATH);
                if (path == null || path == "/") {
                    return;
                }
                int r = path.lastIndexOf('/');
                if (r >= 0)
                    path = path.substring(0, r);
                if (path == null || path.equals("")) {
                    path = "/";
                }
                changePath(path);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();
        String path = intent.getStringExtra(KEY_PATH);
        if (path == null) {
            path = "/";
        }
        tvPath.setText(path);
        RecyclerView.Adapter mAdapter = new RecyclerFSAdapter(path, this);
        rv.setAdapter(mAdapter);


    }

    @Override
    public void changePath(@NonNull String path) {
        Intent intent = getIntent();
        intent.putExtra(KEY_PATH, path);
        tvPath.setText(path);
        rv.swapAdapter(new RecyclerFSAdapter(path, this), false);
        rv.invalidate();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String path = intent.getStringExtra(KEY_PATH);
        if (path == null || path == "/") {
            return;
        }
        int r = path.lastIndexOf('/');
        if (r >= 0)
            path = path.substring(0, r);
        if (path == null || path.equals("")) {
            path = "/";
        }
        changePath(path);
    }
}

interface OnPathChange {
    public void changePath(@NonNull final String path);
}

