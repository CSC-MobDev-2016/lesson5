package com.csc.jv.FileManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListDirAdapter extends BaseAdapter {

    @NonNull
    private final List<CustomListItem> fileItems;
    private final LayoutInflater layoutInflater;

    public ListDirAdapter(Context context, @NonNull List<CustomListItem> fileItems) {

        this.fileItems = fileItems;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return fileItems.size();
    }

    public CustomListItem getItem(int position) {
        return fileItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void remove(String item) {
        fileItems.remove(findItem(item));
        this.notifyDataSetChanged();
    }

    private CustomListItem findItem(String item) {
        for (CustomListItem listItem : fileItems) {
            if (listItem.getName().equals(item)) {
                return listItem;
            }
        }
        return null;
    }

    public void set(String from, String to) {
        CustomListItem item = findItem(from);

        if (item != null) {
            item.setName(to);
            this.notifyDataSetChanged();
        }
    }

    public class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();

            holder.textView = (TextView) view.findViewById(R.id.textViewItem);
            holder.imageView = (ImageView) view.findViewById(R.id.imageViewItem);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.textView.setText(getItem(position).getName());
        holder.imageView.setImageResource(getItem(position).getIcon());

        return view;
    }
}
