package com.csc.my_file_manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import com.squareup.picasso.Picasso;

/**
 * Created by anastasia on 26.03.16.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.FileViewHolder> {
    private List<FileItem> files;
    private MainActivity activity;
    private Context context;

    RecycleViewAdapter(List<FileItem> files, MainActivity activity){
        this.files = files;
        this.activity = activity;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        context = viewGroup.getContext();
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FileViewHolder personViewHolder, int i) {
        File f = files.get(i).f;
        Date lastModDate = new Date(f.lastModified());
        String dateModify = DateFormat.getDateTimeInstance().format(lastModDate);

        personViewHolder.fileName.setText(files.get(i).name);
        personViewHolder.f = f;
        personViewHolder.lastMod.setText(dateModify);
        if (f.isDirectory()) {
            if(i == 0) {
                personViewHolder.preview.setImageResource(R.drawable.up);
                personViewHolder.fileName.setText("");
                personViewHolder.lastMod.setText("");
            } else {
                personViewHolder.preview.setImageResource(R.drawable.folder);
            }
        } else if (files.get(i).isImg()) {
            Picasso.with(context).load(f).fit().into(personViewHolder.preview);
        }
        else {
            personViewHolder.preview.setImageResource(R.drawable.file);
        }
    }

    void setItems(List<FileItem> list) {
        files = list;
        notifyDataSetChanged();
    }

    FileItem getItem(int pos) {
        return files.get(pos);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView fileName;
        TextView lastMod;
        ImageView preview;
        private File f;

        FileViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView)itemView.findViewById(R.id.fileName);
            lastMod = (TextView)itemView.findViewById(R.id.lastMod);
            preview = (ImageView)itemView.findViewById(R.id.preview);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Open");
            menu.add(0, v.getId(), 0, "Rename");
            menu.add(0, v.getId(), 0, "Delete");
            menu.getItem(0).setOnMenuItemClickListener(onFileOpen);
            menu.getItem(1).setOnMenuItemClickListener(onFileRename);
            menu.getItem(2).setOnMenuItemClickListener(onFileDelete);
        }

        private final MenuItem.OnMenuItemClickListener onFileOpen = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                activity.itemClick(itemView, getAdapterPosition());
                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener onFileRename = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final EditText editText = new EditText(context);
                editText.setText(f.getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setMessage("Enter new name for " + f.getName())
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                f.renameTo(new File(f.getParentFile(), editText.getText().toString()));
                                activity.getFiles(f.getParentFile());
                            }})
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}});
                builder.create().show();
                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener onFileDelete = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setMessage("Delete " + f.getName() + "?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                f.delete();
                                activity.getFiles(f.getParentFile());
                            }})
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}});
                builder.create().show();
                return true;
            }
        };
    }
}
