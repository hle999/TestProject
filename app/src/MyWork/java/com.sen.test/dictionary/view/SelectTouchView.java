package com.sen.test.dictionary.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.test.R;


/**
 * Editor: sgc
 * Date: 2015/01/29
 */
public class SelectTouchView extends ViewGroup implements View.OnTouchListener{
    public SelectTouchView(Context context) {
        super(context);
    }

    public SelectTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String heightStr = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height").replace("dip", "");
//        Log.i(SelectTouchView.class.toString(), Float.valueOf(heightStr)+" ??");
        float height = Float.valueOf(heightStr);
        setMinimumHeight((int)height);
    }

    public SelectTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            view.measure(getMeasuredWidth(), getMeasuredHeight());
            view.layout((int)view.getX(), (int)view.getY(),
                    (int)view.getX()+view.getMeasuredWidth(), (int)view.getY()+view.getMeasuredHeight());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        TextView textView = new TextView(getContext());
        textView.setText(R.string.text_label);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLUE);
        addView(textView);
        textView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                Log.i(SelectTouchView.class.toString(), "down");
                 break;

            case MotionEvent.ACTION_UP:
                Log.i(SelectTouchView.class.toString(), "up");
                 break;

        }

        return false;
    }
}
