package com.sen.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by Senny on 2015/8/20.
 */
public class TestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = Uri.parse("http://www.baidu.com");

//        Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
        Intent intent = getIntent();
        startNextMatchingActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
