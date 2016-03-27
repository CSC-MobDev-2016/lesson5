package com.my.fileexplorer;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FileChooser extends AppCompatActivity {

    private FileArrayAdapter adapter;

    private String currentDir;
    private List<Item> items;

    public static final List<String> txtExt = new ArrayList<>();
    public static final List<String> imgExt = new ArrayList<>();
    public static final List<String> musExt = new ArrayList<>();
    public static final List<String> vidExt = new ArrayList<>();

    static {
        txtExt.add(".txt");
        imgExt.add(".jpg");
        imgExt.add(".png");
        musExt.add(".ogg");
        musExt.add(".mp3");
        vidExt.add(".mp4");
    }

    public final static String CURR_DIRECTORY = "com.my.fileexplorer.CURR_DIRECTORY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_view);

        Intent intent = getIntent();
        currentDir = intent.getStringExtra(MainActivity.CURR_DIRECTORY);
        TextView textView = (TextView) findViewById(R.id.curr_dir);
        textView.setText(currentDir);
        File currDir = new File(currentDir);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        items = fill(currDir);
        adapter = new FileArrayAdapter(this, items);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        TextView textView = (TextView) findViewById(R.id.curr_dir);
                        textView.setText(String.valueOf(position));
                        final String fileName = items.get(position).getName();
                        final String newDir = currentDir + File.separator + fileName;
                        textView.setText(newDir);
                        File nextDir = new File(newDir);
                        if (nextDir.isDirectory()) {

                            Intent intent = new Intent(FileChooser.this, FileChooser.class);
                            intent.putExtra(CURR_DIRECTORY, newDir);
                            startActivity(intent);
                        }
                        if (nextDir.isFile()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(FileChooser.this);
                            builder.setTitle(R.string.choose_action);

                            final String[] optionsList = {getString(R.string.open_file),
                                    getString(R.string.rename_file),
                                    getString(R.string.delete_file)};

                            builder.setItems(optionsList, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    // TODO Auto-generated method stub
                                    if (item == 0) {
                                        openFile(newDir);
                                    }
                                    if (item == 1) {
                                        renameFile(fileName, position);
                                        adapter.notifyDataSetChanged();
                                        adapter.notifyItemChanged(position);
                                    }
                                    if (item == 2) {
                                        items.remove(position);
                                        File file = new File(newDir);
                                        file.delete();
                                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
                                        recyclerView.removeViewAt(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, items.size());
                                    }
                                }
                            });
                            builder.setCancelable(true);
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                })
        );
    }

    public void renameFile(final String fileName, final int position) {

        final EditText txtUrl = new EditText(this);
        txtUrl.setHint(fileName);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename_file)
                .setMessage(R.string.inser_new_file_name)
                .setView(txtUrl)
                .setPositiveButton(R.string.rename_file, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newFileName = txtUrl.getText().toString();
                        File from = new File(currentDir, fileName);
                        File to = new File(currentDir, newFileName);
                        from.renameTo(to);
                        items.get(position).setName(newFileName);
                        items.get(position).setPath(currentDir + File.separator + newFileName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void openFile(String newDir) {
        File nextDir = new File(newDir);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String fileExtension = fileExt(newDir);
        if (fileExtension == null) {
            Toast.makeText(FileChooser.this, R.string.no_handler_notification,
                    Toast.LENGTH_LONG).show();
        } else {
            String mimeType = myMime.getMimeTypeFromExtension(fileExt(newDir).substring(1));
            newIntent.setDataAndType(Uri.fromFile(nextDir), mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                FileChooser.this.startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FileChooser.this, R.string.no_handler_notification,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    private List<Item> fill(File f) {
        File[] dirs = f.listFiles();
        List<Item> dir = new ArrayList<Item>();
        List<Item> fls = new ArrayList<Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (ff.isDirectory()) {


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if (fbuf != null) {
                        buf = fbuf.length;
                    } else buf = 0;
                    String num_item = String.valueOf(buf);
                    if (buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    dir.add(new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(),
                            ExtensionsImages.directory_icon));
                } else {
                    Log.d("EXT", fileExt(ff.getAbsolutePath()) + " " + ff.getAbsolutePath());
                    String ext = fileExt(ff.getAbsolutePath());
                    if (txtExt.contains(ext)) {
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify,
                                ff.getAbsolutePath(), ExtensionsImages.txt_icon));
                    }
                    if (musExt.contains(ext)) {
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify,
                                ff.getAbsolutePath(), ExtensionsImages.music_icon));
                    }
                    if (imgExt.contains(ext)) {
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify,
                                ff.getAbsolutePath(), ExtensionsImages.image_icon));
                    }
                    if (vidExt.contains(ext)) {
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify,
                                ff.getAbsolutePath(), ExtensionsImages.video_icon));
                    }
                    if (!(txtExt.contains(ext) || musExt.contains(ext) ||
                            imgExt.contains(ext) || vidExt.contains(ext))) {
                        fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify,
                                ff.getAbsolutePath(), ExtensionsImages.file_icon));
                    }
                }
            }
        } catch (Exception e) {
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        return dir;
    }
}

