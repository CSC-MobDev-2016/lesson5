package com.csc.kharitonov.filemanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExplorerAdapter extends RecyclerView.Adapter<ExplorerAdapter.ViewHolder> {
    private List<DirItem> items;
    private MainActivity activity;
    private String selectedFile = null;

    public ExplorerAdapter(MainActivity activity) {
        this.activity = activity;
        this.items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dir_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        viewHolder.fileItem.setOnClickListener(activity);
        viewHolder.fileItem.setOnLongClickListener(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fileIcon.setImageDrawable(items.get(position).getIcon());
        holder.fileName.setText(items.get(position).getName());
        holder.fileSize.setText(items.get(position).getSize());
        holder.fileDate.setText(items.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<DirItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public String getSelectedFile() {
        return selectedFile;
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener {
        private ImageView fileIcon;
        private TextView fileName;
        private TextView fileSize;
        private TextView fileDate;
        private LinearLayout fileItem;

        public ViewHolder(View itemView) {
            super(itemView);
            fileIcon = (ImageView) itemView.findViewById(R.id.fileIcon);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            fileDate = (TextView) itemView.findViewById(R.id.fileDate);
            fileItem = (LinearLayout) itemView.findViewById(R.id.fileItem);
            fileItem.setTag(this);
            ExplorerAdapter.this.activity.registerForContextMenu(fileItem);
        }

        public ImageView getFileIcon() {
            return fileIcon;
        }

        public TextView getFileName() {
            return fileName;
        }

        public TextView getFileSize() {
            return fileSize;
        }

        public TextView getFileDate() {
            return fileDate;
        }

        public LinearLayout getFileItem() {
            return fileItem;
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("my_tag", "long:" + fileName.getText().toString());
            ExplorerAdapter.this.selectedFile = fileName.getText().toString();
            return false;
        }
    }
}
