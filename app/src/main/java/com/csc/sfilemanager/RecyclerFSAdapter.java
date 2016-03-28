package com.csc.sfilemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Oleg Doronin
 * SFileManager
 * Copyright (c) 2016 CS. All rights reserved.
 */
public class RecyclerFSAdapter extends RecyclerView.Adapter<RecyclerFSAdapter.ViewHolder> {

    private File[] dataset;
    OnPathChange callback;

    public RecyclerFSAdapter(@NonNull final String dirPath, @NonNull final OnPathChange callback) {
        updateDataSet(dirPath);
        this.callback = callback;
    }

    void updateDataSet(@NonNull final String dirPath) {
        dataset = new File(dirPath).listFiles();
        if (dataset != null) {
            Arrays.sort(dataset, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    if (lhs.isDirectory() && !rhs.isDirectory()) {
                        return -1;
                    } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                        return 1;
                    }
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_fs, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(dataset[position].getName());
        holder.icon.setImageResource(dataset[position].isDirectory() ? R.drawable.folder : R.drawable.file);
        holder.file = dataset[position];
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView name;
        public ImageView icon;
        public LinearLayout ll;
        public File file;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.recycler_item_name);
            icon = (ImageView) v.findViewById(R.id.recycler_item_icon);
            ll = (LinearLayout) v.findViewById(R.id.ll_recycler_item);
            ll.setOnClickListener(this);
            ll.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            open(view);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            final View view = v;
            menu.setHeaderTitle(view.getContext().getString(R.string.select_the_action) + "\n" + file.getName());
            menu.add(0, v.getId(), 0, view.getContext().getString(R.string.open)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    open(view);
                    return true;
                }
            });
            menu.add(0, v.getId(), 0, view.getContext().getString(R.string.remove)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(view.getContext().getString(R.string.warning))
                            .setMessage(view.getContext().getString(R.string.you_are_really_want_to_delete_it) + ": " + file.getName())
                            .setCancelable(true)
                            .setNegativeButton(view.getContext().getString(R.string.no),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setPositiveButton(view.getContext().getString(R.string.yes),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            file.delete();
                                            callback.changePath(file.getParent());
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });
            menu.add(0, v.getId(), 0, view.getContext().getString(R.string.rename)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    final EditText input = new EditText(view.getContext());
                    input.setText(file.getName());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    builder.setTitle(view.getContext().getString(R.string.renaming))
                            .setCancelable(true)
                            .setView(input)
                            .setNegativeButton(view.getContext().getString(R.string.no),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setPositiveButton(view.getContext().getString(R.string.yes),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            File newFile = new File(file.getParent(), input.getText().toString());
                                            if (file.exists()) {
                                                file.renameTo(newFile);
                                                callback.changePath(file.getParent());
                                            }
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });
        }

        public void open(View view) {
            if (file.isDirectory()) {
                callback.changePath(file.getAbsolutePath());
            } else {
                try {
                    Intent openFile = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
                    openFile.setType(getMimeType(file.getAbsolutePath()));
                    view.getContext().startActivity(openFile);
                } catch (Exception ex) {
                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.error_open_activity), Toast.LENGTH_LONG).show();
                }
            }
        }

        public String getMimeType(String url) {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        }
    }
}
