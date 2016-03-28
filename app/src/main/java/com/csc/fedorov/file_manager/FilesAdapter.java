package com.csc.fedorov.file_manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by roman on 28.03.2016.
 */
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {
    public List<File> mFiles;
    private Drawable fileIcon;
    private Drawable folderIcon;
    public Context context;

    private static OnItemClickListener clickListener;
    private static OnItemLongClickListener longClickListener;


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.file_item_name);
            imageView = (ImageView) itemView.findViewById(R.id.file_item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null)
                        clickListener.onItemClick(itemView, getLayoutPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        longClickListener.onItemLongClick(itemView, getLayoutPosition());
                    }
                    return true;
                }
            });
        }
    }

    public FilesAdapter(List<File> myDataset) {
        mFiles = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View fileView = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        folderIcon = ContextCompat.getDrawable(context, R.drawable.folder_icon_512x512);
        fileIcon = ContextCompat.getDrawable(context, R.drawable.file_icon);

        return new ViewHolder(fileView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mFiles.get(position).getName());
        if (mFiles.get(position).isDirectory()) {
            holder.imageView.setImageDrawable(folderIcon);
        } else {
            holder.imageView.setImageDrawable(fileIcon);
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }
}
