package com.my.fileexplorer;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FileArrayAdapter extends RecyclerView.Adapter<FileArrayAdapter.FileViewHolder> {

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv;
        TextView fileName;
        TextView fileSize;
        TextView fileDate;
        ImageView filePic;

        FileViewHolder(View itemView) {
            super(itemView);
            rv = (RecyclerView) itemView.findViewById(R.id.rv);
            fileName = (TextView) itemView.findViewById(R.id.TextView01);
            fileSize = (TextView) itemView.findViewById(R.id.TextView02);
            fileDate = (TextView) itemView.findViewById(R.id.TextViewDate);
            filePic = (ImageView) itemView.findViewById(R.id.fd_Icon1);
        }
    }

    List<Item> files;
    Context context;

    FileArrayAdapter(Context context, List<Item> files) {
        this.files = files;
        this.context = context;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        FileViewHolder fvh = new FileViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.fileName.setText(files.get(position).getName());
        holder.fileSize.setText(files.get(position).getData());
        holder.fileDate.setText(files.get(position).getDate());
        if (!files.get(position).getImage().equals(ExtensionsImages.image_icon)) {
            files.get(position).getImage().setPicasso(context, holder.filePic);
        } else {
            String path = files.get(position).getPath(); // + File.separator + files.get(position).getName();
            Log.d("PATH", path);
            Picasso.with(context).load(new File(path)).fit().into(holder.filePic);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
