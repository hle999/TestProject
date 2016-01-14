package com.sen.test.ui.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Senny on 2015/12/12.
 */
public class FlingTouchView extends LinearLayout {

    private FlingTouchManager flingTouchManager;

    public FlingTouchView(Context context) {
        super(context);
    }

    public FlingTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlingTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /*int childCount = getChildCount();
        int widht = r - l - getPaddingLeft();
        int hegiht = b - t - getPaddingTop();
        for (int i = 0;i < childCount;i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(widht, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(hegiht, MeasureSpec.EXACTLY));
            child.layout(getPaddingLeft(), getPaddingTop(), child.getMeasuredWidth(), child.getMeasuredHeight());
        }*/
        super.onLayout(changed, l, t, t, b);
        flingTouchManager = new FlingTouchManager(getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        MotionEvent copyEvent = MotionEvent.obtain(event);
        if (flingTouchManager != null) {
            return flingTouchManager.onInterceptTouch(this, event);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (flingTouchManager != null) {
            return flingTouchManager.onTouch(this, event);
        }
        return super.onTouchEvent(event);
    }

    private class FlingTouchManager {

        private static final int DEFAULT_GUTTER_SIZE = 16; // dips

        private boolean isVerticalEnable = false;
        private boolean isHorizontalEnable = false;
        private boolean mIsVerticalScrolling = false;
        private boolean mIsHorizontalScrolling = false;
        private boolean mIsBeingDragged = false;

        private TouchDirection horizontalTouch;
        private TouchDirection verticalTouch;

        private float mLastMotionX;
        private float mLastMotionY;
        private int mActivePointerId;

        public FlingTouchManager(Context context) {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            int mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
            final int measuredWidth = getMeasuredWidth();
            final int maxGutterSize = measuredWidth / 10;
            final float density = context.getResources().getDisplayMetrics().density;
            int mDefaultGutterSize = (int) (DEFAULT_GUTTER_SIZE * density);
            int mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize);
            horizontalTouch = new HorizontalTouch();
            verticalTouch = new VerticalTouch();
            horizontalTouch.init(mTouchSlop, mGutterSize);
            verticalTouch.init(mTouchSlop, mGutterSize);

        }

        private boolean onInterceptTouch(View view, MotionEvent event) {

            final int action = event.getAction() & MotionEventCompat.ACTION_MASK;

            if (action != MotionEvent.ACTION_DOWN) {
                if (horizontalTouch.isUnableToDrag()
                        || verticalTouch.isUnableToDrag()) {
                    return false;
                }
            }


            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    endDrag();
                    horizontalTouch.endDrag();
                    verticalTouch.endDrag();
                    horizontalTouch.onTouch(view, event);
                    verticalTouch.onTouch(view, event);

                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    mLastMotionX = event.getX();
                    mLastMotionY = event.getY();

                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!isVerticalEnable && !isHorizontalEnable) {
                        mIsBeingDragged = isHorizontalEnable = horizontalTouch.onTouch(view, event);
                        if (!isHorizontalEnable) {
                            mIsBeingDragged = isVerticalEnable = verticalTouch.onTouch(view, event);
                        }
                    }/* else if (isHorizontalEnable) {
                        mIsBeingDragged = horizontalTouch.onTouch(view, event);
                    } else if (isVerticalEnable){
                        mIsBeingDragged = verticalTouch.onTouch(view, event);
                    }*/
                    mLastMotionX = event.getX();
                    mLastMotionY = event.getY();
                    break;


            }

