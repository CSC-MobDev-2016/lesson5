package com.csc.lpaina.lesson5;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.R.drawable.ic_menu_crop;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FileViewHolder> {
    private List<FileWrapper> files;

    public RVAdapter(List<FileWrapper> files) {
        this.files = files;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public void onBindViewHolder(FileViewHolder fileViewHolder, int i) {
        String path = files.get(i).getFile().getAbsolutePath();
        fileViewHolder.fileName.setText(path);
        fileViewHolder.fileIcon.setImageResource(ic_menu_crop);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        final Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout = (FrameLayout) v;
                TextView textView = (TextView) frameLayout.findViewById(R.id.file_name);
                Toast.makeText(context, "Clicked on " + textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return new FileViewHolder(view);
    }


    public static class FileViewHolder extends RecyclerView.ViewHolder {
        final TextView fileName;
        final ImageView fileIcon;

        FileViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileIcon = (ImageView) itemView.findViewById(R.id.file_icon);
        }
    }

}
