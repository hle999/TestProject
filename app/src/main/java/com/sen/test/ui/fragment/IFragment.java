package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.test.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Black_Horse on 2015/4/11.
 */
public class IFragment extends BaseFragment {

    private static final int SEND_SUCCESS = 0x01;

    private TextView textView;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            textView.setText("success");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_i, null);
        textView = (TextView)view.findViewById(R.id.textview_result);
        new SendSocket().start();


        return view;
    }

    class SendSocket extends Thread {

        @Override
        public void run() {
            OutputStream outputStream = null;
            Socket socket = null;
            try {
                InetAddress ip = InetAddress.getByName("192.168.1.100");
                socket = new Socket(ip, 2015);
                outputStream = socket.getOutputStream();
                outputStream.write(new String("hello").getBytes());
                handler.sendEmptyMessage(0);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
