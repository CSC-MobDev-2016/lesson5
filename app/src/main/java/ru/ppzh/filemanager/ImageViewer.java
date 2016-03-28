package ru.ppzh.filemanager;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;

public class ImageViewer extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);
        imageView = (ImageView) findViewById(R.id.image_viewer);

        ImageLoader imageloader = new ImageLoader();
        String imagePath = getIntent().getStringExtra(MainActivity.IMAGE_PATH);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap image = imageloader.decodeSampledBitmapFromFile(
                imagePath,
                size.x,
                size.y
        );
        imageView.setImageBitmap(image);
    }
}
