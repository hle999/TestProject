package com.sen.test.ui.work;

import android.content.Context;

import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

/**
 * Created by Senny on 2015/12/8.
 */
public class DreamTencentLocation implements TencentLocationListener {

    private TencentLocationRequest request;
    private OnTencentLocationListener listener;

    public void setOnTencentLocationListener(OnTencentLocationListener listener) {
        this.listener = listener;
    }

    public DreamTencentLocation() {
        request = TencentLocationRequest.create();
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        request.setInterval(60 * 1000l);

    }

    public boolean startLocation(Context context) {
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        int error = locationManager.requestLocationUpdates(request, this);
        if (error == com.tencent.map.geolocation.TencentLocation.ERROR_OK) {

            return true;
        } else {

        }

        return false;
    }

    public void stopLocation(Context context) {
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(com.tencent.map.geolocation.TencentLocation tencentLocation, int error, String s) {
        if (com.tencent.map.geolocation.TencentLocation.ERROR_OK == error) {
            if (listener != null) {
                listener.onSuccess(tencentLocation);
            }
        } else {
            // ¶¨Î»Ê§°Ü
            if (listener != null) {
                listener.onFailed(tencentLocation);
            }
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        if (listener != null) {
            listener.onStatusUpdate(s, i, s1);
        }
    }

    public interface OnTencentLocationListener {
        void onStatusUpdate(String s, int i, String s1);
        void onSuccess(com.tencent.map.geolocation.TencentLocation tencentLocation);
        void onFailed(com.tencent.map.geolocation.TencentLocation tencentLocation);
    }
}
