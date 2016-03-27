package com.csc.light_file_manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.csc.light_file_manager.items.Item;
import com.csc.light_file_manager.items.ParentItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Филипп on 26.03.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder> {

    private Context context;
    private ExplorerActivity activity;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        Item item;

        ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.item_icon);
            title = (TextView) itemView.findViewById(R.id.item_name);

            itemView.setOnCreateContextMenuListener(mOnCreateContextMenuListener);
        }

        public void bind(Item data) {
            this.item = data;
        }

        private final View.OnCreateContextMenuListener mOnCreateContextMenuListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (item != null) {
                    MenuItem open = menu.add(context.getString(R.string.open));
                    open.setOnMenuItemClickListener(onOpen);
                    MenuItem delete = menu.add(context.getString(R.string.delete));
                    delete.setOnMenuItemClickListener(onDelete);
                    MenuItem rename = menu.add(context.getString(R.string.rename));
                    rename.setOnMenuItemClickListener(onRename);
                }
            }
        };

        private final MenuItem.OnMenuItemClickListener onOpen = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                activity.onMyItemClick(ItemViewHolder.this.itemView, ItemViewHolder.this.getAdapterPosition());
                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener onDelete = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final File f = ItemViewHolder.this.item.getFile();
                final File parent = f.getParentFile();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(context.getString(R.string.delete))
                        .setMessage(context.getString(R.string.element_will_be_deleted) + f.getName())
                        .setCancelable(true)
                        .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean deleted = f.delete();
                                if (deleted) {
                                    activity.showDir(parent);
                                }
                            }
                        });
                builder.create().show();
                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener onRename = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final File f = ItemViewHolder.this.item.getFile();
                final File parent = f.getParentFile();
                final EditText editText = new EditText(context);
                editText.setText(f.getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(context.getString(R.string.rename))
                        .setMessage(context.getString(R.string.element_will_be_renamed) + f.getName())
                        .setCancelable(true)
                        .setView(editText)
                        .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean renamed = f.renameTo(new File(parent, editText.getText().toString()));
                                if (renamed) {
                                    activity.showDir(parent);
                                }
                            }
                        });
                builder.create().show();
                return true;
            }
        };

    }


    private List<Item> items = Collections.emptyList();

    public RVAdapter(ExplorerActivity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    public void updateItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.title.setText(item.getName());

        if (!item.getName().equals(ParentItem.name)) {
            holder.bind(item);
        }
        else {
            holder.bind(null);
        }

        try {
            File file = item.getFile();
            if (file.isFile()) {
                if (Utils.isImageExtension(file.getName())) {
                    Picasso.with(context).load(file).fit().into(holder.icon);
                    return;
                }
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getName().substring(item.getName().lastIndexOf(".") + 1));
                intent.setDataAndType(Uri.fromFile(file), mimeType);
                final ResolveInfo defaultResolution = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                holder.icon.setImageDrawable(defaultResolution.loadIcon(context.getPackageManager()));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.icon.setImageResource(item.getImage());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public Item getItem(int position) {
        return items.get(position);
    }

}
