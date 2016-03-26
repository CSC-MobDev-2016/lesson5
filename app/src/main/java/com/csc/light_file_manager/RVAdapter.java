package com.csc.light_file_manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.csc.light_file_manager.items.Item;
import com.csc.light_file_manager.items.ParentItem;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
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
                    MenuItem open = menu.add("Открыть");
                    open.setOnMenuItemClickListener(onOpen);
                    MenuItem delete = menu.add("Удалить");
                    MenuItem rename = menu.add("Переименовать");
                }
            }
        };

        private final MenuItem.OnMenuItemClickListener onOpen = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                activity.onMyItemClick(ItemViewHolder.this.itemView, ItemViewHolder.this.getAdapterPosition());
//                File f = ItemViewHolder.this.item.getFile();
//                if (f.isDirectory()) {
////                    recyclerView.getLayoutManager().scrollToPosition(0);
//                    activity.showDir(f);
//                    //maybe scroll back when back
//                } else {
//                    try {
//                        Context context = RVAdapter.this.context;
//
//                        String mimeType = Utils.getMimeType(f);
//                        if (mimeType == null) {
//                            throw new NullPointerException();
//                        }
//                        final Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(f), mimeType);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    } catch (Exception e) {
//                        Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
//                    }
//                }

                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener onDelete = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                File f = ItemViewHolder.this.item.getFile();
                File parent = f.getParentFile();
                //f.delete(); alert dialog
                activity.showDir(parent);
                return true;
            }
        };

        private final MenuItem.OnMenuItemClickListener onRename = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                File f = ItemViewHolder.this.item.getFile();
                File parent = f.getParentFile();
                //f.renameTo(); alert dialog
                activity.showDir(ItemViewHolder.this.item.getFile().getParentFile());
                return true;
            }
        };

    }


    private List<Item> items = Collections.emptyList();

    public RVAdapter(Context context, ExplorerActivity activity) {
        this.context = context;
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
            if (item.getFile().isFile()) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getName().substring(item.getName().lastIndexOf(".") + 1));
                intent.setData(Uri.fromFile(item.getFile()));
                intent.setType(mimeType);
                final List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (matches.size() == 1) {
                    holder.icon.setImageDrawable(matches.get(0).loadIcon(context.getPackageManager()));
                    return;
                }
//            for (ResolveInfo match : matches) {
//                final Drawable icon = match.loadIcon(context.getPackageManager());
//                holder.icon.setImageDrawable(icon);
//                return;
//            }
            }
        } catch (Exception e) {

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
