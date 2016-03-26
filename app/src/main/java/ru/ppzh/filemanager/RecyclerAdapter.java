package ru.ppzh.filemanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Item> files;

    public RecyclerAdapter(List<Item> files) {
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.image.setImageResource(files.get(position).getImageResource());
        holder.filename.setText(files.get(position).getName());
        holder.modified.setText(files.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView filename;
        public TextView modified;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.file_img);
            filename = (TextView) v.findViewById(R.id.file_name);
            modified = (TextView) v.findViewById(R.id.last_modified);
        }
    }

    public Item getItem(int position) {
        return files.get(position);
    }

    public void setData(List<Item> files) {
        this.files = files;
    }
}
