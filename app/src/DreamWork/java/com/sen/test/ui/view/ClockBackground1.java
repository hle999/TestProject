package com.sen.test.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Senny on 2015/11/23.
 */
public class ClockBackground1 extends View {

    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private final static String[] HOUR_FLAGS = {"3", "6", "9", "12"};
    private int spaceAngle = 30;
    private int mStrokeWidth1 = 3;
    private int mStrokeWidth2 = 5;
    private int mLength1 = 10;
    private int mLength2 = 20;
    private int mCenterX;
    private int mCenterY;
    private int mRadius;
    private int mTextSize = 40;
    private int mSpace = 10;

    public ClockBackground1(Context context) {
        super(context);
    }

    public ClockBackground1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockBackground1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private void init() {
        mPaint1 = new Paint();
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setStrokeWidth(mStrokeWidth1);
        mPaint2 = new Paint();
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setStrokeWidth(mStrokeWidth2);
        mPaint3 = new Paint();
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setTextSize(mTextSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if ((bottom - top - getPaddingTop() - getPaddingBottom())
                > (right - left - getPaddingLeft() - getPaddingRight())) {
            throw new IllegalStateException("It can't be (height - paddingTop - paddingBottom) > (widht - paddingLeft - paddingRight).");
        }
        mRadius = (bottom - top - getPaddingTop() - getPaddingBottom()) / 2;
        mCenterX = right - mRadius - getPaddingRight();
        mCenterY = getPaddingTop() + mRadius;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int num = 12;
        for (int i = 0; i < num; i++) {
            double angle = i * spaceAngle * Math.PI / 180;
            float x1;
            float y1;
            float x2;
            float y2;
            float x3;
            float y3;
            x1 = mCenterX + (float) (Math.cos(angle) * mRadius);
            y1 = mCenterY + (float) (Math.sin(angle) * mRadius);
            if (i % (HOUR_FLAGS.length - 1) == 0) {
                x2 = mCenterX + (float) (Math.cos(angle) * (mRadius - mLength2));
                y2 = mCenterY + (float) (Math.sin(angle) * (mRadius - mLength2));
                canvas.drawLine(x1, y1, x2, y2, mPaint2);
                String hour = HOUR_FLAGS[i / (HOUR_FLAGS.length - 1)];

                float wordHeight = mPaint3.getFontMetrics().bottom - mPaint3.getFontMetrics().top;
                float wordHeightOffset = mPaint3.getFontMetrics().ascent + mPaint3.getFontMetrics().leading;
                if (Math.cos(angle) == 1L) {
                    x3 = mCenterX + (mRadius - mLength2) - mSpace - mPaint3.measureText(hour);
                    y3 = mCenterY - wordHeight / 2 - wordHeightOffset + mSpace / 2 ;
                } else if (Math.cos(angle) == -1L) {
                    x3 = mCenterX - ((mRadius - mLength2) - mSpace);
                    y3 = mCenterY - wordHeight / 2 - wordHeightOffset + mSpace / 2 ;
                } else if (Math.sin(angle) == 1L) {
                    x3 = mCenterX  - mPaint3.measureText(hour) / 2;
                    y3 = mCenterY + ((mRadius - mLength2) + mSpace / 2  - wordHeight - wordHeightOffset);
                } else {
                    x3 = mCenterX  - mPaint3.measureText(hour) / 2;
                    y3 = mCenterY - ((mRadius - mLength2) - mSpace / 2 +  wordHeightOffset);
                }

                canvas.drawText(hour, x3, y3, mPaint3);
            } else {
                x2 = mCenterX + (float) (Math.cos(angle) * (mRadius - mLength1));
                y2 = mCenterY + (float) (Math.sin(angle) * (mRadius - mLength1));
                canvas.drawLine(x1, y1, x2, y2, mPaint1);
            }

        }
    }
}
