package com.sen.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.sen.test.ui.fragment.MainFragment;

/**
 * Created by Administrator on 14-12-12.
 */
public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_main);

        Fragment fragment = new MainFragment();
        FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
//        bt.addToBackStack(null);
        bt.add(R.id.content, fragment);
        bt.commit();

        String str = "d";
        byte[] bytes = str.getBytes();
        System.out.println("count: "+bytes.length);
        for (byte b:bytes) {
            System.out.println("b: "+b+" "+Integer.toHexString(b & 0xFF) +" "+ Integer.toHexString((b & 0xFF) | 0x100)  + " " +
                    Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3) + " " + Integer.toHexString((b & 0xFF) | 0x100).length());
        }
    }

}
