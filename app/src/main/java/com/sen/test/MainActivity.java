package com.sen.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.inject.Inject;
import com.sen.test.ui.fragment.MainFragment;
import com.sen.test.util.ShellUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import cn.jpush.android.api.JPushInterface;

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

        JPushInterface.init(getApplicationContext());
        /*try {
            *//*String yourShellInput = "busybox am force-stop com.sen.test";  // or whatever ...
            String[] commandAndArgs = new String[]{ "/bin/sh", "-c", yourShellInput };*//*
            String yourShellInput = "rm /system/app/ParentControl.apk";  // or whatever ...
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

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}

