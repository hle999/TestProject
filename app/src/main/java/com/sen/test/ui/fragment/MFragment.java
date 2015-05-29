package com.sen.test.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.sen.test.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;


/**
 * Created by Sen on 2015/5/29.
 */
@EFragment(R.layout.fragment_m)
public class MFragment extends BaseFragment {


    @Inject
    private Context context;

    @Click(R.id.fragment_m_text)
    public void click(View v) {
        System.out.println("Print: "+context);
    }

}
