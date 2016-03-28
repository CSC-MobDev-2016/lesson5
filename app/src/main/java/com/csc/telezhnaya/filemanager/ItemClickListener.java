package com.csc.telezhnaya.filemanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener listener;
    private GestureDetector detector;


    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public ItemClickListener(Context context, OnItemClickListener listener) {
        this.listener = listener;
        detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View child = view.findChildViewUnder(e.getX(), e.getY());
        if (child != null && listener != null && detector.onTouchEvent(e)) {
            listener.onItemClick(child, view.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
