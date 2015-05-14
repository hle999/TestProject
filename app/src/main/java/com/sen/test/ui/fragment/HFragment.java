package com.sen.test.ui.fragment;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.QSK.helloble.DeviceListActivity;
import com.QSK.helloble.HelloBle;
import com.sen.test.R;
import com.sen.test.ui.fragment.BaseFragment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Editor: sgc
 * Date: 2015/04/06
 */
public class HFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private static final int RESULT_CODE = 100;
    private static final int REQUEST_CODE = 35;
    private static final String MY_UUID = "0000110b-0000-1000-8000-00805f9b34fb";

    private List<BLEDeviceInfo> bleDeviceInfoList;
    private ListView listView;

    private Toast toast;

    private BroadcastReceiver discoverBLEReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.contentEquals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice discoverDevice = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (discoverDevice != null) {
                        BLEDeviceInfo bleDeviceInfo = new BLEDeviceInfo();
                        bleDeviceInfo.name = discoverDevice.getName();
                        bleDeviceInfo.address = discoverDevice.getAddress();
                        System.out.println("sgc name: "+bleDeviceInfo.name);
                        try {
                            Method method = discoverDevice.getClass().getMethod("getUuids");
                            ParcelUuid[] phoneUuids = (ParcelUuid[]) method.invoke(discoverDevice);
                            if (phoneUuids != null) {
                                bleDeviceInfo.uuid = phoneUuids[0].getUuid();
                                for (ParcelUuid parcelUuid:phoneUuids) {
                                    System.out.println("sgc: "+parcelUuid.getUuid().toString());
                                }
                            }
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        bleDeviceInfoList.add(bleDeviceInfo);
                        if (listView != null && listView.getAdapter() != null) {
                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                        }
                    }
                } else if (action.contentEquals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

                } else if (action.contentEquals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    System.out.println("BLE bond change!");
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (bluetoothDevice != null) {
                        switch (bluetoothDevice.getBondState()) {
                            case BluetoothDevice.BOND_BONDING:
                                toast.setText("BONDING");
                                toast.show();
                                 break;
                            case BluetoothDevice.BOND_BONDED:
                                toast.setText("BONDED");
                                toast.show();
                                 break;

                            case BluetoothDevice.BOND_NONE:
                                toast.setText("NONE");
                                toast.show();
                                 break;

                        }
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*Intent intent = new Intent(getActivity(), HelloBle.class);
        getActivity().startActivity(intent);
        getFragmentManager().popBackStack();*/
        View view = inflater.inflate(R.layout.fragment_h, null);
        listView = (ListView) view.findViewById(R.id.listview);
        BLEAdapter bleAdapter = new BLEAdapter();
        listView.setAdapter(bleAdapter);
        listView.setOnItemClickListener(this);
        bleDeviceInfoList = new ArrayList<>();
        toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_LONG);
        init();
        openBle();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(discoverBLEReceiver);
    }

    private void init() {
        IntentFilter bleFilter = new IntentFilter();
        bleFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bleFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bleFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bleFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(discoverBLEReceiver, bleFilter);
    }

    private void openBle() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Method[] methods = BluetoothAdapter.class.getDeclaredMethods();
        for (Method method:methods) {
            System.out.println("sss "+method.getName());
        }
        try {
            Method m = BluetoothAdapter.class.getDeclaredMethod("getUuids");
            for (ParcelUuid u: (ParcelUuid[]) m.invoke(bluetoothAdapter) ) {
                System.out.println("local support: "+u.getUuid().toString());
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        bluetoothAdapter.getProfileProxy(getActivity(), this, BluetoothProfile.HEADSET);
        if (!bluetoothAdapter.isEnabled()) {
            Intent intentEnabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnabler, REQUEST_CODE);
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (bleDeviceInfoList != null &&
                bleDeviceInfoList.size() > position) {
//            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            BluetoothDevice bluetoothDevice = BluetoothAdapter.
                    getDefaultAdapter().getRemoteDevice(bleDeviceInfoList.get(position).address);
            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                try {

                    Method creMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    creMethod.setAccessible(true);
                    creMethod.invoke(bluetoothDevice);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                ConnectBLE connectBLE = new ConnectBLE(bluetoothDevice, bleDeviceInfoList.get(position).uuid);
                connectBLE.start();
            }
        }
    }

    private class BLEAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (bleDeviceInfoList != null) {
                return bleDeviceInfoList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(parent.getContext());
                ((TextView) convertView).setTextSize(30);
                ((TextView) convertView).setTextColor(Color.BLACK);
            }
            if (bleDeviceInfoList != null && bleDeviceInfoList.size() > position) {
                ((TextView) convertView).setText(bleDeviceInfoList.get(position).name
                        + "\n" + bleDeviceInfoList.get(position).address);
            }
            return convertView;
        }
    }

    private class ConnectBLE extends Thread {

        private boolean isConnected = false;
        private BluetoothDevice device;
        private UUID uuid;

        ConnectBLE(BluetoothDevice device) {
            this.device = device;
        }

        ConnectBLE(BluetoothDevice device, UUID uuid) {
            this.device = device;
            this.uuid = uuid;
        }

        @Override
        public void run() {
            int time=0;
            while (!connect()) {
                if (isConnected) {
                    break;
                }
                if (time > 10) {
                    break;
                }
                time++;
            }
        }

        private boolean connect() {
            BluetoothSocket bluetoothSocket=null;
            try {
                int sdk = Build.VERSION.SDK_INT;
                System.out.println("sdk: "+sdk);
                if (sdk >= 10) {
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(/*UUID.randomUUID()*/uuid);
                    /*Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
                    m.setAccessible(true);
                    bluetoothSocket = (BluetoothSocket)m.invoke(device, 2);
                    */
                } else {
                    bluetoothSocket =  device.createRfcommSocketToServiceRecord(/*UUID.randomUUID()*/uuid);
                    /*Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    m.setAccessible(true);
                    bluetoothSocket = (BluetoothSocket)m.invoke(device, 2);
                    */
                }
                if (bluetoothSocket != null) {
                    bluetoothSocket.connect();
                    isConnected = true;
                }
            } /*catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }*/ catch (IOException e) {
                e.printStackTrace();
            }
            return isConnected;
        }
    }

    private class BLEDeviceInfo {
        String name;
        String address;
        UUID uuid;
    }
}
