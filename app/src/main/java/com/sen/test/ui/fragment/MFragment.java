package com.sen.test.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.inject.Inject;
import com.sen.test.R;
import com.sen.test.ui.view.LocalView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;


/**
 * Created by Sen on 2015/5/29.
 */
@EFragment(R.layout.fragment_m)
public class MFragment extends BaseFragment {


    @Inject
    private Context context;

    @Click(R.id.fragment_m_localview)
    public void click(View v) {
        ((FrameLayout)v).invalidate(100, 100, 200, 200);
        ((LocalView)((FrameLayout) v).getChildAt(0)).invalidateLocal();
    }

}
