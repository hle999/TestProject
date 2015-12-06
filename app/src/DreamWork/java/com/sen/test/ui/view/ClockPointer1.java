package com.sen.test.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * Created by Senny on 2015/11/24.
 */
public class ClockPointer1 extends View {

    private int SPACE_HOUR_ANGLE = 30;
    private int SPACE_MINUTE_ANGLE = 360 / 60;
    private float SPACE_HOUR_OFFSET = (30 + 0.0f) / 60;
    private Calendar calendar;
    private int hour;
    private int minute;
    private int mRadius;
    private int mCenterX;
    private int mCenterY;
    private int MINUTE_DESCENT = 50;
    private int HOUR_DESCENT = 80;
    private int mHourWidth = 5;
    private int mMinuteWidth = 3;
    private Paint mHourPaint;
    private Paint mMinutePaint;

    public ClockPointer1(Context context) {
        super(context);
    }

    public ClockPointer1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockPointer1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void invalidateTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        invalidate();
    }

    private void init() {
        calendar = Calendar.getInstance();
        mHourPaint = new Paint();
        mMinutePaint = new Paint();
        mHourPaint.setStrokeWidth(mHourWidth);
        mMinutePaint.setStrokeWidth(mMinuteWidth);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

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
        computeTimez();
        float hourX;
        float hourY;
        float minuteX;
        float minuteY;
        hourX = mCenterX + (float) (Math.cos(Math.PI * (hour * SPACE_HOUR_ANGLE + minute * SPACE_HOUR_OFFSET - 90) / 180) * (mRadius - HOUR_DESCENT));
        hourY = mCenterY + (float) (Math.sin(Math.PI * (hour * SPACE_HOUR_ANGLE + minute * SPACE_HOUR_OFFSET - 90) / 180) * (mRadius - HOUR_DESCENT));
        minuteX = mCenterX + (float) (Math.cos(Math.PI * (minute * SPACE_MINUTE_ANGLE - 90) / 180) * (mRadius - MINUTE_DESCENT));
        minuteY = mCenterY + (float) (Math.sin(Math.PI * (minute * SPACE_MINUTE_ANGLE - 90) / 180) * (mRadius - MINUTE_DESCENT));
        canvas.drawLine(mCenterX, mCenterY, hourX, hourY, mHourPaint);
        canvas.drawLine(mCenterX, mCenterY, minuteX, minuteY, mMinutePaint);

    }

    private void computeTimez() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 12) {
            hour -= 12;
        }
        minute = calendar.get(Calendar.MINUTE);
        postInvalidateDelayed(60 * 1000);

    }
}
