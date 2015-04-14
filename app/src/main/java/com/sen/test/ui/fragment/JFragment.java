package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sen.test.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 15-4-13.
 */
public class JFragment extends BaseFragment{

    private static final int LOAD_FAILDED = 1;

    private String DOWNLOAD_URL = "http://gdown.baidu.com/data/wisegame/332f98a0e4c843c6/biyingcidian_4010.apk";

    private TextView textView;

    private String filePath = "/mnt/sdcard/bing.apk";

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1) {
                if (msg.obj instanceof Float) {
                    textView.setText(getActivity().getString(R.string.text_progrss)+(Float)msg.obj+"");
                }
            } else {
                textView.setText("Time is out");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_j, null);
        textView = (TextView)view.findViewById(R.id.textview_percent);
        new DownLoadTread().start();
        return view;
    }

    private void sendMessages(int what, Object object,int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = object;
        if (handler != null) {
            handler.sendMessageDelayed(msg, delayMillis);
        }
    }

    class DownLoadTread extends Thread {

        @Override
        public void run() {
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);

                URL url = new URL(DOWNLOAD_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(1000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection .setRequestProperty("Accept-Encoding", "identity");
                httpURLConnection.connect();
                int reponse = httpURLConnection.getResponseCode();
                if (reponse == 200) {
                    byte[] buffer = new byte[1024*10];
                    inputStream = httpURLConnection.getInputStream();
                    int length = httpURLConnection.getContentLength()/*inputStream.available()*/;
                    int progress = 0;
                    int num = 0;
                    while ((num = inputStream.read(buffer)) > -1){
                        progress += num;
                        fileOutputStream.write(buffer, 0, num);
                        float percent = (progress+0.0f)/length;
                        sendMessages(0, percent, 0);
                        Thread.currentThread().sleep(50);
                    }
                } else {
                    sendMessages(LOAD_FAILDED, null, 0);
                }
//                sendMessages(0, length, 0);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
