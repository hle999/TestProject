package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.ui.view.VerticalViewPager;

/**
 * Created by Senny on 2015/11/23.
 */
public class DeskClockFragment extends Fragment {

    private VerticalViewPager verticalViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deskclock, null);
        verticalViewPager = (VerticalViewPager) root.findViewById(R.id.fragment_desk_clock_viewpager);
        verticalViewPager.setAdapter(new MyPagerAdater(getFragmentManager()));
        verticalViewPager.post(new Runnable() {
            @Override
            public void run() {
                verticalViewPager.getAdapter().notifyDataSetChanged();
            }
        });
        return root;
    }
    private class MyPagerAdater extends FragmentStatePagerAdapter {

        public MyPagerAdater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ClockFragment clockFragment = new ClockFragment();
            clockFragment.setScale(1.0f, 1.0f);
            return clockFragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

}
