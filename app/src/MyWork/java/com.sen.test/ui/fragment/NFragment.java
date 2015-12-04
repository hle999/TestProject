package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * Created by Sgc on 2015/6/23.
 */
public class NFragment extends BaseFragment implements View.OnClickListener {

    private Thread mThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_n, null);
        view.findViewById(R.id.button_start).setOnClickListener(this);
        view.findViewById(R.id.button_stop).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_start:
                mThread = new TestThread();
                mThread.start();
                break;

            case R.id.button_stop:
                if (mThread != null) {
                    mThread.interrupt();
                }
                break;

        }
    }

    private class TestThread extends Thread {

        @Override
        public void run() {

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ttt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                OutputStream outputStream = new FileOutputStream(file);
                int i = 0;
                while (10000 > i) {
                    i++;
                    outputStream.write(1);
                    System.out.println("i: "+i);
                }
                outputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedIOException e) {
                e.printStackTrace();
                System.out.println("break...!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("finish...! "+isInterrupted());

        }
    }
}
