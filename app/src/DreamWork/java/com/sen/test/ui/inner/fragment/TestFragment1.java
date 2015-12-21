package com.sen.test.ui.inner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.sen.test.R;

/**
 * Created by Senny on 2015/12/2.
 */
public class TestFragment1 extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_test, null);
        ListView listView = (ListView) root.findViewById(R.id.fragment_fling_listview);
        listView.setAdapter(new MyAdapter());
        return root;
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

}
