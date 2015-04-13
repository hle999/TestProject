package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private String DOWNLOAD_URL = "http://www.baidu.com/link?url=6DUHFi_dnSLw-GK06U5nSrURlAw07PTc94aLoq92O-uQCeJyvHPKiK28cZDGkEQXh0LsngMI2kJgkuS3dfgmW3dO01dflQtA9EuPE5Ozt4kqZ_y4u8Nd1f6w1Ch0d1iT";

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), "Reponse is "+(Integer)msg.obj, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_j, null);
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
                sendMessages(0, reponse, 0);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
