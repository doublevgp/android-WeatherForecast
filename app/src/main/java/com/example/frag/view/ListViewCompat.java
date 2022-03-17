package com.example.frag.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.example.frag.data.City;
import com.example.frag.data.FavCity;

public class ListViewCompat extends ListView {

    private SlideView mFocusedItemView;
    public ListViewCompat(Context context) {
        super(context);
    }
    public ListViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shrinkListItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SlideView) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);
                if (position != INVALID_POSITION) {
                    FavCity data = (FavCity) getItemAtPosition(position);
                    mFocusedItemView = data.getSlideView();
                }
            }
            default:
                break;
        }

        if (mFocusedItemView != null) {
            mFocusedItemView.onRequireTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }
}

