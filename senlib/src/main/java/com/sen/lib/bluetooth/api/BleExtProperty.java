package com.sen.lib.bluetooth.api;

import android.os.Parcel;
import android.os.Parcelable;

public class BleExtProperty extends BleDescriptor implements Parcelable {
    private static final String TAG = "BleExtProperty";
    public static final Creator<BleExtProperty> CREATOR = new Creator() {
        public BleExtProperty createFromParcel(Parcel source) {
            return new BleExtProperty(source);
        }

        public BleExtProperty[] newArray(int size) {
            return new BleExtProperty[size];
        }
    };

    public BleExtProperty(Parcel source) {
        super(source);
    }

    public BleExtProperty() {
        super(new BleGattID(10496));
    }
}

