package com.sen.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.inject.Inject;
import com.sen.test.ui.fragment.BaseFragment;
import com.sen.test.ui.fragment.MainFragment;

/**
 * Created by Administrator on 14-12-12.
 */
public class MainActivity extends BaseActivity {

    @Inject
    private Context context;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_main);

        Fragment fragment = new MainFragment();
        FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
//        bt.addToBackStack(null);
        bt.add(R.id.content, fragment);
        bt.commit();

        System.out.println("Activity Print: " + context);
    }

}
