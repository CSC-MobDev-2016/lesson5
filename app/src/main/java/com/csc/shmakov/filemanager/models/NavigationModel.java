package com.csc.shmakov.filemanager.models;

import android.os.AsyncTask;
import android.os.Environment;

import com.csc.shmakov.filemanager.models.entities.Item;
import com.csc.shmakov.filemanager.models.entities.OpenedFolder;
import com.csc.shmakov.filemanager.utils.Event;
import com.csc.shmakov.filemanager.utils.EventDispatcher;

import java.io.File;

/**
 * Created by Pavel on 12/26/2015.
 */
public class NavigationModel extends EventDispatcher<Event> {
    public static final NavigationModel INSTANCE = new NavigationModel();
    private NavigationModel() {}

    private final ErrorDispatcher errorDispatcher = ErrorDispatcher.INSTANCE;

    private OpenedFolder currentOpenedFolder;

    private boolean parentAvailable;

    public OpenedFolder getCurrentFolder() {
        return currentOpenedFolder;
    }

    public int getPositionOfItem(Item item) {
        if (currentOpenedFolder == null) {
            return -1;
        }
        return currentOpenedFolder.content.indexOf(item);
    }

    public void fetchFolder(String path) {
        new AsyncTask<String, Void, File>(){
            @Override
            protected File doInBackground(String... params) {
                return new File(params[0]); // Not sure if necessary to make a separate thread for this.
            }

            @Override
            protected void onPostExecute(File dir) {
                if (dir.canRead()) {
                    currentOpenedFolder = new OpenedFolder(dir);
                    parentAvailable = dir.getParentFile() != null && dir.getParentFile().canRead();
                    dispatchEvent(new FolderChangedEvent());
                } else {
                    ErrorDispatcher.INSTANCE.dispatchError(ErrorType.FOLDER_UNREADABLE, dir.getAbsolutePath());
                }
            }
        }.execute(path);
    }

    public void fetchRootFolder() {
        fetchFolder(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public boolean currentFolderHasParent() {
        return parentAvailable;
    }

    public void rename(Item item, String newName) {
        File newFile = new File(item.file.getParent(), newName);
        boolean success = item.file.renameTo(newFile);
        if (!success) {
            errorDispatcher.dispatchError(ErrorType.INVALID_FILENAME);
            return;
        }

        int position = getPositionOfItem(item);
        currentOpenedFolder.content.set(position, new Item(newFile));
        dispatchEvent(new ItemUpdatedEvent(position));
    }

    public void delete(Item item) {
        int position = getPositionOfItem(item);
        boolean success = item.file.delete();
        if (!success) {
            errorDispatcher.dispatchError(ErrorType.FAILED_TO_DELETE);
            return;
        }

        currentOpenedFolder.content.remove(position);
        dispatchEvent(new ItemDeletedEvent(position));
    }

    public static class FolderChangedEvent extends Event {}

    public static class ItemUpdatedEvent extends Event {
        public final int position;

        public ItemUpdatedEvent(int position) {
            this.position = position;
        }
    }

    public static class ItemDeletedEvent extends Event {
        public final int position;

        public ItemDeletedEvent(int position) {
            this.position = position;
        }
    }
}
