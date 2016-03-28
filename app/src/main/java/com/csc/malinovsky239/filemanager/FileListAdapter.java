package com.csc.malinovsky239.filemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ItemViewHolder> {

    private final Context context;
    List<FileSystemItem> items = new ArrayList<>();
    private String currentWorkingDirectory;
    private final OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(FileSystemItem item) {
            if (item.isFolder()) {
                if (item.getTitle().equals("...")) {
                    currentWorkingDirectory = currentWorkingDirectory.substring(0, currentWorkingDirectory.lastIndexOf('/'));
                } else {
                    currentWorkingDirectory = new File(new File(currentWorkingDirectory), item.getTitle()).toString();
                }
                clear();
                File[] listFiles = (new File(currentWorkingDirectory)).listFiles();
                for (File file : listFiles) {
                    addItem(file.getName(), file.isDirectory());
                }
                notifyDataSetChanged();
            } else {
                Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                File file = new File(currentWorkingDirectory);
                File file2 = new File(file, item.getTitle());
                String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file2).toString());
                String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                myIntent.setDataAndType(Uri.fromFile(file2), mimeType);
                context.startActivity(myIntent);
            }
        }

        @Override
        public void onItemLongClick(final FileSystemItem item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("")
                    .setMessage("")
                    .setPositiveButton(R.string.rename_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.rename_button);
                            final EditText input = new EditText(context);
                            builder.setView(input);

                            builder.setPositiveButton(R.string.OK_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newName = input.getText().toString();
                                    File from = new File(currentWorkingDirectory, item.getTitle());
                                    File to = new File(currentWorkingDirectory, newName);
                                    if (from.renameTo(to)) {
                                        item.setTitle(newName);
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                            builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    })
                    .setNeutralButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(currentWorkingDirectory, item.getTitle());
                            if (file.delete()) {
                                items.remove(item);
                                notifyDataSetChanged();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    public FileListAdapter(Context context) {
        this.context = context;
        currentWorkingDirectory = Environment.getExternalStorageDirectory().toString();
    }

    public void clear() {
        items.clear();
        if (!currentWorkingDirectory.equals(context.getString(R.string.root))) {
            addItem("...", true);
        }
    }

    public void addItem(String title, boolean isFolder) {
        items.add(new FileSystemItem(title, isFolder));
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.filename.setText(items.get(position).getTitle());
        if (items.get(position).isFolder()) {
            holder.icon.setImageResource(R.drawable.folder);
        } else {
            holder.icon.setImageResource(R.drawable.file);
        }
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView item;
        TextView filename;
        ImageView icon;

        ItemViewHolder(View itemView) {
            super(itemView);
            item = (CardView) itemView.findViewById(R.id.item);
            filename = (TextView) itemView.findViewById(R.id.filename);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }

        public void bind(final FileSystemItem fileSystemItem, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(fileSystemItem);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(fileSystemItem);
                    return true;
                }
            });
        }
    }

}