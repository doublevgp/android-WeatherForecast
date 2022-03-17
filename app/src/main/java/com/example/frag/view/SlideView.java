package com.example.frag.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.frag.R;

public class SlideView extends LinearLayout {
    private Context context;
    private LinearLayout linearLayout;
//    private RelativeLayout relativeLayout;
    private Scroller scroller;
    private OnSlideListener onSlideListener;

    private int holderWidth = 120;
    private int lastX = 0;
    private int lastY = 0;
    private static final int TAN = 2;

    public interface OnSlideListener{
        public static final int SLIDE_STATUS_OFF = 0;
        public static final int SLIDE_STATUS_START_SCROLL = 1;
        public static final int SLIDE_STATUS_ON = 2;
        public void onSlide(View view, int status);
    }

    public SlideView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        context = getContext();
        scroller = new Scroller(context);
        // 设置linearLayout orientation 为横向
        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(context, R.layout.delete_merge, this);
        linearLayout = findViewById(R.id.delete_merge_content);
        holderWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, holderWidth, getResources().getDisplayMetrics()));
    }

    public void setButtonText(CharSequence text) {
        ((TextView) findViewById(R.id.delete_merge_delete)).setText(text);
    }

    public void setContentView(View view) {
        linearLayout.addView(view);
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }

    public void onRequireTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        // 得到的相对于View初始x轴位置的距离
        int scrollX = getScrollX();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                if (onSlideListener != null) {
                    onSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_START_SCROLL);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                int deltaX = x - lastX;
                int deltaY = y - lastY;
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    break;
                }
                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0) {
                        newScrollX = 0;
                    } else if (newScrollX > holderWidth) {
                        newScrollX = holderWidth;
                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                int newScrollX = 0;
                if (scrollX - holderWidth * 0.75 > 0) {
                    newScrollX = holderWidth;
                }
                this.smoothScrollTo(newScrollX, 0);
                if (onSlideListener != null) {
                    onSlideListener.onSlide(this,
                            newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF
                                    : OnSlideListener.SLIDE_STATUS_ON);
                }
                break;
            }
            default:
                break;
        }
        lastX = x;
        lastY = y;
    }

    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        scroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }
}
