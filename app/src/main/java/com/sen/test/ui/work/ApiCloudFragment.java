package com.sen.test.ui.work;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sen.test.R;
import com.sen.test.dictionary.utils.BaseHandler;
import com.sen.test.ui.fragment.BaseFragment;
import com.sen.test.util.AeSimpleSHA1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sgc on 2015/6/30.
 */
public class ApiCloudFragment extends BaseFragment implements View.OnClickListener{

    private final static int SEND_SUCCESS = 0x012;

    private final static String MENU_URL = "https://d.apicloud.com/mcm/api/Menu";
    private final static String APP_CLOUD_ID = "A6980187936080";
    private final static String APP_CLOUD_KEY = "1244C998-AD33-5B52-93E9-2FF562DE279E";

    private ApiHandler apiHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_api_cloud, null);
        view.findViewById(R.id.button_start).setOnClickListener(this);
        apiHandler = new ApiHandler(this);
        return view;
    }

    public static String getAppKey(long nowSystemMillis) {
        String appKey = null;
        try {
            appKey = AeSimpleSHA1.SHA1(APP_CLOUD_ID + "UZ" + APP_CLOUD_KEY + "UZ" + nowSystemMillis) + "." + nowSystemMillis;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return appKey;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_start:
                new ApiWorker().start();
                break;
        }

    }

    private static class ApiHandler extends BaseHandler<ApiCloudFragment> {

        public ApiHandler(ApiCloudFragment apiCloudFragment) {
            super(apiCloudFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case SEND_SUCCESS:
                    ApiCloudFragment apiCloudFragment = getReference().get();
                    ((TextView)apiCloudFragment.getView().findViewById(R.id.text_show)).setText("OK");
                    break;

            }
        }
    }

    private class ApiWorker extends Thread {

        @Override
        public void run() {
            URL uRL = null;
            try {
                uRL = new URL(MENU_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (uRL != null) {
                try {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("content", ((TextView)ApiCloudFragment.this.getView().findViewById(R.id.text_show)).getText()+"");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) uRL.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("X-APICloud-AppId", APP_CLOUD_ID);
                    httpURLConnection.setRequestProperty("X-APICloud-AppKey", getAppKey(System.currentTimeMillis()));
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
//                    httpURLConnection.setDoInput(true);
//                    httpURLConnection.setDoOutput(true);
//                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.connect();
                    DataOutputStream printout = new DataOutputStream(httpURLConnection.getOutputStream());
                    printout.write(jsonObject.toString().getBytes());
                    printout.flush();
                    printout.close();
                    if (httpURLConnection.getResponseCode() == 200) {
                        /*int length = httpURLConnection.getContentLength();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] data = new byte[length];
                        inputStream.read(data);
                        inputStream.close();*/
                        apiHandler.sendEmptyMessage(SEND_SUCCESS);
//                        httpURLConnection.
                    } else {
                        System.out.println("Error Message: "+httpURLConnection.getResponseMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
