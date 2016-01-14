package com.sen.test.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Senny on 2015/12/18.
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result =  super.dispatchTouchEvent(ev);
        System.out.println("CustomViewPager dispatchTouchEvent " + result);
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        System.out.println("CustomViewPager onInterceptTouchEvent " + result + " " + ev.getX() + " " + ev.getY());
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        System.out.println("CustomViewPager onTouchEvent " + result);
        return result;
    }*/
}
