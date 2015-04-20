package com.sen.test.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.lib.support.CustomViewPager;
import com.sen.lib.view.ItemTabAdpater;
import com.sen.test.R;
import com.sen.lib.view.HorizontalItemTab;

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
            public View getView(View v, ViewGroup container, int postion, int selectIndex) {
                if (v == null) {
                    v = new TextView(container.getContext());
                    ((TextView) v).setText(" TheNihgt ");
                }
                if (container.indexOfChild(v) != selectIndex) {
                    ((TextView) v).setTextColor(Color.BLACK);
                } else {
                    ((TextView) v).setTextColor(Color.BLUE);
                }
                return v;
            }

            @Override
            public int getCount() {
                return 50;
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
            return 10;
        }
    }
}
