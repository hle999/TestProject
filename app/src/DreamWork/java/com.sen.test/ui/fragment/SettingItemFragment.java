package com.sen.test.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.test.R;

/**
 * Created by Senny on 2015/12/7.
 */
public class SettingItemFragment extends Fragment {

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_item, null);
        TextView textView = (TextView) view.findViewById(R.id.fragment_setting_item_title);
        textView.setText(title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(title);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

}
