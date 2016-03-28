package com.csc.telezhnaya.filemanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private FileManagerActivity activity;
    private Context context;
    private List<Item> items = Collections.emptyList();

    public RecyclerViewAdapter(FileManagerActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(v, context, activity);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.name.setText(item.getName());

        if (!item.getName().equals(ReturnItem.PATH)) {
            holder.set(item);
        }

        File file = item.getFile();
        if (file.isFile()) {
            if (isImage(file.getName())) {
                Picasso.with(context).load(file).fit().into(holder.icon);
                return;
            }
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(file.getName()));
            PackageManager manager = context.getPackageManager();

            final ResolveInfo resInfo = manager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resInfo != null) {
                holder.icon.setImageDrawable(resInfo.loadIcon(manager));
                return;
            }
        }
        holder.icon.setImageResource(item.getImage());
    }

    private static final String[] IMG_EXTENSIONS = new String[]{"gif", "jpg", "jpeg", "png"};

    public static boolean isImage(String fileName) {
        for (String extension : IMG_EXTENSIONS) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public Item getItem(int position) {
        return items.get(position);
    }
}
