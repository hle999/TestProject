package com.sen.test.ui.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
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

        private static final int INVALID_POINTER = -1;
        private static final int DEFAULT_GUTTER_SIZE = 16; // dips

        private boolean isVerticalEnable = false;
        private boolean isHorizontalEnable = true;
        private boolean mIsVerticalScrolling = false;
        private boolean mIsHorizontalScrolling = false;
        private boolean mIsBeingDragged = false;

        private float mLastMotionX;
        private float mLastMotionY;
        private float mInitialMotionX;
        private float mInitialMotionY;

        private int mTouchSlop;
        private int mActivePointerId;

        private int mDefaultGutterSize;
        private int mGutterSize;

        public FlingTouchManager(Context context) {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
            final int measuredWidth = getMeasuredWidth();
            final int maxGutterSize = measuredWidth / 10;
            final float density = context.getResources().getDisplayMetrics().density;
            mDefaultGutterSize = (int) (DEFAULT_GUTTER_SIZE * density);
            mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize);
        }

        private boolean onInterceptTouch(MotionEvent event) {

            final int action = event.getAction() & MotionEventCompat.ACTION_MASK;


            switch (action) {

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


                    if (isVerticalEnable && isHorizontalEnable) {

                    } else if (isVerticalEnable){

                    } else if (isHorizontalEnable) {
                        final float x = MotionEventCompat.getX(event, pointerIndex);
                        final float dx = x - mLastMotionX;
                        final float xDiff = Math.abs(dx);
                        final float y = MotionEventCompat.getY(event, pointerIndex);
                        final float yDiff = Math.abs(y - mInitialMotionY);

                        if (dx != 0 && !isGutterDrag(mLastMotionX, dx)) {
                            // Nested view has scrollable area under this point. Let it be handled there.
                            mLastMotionX = x;
                            mLastMotionY = y;
                            return false;
                        }

                        System.out.println("Flingc................1 " + x + " " + mLastMotionX + " " + event.getX() + " " + event.getY());
                        if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                            //Starting drag!
                            mIsBeingDragged = true;
                        } else if (yDiff > mTouchSlop) {
                            //Starting unable to drag
                        }
                        System.out.println("Flingc................2 " + mIsBeingDragged);

                        if (mIsBeingDragged) {

                        }
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    mLastMotionX = mInitialMotionX = 0;
                    mLastMotionY = mInitialMotionY = 0;
                    break;

                case MotionEvent.ACTION_CANCEL:

                    mLastMotionX = mInitialMotionX = 0;
                    mLastMotionY = mInitialMotionY = 0;
                    break;


            }

            return mIsBeingDragged;
        }

        public boolean onTouch(MotionEvent event) {

            System.out.println("Flingc................3");

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    break;

                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP:
                    endDrag();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    endDrag();
                    break;

            }

            return true;
        }

        private boolean isGutterDrag(float x, float dx) {
            return (x < mGutterSize && dx > 0) || (x > getWidth() - mGutterSize && dx < 0);
        }

        private void endDrag() {
            mIsBeingDragged = false;
        }

    }
}
