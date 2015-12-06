package com.sen.test.ui.inner.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.activity.CustomPreferenceActivity;
import com.sen.test.ui.view.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/12/4.
 */
public class PreferenceHeadersFragment extends Fragment implements ItemFragment.OnItemFragmentListener {

    private List<PreferenceActivity.Header> data;
    private CustomPreferenceActivity customPreferenceActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prefernce_headers, null);
        VerticalViewPager verticalViewPager = (VerticalViewPager) root.findViewById(R.id.fragment_preference_headers_viewpager);
        customPreferenceActivity = (CustomPreferenceActivity) getActivity();
        data = customPreferenceActivity.getHeaders();
        verticalViewPager.setAdapter(new VerticalPagerAdapter());
        return root;
    }

    @Override
    public void onClick(PreferenceActivity.Header header) {
        Fragment fragment = Fragment.instantiate(getActivity(), header.fragment, header.extras);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(":android:prefs");
        ft.replace(getId(), fragment);
        ft.commitAllowingStateLoss();
    }

    private class VerticalPagerAdapter extends PagerAdapter {

        private List<View> viewList;

        VerticalPagerAdapter() {
            viewList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            if (position >= viewList.size()) {
                ItemFragment itemFragment = new ItemFragment();
                itemFragment.setHeader(data.get(position));
                itemFragment.setOnItemFragmentListener(PreferenceHeadersFragment.this);
                view = itemFragment.onCreateView(LayoutInflater.from(container.getContext()), container, null);
                viewList.add(view);
            } else if (viewList.get(position) == null) {
                ItemFragment itemFragment = new ItemFragment();
                itemFragment.setHeader(data.get(position));
                itemFragment.setOnItemFragmentListener(PreferenceHeadersFragment.this);
                view = itemFragment.onCreateView(LayoutInflater.from(container.getContext()), container, null);
                viewList.set(position, view);
            } else {
                view = viewList.get(position);
            }
            if (container.indexOfChild(view) == -1) {
                container.addView(view);
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            /*if (position == viewList.indexOf(object)) {
                container.removeView(viewList.get(position));
            }*/
        }
    }

}
