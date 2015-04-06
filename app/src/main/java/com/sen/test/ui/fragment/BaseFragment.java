package com.sen.test.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.sen.test.R;


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

}
