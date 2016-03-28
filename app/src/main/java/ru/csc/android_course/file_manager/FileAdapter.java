package ru.csc.android_course.file_manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by qurbonzoda on 28.03.16.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.fileViewHolder> {

    List<File> files;
    MainActivity activity;

    public FileAdapter(List<File> files, MainActivity activity) {
        this.files = files;
        this.activity = activity;
    }

    @Override
    public fileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_layout, parent, false);
        fileViewHolder holder = new fileViewHolder(v, activity);
        return holder;
    }

    @Override
    public void onBindViewHolder(fileViewHolder holder, int position) {
        holder.bind(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class fileViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView icon;
        TextView fileName;
        final MainActivity activity;
        File file;

        public fileViewHolder(View itemView, MainActivity activity) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.activity = activity;

            icon = (ImageView) itemView.findViewById(R.id.file_icon);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
        }

        public void bind(File file) {
            if (file.isDirectory()) {
                icon.setImageResource(R.drawable.folder_icon1);
            } else {
                icon.setImageResource(R.drawable.file_icon1);
            }
            fileName.setText(file.getName());
            this.file = file;
        }

        @Override
        public void onClick(View v) {
            if (file.isDirectory()) {
                activity.onFolderClick(file);
            } else {
                activity.onFileClick(file);
            }
        }
    }
}
