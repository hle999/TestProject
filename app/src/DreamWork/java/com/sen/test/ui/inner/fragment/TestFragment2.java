package com.sen.test.ui.inner.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;

/**
 * Created by Senny on 2015/12/2.
 */
public class TestFragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wave_pattern, null);

        return root;
    }

}
