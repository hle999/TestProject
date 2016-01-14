package com.sen.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.droidwolf.nativesubprocess.Subprocess;
import com.sen.test.subprocess.WatchDog;

/**
 * Created by Senny on 2015/12/23.
 */
public class TestService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Subprocess.create(this, WatchDog.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*Intent intented = new Intent("android.readboy.parentmanager.BROADCAST_INPUT_PASSWORD");
        intented.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intented);*/
        return super.onStartCommand(intent, flags, startId);
    }

}
