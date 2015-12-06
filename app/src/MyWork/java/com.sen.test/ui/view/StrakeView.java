package com.sen.test.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ProgressBar;

import com.sen.test.R;
import com.sen.test.util.RoundRectDrawableWithShadow;

/**
 * Editor: sgc
 * Date: 2015/03/13
 */
public class StrakeView extends View{

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

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
        ((Activity)getContext()).registerForContextMenu(this);
        ProgressBar progressBar = new ProgressBar(getContext());
        System.out.println("progressBar: "+progressBar.getMax());
    }

    @Override
    protected void onDetachedFromWindow() {
        ((Activity)getContext()).unregisterForContextMenu(this);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        System.out.println("View context menu!");
        ((Activity)getContext()).getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateContextMenu(menu);
    }
}
