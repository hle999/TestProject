package com.sen.test.ui.view;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by Senny on 2015/12/12.
 */
public class FlingTouchView extends ViewGroup {

    private FlingTouchManager flingTouchManager;

    public FlingTouchView(Context context) {
        super(context);
    }

    public FlingTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlingTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int widht = r - l - getPaddingLeft();
        int hegiht = b - t - getPaddingTop();
        for (int i = 0;i < childCount;i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(widht, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(hegiht, MeasureSpec.EXACTLY));
            child.layout(getPaddingLeft(), getPaddingTop(), child.getMeasuredWidth(), child.getMeasuredHeight());
        }
        flingTouchManager = new FlingTouchManager(getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (flingTouchManager != null) {
            return flingTouchManager.onInterceptTouch(event);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (flingTouchManager != null) {
            return flingTouchManager.onTouch(event);
        }
        return super.onTouchEvent(event);
    }

    private class FlingTouchManager {

        private boolean mIsVerticalScroll = false;
        private boolean mIsHorizontalScroll = false;
        private boolean mIsBeingDragged = false;

        private int mLastTouchX;
        private int mLastTouchY;

        private int mTouchSlop;

        public FlingTouchManager(Context context) {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        }

        private boolean onInterceptTouch(MotionEvent event) {

            final int action = event.getAction() & MotionEvent.ACTION_MASK;


            switch (action) {

                case MotionEvent.ACTION_DOWN:

                    break;

                case MotionEvent.ACTION_MOVE:
                    System.out.println("FlingTouch move! ");

                    break;

                case MotionEvent.ACTION_UP:

                    break;

                case MotionEvent.ACTION_CANCEL:

                    break;


            }

            return false;
        }

        public boolean onTouch(MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    break;

                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP:

                    break;

                case MotionEvent.ACTION_CANCEL:

                    break;

            }

            return false;
        }

    }
}
