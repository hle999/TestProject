package com.sen.test;

import android.app.Application;

//import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Sgc on 2015/7/24.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        AVOSCloud.initialize(this, "q8ffciommjuuntv7in1qvpwt0d6wpkdhydnovhh1syv21t0l", "v4l3avzqrs4eag643yxcv7gmp8lc25kzhgw2itioovowalbf");

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }
}
