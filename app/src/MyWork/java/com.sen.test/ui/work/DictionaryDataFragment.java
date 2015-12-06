package com.sen.test.ui.work;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.readboy.Dictionary.aidl.DictStrokeInfo;
import com.readboy.Dictionary.aidl.DictWordInfo;
import com.readboy.Dictionary.aidl.IDictDataAidl;
import com.readboy.sound.Sound;
import com.sen.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Editor: sgc
 * Date: 2015/03/31
 */
public class DictionaryDataFragment extends Fragment {

    private static final int LOAD_DATA_FINISH = 0x01;

    private EditText editText;
    private TextView explainView;
    private TextView textView;
    private Button button;
    private Button bitHuaButton;
    private Spinner spinner;

    private Connection connection;

    private int[] DICT_IDS = {0,1,2,3,4,5,6,7,8,9,10};
    private int currentDict = 0;

    private List<String> labelStrs;

    private int currentSerialNumber = 0;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case LOAD_DATA_FINISH:

                    List<DictWordInfo> data = (List<DictWordInfo>)msg.obj;
                    if (editText != null && data != null) {
                        currentSerialNumber = 0;
                        for (DictWordInfo dictWordInfo:data) {
//                                explainView.setText(dictWorldInfo.word);
                            if (dictWordInfo.explains!= null) {
                                explainView.setText(dictWordInfo.explains);
                            }
                            byte[] buffer = dictWordInfo.worldSound;
                            if (buffer != null) {
                                button.setTag(buffer);
                                button.setVisibility(View.VISIBLE);
                            } else {
                                button.setTag(null);
                                button.setVisibility(View.GONE);
                            }
                            bitHuaButton.setTag(dictWordInfo.word);
                        }
                    }
                     break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        labelStrs = new ArrayList<>();
        labelStrs.add(getString(R.string.dict_bihua));
        labelStrs.add(getString(R.string.dict_chengyu));
        labelStrs.add(getString(R.string.dict_langwen));
        labelStrs.add(getString(R.string.dict_dongman));
        labelStrs.add(getString(R.string.dict_gdhanyu));
        labelStrs.add(getString(R.string.dict_syhanying));
        labelStrs.add(getString(R.string.dict_xdhanyu));
        labelStrs.add(getString(R.string.dict_jianming));
        labelStrs.add(getString(R.string.dict_xshanying));
        labelStrs.add(getString(R.string.dict_syyinghan));
        labelStrs.add(getString(R.string.dict_syyingying));


        View view = inflater.inflate(R.layout.fragment_dictionary_data, null);
        editText = (EditText)view.findViewById(R.id.edittext_input);
        explainView = (TextView)view.findViewById(R.id.textview_explain);
        button = (Button)view.findViewById(R.id.button_play);
        bitHuaButton = (Button)view.findViewById(R.id.button_bihua);
        spinner = (Spinner)view.findViewById(R.id.spinner_dict);
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.textview_item, labelStrs));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (DICT_IDS.length >= position) {
                    currentDict = DICT_IDS[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        connection = new Connection();

        try {
            Intent intent = new Intent("com.readboy.Dictionary.AIDL_DATA");
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                new LoadData(s.toString()).start();
            }
        });

        final Sound mSound = new Sound();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = (byte[])v.getTag();
                if (bytes != null) {

                    mSound.setDataSource(bytes);
                    mSound.start();
                }

            }
        });

        bitHuaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String world = (String)v.getTag();
                try {
                    DictStrokeInfo[] dictStrokeInfos = connection.getAidl().getWordStrokes(world);
                    byte[] bytes = connection.getAidl().getStrokeSound(dictStrokeInfos[currentSerialNumber].serialNumber);
                    if (bytes != null) {

                        mSound.setDataSource(bytes);
                        mSound.start();
                    }
                    currentSerialNumber++;
                    if (currentSerialNumber >= dictStrokeInfos.length) {
                        currentSerialNumber = 0;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (connection != null) {
            getActivity().unbindService(connection);
        }
        super.onDestroyView();
    }

    private void sendMessage(int what, Object obj, int delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        if (handler != null) {
            handler.sendMessageDelayed(msg, delayMillis);
        }
    }

    private class Connection implements ServiceConnection {

        private IDictDataAidl iDictDataAidl;

        private IDictDataAidl getAidl() {
            return iDictDataAidl;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iDictDataAidl = IDictDataAidl.Stub.asInterface(service);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    class LoadData extends Thread {

        String searchStr;

        public LoadData(String searchStr) {
            this.searchStr = searchStr;
        }

        @Override
        public void run() {
            try {
                List<DictWordInfo> data = connection.getAidl().getMatchWordInfo(searchStr, currentDict);
                sendMessage(LOAD_DATA_FINISH, data, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
