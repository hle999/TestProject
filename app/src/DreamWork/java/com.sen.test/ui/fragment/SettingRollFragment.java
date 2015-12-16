package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.provider.Settings;
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
 * Created by Senny on 2015/12/7.
 */
public class SettingRollFragment extends Fragment {

    private String[] ACTION_ARRAY = new String[]{
            Settings.ACTION_WIFI_SETTINGS,
            Settings.ACTION_BLUETOOTH_SETTINGS,
            Settings.ACTION_DATE_SETTINGS,
            Settings.ACTION_USAGE_ACCESS_SETTINGS,
            Settings.ACTION_INTERNAL_STORAGE_SETTINGS};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_roll, null);
        VerticalViewPager verticalViewPager = (VerticalViewPager) view.findViewById(R.id.fragment_setting_roll_id);
        verticalViewPager.setAdapter(new RollPagerAdapter(getFragmentManager()));
        verticalViewPager.setPageMargin(-200);
        verticalViewPager.setOffscreenPageLimit(3);
        verticalViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {

            float MAX_TRANSLATE_X = -80;

            @Override
            public void transformPage(View page, float position) {
                if (position == 0.0f) {
                    page.setScaleX(1.0f);
                    page.setScaleY(1.0f);
                    page.setTranslationX(MAX_TRANSLATE_X);
                } else if (position > 0.0f) {
                    float ration = 1.0f - position;
                    float translateX = MAX_TRANSLATE_X * ration;
                    page.setScaleX(ration);
                    page.setScaleY(ration);
                    page.setTranslationX(translateX);
                } else if (0.0f > position) {
                    float ration = Math.abs(1.0f + position);
                    float translateX = MAX_TRANSLATE_X * ration;
                    page.setScaleX(ration);
                    page.setScaleY(ration);
                    page.setTranslationX(translateX);
                }
            }
        });
        return view;
    }

    private class RollPagerAdapter extends FragmentStatePagerAdapter {

        public RollPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SettingItemFragment itemFragment = new SettingItemFragment();
            itemFragment.setTitle(ACTION_ARRAY[position]);
            return itemFragment;
        }

        @Override
        public int getCount() {
            return ACTION_ARRAY.length;
        }
    }

}
