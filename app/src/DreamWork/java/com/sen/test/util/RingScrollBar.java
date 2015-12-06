package com.sen.test.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by Senny on 2015/11/12.
 */
public class RingScrollBar extends Drawable{

    private int verticalScrollRange;
    private int height;
    private int ringWidth = 6;
    private int circleRadius = 2;
    private float verticalRangeRatio = 0.0f;
    private float startAngle = -40.0f;
    private float sweepAngle = 80.0f;
    private float startRingAngle;
    private float sweepRingAngle;
    private RectF ringRect;
    private Paint mRingPaint;
    private Paint mCirclePaint;
    private Paint testPaint;
    private int mAlpha;
    private int mScrollY;

    public RingScrollBar() {
        mRingPaint = new Paint();
        mRingPaint.setStrokeWidth(ringWidth);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);
        mCirclePaint = new Paint();
        mCirclePaint.setStrokeWidth(circleRadius);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        testPaint = new Paint();
        testPaint.setColor(Color.RED);
    }



    public void setLayout(int l, int t, int r, int b) {
        height = b - t;
        if (ringRect == null) {
            ringRect = new RectF(r - height + 0.0f, t + 0.0f, r + 0.0f, b + 0.0f);
        } else {
            ringRect.set(r - height + 0.0f, t + 0.0f, r + 0.0f, b + 0.0f);
        }
        ringRect.offset(getBounds().right - r, 0);
    }

    private void compute(int scrollY) {
        verticalRangeRatio = (getBounds().bottom - getBounds().top + 0.0f) / (ringRect.bottom - ringRect.top)/*verticalScrollRange*/;
        sweepRingAngle = sweepAngle * verticalRangeRatio;
        float ratioY = (scrollY + 0.0f) / (verticalScrollRange - height);
        startRingAngle = startAngle + (sweepAngle - sweepRingAngle) * ratioY;
    }

    @Override
    public void draw(Canvas canvas) {
        compute(mScrollY);
        int count = canvas.save();
        canvas.translate(0, mScrollY);
        mRingPaint.setAlpha(getAlpha());
        canvas.drawArc(ringRect, startRingAngle, sweepRingAngle, false, mRingPaint);
        mCirclePaint.setAlpha(getAlpha());
        float radius = (height + 0.0f) / 2;
        float x1 = ringRect.left + radius + (float) (Math.cos(Math.PI * startRingAngle / 180) * radius/*(radius - circleRadius / 2)*/) ;
        float y1 = ringRect.top + radius + (float) (Math.sin(Math.PI * startRingAngle / 180) * radius/*(radius - circleRadius / 2)*/);
        float x2 = ringRect.left + radius + (float) (Math.cos(Math.PI * (startRingAngle + sweepRingAngle) / 180) * radius/*(radius - circleRadius / 2)*/) ;
        float y2 = ringRect.top + radius + (float) (Math.sin(Math.PI * (startRingAngle + sweepRingAngle) / 180) * radius/*(radius - circleRadius / 2)*/);
        RectF rectF1 = new RectF(x1 - ringWidth / 2, y1 - ringWidth / 2, x1 + ringWidth / 2, y1 + ringWidth / 2);
        canvas.drawArc(rectF1, startRingAngle, -180, true, mCirclePaint);
        rectF1.set(x2 - ringWidth / 2, y2 - ringWidth / 2, x2 + ringWidth / 2, y2 + ringWidth / 2);
        canvas.drawArc(rectF1, startRingAngle + sweepRingAngle, 180, true, mCirclePaint);
        canvas.restoreToCount(count);
    }

    @Override
    public void setAlpha(int mAlpha) {
        this.mAlpha = mAlpha;
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void setRingAngle(float startAngle, float sweepAngle) {
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
    }

    public void setVerticalScrollRange(int verticalScrollRange, int mSrollY) {
        this.verticalScrollRange = verticalScrollRange;
        this.mScrollY = mSrollY;
    }

}
