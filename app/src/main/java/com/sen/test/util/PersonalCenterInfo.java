package com.sen.test.util;

import android.os.Environment;
import android.util.Log;


import com.readboy.encrypt.EncryptReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Properties;

/**
 * Created by Senny on 2015/10/14.
 */
public class PersonalCenterInfo {

    private static final String USERINFO_PATH = Environment.getExternalStorageDirectory().getPath() + "/.readboy/profile/userInfo.txt";
    private final static String TAG = "PersonalCenter";
    private final static String DEFAULT_UID = "00000000";

    public static String getUid() {

        String uid = readUserID();
        if(uid != null){
            if (8 >=  uid.length()) {
                uid = String.format("%08d", Long.valueOf(uid));
            } else {
                uid = DEFAULT_UID;
                Log.e(TAG, "Uid is not fotmat!");
            }
        }else{
            uid = DEFAULT_UID;
            Log.e(TAG, "Failed to get Info!");
        }
        return uid;
    }

    /**
     * 获取机子的序列号(机器码)
     *
     */
    public static String getserial(){
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            while(!(str.contains("Serial"))) {
                str = input.readLine();

                if(str.contains("Serial")){
                    str = str.replace("Serial", "");
                    macSerial = (str.replace(":", "")).trim();
                    break;
                }
            }

        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    private static String readUserID(){
        Properties props = new Properties();
        EncryptReader encryptReader = null;
        InputStreamReader inputStreamReader = null;

        try {
            encryptReader = new EncryptReader(USERINFO_PATH);
            inputStreamReader = new InputStreamReader(encryptReader, "gbk");
            props.load(inputStreamReader);

            /*User user = new User();
            user.setUid(Integer.parseInt(props.getProperty("uid")));
            user.setRealName(props.getProperty("realname"));
            user.setUserName(props.getProperty("username"));
            user.setImagePath(props.getProperty("imagePath"));
            user.setGrade(Integer.parseInt(props.getProperty("grade")));
            ConfigData.getInstance().setUser(user);*/
           return props.getProperty("uid");

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            props.clear();
            if (encryptReader != null) {
                encryptReader.close();
                if (inputStreamReader != null) {
                    try{
                        inputStreamReader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

}
