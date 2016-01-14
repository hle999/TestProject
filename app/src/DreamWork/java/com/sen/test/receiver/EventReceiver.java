package com.sen.test.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Senny on 2016/1/6.
 */
public class EventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null
                && intent.getAction().contentEquals("com.readboy.parentmanager.TIME_PICK")) {
            System.out.println("EventReceiver: TIME_PICK");
        }
        System.out.println("EventReceiver.....2");

    }

}
