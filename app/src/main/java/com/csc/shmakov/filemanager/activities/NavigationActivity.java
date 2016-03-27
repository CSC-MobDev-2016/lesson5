package com.csc.shmakov.filemanager.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.csc.shmakov.filemanager.R;
import com.csc.shmakov.filemanager.models.ErrorDispatcher;
import com.csc.shmakov.filemanager.models.ErrorType;
import com.csc.shmakov.filemanager.models.NavigationModel;
import com.csc.shmakov.filemanager.models.entities.Item;
import com.csc.shmakov.filemanager.models.entities.OpenedFolder;
import com.csc.shmakov.filemanager.utils.Event;
import com.csc.shmakov.filemanager.utils.Observer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static final Map<Integer, Integer> iconResourceIds = new HashMap<>();
    static {
        iconResourceIds.put(Item.Type.FOLDER, R.drawable.folder);
        iconResourceIds.put(Item.Type.IMAGE, R.drawable.image);
        iconResourceIds.put(Item.Type.OTHER, R.drawable.other);
    }

    private static final Map<ErrorType, Integer> errorMessageIds = new EnumMap<>(ErrorType.class);
    static {
        errorMessageIds.put(ErrorType.FOLDER_UNREADABLE, R.string.folder_unreadable_error);
        errorMessageIds.put(ErrorType.INVALID_FILENAME, R.string.invalid_filename);
        errorMessageIds.put(ErrorType.INVALID_FILENAME, R.string.invalid_filename);
    }

    private final NavigationModel model = NavigationModel.INSTANCE;

    private final ErrorDispatcher errorDispatcher = ErrorDispatcher.INSTANCE;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAllCreations();
        if (savedInstanceState == null) {
            onFirstCreation();
        } else {
            onSubsequentCreations();
        }
    }

    private void onAllCreations() {
        setContentView(R.layout.activity_navigation);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true; // Better delete animations
            }
        });
        recyclerView.setAdapter(adapter);

        model.addObserver(modelObserver);
        errorDispatcher.addObserver(errorObserver);

        if (model.getCurrentFolder() == null) {
            showRootFolder();
        }
    }

    private void onFirstCreation() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.removeObserver(modelObserver);
        errorDispatcher.removeObserver(errorObserver);
    }

    @Override
    public void onBackPressed() {
        if (model.currentFolderHasParent()) {
            model.fetchFolder(model.getCurrentFolder().parentPath);
        } else {
            super.onBackPressed();
        }
    }

    private void onSubsequentCreations() {}

    private void showRootFolder() {
        model.fetchRootFolder();
    }

    private final Observer<Event> modelObserver = new Observer<Event>() {
            @Override
            public void onEvent(Event event) {
                if (event instanceof NavigationModel.FolderChangedEvent) {
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                } else if (event instanceof NavigationModel.ItemUpdatedEvent) {
                    int folderPosition = ((NavigationModel.ItemUpdatedEvent) event).position;
                    adapter.notifyItemChanged(getRecyclerPosition(folderPosition));
                } else if (event instanceof NavigationModel.ItemDeletedEvent) {
                    int folderPosition = ((NavigationModel.ItemDeletedEvent) event).position;
                    adapter.notifyItemRemoved(getRecyclerPosition(folderPosition));
                }
            }
        };

    private int getRecyclerPosition(int folderPosition) {
        return folderPosition + (model.currentFolderHasParent() ? 1 : 0);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private final ImageView icon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
            icon = (ImageView) itemView.findViewById(R.id.item_icon);
        }

        public void bindToDoubleDot() {
            textView.setText("..");
            icon.setImageResource(R.drawable.up);
            itemView.setOnClickListener(upClickListener);
        }
        public void bindToItem(Item item) {
            textView.setText(item.name);
            icon.setImageResource(iconResourceIds.get(item.type));
            ItemInteractionHandler handler = new ItemInteractionHandler(NavigationActivity.this, item);
            itemView.setOnClickListener(handler);
            itemView.setOnLongClickListener(handler);
        }

    }

    private final RecyclerView.Adapter<ItemViewHolder> adapter = new RecyclerView.Adapter<ItemViewHolder>() {
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            OpenedFolder currentFolder = model.getCurrentFolder();
            if (currentFolder == null) {
                return;
            }
            if (position == 0 && model.currentFolderHasParent()) {
                holder.bindToDoubleDot();
            } else {
                int index = model.currentFolderHasParent() ? position - 1 : position;
                holder.bindToItem(currentFolder.content.get(index));
            }
        }

        @Override
        public int getItemCount() {
            OpenedFolder currentFolder = model.getCurrentFolder();
            if (currentFolder == null) {
                return 0;
            }

            return currentFolder.content.size() + (model.currentFolderHasParent() ? 1 : 0);
        }
    };

    private final View.OnClickListener upClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            model.fetchFolder(model.getCurrentFolder().parentPath);
        }
    };

    private final Observer<ErrorDispatcher.ErrorEvent> errorObserver =
            new Observer<ErrorDispatcher.ErrorEvent>() {
                @Override
                public void onEvent(ErrorDispatcher.ErrorEvent event) {
                    String message = getString(errorMessageIds.get(event.type), event.params);
                    showError(message);
                }
            };

    private void showError(String message) {
        new AlertDialog.Builder(NavigationActivity.this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
