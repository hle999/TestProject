package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sen.test.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 15-4-13.
 */
public class JFragment extends BaseFragment implements View.OnClickListener {

    private static final int LOAD_ING = 0x01;
    private static final int LOAD_SUCCESS = 0x02;
    private static final int LOAD_FAILDED = 0x03;
    private static final int HAS_DOWNLOAD = 0x04;
    private RequestQueue requestQueue;

    private String DOWNLOAD_URL = "http://gdown.baidu.com/data/wisegame/332f98a0e4c843c6/biyingcidian_4010.apk";

    private TextView textView;
    private Button retry;

    private String dirPath = "/mnt/sdcard/";
    private String fileName = "HttpDownloadtest.apk.temp";

    private int OFFSET = 3;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case LOAD_ING:
                    if (msg.obj != null && msg.obj instanceof Float) {
                        textView.setText(getActivity().getString(R.string.text_progrss) + (Float) msg.obj + "");
                    }
                    break;
                case LOAD_SUCCESS:
                    textView.setText("Download success!");
                    retry.setVisibility(View.GONE);
                    break;
                case LOAD_FAILDED:
                    textView.setText("Download failed!");
                    retry.setVisibility(View.VISIBLE);
                    break;
                case HAS_DOWNLOAD:
                    textView.setText("Download has exsist!");
                    break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_j, null);
        textView = (TextView) view.findViewById(R.id.textview_percent);
        retry = (Button) view.findViewById(R.id.button_retry);
        retry.setOnClickListener(this);
//        new DownLoadTread().start();
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(new StringRequest(Request.Method.POST, "http://www.baidu.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("Success............");
                System.out.println(s);
                System.out.println("End............");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println(volleyError.getMessage());
            }
        }));

        return view;
    }

    private void sendMessages(int what, Object object, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = object;
        if (handler != null) {
            handler.sendMessageDelayed(msg, delayMillis);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_retry:
                new DownLoadTread().start();
                retry.setVisibility(View.INVISIBLE);
                break;
        }
    }

    class DownLoadTread extends Thread {

        @Override
        public void run() {
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            try {
                /*Date date = new Date();
                date.setTime(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd.HHmmss");
                fileName = simpleDateFormat.format(date)+".apk.temp";*/
                File tempFile = new File(dirPath + "/" + fileName);
                File apkFile = new File(tempFile.getAbsolutePath().replace(".temp", ""));
                if (tempFile.exists() && tempFile.length() > OFFSET) {
                    if (breakPoint(tempFile, (int) tempFile.length() - 3)) {
                        return;
                    }
                } else if (apkFile.exists()) {
                    sendMessages(HAS_DOWNLOAD, null, 0);
                    return;
                }

                URL url = new URL(DOWNLOAD_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(1000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                int reponse = httpURLConnection.getResponseCode();
                if (reponse == 200) {
                    byte[] buffer = new byte[1024 * 10];
                    inputStream = httpURLConnection.getInputStream();
                    int length = httpURLConnection.getContentLength()/*inputStream.available()*/;
                    int progress = 0;
                    int num = 0;
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    tempFile.createNewFile();
                    fileOutputStream = new FileOutputStream(tempFile);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    while ((num = inputStream.read(buffer)) > -1) {
                        progress += num;
                        bufferedOutputStream.write(buffer, 0, num);
                        float percent = (progress + 0.0f) / length;
                        sendMessages(LOAD_ING, percent, 0);
                        Thread.currentThread().sleep(50);
                    }
                    tempFile.renameTo(new File(tempFile.getAbsolutePath().replace(".temp", "")));
                    sendMessages(LOAD_SUCCESS, null, 0);
                } else {
                    sendMessages(LOAD_FAILDED, null, 0);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                sendMessages(LOAD_FAILDED, null, 0);
            } catch (IOException e) {
                e.printStackTrace();
                sendMessages(LOAD_FAILDED, null, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
                sendMessages(LOAD_FAILDED, null, 0);
            } finally {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
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

        private boolean breakPoint(File outFile, int range) {
            HttpURLConnection httpURLConnection = null;
            InputStream internetInputStream = null;
            /*FileOutputStream fileOutputStream = null;
            BufferedOutputStream bufferedOutputStream = null;*/
            RandomAccessFile randomAccessFile = null;
            try {
                URL url = new URL(DOWNLOAD_URL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(1000);
                httpURLConnection.setConnectTimeout(1000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                httpURLConnection.setRequestProperty("Range", "bytes=" + range + "-");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                int reponse = httpURLConnection.getResponseCode();
                System.out.println("BreakPoint......0: "+reponse);
                if (reponse == 206) {
                    byte[] buffer = new byte[1024 * 10];
                    int totalLength = httpURLConnection.getContentLength();
                    internetInputStream = httpURLConnection.getInputStream();
                    byte[] vertification = new byte[OFFSET];
                    FileInputStream fileInputStream = new FileInputStream(outFile);
                    fileInputStream.skip(range);
                    fileInputStream.read(vertification);
                    fileInputStream.close();
                    internetInputStream.read(buffer, 0, OFFSET);
                    System.out.println("BreakPoint......1: "+vertification[0]+" "+vertification[1]+" "+vertification[2]+" "+buffer[0]+" "+buffer[1]+" "+buffer[2]);
                    if (!(vertification[0] == buffer[0] && vertification[1] == buffer[1] && vertification[2] == buffer[2])) {
                        System.out.println("BreakPoint......2 ");
                        return false;
                    }
                    int progress = (int)outFile.length();
                    int num = 0;
                    totalLength += progress;
                    /*fileOutputStream = new FileOutputStream(outFile);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bufferedOutputStream.write(buffer, progress, 0);*/
                    randomAccessFile = new RandomAccessFile(outFile.getAbsoluteFile(), "rw");
                    randomAccessFile.seek(progress);
                    while ((num = internetInputStream.read(buffer)) > 0) {
                        progress += num;
                        randomAccessFile.write(buffer, 0, num);
                        float percent = (progress + 0.0f) / totalLength;
                        sendMessages(LOAD_ING, percent, 0);
                        Thread.currentThread().sleep(50);
                    }
                    outFile.renameTo(new File(outFile.getAbsolutePath().replace(".temp", "")));
                    sendMessages(LOAD_SUCCESS, null, 0);
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                /*try {
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (internetInputStream != null) {
                        internetInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return false;
        }
    }
}
