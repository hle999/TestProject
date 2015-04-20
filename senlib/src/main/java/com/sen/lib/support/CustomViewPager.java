package com.sen.lib.support;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-4-20.
 */
public class CustomViewPager extends ViewPager{

    private List<OnPageChangeListener> listenerList;

    public void addPagerChangeListener(OnPageChangeListener listener) {
        if (listenerList == null) {
            listenerList = new ArrayList<OnPageChangeListener>();
        }
        listenerList.add(listener);
    }

    public CustomViewPager(Context context) {
        super(context);
        setOnPageChangeListener();
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPageChangeListener();
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (listenerList != null) {
            listenerList.clear();
        }
        listenerList = null;
    }

    private void setOnPageChangeListener() {
        super.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (listenerList != null) {
                    for (OnPageChangeListener listener:listenerList) {
                        listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (listenerList != null) {
                    for (OnPageChangeListener listener:listenerList) {
                        listener.onPageSelected(position);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listenerList != null) {
                    for (OnPageChangeListener listener:listenerList) {
                        listener.onPageScrollStateChanged(state);
                    }
                }
            }
        });
    }




}
