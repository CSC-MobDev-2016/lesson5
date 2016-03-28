package com.csc.jv.FileManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {

    private static final String ROOT = "/";


    private static final int DIALOG_OPEN = 0;
    private static final int DIALOG_DELETE = 1;
    private static final int DIALOG_RENAME = 2;

    private File currentDir;
    private File selectedFile;
    private List<String> currentDirFiles = new ArrayList<>();

    private ListView listView;
    private ListDirAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setOnItemClickListener(this);
        }

        changeDir(new File(ROOT));
    }

    private void changeDir(File dir) {
        currentDir = dir;

        List<CustomListItem> currentDirNames = new ArrayList<>();

        if (currentDir.listFiles() != null) {
            currentDirFiles.clear();

            for (final File file : currentDir.listFiles()) {
                currentDirFiles.add(file.getAbsolutePath());

                if (file.isDirectory()) {
                    currentDirNames.add(new CustomListItem(file.getName(), R.drawable.folder_icon));
                } else {
                    currentDirNames.add(new CustomListItem(file.getName(), R.drawable.file_icon));
                }
            }
        } else {
            currentDir = currentDir.getParentFile();
            startActivity(new Intent(this, EmptyFolderActivity.class));
            return;
        }

        adapter = new ListDirAdapter(this, currentDirNames);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && currentDir.getParentFile() != null) {
            changeDir(currentDir.getParentFile());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String selectedItem = currentDirFiles.get(position);

        selectedFile = new File(selectedItem);
        if (selectedFile.isDirectory()) {
            changeDir(selectedFile);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.selection)
                    .setItems(getResources().getStringArray(R.array.dialog_array), this)
                    .show();
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case DIALOG_OPEN:
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("file://" +
                        selectedFile.getAbsolutePath()));
                startActivity(i);
                break;
            case DIALOG_DELETE:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_file)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedFile.delete();
                                adapter.remove(selectedFile.getName());
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
            case DIALOG_RENAME:
                final EditText inputName = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.rename_file)
                        .setView(inputName)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newFileName = inputName.getText().toString();
                                File to = new File(selectedFile.getParent(), newFileName);
                                selectedFile.renameTo(to);
                                adapter.set(selectedFile.getName(), newFileName);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
        }


    }
}
