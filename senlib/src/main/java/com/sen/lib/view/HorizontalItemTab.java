package com.sen.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 15-4-20.
 */
public class HorizontalItemTab extends HorizontalScrollView implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int DIRECTION_RIGHT = 0x01;
    private static final int DIRECTION_LEFT = 0x02;

    private int tabHeight = 3;
    private int selectItemIndex = 0;
    private float positionPercent = 0;
    private int direction = DIRECTION_RIGHT;

    private Paint tabPaint = null;

    public int getSelectItemIndex() {
        return selectItemIndex;
    }

    private void setSelectItemIndex(int selectItemIndex) {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (itemTabAdpater != null && viewGroup != null && viewGroup.getChildCount() > selectItemIndex) {
            itemTabAdpater.getView(viewGroup.getChildAt(getSelectItemIndex()), viewGroup, getSelectItemIndex(), selectItemIndex);
            itemTabAdpater.getView(viewGroup.getChildAt(selectItemIndex), viewGroup, selectItemIndex, selectItemIndex);
            this.selectItemIndex = selectItemIndex;
        }
    }

    private ItemTabAdpater itemTabAdpater;

    public ItemTabAdpater getAdpater() {
        return itemTabAdpater;
    }

    public void setAdpater(ItemTabAdpater itemTabAdpater) {
        this.itemTabAdpater = itemTabAdpater;
        reset();
    }

    public HorizontalItemTab(Context context) {
        super(context);
    }

    public HorizontalItemTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public HorizontalItemTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFillViewport(true);
        setWillNotDraw(false);

        tabPaint = new Paint();
        tabPaint.setColor(Color.BLUE);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(linearLayout, layoutParams);

    }

    public void disableTouchScroll() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    protected void reset() {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup != null) {
            if (itemTabAdpater != null) {
                itemTabAdpater.setRoot(this);
                if (getTag() != itemTabAdpater.toString()) {
                    for (int i = 0; i < itemTabAdpater.getCount(); i++) {
                        addItem(itemTabAdpater.getView(null, viewGroup, i, 0));
                    }
                    setTag(itemTabAdpater.toString());
                } else {
                    for (int i = 0; i < itemTabAdpater.getCount(); i++) {
                        View childView = viewGroup.getChildAt(i);
                        if (childView != null) {
                            itemTabAdpater.getView(childView, viewGroup, i, getSelectItemIndex());
                        } else {
                            addItem(itemTabAdpater.getView(childView, viewGroup, i, getSelectItemIndex()));
                        }
                    }
                }
            }
        }
    }

    private void addItem(View item) {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup != null && item != null) {
            viewGroup.addView(item);
        }
    }

    public void clearAllItems() {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
    }

    public void resetTab(float positionPercent, int itemIndex) {
        if (positionPercent != 0) {
            if (this.positionPercent > positionPercent) {
                direction = DIRECTION_LEFT;
                itemIndex++;
            } else if (this.positionPercent < positionPercent) {
                direction = DIRECTION_RIGHT;
            }
        }
        ViewGroup viewGroup = (ViewGroup)getChildAt(0);
        if (viewGroup != null && itemIndex > 0 && (viewGroup.getChildCount()-2) > itemIndex) {
            float tempPositionPercent = positionPercent;
            if (positionPercent == 0 && direction == DIRECTION_RIGHT) {
                tempPositionPercent = 1;
            } else if (this.positionPercent == 0 && direction == DIRECTION_LEFT) {
                tempPositionPercent = 1;
            }
            View currentItem = viewGroup.getChildAt(itemIndex);
            View nextItem = viewGroup.getChildAt(itemIndex);
            float currentLeft = currentItem.getLeft();
            float nextLeft = nextItem.getRight();
            scrollBy((int) ((nextLeft - currentLeft) * (tempPositionPercent - this.positionPercent)), 0);
        }
        this.positionPercent = positionPercent;
        if (getSelectItemIndex() != itemIndex) {
            setSelectItemIndex(itemIndex);
        }
        invalidate();

    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof Integer) {
            setSelectItemIndex(indexOfChild(v));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup != null && viewGroup.getChildCount() > selectItemIndex) {
            View currentItem = viewGroup.getChildAt(selectItemIndex);
            float currentLeft = currentItem.getLeft();
            float currentRight = currentItem.getRight();
            if (direction == DIRECTION_RIGHT) {
                if (viewGroup.getChildCount() > (selectItemIndex + 1)) {
                    View nextItem = viewGroup.getChildAt(selectItemIndex + 1);
                    float nextLeft = nextItem.getLeft();
                    float nextRight = nextItem.getRight();
                    currentLeft = currentLeft + (nextLeft - currentLeft) * positionPercent;
                    currentRight = currentRight + (nextRight - currentRight) * positionPercent;
                }
            } else if (direction == DIRECTION_LEFT) {
                if ((selectItemIndex - 1) > 0 && positionPercent != 0) {
                    View nextItem = viewGroup.getChildAt(selectItemIndex - 1);
                    float nextLeft = nextItem.getLeft();
                    float nextRight = nextItem.getRight();
                    currentLeft = currentLeft + (nextLeft - currentLeft) * (1 - positionPercent);
                    currentRight = currentRight + (nextRight - currentRight) * (1 - positionPercent);
                }
            }
            canvas.drawRect(currentLeft, getHeight() - tabHeight, currentRight, getHeight(), tabPaint);

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        resetTab(positionOffset, position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