            return mIsBeingDragged;
        }

        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = event.getX();
                    mLastMotionY = event.getY();
                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    isHorizontalEnable = false;
                    isVerticalEnable = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (isHorizontalEnable) {
                        scrollTo(getScrollX() - (int) (event.getX() - mLastMotionX), getScrollY());
                    } else if (isVerticalEnable) {
                        scrollTo(getScrollX(), getScrollY() - (int) (event.getY() - mLastMotionY));

                    } else {
                    }
                    mLastMotionX = event.getX();
                    mLastMotionY = event.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    scrollBy(-getScrollX(), -getScrollY());
                    break;

                case MotionEvent.ACTION_CANCEL:
                    scrollBy(-getScrollX(), -getScrollY());
                    break;

            }

            return true;
        }

        public void endDrag() {
            mIsBeingDragged = false;
            isHorizontalEnable = false;
            isVerticalEnable = false;
        }

    }

    private class HorizontalTouch implements TouchDirection {

        private float mLastMotionX;
        private float mLastMotionY;
        private float mInitialMotionX;
        private float mInitialMotionY;

        private int mTouchSlop;
        private int mActivePointerId;
        private int mGutterSize;
        private boolean mIsUnableToDrag;

        @Override
        public void init(int mTouchSlop, int mGutterSize) {
            this.mTouchSlop = mTouchSlop;
            this.mGutterSize = mGutterSize;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = mInitialMotionX = event.getX();
                    mLastMotionY = mInitialMotionY = event.getY();
                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    final int activePointerId = mActivePointerId;
                    if (activePointerId == INVALID_POINTER) {
                        // If we don't have a valid id, the touch down wasn't on content.
                        break;
                    }

                    final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId);


                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float dx = x - mLastMotionX;
                    final float xDiff = Math.abs(dx);
                    final float y = MotionEventCompat.getY(event, pointerIndex);
                    final float yDiff = Math.abs(y - mInitialMotionY);

                    if (dx != 0 && !isGutterDrag(mLastMotionX, dx) && canScroll(view, false, (int) dx, (int) x, (int) y)) {
                        // Nested view has scrollable area under this point. Let it be handled there.
                        mLastMotionX = x;
                        mLastMotionY = y;
                        mIsUnableToDrag = true;
                        return false;
                    }

                    if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                        //Starting drag!
                       return true;
                    } else if (yDiff > mTouchSlop) {
                        //Starting unable to drag
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    endDrag();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    endDrag();
                    break;

            }

            return false;
        }

        @Override
        public boolean isGutterDrag(float x, float dx) {
            return (x < mGutterSize && dx > 0) || (x > getWidth() - mGutterSize && dx < 0);
        }

        @Override
        public boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
            if (v instanceof ViewGroup) {
                final ViewGroup group = (ViewGroup) v;
                final int scrollX = v.getScrollX();
                final int scrollY = v.getScrollY();
                final int count = group.getChildCount();
                // Count backwards - let topmost views consume scroll distance first.
                for (int i = count - 1; i >= 0; i--) {
                    // TODO: Add versioned support here for transformed views.
                    // This will not work for transformed views in Honeycomb+
                    final View child = group.getChildAt(i);
                    if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                            y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                            canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                    y + scrollY - child.getTop())) {
                        return true;
                    }
                }
            }

            return checkV && ViewCompat.canScrollHorizontally(v, -dx);
        }

        @Override
        public boolean isUnableToDrag() {
            return mIsUnableToDrag;
        }

        @Override
        public void endDrag() {
            mLastMotionX = mInitialMotionX = 0;
            mLastMotionY = mInitialMotionY = 0;
            mIsUnableToDrag = false;
        }
    }

    private class VerticalTouch implements TouchDirection {

        private float mLastMotionX;
        private float mLastMotionY;
        private float mInitialMotionX;
        private float mInitialMotionY;

        private int mTouchSlop;
        private int mActivePointerId;
        private int mGutterSize;
        private boolean mIsUnableToDrag;

        @Override
        public void init(int mTouchSlop, int mGutterSize) {
            this.mTouchSlop = mTouchSlop;
            this.mGutterSize = mGutterSize;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = mInitialMotionX = event.getX();
                    mLastMotionY = mInitialMotionY = event.getY();
                    mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    final int activePointerId = mActivePointerId;
                    if (activePointerId == INVALID_POINTER) {
                        // If we don't have a valid id, the touch down wasn't on content.
                        break;
                    }

                    final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId);
                    final float y = MotionEventCompat.getY(event, pointerIndex);
                    final float dy = y - mLastMotionY;
                    final float yDiff = Math.abs(dy);
                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float xDiff = Math.abs(x - mInitialMotionX);

                    if (dy != 0 && !isGutterDrag(mLastMotionY, dy) && canScroll(view, false, (int) dy, (int) x, (int) y)) {
                        // Nested view has scrollable area under this point. Let it be handled there.
                        mLastMotionX = x;
                        mLastMotionY = y;
                        mIsUnableToDrag = true;
                        return false;
                    }

                    if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff) {
                        //Starting drag!
                        return true;
                    } else if (xDiff > mTouchSlop) {
                        //Starting unable to drag
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    endDrag();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    endDrag();
                    break;

            }

            return false;
        }

        @Override
        public boolean isGutterDrag(float y, float dy) {
            return (y < mGutterSize && dy > 0) || (y > getHeight() - mGutterSize && dy < 0);
        }

        @Override
        public boolean canScroll(View v, boolean checkV, int dy, int x, int y) {
            if (v instanceof ViewGroup) {
                final ViewGroup group = (ViewGroup) v;
                final int scrollX = v.getScrollX();
                final int scrollY = v.getScrollY();
                final int count = group.getChildCount();
                // Count backwards - let topmost views consume scroll distance first.
                for (int i = count - 1; i >= 0; i--) {
                    // TODO: Add versioned support here for transformed views.
                    // This will not work for transformed views in Honeycomb+
                    final View child = group.getChildAt(i);
                    if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                            y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                            canScroll(child, true, dy, x + scrollX - child.getLeft(),
                                    y + scrollY - child.getTop())) {
                        return true;
                    }
                }
            }
            return checkV && ViewCompat.canScrollVertically(v, -dy);
        }

        @Override
        public boolean isUnableToDrag() {
            return mIsUnableToDrag;
        }

        @Override
        public void endDrag() {
            mLastMotionX = mInitialMotionX = 0;
            mLastMotionY = mInitialMotionY = 0;
            mIsUnableToDrag = false;
        }
    }

    private interface TouchDirection {
        int INVALID_POINTER = -1;
        void init(int mTouchSlop, int mGutterSize);
        boolean onTouch(View view, MotionEvent event);
        boolean isGutterDrag(float p, float dp);
        boolean canScroll(View v, boolean checkV, int dp, int x, int y);
        boolean isUnableToDrag();
        void endDrag();
    }
}
