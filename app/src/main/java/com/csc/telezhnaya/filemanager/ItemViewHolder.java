package com.csc.telezhnaya.filemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Toast;

import java.io.File;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private FileManagerActivity activity;
    private Item item;
    ImageView icon;
    TextView name;

    ItemViewHolder(View itemView, final Context context, FileManagerActivity activity) {
        super(itemView);
        this.context = context;
        this.activity = activity;
        icon = (ImageView) itemView.findViewById(R.id.item_icon);
        name = (TextView) itemView.findViewById(R.id.item_name);
        OnCreateContextMenuListener menuListener = new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                if (item != null) {
                    MenuItem open = menu.add(context.getString(R.string.open));
                    MenuItem delete = menu.add(context.getString(R.string.delete));
                    MenuItem rename = menu.add(context.getString(R.string.rename));
                    open.setOnMenuItemClickListener(onOpen);
                    delete.setOnMenuItemClickListener(onDelete);
                    rename.setOnMenuItemClickListener(onRename);
                }
            }
        };
        itemView.setOnCreateContextMenuListener(menuListener);
    }

    public void set(Item data) {
        this.item = data;
    }

    private final OnMenuItemClickListener onOpen = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            activity.onItemClick(ItemViewHolder.this.getAdapterPosition());
            return true;
        }
    };

    private final OnMenuItemClickListener onDelete = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final File file = ItemViewHolder.this.item.getFile();
            final File parent = file.getParentFile();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(context.getString(R.string.delete))
                    .setMessage(context.getString(R.string.delete_process) + file.getName())
                    .setCancelable(true)
                    .setPositiveButton(context.getString(android.R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (file.delete()) {
                                        activity.showDirectory(parent);
                                    } else {
                                        Toast.makeText(context, "Unable to delete " + file.getName(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
            builder.create().show();
            return true;
        }
    };

    private final OnMenuItemClickListener onRename = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final File file = ItemViewHolder.this.item.getFile();
            final File parent = file.getParentFile();
            final EditText editText = new EditText(context);
            editText.setText(file.getName());
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(context.getString(R.string.rename))
                    .setMessage(context.getString(R.string.rename_process) + file.getName())
                    .setCancelable(true)
                    .setView(editText)
                    .setPositiveButton(context.getString(android.R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (file.renameTo(new File(parent, editText.getText().toString()))) {
                                        activity.showDirectory(parent);
                                    } else {
                                        Toast.makeText(context, "Unable to rename " + file.getName(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
            builder.create().show();
            return true;
        }
    };
}
