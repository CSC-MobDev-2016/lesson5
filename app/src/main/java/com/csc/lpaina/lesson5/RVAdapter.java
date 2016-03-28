package com.csc.lpaina.lesson5;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hugo.weaving.DebugLog;

import static android.R.drawable.ic_menu_agenda;
import static android.R.drawable.ic_menu_slideshow;

@DebugLog
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FileViewHolder> {
    private final List<FileWrapper> files;

    public RVAdapter(List<FileWrapper> files) {
        this.files = files;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public void onBindViewHolder(FileViewHolder fileViewHolder, int i) {
        FileWrapper fileWrapper = files.get(i);
        String path = fileWrapper.getPath();
        fileViewHolder.fileName.setText(path);

        if (fileWrapper.isDirectory()) {
            fileViewHolder.intent = new Intent(fileViewHolder.context, MainActivity.class);
            fileViewHolder.intent.putExtra(MainActivity.PATH, fileWrapper.getAbsolutePath());
            fileViewHolder.fileIcon.setImageResource(ic_menu_slideshow);
        } else {
            fileViewHolder.intent = new Intent(Intent.ACTION_VIEW);
            fileViewHolder.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            fileViewHolder.intent.setDataAndType(Uri.fromFile(fileWrapper.getFile()), fileWrapper.getMimeType());
            ResolveInfo info = fileViewHolder.context.getPackageManager().resolveActivity(fileViewHolder.intent, 0);
            try {
                fileViewHolder.fileIcon.setImageResource(info.getIconResource());
            } catch (Resources.NotFoundException e) {
                fileViewHolder.fileIcon.setImageResource(ic_menu_agenda);
            }
        }
    }

    @Override
    public FileViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new FileViewHolder(view);
    }


    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView fileName;
        final ImageView fileIcon;
        final CardView cardView;
        final Context context;
        Intent intent;

        FileViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileIcon = (ImageView) itemView.findViewById(R.id.file_icon);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            context.startActivity(intent);
        }
    }

}
