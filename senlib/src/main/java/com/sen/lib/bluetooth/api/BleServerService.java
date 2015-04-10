package com.sen.lib.bluetooth.api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sen.lib.bluetooth.gatt.BluetoothGattID;
import com.sen.lib.bluetooth.gatt.IBluetoothGatt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class BleServerService {
    private final String TAG = "BleServerService";
    private HashMap<Integer, BleCharacteristic> mCharHdlMap = null;
    private HashMap<Integer, BleServerService> mServiceHdlMap = null;
    private HashMap<Integer, BleServerService.AttributeRequestInfo> mAttrReqMap = null;
    private ArrayList<BleCharacteristic> mCharQueue = null;
    private ArrayList<BleDescriptor> mDirtyDescQueue = null;
    private BleGattID mServiceId;
    private BleGattID mAppUuid;
    private BleServerProfile mProfileHandle;
    private IBluetoothGatt mService;
    private int mSvcHandle = -1;
    private byte mSupTransport;
    private BleServerService.BleServiceCallback mGattServiceCallback;
    private boolean isServiceAvailable = false;
    private int mSvcInstance = 0;
    private boolean isPrimary = false;
    private int mNumHandles;
    private final int CHAR_ADDED = 0;
    private final int CHAR_DESC_ADDED = 1;
    private final int ATTRIBUTE_WRITE = 2;
    private final int ATTRIBUTE_READ = 3;
    private final int HDL_VAL_INDICATION = 4;
    private final int HDL_VAL_NOTIFICATION = 5;
    private final int MTU_EXCHANGE = 6;
    private final int EXECUTE_WRITE = 7;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i("BleServerService", "Handler: Handling message " + msg.what);
            BleServerService.HandlerMessage mHdlmsg = (BleServerService.HandlerMessage)msg.obj;
            BleCharacteristic charObj = null;
            Object descObj = null;
            switch(msg.what) {
                case 0:
                    charObj = (BleCharacteristic)BleServerService.this.mCharQueue.get(0);
                    if(mHdlmsg.mStatus == 0) {
                        if(!charObj.isRegistered()) {
                            charObj.addHandle(mHdlmsg.mUuid, mHdlmsg.mHandle);
                            Log.i("BleServerService", "Adding Handle " + mHdlmsg.mHandle + "for UUID" + mHdlmsg.mUuid);
                            BleServerService.this.mCharHdlMap.put(Integer.valueOf(mHdlmsg.mHandle), charObj);
                        }

                        if(!charObj.getDirtyDescQueue().isEmpty()) {
                            Log.i("BleServerService", "Adding Characteristic descriptors now");
                            BleServerService.this.addCharacteristic(charObj, false);
                            return;
                        }
                    }

                    BleServerService.this.onCharacteristicAdded(mHdlmsg.mStatus, charObj);
                    synchronized(this) {
                        BleServerService.this.mCharQueue.remove(0);
                        if(!BleServerService.this.mCharQueue.isEmpty()) {
                            Log.i("BleServerService", "Handle: Start adding a new characteristic...");
                            BleServerService.this.addCharacteristic((BleCharacteristic)BleServerService.this.mCharQueue.get(0), false);
                        }
                        break;
                    }
                case 1:
                    BleGattID uuid = mHdlmsg.mUuid;
                    Log.e("BleServerService", "Handle UUID =" + uuid);
                    charObj = (BleCharacteristic)BleServerService.this.mCharQueue.get(0);
                    if(charObj == null) {
                        Log.e("BleServerService", "onCharacteristicAdded: Should n\'t be null");
                        return;
                    }

                    if(mHdlmsg.mStatus == 0) {
                        charObj.addHandle(uuid, mHdlmsg.mHandle);
                        Log.i("BleServerService", "Adding Handle " + mHdlmsg.mHandle + "for UUID" + mHdlmsg.mUuid);
                        if(charObj.getDescriptor(uuid) != null) {
                            charObj.updateDirtyDescQueue();
                        }

                        ArrayList tempObj1 = charObj.getDirtyDescQueue();
                        BleServerService.this.mCharHdlMap.put(Integer.valueOf(mHdlmsg.mHandle), charObj);
                        if(!tempObj1.isEmpty()) {
                            Log.i("BleServerService", "Adding further Characteristic descriptors now");
                            BleServerService.this.addCharacteristic(charObj, false);
                            return;
                        }

                        Log.i("BleServerService", "One complete characteristic added successfully..");
                        Iterator dataList1 = BleServerService.this.mCharHdlMap.entrySet().iterator();

                        while(dataList1.hasNext()) {
                            Entry dataList22 = (Entry)dataList1.next();
                            Log.e("BleServerService", "mCharHdlMap: Key = " + dataList22.getKey() + "Attribute =" + dataList22.getValue());
                        }
                    }

                    BleServerService.this.onCharacteristicAdded(mHdlmsg.mStatus, charObj);
                    synchronized(this) {
                        BleServerService.this.mCharQueue.remove(0);
                        if(!BleServerService.this.mCharQueue.isEmpty()) {
                            Log.i("BleServerService", "Handle: Start adding a new characteristic...");
                            BleServerService.this.addCharacteristic((BleCharacteristic)BleServerService.this.mCharQueue.get(0), false);
                        }
                        break;
                    }
                case 2:
                    BleCharacteristic tempObj = (BleCharacteristic)BleServerService.this.mCharHdlMap.get(Integer.valueOf(mHdlmsg.mAttrInfo.mAttrHandle));
                    ArrayList dataList = null;
                    if(!mHdlmsg.mAttrInfo.mIsPrep) {
                        if(tempObj != null) {
                            Log.e("BleServerService", "###Offset is " + mHdlmsg.mAttrInfo.mOffset + " len is " + mHdlmsg.mAttrInfo.mLen);
                            byte dataList21 = tempObj.setValue(mHdlmsg.mAttrInfo.mData, mHdlmsg.mAttrInfo.mOffset, mHdlmsg.mAttrInfo.mLen, mHdlmsg.mAttrInfo.mAttrHandle, mHdlmsg.mAttrInfo.mLen, mHdlmsg.mAttrInfo.mAddress);
                            Log.i("BleServerService", "SetValue status =" + dataList21 + " ");
                            BleServerService.this.onCharacteristicWrite(mHdlmsg.mAttrInfo.mAddress, tempObj);
                            if(mHdlmsg.mAttrInfo.mNeedRsp) {
                                BleServerService.this.sendResponse(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, mHdlmsg.mAttrInfo.mAttrHandle, mHdlmsg.mAttrInfo.mOffset, mHdlmsg.mAttrInfo.mData, dataList21, true);
                            }
                        } else {
                            Log.e("BleServerService", "Attribute_Write: Error Invalid handle");
                            BleServerService.this.sendResponse(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, mHdlmsg.mAttrInfo.mAttrHandle, mHdlmsg.mAttrInfo.mOffset, mHdlmsg.mAttrInfo.mData, 1, true);
                        }
                    } else {
                        PrepareWriteContext dataList23 = new PrepareWriteContext(mHdlmsg.mAttrInfo.mOffset, mHdlmsg.mAttrInfo.mData, mHdlmsg.mAttrInfo.mAttrHandle);
                        if((dataList = (ArrayList)tempObj.writeQueue.get(mHdlmsg.mAttrInfo.mAddress)) == null) {
                            Log.i("BleServerService", "Preparing write for first time for address: " + mHdlmsg.mAttrInfo.mAddress);
                            dataList = new ArrayList();
                            dataList.add(dataList23);
                            tempObj.writeQueue.put(mHdlmsg.mAttrInfo.mAddress, dataList);
                            tempObj.writeSizeQueue.put(mHdlmsg.mAttrInfo.mAddress, Integer.valueOf(mHdlmsg.mAttrInfo.mData.length));
                        } else {
                            Log.i("BleServerService", "Adding more entries for address: " + mHdlmsg.mAttrInfo.mAddress);
                            boolean retValue2 = false;
                            int retValue1;
                            if((retValue1 = ((Integer)tempObj.writeSizeQueue.get(mHdlmsg.mAttrInfo.mAddress)).intValue()) + mHdlmsg.mAttrInfo.mData.length > 200) {
                                Log.e("BleServerService", "#### Prepare failed: Exceeding prepare queue size");
                                tempObj.writeQueue.remove(mHdlmsg.mAttrInfo.mAddress);
                                tempObj.writeSizeQueue.remove(mHdlmsg.mAttrInfo.mAddress);
                                BleServerService.this.sendResponse(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, mHdlmsg.mAttrInfo.mAttrHandle, mHdlmsg.mAttrInfo.mOffset, mHdlmsg.mAttrInfo.mData, 9, false);
                                return;
                            }

                            dataList.add(dataList23);
                            tempObj.writeQueue.put(mHdlmsg.mAttrInfo.mAddress, dataList);
                            tempObj.writeSizeQueue.put(mHdlmsg.mAttrInfo.mAddress, Integer.valueOf(retValue1 + mHdlmsg.mAttrInfo.mData.length));
                        }

                        BleServerService.this.sendResponse(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, mHdlmsg.mAttrInfo.mAttrHandle, mHdlmsg.mAttrInfo.mOffset, mHdlmsg.mAttrInfo.mData, 0, false);
                    }
                    break;
                case 3:
                    BleCharacteristic charObj21 = (BleCharacteristic)BleServerService.this.mCharHdlMap.get(Integer.valueOf(mHdlmsg.mAttrInfo.mAttrHandle));
                    Log.d("BleServerService", "Handle Message: ATTRIBUTE_READ");
                    if(charObj21 == null) {
                        Log.e("BleServerService", "Atrribute read error. Invalid attribute handle" + mHdlmsg.mAttrInfo.mAttrHandle);
                        BleServerService.this.sendResponse(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, mHdlmsg.mAttrInfo.mAttrHandle, mHdlmsg.mAttrInfo.mOffset, new byte[1], 1, false);
                        return;
                    }

                    BleServerService.this.mAttrReqMap.put(Integer.valueOf(mHdlmsg.mAttrInfo.mTransId), mHdlmsg.mAttrInfo);
                    BleServerService.this.onCharacteristicRead(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, mHdlmsg.mAttrInfo.mAttrHandle, charObj21);
                    break;
                case 4:
                    Log.i("BleServerService", "Handle Value Indication");
                    break;
                case 5:
                    Log.i("BleServerService", "Handle Value Notification");
                    break;
                case 6:
                    Log.i("BleServerService", "AttributeMtuExchange");
                    break;
                case 7:
                    ArrayList dataList2 = null;
                    byte retValue = 0;
                    Iterator charObj2 = BleServerService.this.mCharHdlMap.entrySet().iterator();

                    while(true) {
                        while(charObj2.hasNext()) {
                            Entry entry = (Entry)charObj2.next();
                            BleCharacteristic tempOb = (BleCharacteristic)entry.getValue();
                            if((dataList2 = (ArrayList)tempOb.writeQueue.get(mHdlmsg.mAttrInfo.mAddress)) != null) {
                                PrepareWriteContext context;
                                if(mHdlmsg.mAttrInfo.mExecWriteMtu == 0) {
                                    Log.e("BleServerService", "## Flushing out all prepared writes for address: " + mHdlmsg.mAttrInfo.mAddress);
                                    dataList2.clear();
                                } else {
                                    for(Iterator i$ = dataList2.iterator(); i$.hasNext(); retValue = tempOb.setValue(context.mData, context.mOffset, context.mData.length, context.mHandle, ((Integer)tempOb.writeSizeQueue.get(mHdlmsg.mAttrInfo.mAddress)).intValue(), mHdlmsg.mAttrInfo.mAddress)) {
                                        context = (PrepareWriteContext)i$.next();
                                        Log.e("BleServerService", "##### Setting char value from offset " + context.mOffset + " to a length of " + context.mData.length + " into handle " + context.mHandle);
                                    }
                                }

                                tempOb.writeQueue.remove(mHdlmsg.mAttrInfo.mAddress);
                                tempOb.writeSizeQueue.remove(mHdlmsg.mAttrInfo.mAddress);
                                BleServerService.this.sendResponse(mHdlmsg.mAttrInfo.mAddress, mHdlmsg.mAttrInfo.mTransId, 0, 0, new byte[1], retValue, true);
                                if(retValue == 0) {
                                    BleServerService.this.onCharacteristicWrite(mHdlmsg.mAttrInfo.mAddress, tempOb);
                                }
                            } else {
                                Log.e("BleServerService", "####ExecuteWrite: No prep write queue in char " + tempOb.getHandle(tempOb.getID()));
                            }
                        }

                        return;
                    }
            }

        }
    };

    int getConnId(String address) {
        if(this.mProfileHandle == null) {
            return -1;
        } else {
            HashMap connMap = this.mProfileHandle.getConnMap();
            return ((Integer)connMap.get(address)).intValue();
        }
    }

    public BleServerService(BleGattID serviceId, int numHandles) {
        this.mServiceId = serviceId;
        this.mNumHandles = numHandles;
        this.mSupTransport = 2;
        this.mGattServiceCallback = new BleServerService.BleServiceCallback(this);
        this.mCharHdlMap = new HashMap();
        this.mServiceHdlMap = new HashMap();
        this.mCharQueue = new ArrayList();
        this.mAttrReqMap = new HashMap();
        if(this.mServiceId.getServiceType() == -1) {
            this.mServiceId.setServiceType(0);
        }

    }

    public BleServerService(BleGattID serviceId, byte supTransport, int numHandles) {
        this.mServiceId = serviceId;
        this.mNumHandles = numHandles;
        this.mSupTransport = supTransport;
        this.mGattServiceCallback = new BleServerService.BleServiceCallback(this);
        this.mCharHdlMap = new HashMap();
        this.mCharQueue = new ArrayList();
        this.mServiceHdlMap = new HashMap();
        this.mAttrReqMap = new HashMap();
        if(this.mServiceId.getServiceType() == -1) {
            this.mServiceId.setServiceType(0);
        }

    }

    protected void setServiceHandle(int svcHandle) {
        this.mSvcHandle = svcHandle;
    }

    protected void setServiceInstance(int svcInstance) {
        this.mSvcInstance = svcInstance;
    }

    protected int getServiceHandle() {
        return this.mSvcHandle;
    }

    protected BleGattID getUuid() {
        return this.mServiceId;
    }

    protected void initService() {
        if(this.mService != null) {
            try {
                this.mService.registerServerServiceCallback(this.mServiceId, this.mAppUuid, this.mGattServiceCallback);
            } catch (Throwable var2) {
                Log.e("BleServerService", "initService", var2);
            }
        }

    }

    public void createService() {
        if(this.mService != null) {
            try {
                this.mService.GATTServer_CreateService(this.mProfileHandle.getAppHandle(), this.mServiceId, this.mNumHandles);
            } catch (Throwable var2) {
                Log.e("BleServerService", "createService", var2);
            }
        }

    }

    public void deleteService() {
        if(this.mService != null) {
            try {
                this.mService.GATTServer_DeleteService(this.mSvcHandle);
            } catch (Throwable var2) {
                Log.e("BleServerService", "deleteService ", var2);
            }
        }

    }

    public void startService() {
        if(this.mService != null) {
            try {
                this.mService.GATTServer_StartService(this.mSvcHandle, this.mSupTransport);
            } catch (Throwable var2) {
                Log.e("BleServerService", "startService ", var2);
            }
        }

    }

    public void stopService() {
        if(this.mService != null) {
            this.mProfileHandle.notifyAction(4);

            try {
                this.mService.unregisterServerServiceCallback(this.mSvcHandle);
                this.mService.GATTServer_StopService(this.mSvcHandle);
            } catch (Throwable var2) {
                Log.e("BleServerService", "stopService ", var2);
            }
        }

    }

    public void addIncludedService(BleServerService service) {
        if(this.mService != null) {
            try {
                if(service.isRegistered()) {
                    this.mServiceHdlMap.put(Integer.valueOf(service.getServiceHandle()), service);
                    this.mService.GATTServer_AddIncludedService(this.mSvcHandle, service.getServiceHandle());
                } else {
                    Log.i("BleServerService", "addIncludedService: Service to be included is not registered.");
                }
            } catch (Throwable var3) {
                Log.e("BleServerService", "addIncludedService", var3);
            }
        }

    }

    public void updateCharacteristic(BleCharacteristic charObj) {
        this.addCharacteristic(charObj);
    }

    public void sendResponse(String address, int transId, byte[] data, int statusCode) {
        Log.d("BleServerService", "sendResponse() address = " + address + ", transId = " + transId + ",statusCode = " + statusCode);
        if(this.mService == null) {
            Log.e("BleServerService", "sendResponse(): error. GattService not available");
        } else {
            BleServerService.AttributeRequestInfo attrInfo = (BleServerService.AttributeRequestInfo)this.mAttrReqMap.remove(Integer.valueOf(transId));
            if(attrInfo == null) {
                Log.e("BleServerService", "sendResponse() error. attrInfo not found with transId " + transId);
            } else {
                Object dataToSend = null;
                byte[] dataToSend1;
                if(attrInfo.mOffset == 0) {
                    dataToSend1 = data;
                } else {
                    dataToSend1 = new byte[data.length - attrInfo.mOffset];
                    System.arraycopy(data, attrInfo.mOffset, dataToSend1, 0, dataToSend1.length);
                }

                try {
                    this.mService.GATTServer_SendRsp(attrInfo.mConnId, attrInfo.mTransId, (byte)statusCode, attrInfo.mAttrHandle, attrInfo.mOffset, dataToSend1, (byte)0, false);
                } catch (Throwable var8) {
                    Log.e("BleServerService", "sendResponse(): error", var8);
                }

            }
        }
    }

    public void sendResponse(String address, int transId, int handle, int offset, byte[] data, int statusCode, boolean isWrite) {
        int connId = this.getConnId(address);
        if(connId != -1 && this.mService != null) {
            try {
                this.mService.GATTServer_SendRsp(connId, transId, (byte)statusCode, handle, offset, data, (byte)0, isWrite);
            } catch (Throwable var10) {
                Log.e("BleServerService", "sendResponse", var10);
            }
        }

    }

    public void sendNotification(String address, int attrHandle, byte[] value) {
        int connId = this.getConnId(address);
        if(connId != -1 && this.mService != null) {
            try {
                this.mService.GATTServer_HandleValueNotification(connId, attrHandle, value);
            } catch (Throwable var6) {
                Log.e("BleServerService", "sendNotification", var6);
            }
        }

    }

    public void sendIndication(String address, int attrHandle, byte[] value) {
        int connId = this.getConnId(address);
        if(connId != -1 && this.mService != null) {
            try {
                this.mService.GATTServer_HandleValueIndication(connId, attrHandle, value);
            } catch (Throwable var6) {
                Log.e("BleServerService", "sendIndication", var6);
            }
        }

    }

    public void addCharacteristic(BleCharacteristic charObj) {
        this.addCharacteristic(charObj, true);
    }

    private void addCharacteristic(BleCharacteristic charObj, boolean addtoQueue) {
        Log.i("BleServerService", "GATTServer_AddCharacteristic");
        ArrayList dirtyDescQueue = charObj.getDirtyDescQueue();
        if(this.mService != null && charObj != null && charObj.getID() != null) {
            try {
                if(charObj.isRegistered()) {
                    Log.d("BleServerService", "Starting to add descriptors, dirtyDesc size =" + dirtyDescQueue.size());
                    boolean dirtyMask = charObj.isDirty();
                    if(!dirtyDescQueue.isEmpty()) {
                        BleDescriptor t = (BleDescriptor)dirtyDescQueue.get(0);
                        Log.i("BleServerService", "GATTServer_AddCharDescriptor");
                        this.mService.GATTServer_AddCharDescriptor(this.mSvcHandle, t.getPermission(), t.mID);
                    } else if(dirtyMask) {
                        Log.i("BleServerService", "GATTServer_AddCharValue");
                        HashMap t1 = this.mProfileHandle.getConnMap();

                        boolean clientCfg;
                        for(Iterator i$ = t1.entrySet().iterator(); i$.hasNext(); clientCfg = false) {
                            Entry entry = (Entry)i$.next();
                            String address = (String)entry.getKey();
                            int connId = ((Integer)entry.getValue()).intValue();
                        }

                        return;
                    }
                } else {
                    if(addtoQueue) {
                        synchronized(this) {
                            this.mCharQueue.add(charObj);
                            Log.e("BleServerService", "Adding a new characteristic... SIZE IS " + this.mCharQueue.size() + "Uuid=" + charObj.getID());
                            if(this.mCharQueue.size() > 1) {
                                return;
                            }
                        }
                    }

                    BleGattID t2 = charObj.getID();
                    this.mService.GATTServer_AddCharacteristic(this.mSvcHandle, t2, charObj.getPermission(), charObj.getProperty(), charObj.isDirty(), dirtyDescQueue.size());
                }
            } catch (Throwable var13) {
                Log.e("BleServerService", "addCharacteristic", var13);
            }

        } else {
            Log.i("BleServerService", "GattService/Characteristic object passed in is null.. Cannot add the chanaracteristic...");
        }
    }

    void onServiceAvailable(BleServerProfile profile, IBluetoothGatt service, BleGattID appId) {
        if(service != null) {
            this.isServiceAvailable = true;
        }

        this.mProfileHandle = profile;
        this.mService = service;
        this.mAppUuid = appId;
        this.initService();
    }

    public boolean isStarted() {
        return this.isServiceAvailable;
    }

    public boolean isRegistered() {
        return this.mSvcHandle != -1;
    }

    public void onIncludedServiceAdded(byte status, BleServerService includedService) {
        Log.d("BleServerService", "OnIncludedServiceAdded : status=" + status + "Included service" + includedService.getUuid());
    }

    public void onCharacteristicAdded(byte status, BleCharacteristic charObj) {
        Log.d("BleServerService", "OnCharacteristicAdded : Characteristic uuid = " + charObj.getID() + "status=" + status);
    }

    public void onResponseSendCompleted(byte status, BleCharacteristic charObj) {
        Log.d("BleServerService", "onResponseSendCompleted : status=" + status);
    }

    public void onCharacteristicRead(String address, int transId, int attrHandle, BleCharacteristic charObj) {
        BleServerService.AttributeRequestInfo attrInfo = (BleServerService.AttributeRequestInfo)this.mAttrReqMap.remove(Integer.valueOf(transId));
        Log.d("BleServerService", "Inside onCharacteristicRead()");
        if(attrInfo == null) {
            Log.e("BleServerService", "onCharacteristicRead() error. attrInfo not found with transId " + transId);
        } else if(charObj == null) {
            Log.e("BleServerService", "onCharacteristicRead() error. charObj is null");
        } else {
            byte[] data = charObj.getValueByHandle(attrHandle);
            if(data == null) {
                Log.d("BleServerService", "Attribute not found with handle " + attrHandle);

                try {
                    this.mService.GATTServer_SendRsp(attrInfo.mConnId, attrInfo.mTransId, (byte)10, attrInfo.mAttrHandle, attrInfo.mOffset, (byte[])null, (byte)0, false);
                } catch (Throwable var10) {
                    Log.e("BleServerService", "onCharacteristicRead(): error", var10);
                }

            } else {
                int dataLength = data == null?0:data.length;
                if(attrInfo.mOffset >= dataLength) {
                    Log.e("BleServerService", "onCharacteristicRead() error. dataLength < attrInfo.mOffset");

                    try {
                        this.mService.GATTServer_SendRsp(attrInfo.mConnId, attrInfo.mTransId, (byte)7, attrInfo.mAttrHandle, attrInfo.mOffset, (byte[])null, (byte)0, false);
                    } catch (Throwable var11) {
                        Log.e("BleServerService", "onCharacteristicRead(): error", var11);
                    }

                } else {
                    Object dataToSend = null;
                    byte[] dataToSend1;
                    if(attrInfo.mOffset == 0) {
                        dataToSend1 = data;
                    } else {
                        dataToSend1 = new byte[data.length - attrInfo.mOffset];
                        System.arraycopy(data, attrInfo.mOffset, dataToSend1, 0, dataToSend1.length);
                    }

                    try {
                        this.mService.GATTServer_SendRsp(attrInfo.mConnId, attrInfo.mTransId, (byte)0, attrInfo.mAttrHandle, attrInfo.mOffset, dataToSend1, (byte)0, false);
                    } catch (Throwable var12) {
                        Log.e("BleServerService", "sendResponse(): error", var12);
                    }

                }
            }
        }
    }

    public void onCharacteristicWrite(String address, BleCharacteristic charObj) {
        Log.d("BleServerService", "onCharacteristicWrite : modified characteristic=" + charObj.getID());
    }

    private class BleServiceCallback extends IBleServiceCallback.Stub {
        private BleServerService mGattService;

        public BleServiceCallback(BleServerService service) {
            this.mGattService = service;
        }

        public void onServiceRegistered(byte status, BluetoothGattID svcId) {
            Log.i("BleServerService", "onServiceRegistered");
            if(status == 0) {
                this.mGattService.setServiceInstance(svcId.getInstanceID());
                this.mGattService.createService();
            } else {
                Log.e("BleServerService", "#######Service registration failed...");
                BleServerService.this.mProfileHandle.notifyAction(1);
            }

        }

        public void onServiceCreated(byte status, int svcHandle) {
            Log.i("BleServerService", "onServiceCreated");
            if(status == 0) {
                this.mGattService.setServiceHandle(svcHandle);
                BleServerService.this.mProfileHandle.notifyAction(0);
            } else {
                BleServerService.this.mProfileHandle.notifyAction(1);
            }

        }

        public void onIncludedServiceAdded(byte status, int incSvcHandle) {
            Log.i("BleServerService", "onIncludedServiceAdded");
            this.mGattService.onIncludedServiceAdded(status, (BleServerService)BleServerService.this.mServiceHdlMap.get(Integer.valueOf(incSvcHandle)));
        }

        public void onCharacteristicAdded(byte status, BluetoothGattID charId, int charHdl) {
            Log.i("BleServerService", "onCharacteristicAdded id is " + charId.toString() + "UUID is " + BleApiHelper.gatt2BleID(charId));
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(0, BleServerService.this.new HandlerMessage(status, BleApiHelper.gatt2BleID(charId), charHdl)));
        }

        public void onCharacteristicDescrAdded(byte status, BluetoothGattID chardescId, int chardescHdl) {
            Log.i("BleServerService", "onCharacteristicDescAdded id is " + chardescId.toString() + "UUID is " + BleApiHelper.gatt2BleID(chardescId));
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(1, BleServerService.this.new HandlerMessage(status, BleApiHelper.gatt2BleID(chardescId), chardescHdl)));
        }

        public void onServiceDeleted(byte status) {
            Log.i("BleServerService", "onServiceDeleted");
            this.mGattService.setServiceHandle(-1);
        }

        public void onServiceStarted(byte status) {
            Log.i("BleServerService", "onServiceStarted");
            if(status == 0) {
                this.mGattService.isServiceAvailable = true;
                BleServerService.this.mProfileHandle.notifyAction(2);
            } else {
                BleServerService.this.mProfileHandle.notifyAction(3);
            }

        }

        public void onServiceStopped(byte status) {
            Log.i("BleServerService", "onServiceStopped");
            this.mGattService.isServiceAvailable = false;
        }

        public void onHandleValueIndicationCompleted(byte status, int attrHandle) {
            Log.i("BleServerService", "onHandleValueIndicationCompleted");
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(4, BleServerService.this.new HandlerMessage(status, attrHandle)));
        }

        public void onHandleValueNotificationCompleted(byte status, int attrHandle) {
            Log.i("BleServerService", "onHandleValueNotificationCompleted");
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(5, BleServerService.this.new HandlerMessage(status, attrHandle)));
        }

        public void onResponseSendCompleted(byte status, int attrHandle) {
            Log.i("BleServerService", "onResponseSendCompleted");
            BleCharacteristic charObj = (BleCharacteristic)BleServerService.this.mCharHdlMap.get(Integer.valueOf(attrHandle));
            this.mGattService.onResponseSendCompleted(status, charObj);
        }

        public void onAttributeRequestRead(String address, int connId, int transId, int attrHandle, int offset, boolean isLong) {
            Log.i("BleServerService", "onAttributeRequestRead");
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(3, BleServerService.this.new HandlerMessage(BleServerService.this.new AttributeRequestInfo(address, connId, transId, attrHandle, offset, isLong))));
        }

        public void onAttributeRequestWrite(String address, int connId, int transId, int attrHandle, boolean isPrep, int len, boolean needRsp, int offset, byte[] data) {
            Log.i("BleServerService", "onAttributeRequestWrite");
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(2, BleServerService.this.new HandlerMessage(BleServerService.this.new AttributeRequestInfo(address, connId, transId, attrHandle, isPrep, len, needRsp, offset, data))));
        }

        public void onAttributeExecWrite(String address, int connId, int transId, int execWrite) {
            Log.i("BleServerService", "onAttributeExecWrite execWrite is " + execWrite);
            BleServerService.this.mHandler.sendMessage(BleServerService.this.mHandler.obtainMessage(7, BleServerService.this.new HandlerMessage(BleServerService.this.new AttributeRequestInfo(address, connId, transId, execWrite))));
        }
    }

    private class HandlerMessage {
        public byte mStatus;
        public BleGattID mUuid;
        public int mHandle;
        public BleServerService.AttributeRequestInfo mAttrInfo;

        HandlerMessage(byte status, int handle) {
            this.mStatus = status;
            this.mHandle = handle;
        }

        HandlerMessage(byte status, BleGattID uuid, int handle) {
            this.mStatus = status;
            this.mUuid = uuid;
            this.mHandle = handle;
        }

        HandlerMessage(BleServerService.AttributeRequestInfo attrInfo) {
            this.mAttrInfo = attrInfo;
        }
    }

    private class AttributeRequestInfo {
        public boolean mIsPrep;
        public boolean mIsLong;
        public boolean mNeedRsp;
        public int mLen;
        public int mOffset;
        public int mTransId;
        public int mConnId;
        public int mAttrHandle;
        public int mMtuSize;
        public int mExecWriteMtu;
        public byte[] mData;
        public String mAddress;

        AttributeRequestInfo(String address, int connId, int transId, int attrHandle, int offset, boolean isLong) {
            this.mAddress = address;
            this.mTransId = transId;
            this.mAttrHandle = attrHandle;
            this.mOffset = offset;
            this.mIsLong = isLong;
            this.mConnId = connId;
        }

        AttributeRequestInfo(String address, int connId, int transId, int execWriteMtu) {
            this.mAddress = address;
            this.mTransId = transId;
            this.mConnId = connId;
            this.mExecWriteMtu = execWriteMtu;
        }

        AttributeRequestInfo(String address, int connId, int transId, int attrHandle, boolean isPrep, int len, boolean needRsp, int offset, byte[] data) {
            this.mAddress = address;
            this.mTransId = transId;
            this.mAttrHandle = attrHandle;
            this.mIsPrep = isPrep;
            this.mLen = len;
            this.mNeedRsp = needRsp;
            this.mOffset = offset;
            this.mData = data;
            this.mConnId = connId;
        }
    }
}

