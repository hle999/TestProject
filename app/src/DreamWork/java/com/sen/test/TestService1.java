package com.sen.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.droidwolf.nativesubprocess.Subprocess;
import com.sen.test.subprocess.WatchDog;

/**
 * Created by Senny on 2016/1/6.
 */
public class TestService1 extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, TestService.class));
        Subprocess.create(this, WatchDog.class);
    }
}
