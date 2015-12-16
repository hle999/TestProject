package com.sen.test.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Senny on 2015/12/7.
 */
public class FlingView extends ViewGroup {

    public FlingView(Context context) {
        super(context);
    }

    public FlingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        int width = r - l;
        int height = b - t;
        for (int i = 0;i < count;i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }

    }

}
