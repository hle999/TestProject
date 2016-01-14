package com.sen.test.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.sen.test.R;
import com.sen.test.TestService;
import com.sen.test.ui.inner.fragment.TestFragment1;

/**
 * Created by Senny on 2015/12/6.
 */
public class FlingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fling, null);
        /*ListView listView = (ListView) view.findViewById(R.id.fragment_fling_listview);
        listView.setAdapter(new MyAdapter());*/
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.test_viewpager);
        viewPager.setAdapter(new MyFragmentAdapter(getFragmentManager()));
        view.findViewById(R.id.fling_test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Uri uri = Uri.parse("content://com.readboy.parentmanager.recordprovider/read_user_info");
                ContentResolver contentResolver = getActivity().getContentResolver();
                try {
                    Cursor cursor = contentResolver.query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String password = cursor.getString(1);
                    } else {
                        //fail
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }*/
                Intent intent = new Intent(getActivity(), TestService.class);
                getActivity().startService(intent);
            }
        });
        return view;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = new Button(parent.getContext());
            return convertView;
        }
    }

    private class MyFragmentAdapter extends FragmentStatePagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = instantiate(getActivity(), TestFragment1.class.getName());
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
