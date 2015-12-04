package com.sen.test.ui.view;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Senny on 2015/10/30.
 */
public class CustomNestedScrollView extends ScrollView implements NestedScrollingParent{

    private NestedScrollingParentHelper nestedScrollingParentHelper;

    public CustomNestedScrollView(Context context) {
        super(context);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        System.out.print("CustomNested onStartNestedScroll");
        return false;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        if (nestedScrollingParentHelper != null) {
            nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        }
        System.out.print("CustomNested onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(View target) {
        if (nestedScrollingParentHelper != null) {
            nestedScrollingParentHelper.onStopNestedScroll(target);
        }
        System.out.print("CustomNested onStopNestedScroll");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        System.out.print("CustomNested onNestedScroll");
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        System.out.print("CustomNested onNestedPreScroll");
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        System.out.print("CustomNested onNestedFling");
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        System.out.print("CustomNested onNestedPreFling");
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        if (nestedScrollingParentHelper != null) {
            return nestedScrollingParentHelper.getNestedScrollAxes();
        }
        System.out.print("CustomNested getNestedScrollAxes");
        return 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }
}
