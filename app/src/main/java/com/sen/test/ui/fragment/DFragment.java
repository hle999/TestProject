package com.sen.test.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sen.test.R;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Editor: sgc
 * Date: 2015/03/09
 */
public class DFragment extends Fragment implements View.OnClickListener{

    private static final int WIFI_ENABLING = 0x11;
    private static final int WIFI_ENABLED= 0x12;
    private static final int WIFI_DISABLING = 0x13;
    private static final int WIFI_DISABLED = 0x14;
    private static final int WIFI_UNKNOWN = 0x15;

    private static final int GPRS_ENABLING = 0x21;
    private static final int GPRS_ENABLED= 0x22;
    private static final int GPRS_DISABLING = 0x23;
    private static final int GPRS_DISABLED = 0x24;
    private static final int GPRS_UNKNOWN = 0x25;

    private static final int REFRASH_HISTORY_URI = 0x35;

    private TextView websiteHistoryText;
    private TextView wifiTextView;
    private TextView gprsTextView;
    private WifiManager wifiManager;
    private ScanWifiState scanWifiState;
    private ConnectivityManager connectivityManager;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case WIFI_ENABLING:
                    wifiTextView.setText(R.string.text_wifi_enabling);
                     break;

                case WIFI_ENABLED:
                    wifiTextView.setText(R.string.text_wifi_enabled);
                     break;

                case WIFI_DISABLING:
                    wifiTextView.setText(R.string.text_wifi_disabling);
                     break;

                case WIFI_DISABLED:
                    wifiTextView.setText(R.string.text_wifi_disabled);
                     break;

                case WIFI_UNKNOWN:
                    wifiTextView.setText(R.string.text_wifi_unknown);
                     break;

                case GPRS_ENABLING:
                    gprsTextView.setText(R.string.text_gprs_enabling);
                    break;

                case GPRS_ENABLED:
                    gprsTextView.setText(R.string.text_gprs_enabled);
                    break;

                case GPRS_DISABLING:
                    gprsTextView.setText(R.string.text_gprs_disabling);
                    break;

                case GPRS_DISABLED:
                    gprsTextView.setText(R.string.text_gprs_disabled);
                    break;

                case GPRS_UNKNOWN:
                    gprsTextView.setText(R.string.text_gprs_unknown);
                    break;

