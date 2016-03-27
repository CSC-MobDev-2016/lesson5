package com.csc.shmakov.filemanager.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.csc.shmakov.filemanager.R;
import com.csc.shmakov.filemanager.models.NavigationModel;
import com.csc.shmakov.filemanager.models.entities.Item;

/**
 * Created by Pavel on 3/26/2016.
 */
public class ItemInteractionHandler  implements View.OnClickListener, View.OnLongClickListener {
    private final Runnable[] menuClickHandlers;

    private final Context context;
    private final Item item;

    private final NavigationModel model = NavigationModel.INSTANCE;

    public ItemInteractionHandler(Context context, Item item) {
        this.context = context;
        this.item = item;
        menuClickHandlers = new Runnable[] {openItemHandler, renameItemHandler, deleteItemHandler};
    }

    @Override
    public void onClick(View v) {
        openItem();
    }

    @Override
    public boolean onLongClick(View v) {
        new AlertDialog.Builder(context)
                .setItems(R.array.file_menu,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                menuClickHandlers[which].run();
                            }
                        }).show();

        return true;
    }

    private final Runnable openItemHandler = new Runnable() {
        @Override
        public void run() {
            openItem();
        }
    };

    private final Runnable renameItemHandler = new Runnable() {
        @Override
        public void run() {
            final EditText editText = new EditText(context);
            editText.setText(item.name);
            new AlertDialog.Builder(context)
                    .setView(editText)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = editText.getText().toString();
                            model.rename(item, newName);
                        }
                    })
                    .show();

        }
    };

    private final Runnable deleteItemHandler = new Runnable() {
        @Override
        public void run() {
            model.delete(item);
        }
    };

    private void openItem() {
        if (item.type == Item.Type.FOLDER) {
            showFolder();
        } else if (item.type == Item.Type.IMAGE) {
            startSlideshow(item);
        } else {
            openFileWithSuitableApp();
        }
    }

    private void showFolder() {
        model.fetchFolder(item.path);
    }

    private void startSlideshow(Item startingItem) {
        Intent intent = new Intent(context, SlideshowActivity.class);
        intent.putExtra(SlideshowActivity.POSITION_EXTRA, model.getPositionOfItem(startingItem));
        context.startActivity(intent);
    }


    private void openFileWithSuitableApp() {
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.extension);
        newIntent.setDataAndType(Uri.fromFile(item.file), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.no_app_found, Toast.LENGTH_LONG);
        }
    }
}
