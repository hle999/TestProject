package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sen.test.R;

import roboguice.RoboGuice;


/**
 * Editor: sgc
 * Date: 2015/03/12
 */
public class BaseFragment extends Fragment {

    public static void startFragment(FragmentManager fragmentManager, Fragment fragment, String tag) {
        FragmentTransaction bt = fragmentManager.beginTransaction();
        bt.add(R.id.content, fragment, tag);
        bt.commit();
    }

    public static void startFragmentWithCanBack(FragmentManager fragmentManager, Fragment fragment, String tag) {
        FragmentTransaction bt = fragmentManager.beginTransaction();
        bt.addToBackStack(null);
        bt.add(R.id.content, fragment, tag);
        bt.commit();
    }

    public static void startAnimationFragmentWithCanBack(FragmentManager fragmentManager, Fragment fragment, String tag) {
        FragmentTransaction bt = fragmentManager.beginTransaction();
        bt.addToBackStack(null);
        bt.setCustomAnimations(android.R.anim.fade_in, 0, 0, android.R.anim.fade_out);
        bt.add(R.id.content, fragment, tag);
        bt.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }

}