                case REFRASH_HISTORY_URI:
//                    Cursor cursor = Browser.getAllVisitedUrls(getActivity().getContentResolver());
                    String str = "";
//                    Context onlineStudyContext = null;
//                    try {
//                        onlineStudyContext = getActivity().createPackageContext("com.readboy.online_study", Context.CONTEXT_IGNORE_SECURITY);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    PackageInfo packageInfo = null;
//                    try {
//                        packageInfo = getActivity().getPackageManager().getPackageInfo("com.readboy.online_study", PackageManager.GET_PROVIDERS);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    ContentResolver contentResolver = onlineStudyContext.getContentResolver();
//                    Cursor cursor = contentResolver.query(Browser.BOOKMARKS_URI, null, Browser.BookmarkColumns.DATE+" >? ", new String[]{RecorderDate.getATodayStartTime(System.currentTimeMillis())+""}, null);
////                    Cursor cursor = contentResolver.query(WebSettings., null, null, null, null);
//                    if (cursor != null) {
//                        if (!cursor.moveToFirst()) {
//                            cursor.close();
//                        } else {
//                            try {
//                                do {
//                            /*for (int i=0;i<cursor.getColumnCount();i++) {
////                                if (cursor.getString(i) != null) {
////                                    str += cursor.getString(i)+"\n";
////                                }
//                                str += cursor.getColumnName(i)+"\n";
////                                System.out.println("DFragment: "+cursor.getString(i));
//                            }*/
//                                    if (cursor.getString(0) != null) {
//                                        str += cursor.getString(0)+"\n";
//                                    }
//                                } while (cursor.moveToNext());
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                            } finally {
//                                cursor.close();
//                            }
//                        }
//
//                    }
                    if (getActivity() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.readboy.com"));
                        List<ResolveInfo> resolveInfoList = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for(ResolveInfo resolveInfo:resolveInfoList) {
                            String label = resolveInfo.activityInfo.loadLabel(getActivity().getPackageManager())+"";
                            str += label + "\n";
                        }

                        websiteHistoryText.setText(str);
                    }
                    break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, null);
        wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        view.findViewById(R.id.button_wifi_connect).setOnClickListener(this);
        view.findViewById(R.id.button_wifi_disconnect).setOnClickListener(this);
        view.findViewById(R.id.button_gprs_connect).setOnClickListener(this);
        view.findViewById(R.id.button_gprs_disconnect).setOnClickListener(this);
        websiteHistoryText = (TextView)view.findViewById(R.id.website_history);
        wifiTextView = (TextView)view.findViewById(R.id.wifi_state);
        gprsTextView = (TextView)view.findViewById(R.id.gprs_state);
        scanWifiState = new ScanWifiState(wifiManager, connectivityManager);
        scanWifiState.start();
        /*ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(Browser.SEARCHES_URI, null, null, null, null);*/

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scanWifiState != null) {
            scanWifiState.setStop(true);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_wifi_connect:
                 if (wifiManager != null) {
                     wifiManager.setWifiEnabled(true);
                     wifiManager.reconnect();
                 }
                 break;

            case R.id.button_wifi_disconnect:
                 if (wifiManager != null) {
                     wifiManager.setWifiEnabled(false);
                 }
                 break;

            case R.id.button_gprs_connect:
                 openMobileNetwork();
                 break;

            case R.id.button_gprs_disconnect:
                 closeMobileNetWork();
                 break;
        }

    }

    private void openMobileNetwork() {
        setMobileNetwork(true);
    }

    private void closeMobileNetWork() {
        setMobileNetwork(false);
    }

    private void setMobileNetwork(boolean open) {
        Object[] arg = null;
        try {
            boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);
            if(open && !isMobileDataEnable){
                invokeBooleanArgMethod("setMobileDataEnabled", open);
            } else if (!open && isMobileDataEnable) {
                invokeBooleanArgMethod("setMobileDataEnabled", open);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean invokeMethod(String methodName,
                                Object[]  arg) throws Exception {


        Class ownerClass = connectivityManager.getClass();

        Class[]  argsClass = null;
        if (arg != null) {
            argsClass = new Class[1];
            argsClass[0] = arg.getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        Boolean isOpen = (Boolean) method.invoke(connectivityManager, arg);

        return isOpen;
    }

    public Object invokeBooleanArgMethod(String methodName,
                                         boolean value) throws Exception {


        Class ownerClass = connectivityManager.getClass();

        Class[]  argsClass = new Class[1];
        argsClass[0] = boolean.class;

        Method method = ownerClass.getMethod(methodName,argsClass);

        return method.invoke(connectivityManager, value);
    }

    private void sendMessages(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    private class ScanWifiState extends Thread {

        private boolean isStop = false;
        private WifiManager scanWifiManager;
        private ConnectivityManager scanConnectivityManager;

        public boolean isStop() {
            return isStop;
        }

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        public ScanWifiState(WifiManager scanWifiManager, ConnectivityManager scanConnectivityManager) {
            this.scanWifiManager = scanWifiManager;
            this.scanConnectivityManager = scanConnectivityManager;
        }

        @Override
        public void run() {

            while (!isStop) {
                scan();
                try {
                    Thread.currentThread().sleep(500);
                    sendMessages(REFRASH_HISTORY_URI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }




        }

        private void scan() {
            /*if (scanWifiManager != null) {

                switch (scanWifiManager.getWifiState()) {

                    case WifiManager.WIFI_STATE_ENABLING:
                        sendMessages(WIFI_ENABLING);
                        break;

                    case WifiManager.WIFI_STATE_ENABLED:
                        sendMessages(WIFI_ENABLED);
                        break;

                    case WifiManager.WIFI_STATE_DISABLING:
                        sendMessages(WIFI_DISABLING);
                        break;

                    case WifiManager.WIFI_STATE_DISABLED:
                        sendMessages(WIFI_DISABLED);
                        break;

                    case WifiManager.WIFI_STATE_UNKNOWN:
                        sendMessages(WIFI_UNKNOWN);
                        break;

                }

            }*/
            if (connectivityManager != null) {
                NetworkInfo.State gprsState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                NetworkInfo.State wifiState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if (gprsState == NetworkInfo.State.CONNECTING) {
                    sendMessages(GPRS_ENABLING);
                } else if (gprsState == NetworkInfo.State.CONNECTED) {
                    sendMessages(GPRS_ENABLED);
                } else if (gprsState == NetworkInfo.State.DISCONNECTING) {
                    sendMessages(GPRS_DISABLING);
                } else if (gprsState == NetworkInfo.State.DISCONNECTED) {
                    sendMessages(GPRS_DISABLED);
                } else if (gprsState == NetworkInfo.State.UNKNOWN) {
                    sendMessages(GPRS_UNKNOWN);
                }
                if (wifiState == NetworkInfo.State.CONNECTING) {
                    sendMessages(WIFI_ENABLING);
                } else if (wifiState == NetworkInfo.State.CONNECTED) {
                    sendMessages(WIFI_ENABLED);
                } else if (wifiState == NetworkInfo.State.DISCONNECTING) {
                    sendMessages(WIFI_DISABLING);
                } else if (wifiState == NetworkInfo.State.DISCONNECTED) {
                    sendMessages(WIFI_DISABLED);
                } else if (wifiState == NetworkInfo.State.UNKNOWN) {
                    sendMessages(WIFI_UNKNOWN);
                }
            }

        }
    }
}
