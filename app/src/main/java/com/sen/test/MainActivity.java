package com.sen.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.inject.Inject;
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

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
//        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        Fragment fragment = new MainFragment();
        FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
//        bt.addToBackStack(null);
        bt.add(R.id.content, fragment);
        bt.commit();

//        JPushInterface.init(getApplicationContext());
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
        //消息通知栏
        //定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        //定义通知栏展现的内容信息
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Hello World!";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);

        //定义下拉通知栏时要展现的内容信息
        Context context = getApplicationContext();
        CharSequence contentTitle = "TestProject";
        CharSequence contentText = "Hello World!";
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notify_item);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        notification.contentIntent = contentIntent;
//        contentView.setOnClickPendingIntent(R.id.notify_button_1, contentIntent);
//        notification.contentView = contentView;
        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(1, notification);
        /*Uri uri = Uri.parse("http://www.baidu.com");

        Intent intent = new  Intent("com.test.test");
        startActivity(intent);*/
        /*getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent("com.test.test");
                startActivity(intent);
            }
        }, 5000);*/

        /*try {
            Connection connection = DriverManager.getConnection("content://com.readboy.parentmanager.recordprovider");
            System.out.println("load PM SQL success");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("ParentMnagerREE " + requestCode + " " + resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(this);
    }


}

