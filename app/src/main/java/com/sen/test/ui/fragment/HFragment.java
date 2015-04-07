package com.sen.test.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.QSK.helloble.DeviceListActivity;
import com.sen.test.ui.fragment.BaseFragment;

/**
 * Editor: sgc
 * Date: 2015/04/06
 */
public class HFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(), DeviceListActivity.class);
        getActivity().startActivity(intent);
        getFragmentManager().popBackStack();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
