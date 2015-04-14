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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 15-4-13.
 */
public class JFragment extends BaseFragment{

    private String DOWNLOAD_URL = "http://gdown.baidu.com/data/wisegame/332f98a0e4c843c6/biyingcidian_4010.apk";

    private TextView textView;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Integer) {
                textView.setText(getActivity().getString(R.string.text_progrss)+(Integer)msg.obj+"");
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
            try {
                URL url = new URL(DOWNLOAD_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(1000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                int reponse = httpURLConnection.getResponseCode();
                inputStream = httpURLConnection.getInputStream();
                int length = inputStream.available();
                byte[] buffer = new byte[1024];
                /*while (inputStream.available() > 0) {
                    inputStream.read(buffer);
                    float percent = (length-inputStream.available()+0.0f)/length;
                    sendMessages(0, percent, 0);
                    Thread.currentThread().sleep(300);
                }*/
                sendMessages(0, length, 0);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }/* catch (InterruptedException e) {
                e.printStackTrace();
            } */finally {
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
