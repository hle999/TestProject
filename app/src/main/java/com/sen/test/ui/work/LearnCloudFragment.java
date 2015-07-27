package com.sen.test.ui.work;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.ui.fragment.BaseFragment;

/**
 * Created by Sgc on 2015/7/24.
 */
public class LearnCloudFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learcloud, null);
        return view;
    }
}
