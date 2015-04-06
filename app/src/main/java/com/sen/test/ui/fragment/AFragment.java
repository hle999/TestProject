package com.sen.test.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.sen.test.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Editor: sgc
 * Date: 2015/01/30
 */
public class AFragment extends Fragment implements View.OnClickListener{

    private static final int COPY_SUCCESS = 0x01;
    private static final int COPY_FAILED_UNKNOW = 0x02;
    private static final int COPY_FAILED_SOURCE_NOT_EXIST = 0x03;

    private ProgressBar progressBar;
    private TextView tip;
    private Button button;

    private Toast toast;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            switch (msg.what) {

                case COPY_SUCCESS:
                    String path = (String)msg.obj;
                    if (path != null) {
                        tip.setText(getActivity().getString(R.string.tip_copy_success)
                                +"\n"+getActivity().getString(R.string.tip_file_path)+path);
                    } else {
                        tip.setText(R.string.tip_copy_success);
                    }
                    tip.setTextColor(Color.GREEN);
                     break;

                case COPY_FAILED_UNKNOW:
                    tip.setText(R.string.tip_copy_fail_unkown);
                    tip.setTextColor(Color.RED);
                     break;

                case COPY_FAILED_SOURCE_NOT_EXIST:
                    tip.setText(R.string.tip_copy_fail_source_not_exist);
                    tip.setTextColor(Color.RED);
                     break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_a, null);
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        tip = (TextView)view.findViewById(R.id.tip);
        button = (Button)view.findViewById(R.id.start);
        button.setOnClickListener(this);

        toast = new Toast(getActivity());

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.start:
                progressBar.setVisibility(View.VISIBLE);
                tip.setVisibility(View.VISIBLE);
                tip.setText(R.string.tip_copying);
                tip.setTextColor(Color.BLACK);
                v.setVisibility(View.GONE);
                ActionThread actionThread = new ActionThread();
                actionThread.start();

                 break;

        }

    }

    private void sendMessages(int what, Object obj, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        if (handler != null) {
            handler.sendMessageDelayed(msg, delayMillis);
        }
    }

    private class ActionThread extends Thread {

        @Override
        public void run() {
            File sourceFile = new File("/data/data/com.readboy.parentmanager/databases/task_records.db");
            File targetFile = new File("/mnt/sdcard/家长管理数据/task_records.db");
            if (!sourceFile.exists()) {
                sendMessages(COPY_FAILED_SOURCE_NOT_EXIST, null, 0);
                return;
            }
            targetFile.mkdirs();
            if (targetFile.exists()) {
                targetFile.delete();
            }
            FileInputStream fileInputStream=null;
            FileOutputStream fileOutputStream=null;
            try {
                fileInputStream = new FileInputStream(sourceFile);
                fileOutputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                fileOutputStream.write(buffer);
                sendMessages(COPY_SUCCESS, targetFile.getAbsoluteFile(), 0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                sendMessages(COPY_FAILED_UNKNOW, null, 0);
            } catch (IOException e) {
                e.printStackTrace();
                sendMessages(COPY_FAILED_UNKNOW, null, 0);
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
