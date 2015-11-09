package com.sen.test.ui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.lib.graphics.TransitionalColor;
import com.sen.lib.support.CustomViewPager;
import com.sen.lib.view.BaseItemAdapter;
import com.sen.lib.view.HorizontalItemTab;
import com.sen.lib.view.ItemTabAdapter;
import com.sen.lib.view.VerticalScrollWidget;
import com.sen.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-4-20.
 */
public class KFragment extends BaseFragment implements AFragment.FragmentClick{

    private TestPageAdapter testPageAdapter;
    private HorizontalItemTab horizontalScrollView;
    private VerticalScrollWidget verticalScrollWidget;
    private CustomViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_k, null);
        viewPager = (CustomViewPager)view.findViewById(R.id.scroll_viewpager);
        testPageAdapter = new TestPageAdapter(getFragmentManager());
        viewPager.setAdapter(testPageAdapter);
        viewPager.setPageTransformer(true, new TestPageTransformer());
        if (view.findViewById(R.id.scroll_items) instanceof HorizontalItemTab) {
            initHorizonScroll(view);
        } else {

            initVerticalScroll(view);
        }
        return view;
    }

    private int testItem = -1;

    @Override
    public void onClick() {
        testItem = 2;
        /*if (testPageAdapter.getCount() == 25) {

            testPageAdapter.setCount(testPageAdapter.getCount() - 7);
        } else {
            testPageAdapter.setCount(testPageAdapter.getCount() + 7);
        }
        testPageAdapter.notifyDataSetChanged();
        int lastCurrenItem = viewPager.getCurrentItem();
//        viewPager.setAdapter(testPageAdapter);
        if (lastCurrenItem >= testPageAdapter.getCount()) {
            viewPager.setCurrentItem(testPageAdapter.getCount() - 1, false);
        } else {
            viewPager.setCurrentItem(lastCurrenItem, false);
        }*/

        if (horizontalScrollView != null) {
            horizontalScrollView.getAdpater().notifyDataChange();
        } else if (verticalScrollWidget != null) {
            verticalScrollWidget.getAdpater().notifyDataChange();
        }
    }

    private void initVerticalScroll(View view) {
        verticalScrollWidget = (VerticalScrollWidget)view.findViewById(R.id.scroll_items);
        verticalScrollWidget.setAdapter(new VerticalAdapter());
    }

    private void initHorizonScroll(View view) {

        horizontalScrollView = (HorizontalItemTab) view.findViewById(R.id.scroll_items);
//        horizontalScrollView.getItemGroup().setBackgroundColor(Color.BLUE);
        horizontalScrollView.setAdapter(new HorizontalAdapter());
        viewPager.addPagerChangeListener(horizontalScrollView);

    }

    class TestPageAdapter extends FragmentStatePagerAdapter {

        private int count = 10;

        public void setCount(int count) {
            this.count = count;
        }

        public TestPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            AFragment fragment = new AFragment();
            fragment.setClick(KFragment.this);
            return fragment;
        }

        @Override
        public int getCount() {
            return count;
        }
    }

    class Text extends TextView {

        public Text(Context context) {
            super(context);
        }

        public Text(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public Text(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }
    }

    private class VerticalAdapter extends BaseItemAdapter {
        @Override
        public View getView(View v, ViewGroup container, int postion) {
            if (v == null) {
                v = new Text(container.getContext());
                ((TextView) v).setTextColor(Color.BLACK);
            }
            ((TextView) v).setText(" TheNihgt " + postion);
            ((TextView) v).setTextSize(50);
            return v;
        }

        @Override
        public int getCount() {
            return testPageAdapter.getCount();
        }
    };

    private class HorizontalAdapter extends ItemTabAdapter {

        @Override
        public void onScroll(int position, float positionOffset) {
            View currentView = horizontalScrollView.getItemView(position);
            if (currentView != null) {
                int lf = (int) (0xf * (1 - positionOffset));
                int hf = lf;
                hf = hf << 4;
                int f = hf + lf;
                int color = Color.rgb(f, f, f);
                ((TextView) currentView).setTextColor(color);
                View nextView = horizontalScrollView.getItemView(position + 1);
                if (nextView != null) {
                    int lf1 = (int) (0xf * positionOffset);
                    int hf1 = lf1;
                    hf1 = hf1 << 4;
                    int f1 = hf1 + lf1;
                    int color1 = Color.rgb(f1, f1, f1);
                    ((TextView) nextView).setTextColor(color1);
                }
            }
        }

        @Override
        public void onScrolledStateChange(int state) {

        }

        @Override
        public View getView(View v, ViewGroup container, final int postion) {
            if (v == null) {
                v = new Text(container.getContext());

//                    v.setBackgroundColor(Color.BLUE);
            }
            if (postion != horizontalScrollView.getSelectItemIndex()) {
                ((TextView) v).setTextColor(Color.BLACK);
            } else {
                ((TextView) v).setTextColor(Color.WHITE);
            }
            if (postion == testItem) {
                ((TextView) v).setText(" Thiiihgt " + postion);
                ((TextView) v).setTextSize(60);
            } else {

                ((TextView) v).setText(" TheNihgt " + postion);
                ((TextView) v).setTextSize(50);
            }
                /*if (container.indexOfChild(v) != selectIndex) {
                    ((TextView) v).setTextColor(Color.BLACK);
                } else {
                    ((TextView) v).setTextColor(Color.BLUE);
                }*/
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(postion, true);
                }
            });
            return v;
        }

        @Override
        public int getCount() {
            return testPageAdapter.getCount();
        }
    };

    class TestPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            System.out.println("Page: "+page+" "+position);
        }
    }
}
