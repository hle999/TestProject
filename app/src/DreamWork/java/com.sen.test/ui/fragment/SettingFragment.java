package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.ui.view.VerticalViewPager;

/**
 * Created by Senny on 2015/11/25.
 */
public class SettingFragment extends Fragment {

    private VerticalViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, null);
        viewPager = (VerticalViewPager) root.findViewById(R.id.fragment_setting_viewpager);
        viewPager.setPageMargin(-200);
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            float DEFAULT_SCALE = 1.0f;
            float SCALE_FACTOR = 0.30f;// 缩放因子 0.50f
            float ROTATION_FACTOR = 20f;// 旋转因子
            float ALPHA_FACTOR = 0.8f;

            @Override
            public void transformPage(View view, float position) {
                if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    if (position < 0) {
                        // view.setRotationY(position * ROTATION_FACTOR);
                        view.setScaleX(SCALE_FACTOR * position + DEFAULT_SCALE);
                        view.setScaleY(SCALE_FACTOR * position + DEFAULT_SCALE);
                        // view.setAlpha(ALPHA_FACTOR * position + 1.0f);
                    } else {
                        // view.setRotationY(position * ROTATION_FACTOR);
                        view.setScaleX(SCALE_FACTOR * -position + DEFAULT_SCALE);
                        view.setScaleY(SCALE_FACTOR * -position + DEFAULT_SCALE);
                        // view.setAlpha(ALPHA_FACTOR * -position + 1.0f);
                    }
                }
            }
        });
        viewPager.setAdapter(new MyPagerAdapter(getFragmentManager()));
        return root;
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
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
