package com.csc.kharitonov.filemanager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    private String currentPath;

    private IconHolder iconHolder;
    private ExplorerAdapter adapter;
    private RecyclerView filesView;
    private TextView currentDirTextView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (((ExplorerAdapter.ViewHolder) v.getTag()).getFileName().getText().equals(Utils.LEVEL_UP))
            return;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        filesView = (RecyclerView) findViewById(R.id.filesView);
        filesView.setLayoutManager(new LinearLayoutManager(this));

        currentDirTextView = (TextView) findViewById(R.id.currentDir);

        iconHolder = new IconHolder(
                getResources().getDrawable(R.drawable.ic_folder_black_48dp),
                getResources().getDrawable(R.drawable.ic_insert_drive_file_black_48dp),
                getResources().getDrawable(R.drawable.ic_arrow_back_black_48dp)
        );

        adapter = new ExplorerAdapter(this);

        filesView.setAdapter(adapter);

        if (savedInstanceState == null || savedInstanceState.getString(Utils.PATH_IN_BUNDLE) == null) {
            readDir(Utils.ROOT_PATH, false);
        } else {
            readDir(savedInstanceState.getString(Utils.PATH_IN_BUNDLE), true);
        }

        View copyPathButton = findViewById(R.id.copy_path_button);
        assert copyPathButton != null;
        copyPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = currentDirTextView.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("path", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, text + " saved in buffer", Toast.LENGTH_LONG).show();
            }
        });
    }

    void readDir(String path, boolean isRestoring) {
        new DirReader(adapter, currentDirTextView, this, iconHolder, isRestoring).execute(path);
    }

    void scrollUp() {
        filesView.getLayoutManager().scrollToPosition(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.PATH_IN_BUNDLE, currentDirTextView.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        currentDirTextView.setText(savedInstanceState.getString(Utils.PATH_IN_BUNDLE));
    }

    @Override
    public void onClick(View v) {
        ExplorerAdapter.ViewHolder holder = (ExplorerAdapter.ViewHolder) (v.getTag());

        if (holder.getFileName().getText().toString().equals(Utils.LEVEL_UP)) {
            String parent = new File(currentDirTextView.getText().toString()).getParent();
            if (!parent.equals(Utils.ROOT_PATH))
                parent += "/";
            readDir(parent, false);

        } else if (holder.getFileSize().getText().toString().equals(Utils.VOID_STRING)) {
            readDir(currentDirTextView.getText().toString() + holder.getFileName().getText().toString() + "/", false);

        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = MimeTypeMap
                    .getSingleton()
                    .getMimeTypeFromExtension(Utils.getExtention(holder.getFileName().getText().toString()));
            intent.setDataAndType(
                    Uri.fromFile(new File(
                            currentDirTextView.getText().toString()
                                    + holder.getFileName().getText().toString())),
                    mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy: {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                final EditText editText = new EditText(this);
                dialog.setView(editText)
                    .setTitle(getText(R.string.dest))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                File src = new File(currentDirTextView.getText().toString()
                                        + adapter.getSelectedFile());
                                File dest = new File(editText.getText().toString()
                                        + adapter.getSelectedFile());
                                if (src.isDirectory())
                                    FileUtils.copyDirectory(src, dest);
                                else
                                    FileUtils.copyFile(src, dest);
                                Toast.makeText(MainActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                            }
                            readDir(currentDirTextView.getText().toString(), false);
                        }
                    })
                    .setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
            }
            return true;

            case R.id.rename: {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                final EditText editText = new EditText(this);
                editText.setText(currentDirTextView.getText().toString() + adapter.getSelectedFile());

                dialog.setView(editText)
                    .setTitle(getText(R.string.enter_new_name))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                File src = new File(currentDirTextView.getText().toString()
                                        + adapter.getSelectedFile());
                                File dest = new File(editText.getText().toString());
                                if (src.isDirectory())
                                    FileUtils.moveDirectory(src, dest);
                                else
                                    FileUtils.moveFile(src, dest);
                                Toast.makeText(MainActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                            }
                            readDir(currentDirTextView.getText().toString(), false);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
                return true;
            }


            case R.id.delete: {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getText(R.string.are_you_sure))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    File src = new File(currentDirTextView.getText().toString()
                                            + adapter.getSelectedFile());
                                    if (src.isDirectory())
                                        FileUtils.deleteDirectory(src);
                                    else
                                        FileUtils.forceDelete(src);
                                    Toast.makeText(MainActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                                readDir(currentDirTextView.getText().toString(), false);
                            }
                        })
                        .setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
            }
            return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}
