package com.sen.test.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sen.test.R;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BlueToothFragment extends Fragment implements OnClickListener{

    private static final String TAG = "BluetoothFlagment";
    private static final String MP3_FILE = "better_in_time.mp3";
    private static final int RESULT_CODE = 100;
    private static final int REQUEST_CODE = 35;

    private boolean isConnected = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_bluetooth, null);

		/*Button button = (Button)view.findViewById(R.id.c_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

                getFragmentManager().popBackStack();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.child_container, new DFragment());
                ft.addToBackStack(null);
				ft.commit();
                Log.i("FragmentTest", "C " + getFragmentManager().getBackStackEntryCount() + "");
			}
		});*/
        view.findViewById(R.id.button_start_bluetooth).setOnClickListener(this);
		return view;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(bluetoothReciver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bluetoothReciver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_bluetooth:
                 openBluetooth();
                 break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == 1) {

                }
                    break;
        }
    }

    private void openBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intentEnabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnabler, REQUEST_CODE);
        } else {
            bluetoothAdapter.startDiscovery();
        }
    }

    private String address;

    BroadcastReceiver bluetoothReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//找到设备
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                address = device.getAddress();
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                Log.i(TAG, "find device state" + device.getBondState());
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.i(TAG, "find device:" + device.getName()
                            + device.getAddress());
                    try {

                        Method creMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        creMethod.invoke(device);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.i(TAG, "connect device!");

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {//搜索完成

//                setTitle("搜索完成");
//
//                if (mNewDevicesAdapter.getCount() == 0) {
//
//                    Log.v(TAG, "find over");
//
//                }
//                BluetoothDevice device = intent
//                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isConnected) {
                    return;
                }
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                Log.i(TAG, "find device finish ");
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                new ConnectThread(device).start();
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.contentEquals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.i(TAG, "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i(TAG, "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.i(TAG, "取消配对");
                    default:
                        break;
                }
            }

            //执行更新列表的代码
        }

    };

    private void showToast(int id) {
        Toast.makeText(BlueToothFragment.this.getActivity(),
                id, Toast.LENGTH_SHORT).show();
    }

    class ConnectThread extends Thread {

        private BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            int time=0;
            while (!connect()) {
                if (isConnected) {
                    break;
                }
                if (time > 3) {
                    break;
                }
                time++;
            }
        }

        private boolean connect() {
            BluetoothSocket bluetoothSocket=null;
            if (device != null) {
                try {
//                    bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    m.setAccessible(true);
                    bluetoothSocket = (BluetoothSocket)m.invoke(device, 2);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
//                    device.createBond();


                }
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                try {

                    bluetoothSocket.connect();
                    isConnected = true;
                    AssetFileDescriptor assetFileDescriptor = BlueToothFragment.this.getActivity().getAssets().openFd(MP3_FILE);

                    OutputStream outputStream = bluetoothSocket.getOutputStream();
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
//                    InputStream inputStream = assetFileDescriptor.createInputStream();
//                    byte[] buffer = new byte[inputStream.available()];
//                    inputStream.read(buffer);
//                    outputStream.write(buffer);
//                    outputStream.close();
//                    outputStream.write(buffer);
//                    bluetoothSocket.close();

                    Log.i(TAG, "conncted success!");
                } catch (IOException e) {
                    e.printStackTrace();
//                    showToast(R.string.toast_connect_bluetooth_fail);
                    return false;
                }


            }
            return true;
        }

    }
}
