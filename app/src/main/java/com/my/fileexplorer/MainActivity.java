package com.my.fileexplorer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static String CURR_DIRECTORY = "com.my.fileexplorer.CURR_DIRECTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void in(View v) {
        Intent intent = new Intent(this, FileChooser.class);
        String root = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        intent.putExtra(CURR_DIRECTORY, root);
        startActivity(intent);
    }

    public void sad(View v) {
        Toast.makeText(getApplicationContext(),
                R.string.to_sdcard_excuse,
                Toast.LENGTH_SHORT).show();
    }
}
