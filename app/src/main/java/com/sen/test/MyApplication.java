package com.sen.test;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by Sgc on 2015/7/24.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "q8ffciommjuuntv7in1qvpwt0d6wpkdhydnovhh1syv21t0l", "v4l3avzqrs4eag643yxcv7gmp8lc25kzhgw2itioovowalbf");
    }
}
