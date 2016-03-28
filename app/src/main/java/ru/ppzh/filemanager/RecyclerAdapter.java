package ru.ppzh.filemanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
        implements View.OnCreateContextMenuListener {
    private Context context;
    private List<Item> files;
    private int position;

    public RecyclerAdapter(List<Item> files, Context context) {
        this.files = files;
        this.context = context;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.image.setImageBitmap(files.get(position).getPreview());
        holder.filename.setText(files.get(position).getName());
        holder.modified.setText(files.get(position).getDate());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public Item getItem(int position) {
        return files.get(position);
    }

    public void setData(List<Item> files) {
        this.files = files;
    }

    public boolean deleteItem(Item item) {
        return files.remove(item);
    }

    public boolean renameItem(Item item, File newFile) {
        if (deleteItem(item)) {
            item.setPath(newFile.getAbsolutePath());
            item.setName(newFile.getName());
            item.setDate(
                    DateFormat.getDateTimeInstance().format(
                            new Date(newFile.lastModified())
                    )
            );
            files.add(item);
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView filename;
        public TextView modified;

        public ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(RecyclerAdapter.this);
            image = (ImageView) v.findViewById(R.id.file_img);
            filename = (TextView) v.findViewById(R.id.file_name);
            modified = (TextView) v.findViewById(R.id.last_modified);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (!files.get(position).isDirectory()) {
            menu.add(Menu.NONE, R.id.open, Menu.NONE, R.string.open);
            menu.add(Menu.NONE, R.id.rename, Menu.NONE, R.string.rename);
            menu.add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete);
        }
    }
}
