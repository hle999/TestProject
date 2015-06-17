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

import java.io.IOException;
import java.io.InputStream;

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

        /*try {
            String yourShellInput = "busybox am force-stop com.sen.test";  // or whatever ...
            String[] commandAndArgs = new String[]{ "/bin/sh", "-c", yourShellInput };
            Process process = Runtime.getRuntime().exec(yourShellInput);
            InputStream inputStream = process.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            System.out.println("printss: " + new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
