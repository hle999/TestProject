package com.QSK.helloble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.QSK.bleProfiles.FindMeProfileClient;
import com.QSK.bleProfiles.HelloBLEProfileClient;
import com.QSK.bleProfiles.HelloBleServiceClient;
import com.sen.test.R;

import java.io.UnsupportedEncodingException;


public class HelloBle extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_SELECT_DEVICE = 0;
	
    private static final int STATE_BT_OFF = 0;
    private static final int STATE_DISCONNECTED = 1;
    private static final int STATE_CONNECTED = 2;
    
    private int mState = STATE_BT_OFF;
	
	private static String TAG = "HelloBleActivity";
	
	private BluetoothDevice mDevice = null;
	boolean appRegistered = false;
	boolean mIsConnected = false;
	
	BluetoothAdapter mBtAdapter = null;
	FindMeProfileClient mFindMeProfileClient = null;
	HelloBLEProfileClient mHelloBleProfileClient = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello_ble);
		
		InitUIHandlers();
		
		/* Ensure Bluetooth is enabled */
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available - exiting...",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
        	mState = STATE_DISCONNECTED;
        }
				
		setUiState();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FindMeProfileClient.FINDME_REGISTERED);
		intentFilter.addAction(FindMeProfileClient.FINDME_CONNECTED);
		intentFilter.addAction(FindMeProfileClient.FINDME_DISCONNECTED);
		intentFilter.addAction(HelloBLEProfileClient.HELLOBLE_REGISTERED);
		intentFilter.addAction(HelloBLEProfileClient.HELLOBLE_CONNECTED);
		intentFilter.addAction(HelloBLEProfileClient.HELLOBLE_DISCONNECTED);
		intentFilter.addAction(HelloBleServiceClient.HELLOBLE_NAME);

		registerReceiver(regReceiver, intentFilter);
		mFindMeProfileClient = new FindMeProfileClient(this);
		mHelloBleProfileClient = new HelloBLEProfileClient(this);
	}
	
	void InitUIHandlers()
	{		
		// Select Device
		((Button)findViewById(R.id.button_selectdevice)).setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            Intent newIntent = new Intent(HelloBle.this, DeviceListActivity.class);
	            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
	        }
	    });
		
		// Connect Device		
		((Button)findViewById(R.id.button_connect)).setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	    		if (mFindMeProfileClient != null && mDevice != null && mHelloBleProfileClient != null) {
	    			mHelloBleProfileClient.connect(mDevice);
	    			mFindMeProfileClient.connect(mDevice);
	    		}	            
	        }
	    });

		// Dis-Connect Device		
		((Button)findViewById(R.id.button_disconnect)).setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if (mFindMeProfileClient != null && mDevice != null) {
	        		mHelloBleProfileClient.disconnect(mDevice);
	        		mFindMeProfileClient.disconnect(mDevice);
	        	}
	        }
	    });
		
		// getusername		
		((Button)findViewById(R.id.button_getusername)).setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if (mHelloBleProfileClient != null && mDevice != null) {
	        		
	        		String username = null;
	        		
	        		try {
	        			username = mHelloBleProfileClient.getLocalUserName(mDevice);
						if(username != null)
							((TextView) findViewById(R.id.textView_username)).setText(username);
						else
							((TextView) findViewById(R.id.textView_username)).setText("NULL");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	        		
	        		
	        		mHelloBleProfileClient.refresh(mDevice);
	        	}
	        }
	    });
		

		// Alert 		
		((Button)findViewById(R.id.button_alert)).setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if (mFindMeProfileClient != null && mDevice != null) {
	        		
	        		RadioGroup radioAlertLevel = (RadioGroup) findViewById(R.id.radioalertlevel);

	        		switch(radioAlertLevel.getCheckedRadioButtonId())
	        		{
	        		case R.id.radioButton_off:
	        			mFindMeProfileClient.alert(mDevice, FindMeProfileClient.ALERT_LEVEL_NONE);	        		
	        			break;
	        		case R.id.radioButton_low:
	        			mFindMeProfileClient.alert(mDevice, FindMeProfileClient.ALERT_LEVEL_LOW);
	        			break;
	        		case R.id.radioButton_high:
	        			mFindMeProfileClient.alert(mDevice, FindMeProfileClient.ALERT_LEVEL_HIGH);
	        			break;
	        		
	        		}
	        	}

	        }
	    });
	}
	
	private final BroadcastReceiver regReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {

			String action = intent.getAction();
			
			if (action.equals(FindMeProfileClient.FINDME_REGISTERED)) {
				Log.d(TAG, "Received app Register Intent");
				appRegistered = true;
			
			} else if (action.equals(FindMeProfileClient.FINDME_CONNECTED)) {
				
				mIsConnected = true;
			    Log.d(TAG," findme(onDeviceConnected" + mDevice.getAddress().toString() + ")");
			    mState = STATE_CONNECTED;    	
			    setUiState();    

			    if(mHelloBleProfileClient != null)
			     mHelloBleProfileClient.setUserName(mDevice);
			    
								
			} else if (action.equals(HelloBLEProfileClient.HELLOBLE_CONNECTED)) {
				
				mIsConnected = true;
			    Log.d(TAG," findme(onDeviceConnected" + mDevice.getAddress().toString() + ")");
			    mState = STATE_CONNECTED;    	
			    setUiState();    

			     mHelloBleProfileClient.setUserName(mDevice);
			    
								
			} else if (action.equals(FindMeProfileClient.FINDME_DISCONNECTED)) {
				mIsConnected = false;
				Log.d(TAG," findme(onDeviceConnected" + mDevice.getAddress().toString() + ")");
			    mState = STATE_DISCONNECTED;    	
			    setUiState();
			} else if (action.equals(HelloBleServiceClient.HELLOBLE_NAME)) {
				
				String userName = intent.getExtras().getString(Intent.EXTRA_TEXT);
				
				((TextView) findViewById(R.id.textView_username)).setText(userName);
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode != Activity.RESULT_OK) {
				finish();
			} else {
				mBtAdapter.startDiscovery();
			}
		} else if (requestCode == REQUEST_SELECT_DEVICE) {
			if (resultCode == Activity.RESULT_OK && data != null
					&& appRegistered == true) {
				String deviceAddress = data
						.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
				mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
						deviceAddress);
				setUiState();
			} else {
				Toast.makeText(this, "failed to select the device - try again",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_hello_ble, menu);
		return true;
	}
	
	private void setUiState() {

		findViewById(R.id.button_connect).setEnabled(mState == STATE_DISCONNECTED);
		findViewById(R.id.button_selectdevice).setEnabled(mState == STATE_DISCONNECTED);
		findViewById(R.id.button_disconnect).setEnabled(mState == STATE_CONNECTED);
		findViewById(R.id.button_alert).setEnabled(mState == STATE_CONNECTED);
    	
    	if (mDevice != null) {
    		((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName());
    	}

    	switch(mState) {
    	case STATE_CONNECTED:
    		((TextView) findViewById(R.id.statusName)).setText(R.string.connected);
    		break;
    	case STATE_DISCONNECTED:
    		((TextView) findViewById(R.id.statusName)).setText(R.string.disconnected);
    		break;
    	}
    }
    
    @Override
    public void onDestroy() {
        unregisterReceiver(regReceiver);
       
        if (mHelloBleProfileClient != null) {
    		
    		try {
    			mHelloBleProfileClient.deregister();
			} catch (InterruptedException ignored) {
			}
			
    		mHelloBleProfileClient.finish();
    	}
        
        if (mFindMeProfileClient != null) {
    		if (mDevice != null) {
    			mFindMeProfileClient.cancelBackgroundConnection(mDevice);
    		}
    		
    		try {
    			mFindMeProfileClient.deregister();
			} catch (InterruptedException ignored) {
			}
			
    		mFindMeProfileClient.finish();
    	}
        	
    	super.onDestroy();
    }
}
