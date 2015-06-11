package com.sen.test.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Sen on 2015/6/3.
 */
public class LocalView extends View {

    private boolean isLocal = false;

    public LocalView(Context context) {
        super(context);
    }

    public LocalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void invalidateLocal() {
        isLocal = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        if (isLocal) {

            paint.setColor(Color.YELLOW);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
            canvas.save();
        }
        isLocal = false;
    }
}
