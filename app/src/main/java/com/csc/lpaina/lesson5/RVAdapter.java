package com.csc.lpaina.lesson5;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hugo.weaving.DebugLog;

import static android.R.drawable.ic_menu_crop;

@DebugLog
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FileViewHolder> {
    private static List<FileWrapper> files;

    public RVAdapter(List<FileWrapper> files) {
        RVAdapter.files = files;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public void onBindViewHolder(FileViewHolder fileViewHolder, int i) {
        String path = files.get(i).getAbsolutePath();
        fileViewHolder.fileName.setText(path);
        fileViewHolder.fileIcon.setImageResource(ic_menu_crop);
    }

    @Override
    public FileViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new FileViewHolder(view);
    }


    public static class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView fileName;
        final ImageView fileIcon;
        final CardView cardView;

        FileViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileIcon = (ImageView) itemView.findViewById(R.id.file_icon);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Toast.makeText(context, "layout: " + getLayoutPosition() + " adapter: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.PATH, files.get(getAdapterPosition()).getAbsolutePath());
            context.startActivity(intent);
        }
    }

}
