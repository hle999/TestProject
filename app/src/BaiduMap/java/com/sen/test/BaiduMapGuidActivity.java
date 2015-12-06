package com.sen.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/9/28.
 */
public class BaiduMapGuidActivity extends Activity {

    public final static String ROUTE_PLAN_START_NODE = "route_start_node";
    public final static String ROUTE_PLAN_END_NODE = "route_end_node";
    public final static double pi = 3.1415926535897932384626;
    private BNRoutePlanNode mSBNRoutePlanNode = null;
    private BNRoutePlanNode mEBNRoutePlanNode = null;
    private LocationClient locationClient;
    private boolean isResetLocationSuccess = false;
    private boolean isResetRouteSuccess = false;
    private int intNoiceLevel = BNRouteGuideManager.VoiceMode.Novice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        createHandler();
        View view = BNRouteGuideManager.getInstance().onCreate(this, new BNRouteGuideManager.OnNavigationListener() {

            @Override
            public void onNaviGuideEnd() {
                finish();
            }

            @Override
            public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {

            }
        });

        if ( view != null ) {
            setContentView(view);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mSBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(ROUTE_PLAN_START_NODE);
                mEBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(ROUTE_PLAN_END_NODE);
            }
        }
        initLocation();
    }

    @Override
    protected void onResume() {
        BNRouteGuideManager.getInstance().onResume();
        super.onResume();

//        hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
        if (locationClient != null && !locationClient.isStarted()
                && !isResetLocationSuccess) {
            locationClient.start();
        }
    }

    protected void onPause() {
        super.onPause();
        BNRouteGuideManager.getInstance().onPause();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        }
        BNRouteGuideManager.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        BNRouteGuideManager.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        BNRouteGuideManager.getInstance().onBackPressed(true);
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    private void addCustomizedLayerItems() {
        List<BNRouteGuideManager.CustomizedLayerItem> items = new ArrayList<BNRouteGuideManager.CustomizedLayerItem>();
        BNRouteGuideManager.CustomizedLayerItem item1 = null;
        if (mSBNRoutePlanNode != null) {
            item1 = new BNRouteGuideManager.CustomizedLayerItem(mSBNRoutePlanNode.getLongitude(), mSBNRoutePlanNode.getLatitude(),
                    mSBNRoutePlanNode.getCoordinateType(), getResources().getDrawable(R.drawable.ic_launcher), BNRouteGuideManager.CustomizedLayerItem.ALIGN_CENTER);
            items.add(item1);

            BNRouteGuideManager.getInstance().setCustomizedLayerItems(items);
        }
        BNRouteGuideManager.getInstance().showCustomizedLayer(true);
    }

    private void initLocation() {
        MyLocation myLocation = new MyLocation();
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myLocation);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

        locationClient.setLocOption(option);
        locationClient.start();
    }

    private LatLng bd09ToGcj02(double bdLat, double bdLon) {
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double ggLon = z * Math.cos(theta);
        double ggLat = z * Math.sin(theta);
        return new LatLng(ggLat, ggLon);
    }

    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private Handler hd = null;

    private void createHandler() {
        if ( hd == null ) {
            hd = new Handler(getMainLooper()) {
                public void handleMessage(android.os.Message msg) {
                    if ( msg.what == MSG_SHOW ) {
                        addCustomizedLayerItems();
//						hd.sendEmptyMessageDelayed(MSG_HIDE, 5000);
                    } else if ( msg.what == MSG_HIDE ) {
                        BNRouteGuideManager.getInstance().showCustomizedLayer(false);
//						hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
                    }

                }
            };
        }
    }

    class MyLocation implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (!isResetLocationSuccess && bdLocation != null && bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                LatLng newSNode = bd09ToGcj02(bdLocation.getLatitude(), bdLocation.getLongitude());
                BNRoutePlanNode sNode = new BNRoutePlanNode(newSNode.longitude,
                        newSNode.latitude, null, null, BNRoutePlanNode.CoordinateType.GCJ02);
                isResetLocationSuccess = true;
                if (locationClient != null) {
                    locationClient.stop();
                }
                if (mSBNRoutePlanNode != null) {
                    if (mSBNRoutePlanNode.getLatitude() != sNode.getLatitude()
                            || mSBNRoutePlanNode.getLongitude() != sNode.getLongitude()) {
                        List<BNRoutePlanNode> list = new ArrayList();
                        list.add(sNode);
                        list.add(mEBNRoutePlanNode);
                        BNRouteGuideManager.getInstance().setVoiceModeInNavi(BNRouteGuideManager.VoiceMode.Quite);
                        BaiduNaviManager.getInstance().launchNavigator(BaiduMapGuidActivity.this, list, 1, true, new DreamRoutePlanListener(sNode, mEBNRoutePlanNode));
                    }
                }
            }
        }
    }

    class DreamRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode snode = null;
        private BNRoutePlanNode enode = null;
        public DreamRoutePlanListener(BNRoutePlanNode snode, BNRoutePlanNode enode){
            this.snode = snode;
            this.enode = enode;
        }

        @Override
        public void onJumpToNavigator() {
            /*View view = BNRouteGuideManager.getInstance().onCreate(BaiduMapGuidActivity.this, new BNRouteGuideManager.OnNavigationListener() {

                @Override
                public void onNaviGuideEnd() {
                    finish();
                }

                @Override
                public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {

                }
            });
            BaiduMapGuidActivity.this.setContentView(view);*/
            /*if (!isResetRouteSuccess) {
                isResetRouteSuccess = true;
                BNRouteGuideManager.getInstance().resetEndNodeInNavi(enode);
                BNRouteGuideManager.getInstance().setVoiceModeInNavi(intNoiceLevel);
                Toast.makeText(BaiduMapGuidActivity.this, "GPS reset View!", Toast.LENGTH_SHORT).show();
            }*/
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            BNRouteGuideManager.getInstance().setVoiceModeInNavi(intNoiceLevel);
        }
    }

}
