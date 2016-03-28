package ru.csc.android_course.file_manager;

import android.support.v7.widget.RecyclerView;
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

    public FileAdapter(List<File> files) {
        this.files = files;
    }

    @Override
    public fileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_layout, parent, false);
        fileViewHolder holder = new fileViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(fileViewHolder holder, int position) {
        /*
        if (files.get(position).isDirectory()) {
            holder.icon.setImageDrawable();
        }
        */
        holder.fileName.setText(files.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class fileViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView fileName;

        public fileViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.file_icon);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
        }
    }
}
