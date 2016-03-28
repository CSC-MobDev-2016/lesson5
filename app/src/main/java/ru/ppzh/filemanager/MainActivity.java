package ru.ppzh.filemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final String IMAGE_PATH = "ru.ppzh.image_path";
    public static final String CURRENT_FOLDER_PATH = "ru.ppzh.current_folder_path";
    public static final int FILE_IMAGE_PREVIEW_DIMENSION = 50;

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
            if (savedInstanceState != null) {
                String path = savedInstanceState.getString(CURRENT_FOLDER_PATH);

                //Log.i(TAG, "Restore currentFolder,  path: " + path);

                currentFolder = new File(path);
            } else {
                currentFolder = Environment.getExternalStorageDirectory();
            }
        } else {
            currentFolder = new File("", getString(R.string.no_storage));
        }

        mAdapter = new RecyclerAdapter(getFiles(currentFolder), this);
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
                                    open(item);
                                }
                            }
                        }
                )
        );

        registerForContextMenu(mRecyclerView);
    }

    private void open(Item item) {
        if (isImage(new File(item.getPath()))) {
            openWithOwnViewer(item);
        } else {
            openWithExternalApp(item);
        }
    }

    private void openWithOwnViewer(Item item) {
        Intent intent = new Intent(this, ImageViewer.class);
        intent.putExtra(IMAGE_PATH, item.getPath());
        startActivity(intent);
    }

    private void openWithExternalApp(Item item) {
        if (!openFile(item)) {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.unknown_file,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public boolean isExternalStorageAccessable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    boolean openFile(Item item) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromFile(
                new File(item.getPath())
        ));

        final ResolveInfo defaultResolution =
                getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (defaultResolution != null) {
            final ActivityInfo activity = defaultResolution.activityInfo;

            //Log.i(TAG, "default - " + activity.name);

            if (!activity.name.equals("com.android.internal.app.ResolverActivity")) {
                intent.setClassName(activity.applicationInfo.packageName, activity.name);
                startActivity(intent);
                return true;
            }
        }

        final List<ResolveInfo> resolveInfoList =
                getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!resolveInfoList.isEmpty()) {

            //Log.i(TAG, "from list - " + resolveInfoList.get(0).activityInfo.name);

            intent.setClassName(resolveInfoList.get(0).activityInfo.packageName,
                    resolveInfoList.get(0).activityInfo.name);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private List<Item> getFiles(File file) {
        File[] files = file.listFiles();
        List<Item> dirs = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();

        this.setTitle(file.getName());

        String filename, modificationDate, path;
        Bitmap preview;
        ImageLoader imageLoader = new ImageLoader();
        for (File f : files) {
            filename = f.getName();
            modificationDate = DateFormat.getDateTimeInstance().format(
                    new Date(f.lastModified())
            );
            path = f.getAbsolutePath();

            preview = null;
            if (f.isDirectory()) {
                preview = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_folder_black_48dp);
                dirs.add(
                        new Item(filename, modificationDate, path, preview, true)
                );
            } else {
                if (isImage(f)) {
                    preview = imageLoader.decodeSampledBitmapFromFile(
                            path,
                            FILE_IMAGE_PREVIEW_DIMENSION,
                            FILE_IMAGE_PREVIEW_DIMENSION
                    );
                }

                if (preview == null) {
                    preview = BitmapFactory.decodeResource(getResources(),
                                                           R.drawable.ic_file_black_48dp);
                }

                fls.add(
                        new Item(filename, modificationDate, path, preview, false)
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
                            BitmapFactory.decodeResource(getResources(),
                                    R.drawable.ic_folder_parent_black_48dp),
                            true)
            );
        }

        return dirs;
    }

    private boolean isImage(File f) {
        String mime = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(f.toURI().toString());
        if (extension != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        //Log.i(TAG, f.getName() + " " + mime);

        return mime != null && mime.split("/")[0].equals("image");
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        int position = mAdapter.getPosition();
        Item item = mAdapter.getItem(position);
        switch (menuItem.getItemId()) {
            case R.id.open:
                open(item);
                return true;
            case R.id.rename:
                openRenameDialog(item);
                return true;
            case R.id.delete:
                openDeleteDialog(item);
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    private void openRenameDialog(final Item item) {
        final EditText renamePrompt = new EditText(this);
        String filename = item.getName();
        String extension = item.getExtension();
        renamePrompt.setText(filename.substring(0, filename.lastIndexOf(extension)));

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename)
                .setMessage(R.string.rename_msg)
                .setView(renamePrompt)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFilename = renamePrompt.getText().toString();
                        if (isCorrectFilename(newFilename)) {
                            String newAbsolutePath = constructNewAbsolutePath(item, newFilename);
                            File newFile = new File(newAbsolutePath);
                            renameFile(item, newFile);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.rename_error_msg,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do here
                    }
                })
                .create()
                .show();
    }

    private boolean isCorrectFilename(String newFilename) {
        // TODO
        // Check if filename is correct,
        // i.e. doesn't match others filenames
        // or consist of forbidden characters.

        // Haven't had enough time to do this.
        return true;
    }

    private String constructNewAbsolutePath(Item item, String newFilename) {
        String path = item.getPath();
        return path.substring(0, path.lastIndexOf('/')+1) + newFilename + item.getExtension();
    }

    private void renameFile(Item item, File newFile) {
        File file = new File(item.getPath());
        if (file.renameTo(newFile) && mAdapter.renameItem(item, newFile)) {
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, R.string.rename_error_msg, Toast.LENGTH_LONG).show();
        }
    }

    private void openDeleteDialog(final Item item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_msg)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFileFromDevice(item);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do here
                    }
                })
                .create()
                .show();
    }

    private void deleteFileFromDevice(Item item) {
        File file = new File(item.getPath());
        if (file.delete() && mAdapter.deleteItem(item)) {
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, R.string.delete_error_msg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FOLDER_PATH, currentFolder.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }
}
