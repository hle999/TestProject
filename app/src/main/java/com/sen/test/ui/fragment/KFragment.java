package com.sen.test.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.lib.support.CustomViewPager;
import com.sen.lib.view.HorizontalItemTab;
import com.sen.lib.view.ItemTabAdpater;
import com.sen.test.R;

/**
 * Created by Administrator on 15-4-20.
 */
public class KFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_k, null);
        CustomViewPager viewPager = (CustomViewPager)view.findViewById(R.id.scroll_viewpager);
        final HorizontalItemTab horizontalScrollView = (HorizontalItemTab)view.findViewById(R.id.scroll_items);
        horizontalScrollView.setAdpater(new ItemTabAdpater() {

            @Override
            public void onScroll(ViewGroup container, int position, float positionOffset) {
                if (container != null) {
                    View currentView = container.getChildAt(position);
                    int lf = (int)(0xf*(1-positionOffset));
                    int hf = lf;
                    hf = hf << 4;
                    int f = hf + lf;
                    int color = Color.rgb(f, f, f);
                    ((TextView)currentView).setTextColor(color);
                    if (container.getChildCount() > position+1) {
                        View nextView = container.getChildAt(position+1);
                        int lf1 = (int)(0xf*positionOffset);
                        int hf1 = lf1;
                        hf1 = hf1 << 4;
                        int f1 = hf1 + lf1;
                        int color1 = Color.rgb(f1, f1, f1);
                        ((TextView)nextView).setTextColor(color1);
                    }
                }
            }

            @Override
            public View getView(View v, ViewGroup container, int postion, int selectIndex) {
                if (v == null) {
                    v = new TextView(container.getContext());
                    ((TextView) v).setText(" TheNihgt "+postion);
                    ((TextView) v).setTextSize(50);
                    v.setBackgroundColor(Color.BLUE);
                }
                /*if (container.indexOfChild(v) != selectIndex) {
                    ((TextView) v).setTextColor(Color.BLACK);
                } else {
                    ((TextView) v).setTextColor(Color.BLUE);
                }*/
                return v;
            }

            @Override
            public int getCount() {
                return 20;
            }
        });
        viewPager.addPagerChangeListener(horizontalScrollView);
        viewPager.setAdapter(new TestPageAdapter(getFragmentManager()));
        return view;
    }

    class TestPageAdapter extends FragmentStatePagerAdapter {

        public TestPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new AFragment();
        }

        @Override
        public int getCount() {
            return 20;
        }
    }
}
