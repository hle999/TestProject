package com.sen.test.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Senny on 2015/11/24.
 */
public class ClockView1 extends ViewGroup {


    public ClockView1(Context context) {
        super(context);
    }

    public ClockView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View clockbackground = new ClockBackground1(getContext());
        View clockPointer = new ClockPointer1(getContext());
        addView(clockbackground);
        addView(clockPointer);
        for (int i = 0;i < getChildCount();i++) {
            View child = getChildAt(i);
            child.measure(r - l, b - t);
            child.layout(0, 0, r - l, b - t);
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(800, MeasureSpec.EXACTLY), heightMeasureSpec);
    }*/
}
