package com.sen.test.ui.work;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sgc on 2015/7/2.
 */
public class MenuChooseFragment extends BaseFragment implements View.OnClickListener {

    private final static int LOAD_MENU = 0x012;
    private final static int SEND_CHOOSE = 0x013;
    private final static int WORK_FAID = 0x014;
    private final static int SHOW_VOTES = 0x015;
    private final static int GET_VOTES = 0x016;

    private final static String APP_CLOUD_API = "https://d.apicloud.com/mcm/api/";
    private final static String APP_CLOUD_ID = "A6980187936080";
    private final static String APP_CLOUD_KEY = "1244C998-AD33-5B52-93E9-2FF562DE279E";
    private final static String APP_CLOUD_MENU = "menu";
    private final static String APP_CLOUD_VOTE = "vote";

    private ApiHandler apiHandler;
    private ViewGroup vgMenu;
    private View progress;
    private ExecutorService pool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_choose, null);
        vgMenu = (ViewGroup) view.findViewById(R.id.linearv_content);
        progress = view.findViewById(R.id.linearv_progress);
        apiHandler = new ApiHandler(this);
        pool = Executors.newSingleThreadExecutor();
        pool.execute(new WorkerThread(LOAD_MENU, null, null, null, null));
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof String) {
            pool.execute(new WorkerThread(GET_VOTES, null, (String)v.getTag(), null, null));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pool != null) {
            pool.shutdown();
        }
    }

    private static class ApiHandler extends BaseHandler<MenuChooseFragment> {

        public ApiHandler(MenuChooseFragment menuChooseFragment) {
            super(menuChooseFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            Gson gson = new Gson();
            MenuChooseFragment menuChooseFragment = getReference().get();

            switch (msg.what) {

                case LOAD_MENU:
                    if (msg.obj instanceof String) {
                        String str = (String) msg.obj;
                        List<MenuInfo> menuInfos = gson.fromJson(str, new TypeToken<List<MenuInfo>>() {
                        }.getType());
                        int num = 1;
                        for (MenuInfo menuInfo : menuInfos) {
                            MenuItem menuItem = menuChooseFragment.new MenuItem(menuChooseFragment.getActivity());
                            menuItem.reset(menuChooseFragment.getString(R.string.text_package) + num, menuInfo.content, menuInfo.id);
                            menuItem.setPadding(50, 10, 50, 10);
                            menuItem.setOnClickListener(menuChooseFragment);
                            menuChooseFragment.vgMenu.addView(menuItem);
                            num++;
                        }
//                        menuChooseFragment.progress.setVisibility(View.GONE);
                        menuChooseFragment.pool.execute(menuChooseFragment.new WorkerThread(SHOW_VOTES, null, null, null, null));
                    }
                    break;

                case SEND_CHOOSE:
                    if (msg.obj instanceof Vote) {
                        Vote sendVote = (Vote)msg.obj;
                        if (sendVote != null) {
                            sendVote.votes++;
                            menuChooseFragment.pool.execute(menuChooseFragment.new WorkerThread(SEND_CHOOSE, sendVote.id, null, gson.toJson(sendVote), "{\"votes\":\""+sendVote.votes+"\"}"));
                        }
                    }
                    menuChooseFragment.progress.setVisibility(View.VISIBLE);
                    break;

                case SHOW_VOTES:
                    if (msg.obj instanceof String) {
                        String str = (String) msg.obj;
                        List<Vote> votes = gson.fromJson(str, new TypeToken<List<Vote>>(){}.getType());
                        for (Vote vote : votes) {
                            for (int i = 0;i <menuChooseFragment.vgMenu.getChildCount();i++) {
                                MenuItem menuItem = (MenuItem)menuChooseFragment.vgMenu.getChildAt(i);
                                if (vote.type.contentEquals((String)menuItem.getTag())) {
                                    menuItem.setTicks(vote.votes);
                                }
                            }
                        }
                    }
                    menuChooseFragment.progress.setVisibility(View.GONE);
                    break;

                case GET_VOTES:
                    if (msg.obj instanceof String) {
                        String type = msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY);
                        if (type != null) {
                            String str = (String) msg.obj;
                            List<Vote> voteList = gson.fromJson(str, new TypeToken<List<Vote>>(){}.getType());
                            Vote sendVote = null;
                            for (Vote vote : voteList) {
                                if (vote.type.contentEquals(type)) {
                                    sendVote = vote;
                                }
                            }
                            if (sendVote == null) {
                                sendVote = menuChooseFragment.new Vote();
                                sendVote.type = type;
                                sendVote.votes = 0;
                            }
                            sendMessages(SEND_CHOOSE, sendVote, 0, 0, 0);
                        }
                    }
                    break;

                case WORK_FAID:
                    menuChooseFragment.progress.setVisibility(View.GONE);
                    Toast.makeText(menuChooseFragment.getActivity(), R.string.text_tip_action_failed, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

    private class WorkerThread extends Thread {


        int what = LOAD_MENU;
        String type;
        String json;
        String obj;
        String votes;

        WorkerThread(int what, String obj, String type, String json, String votes) {
            this.what = what;
            this.obj = obj;
            this.type = type;
            this.json = json;
            this.votes = votes;
        }

        @Override
        public void run() {

            switch (what) {

                case LOAD_MENU:
                    byte[] menuData = connect("GET", APP_CLOUD_API + APP_CLOUD_MENU, null);
                    if (menuData != null) {
                        String str = new String(menuData);
                        apiHandler.sendMessages(LOAD_MENU, str, 0, 0, 0);
                    } else {
                        apiHandler.sendMessages(WORK_FAID, null, 0, 0, 0);
                    }
                    break;

                case SEND_CHOOSE:
//                    connect(APP_CLOUD_API+APP_CLOUD_MENU, null);
                    if (apiHandler != null) {
                        if (obj != null) {
                            connect("PUT", APP_CLOUD_API + APP_CLOUD_VOTE + "/" + obj , null);
                        } else {
                            connect("POST", APP_CLOUD_API + APP_CLOUD_VOTE , json);
                        }
                        votes = null;
                        byte[] voteData = connect("GET", APP_CLOUD_API + APP_CLOUD_VOTE, null);
                        if (voteData != null) {
                            String str = new String(voteData);
                            apiHandler.sendMessages(SHOW_VOTES, str, 0, 0, 0);
                        } else {
                            apiHandler.sendMessages(WORK_FAID, null, 0, 0, 0);
                        }
                    }

                    break;

                case SHOW_VOTES:
                    if (apiHandler != null) {
                        byte[] voteData = connect("GET", APP_CLOUD_API + APP_CLOUD_VOTE, null);
                        if (voteData != null) {
                            String str = new String(voteData);
                            apiHandler.sendMessages(SHOW_VOTES, str, 0, 0, 0);
                        } else {
                            apiHandler.sendMessages(WORK_FAID, null, 0, 0, 0);
                        }
                    }
                    break;

                case GET_VOTES:
                    if (apiHandler != null && type != null) {
                        byte[] voteData = connect("GET", APP_CLOUD_API + APP_CLOUD_VOTE, null);
                        if (voteData != null) {
                            String str = new String(voteData);
                            apiHandler.sendMessages(GET_VOTES, type, str, 0, 0, 0);
                        } else {
                            apiHandler.sendMessages(WORK_FAID, null, 0, 0, 0);
                        }
                    }
                    break;

            }

        }

        String getAppKey(long nowSystemMillis) {
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

        byte[] connect(String requestMethod, String url, String json) {
            URL uRL = null;
            try {
                uRL = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (uRL != null) {
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) uRL.openConnection();
                    httpURLConnection.setRequestMethod(requestMethod);
                    httpURLConnection.setRequestProperty("X-APICloud-AppId", APP_CLOUD_ID);
                    httpURLConnection.setRequestProperty("X-APICloud-AppKey", getAppKey(System.currentTimeMillis()));
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.connect();
                    if (what == SEND_CHOOSE) {
                        if (json != null) {
                            DataOutputStream printout = new DataOutputStream(httpURLConnection.getOutputStream());
                            printout.write(json.getBytes());
                            printout.flush();
                            printout.close();
                        } else if (votes != null) {
                            DataOutputStream printout = new DataOutputStream(httpURLConnection.getOutputStream());
                            printout.write(votes.getBytes());
                            printout.flush();
                            printout.close();
                        }
                    }
                    if (httpURLConnection.getResponseCode() == 200) {
                        int length = httpURLConnection.getContentLength();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] data = new byte[length];
                        inputStream.read(data);
                        inputStream.close();
                        return data;
                    } else {
                        System.out.println("Error Message: " + httpURLConnection.getResponseMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class MenuInfo {
        String id;
        String content;
    }

    private class Vote {
        String id;
        String type;
        int votes;
    }

    private class MenuItem extends LinearLayout {

        public MenuItem(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
        }

        public MenuItem(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void reset(final String title, final String content, final String type) {
            setTag(type);
            post(new Runnable() {
                @Override
                public void run() {
                    if (title != null && content != null && type != null) {
                        TextView tvTitle = new TextView(getContext());
                        tvTitle.setGravity(Gravity.CENTER);
                        tvTitle.setText(title);
                        tvTitle.setTextSize(50);
                        tvTitle.setTextColor(Color.BLACK);
                        tvTitle.setBackgroundColor(Color.RED);
                        TextView tvContent = new TextView(getContext());
                        tvContent.setText(content);
                        tvContent.setTextSize(30);
                        tvContent.setTextColor(Color.BLACK);
                        TextView tvTicks = new TextView(getContext());
                        tvTicks.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvTicks.setId(100);
                        tvTicks.setTextSize(20);
                        addView(tvTitle);
                        addView(tvContent);
                        addView(tvTicks);
                    }
                }
            });
        }

        public void setTicks(int num) {
            TextView tvTicks = (TextView)findViewById(100);
            tvTicks.setText(num + getString(R.string.text_tick));

        }
    }
}
