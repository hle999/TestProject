package com.sen.test.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.sen.test.util.RoundRectDrawableWithShadow;

/**
 * Editor: sgc
 * Date: 2015/03/13
 */
public class StrakeView extends View {

    public StrakeView(Context context) {
        super(context);

    }

    public StrakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StrakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        RoundRectDrawableWithShadow roundRectDrawableWithShadow =
                new RoundRectDrawableWithShadow(this.getResources(), Color.RED, 10.0f, 10.0f, 10.0f);
        setBackground(roundRectDrawableWithShadow);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
}
