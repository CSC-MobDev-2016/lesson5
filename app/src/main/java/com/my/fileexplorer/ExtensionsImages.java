package com.my.fileexplorer;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public enum ExtensionsImages {
    directory_icon {
        public void setPicasso(Context context, ImageView imageView) {
            Picasso.with(context).load(R.drawable.folder_blue).into(imageView);
        }
    }, file_icon {
        public void setPicasso(Context context, ImageView imageView) {
            Picasso.with(context).load(R.drawable.alien).into(imageView);
        }
    }, txt_icon {
        public void setPicasso(Context context, ImageView imageView) {
            Picasso.with(context).load(R.drawable.file_icon).into(imageView);
        }
    }, music_icon {
        public void setPicasso(Context context, ImageView imageView) {
            Picasso.with(context).load(R.drawable.music_icon).into(imageView);
        }
    }, image_icon {
        public void setPicasso(Context context, ImageView imageView) {
            Picasso.with(context).load(R.drawable.image_icon2).into(imageView);
        }
    }, video_icon {
        public void setPicasso(Context context, ImageView imageView) {
            Picasso.with(context).load(R.drawable.image_icon).into(imageView);
        }
    };

    public abstract void setPicasso(Context context, ImageView imageView);
}
