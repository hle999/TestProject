package com.sen.test.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Senny on 2015/11/12.
 */
public class RingScrollView extends ScrollView {

    private RingScrollBar ringScrollBar;

    public RingScrollView(Context context) {
        super(context);
    }

    public RingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        ringScrollBar = new RingScrollBar();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_UP) {
            mScrollBarFlag = false;
        } else {
            mScrollBarFlag = true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ringScrollBar != null) {
            int alpha = ringScrollBar.getAlpha();
            if (mScrollY == getScrollY() && !mScrollBarFlag) {
                if (alpha > 0) {
                    if ((alpha - 15) > 15) {
                        alpha -= 15;
                    } else {
                        alpha = 0;
                    }
                    postInvalidate();
                }
            } else {
                alpha = 255;
            }
            ringScrollBar.setAlpha(alpha);
            ringScrollBar.setVerticalScrollRange(getChildAt(0).getHeight(), getScrollY());
            ringScrollBar.setBounds(0, 0, getRight() - 20, 130);
            ringScrollBar.setLayout(getLeft(), getTop(), getRight(), getBottom());
            ringScrollBar.draw(canvas);
        }
        mScrollY = getScrollY();
    }

    private int mScrollY;
    private boolean mScrollBarFlag;
}
