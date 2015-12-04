package com.sen.test.ui.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.sen.test.BaiduMapGuidActivity;
import com.sen.test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/9/25.
 */
public class BaiduMapFragment extends Fragment implements View.OnClickListener {

    public final static int ROUTE_PLAN_BUS = 0x01;
    public final static int ROUTE_PLAN_DRIVING = 0x02;
    public final static int ROUTE_PLAN_WALK = 0x03;

    public static double pi = 3.1415926535897932384626;

    private static final String APP_FOLDER_NAME = "BNSDKDream";

    private String authinfo = null;
    private LocationClient locationClient;
    private MapView baiduMap;
    private BDLocation myBDLocation;
    private String mSDCardPath;
    private MyLocation myLocation;
    private TransitRouteLine transitRouteLine;
    private DrivingRouteLine drivingRouteLine;
    private WalkingRouteLine walkingRouteLine;
    private boolean isBlnLocationStart = false;
    private Marker localMarker;
    private OverlayManager routePlanLay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (initDirs()) {
            initNavi();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baidu_map, null);
        baiduMap = (MapView) view.findViewById(R.id.bmapView);
        view.findViewById(R.id.baidu_map_location).setOnClickListener(this);
        view.findViewById(R.id.baidu_map_route).setOnClickListener(this);
        view.findViewById(R.id.baidu_map_navigation).setOnClickListener(this);
        initLocation();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBlnLocationStart) {
            if (locationClient != null && !locationClient.isStarted()) {
                locationClient.start();
            }
        }
        if (baiduMap != null) {
            baiduMap.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        }
        if (baiduMap != null) {
            baiduMap.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationClient != null && myLocation != null) {
            locationClient.unRegisterLocationListener(myLocation);
        }
        if (baiduMap != null) {
            baiduMap.onDestroy();
        }
    }

    private void initLocation() {
        myLocation = new MyLocation();
        locationClient = new LocationClient(getActivity().getApplicationContext());
        locationClient.registerLocationListener(myLocation);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

        locationClient.setLocOption(option);
    }

    public void location() {
        if (locationClient != null && !locationClient.isStarted()) {
            locationClient.start();
            isBlnLocationStart = true;
        }
    }

    public void route(int type, String city, PlanNode startNode, PlanNode endNode) {
        RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(listener);
        switch (type) {

            case ROUTE_PLAN_BUS:
                if (city != null) {
                    routePlanSearch.transitSearch(new TransitRoutePlanOption().from(startNode).city(city).to(endNode));
                }
                break;

            case ROUTE_PLAN_DRIVING:
                routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
                break;

            case ROUTE_PLAN_WALK:
                routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));
                break;

        }

    }

    public void navigation(double bd09SLat, double bd09SLng, double bd09ELat, double bd09ELng) {
        LatLng newSNode = bd09ToGcj02(bd09SLat, bd09SLng);
        LatLng newENode = bd09ToGcj02(bd09ELat, bd09ELng);
        BNRoutePlanNode sNode = new BNRoutePlanNode(newSNode.longitude,
                newSNode.latitude, null, null, BNRoutePlanNode.CoordinateType.GCJ02);
        BNRoutePlanNode eNode = new BNRoutePlanNode(newENode.longitude,
                newENode.latitude, null, null, BNRoutePlanNode.CoordinateType.GCJ02);
        List<BNRoutePlanNode> list = new ArrayList();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true, new DreamRoutePlanListener(sNode, eNode));
    }

    private void initNavi() {
        BaiduNaviManager.getInstance().setNativeLibraryPath(mSDCardPath + "/BaiduNaviSDK_SO");
        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "Checking key success!";
                        } else {
                            authinfo = "Checking key fail, " + msg;
                        }
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), authinfo, Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    public void initSuccess() {
                        Toast.makeText(getActivity(), "Baidu's guider initting success!", Toast.LENGTH_SHORT).show();
                    }

                    public void initStart() {
                        Toast.makeText(getActivity(), "Baidu's guider initting start!", Toast.LENGTH_SHORT).show();
                    }

                    public void initFailed() {
                        Toast.makeText(getActivity(), "Baidu's guider initting faild!", Toast.LENGTH_SHORT).show();
                    }
                }, null /*mTTSCallback*/);
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.baidu_map_location:
                location();
                break;

            case R.id.baidu_map_route:
                if (myBDLocation != null) {
                    PlanNode stNode = PlanNode.withLocation(new LatLng(myBDLocation.getLatitude(), myBDLocation.getLongitude()));
                    PlanNode edNode = PlanNode.withCityNameAndPlaceName(myBDLocation.getCity(), getString(R.string.text_zhu_gong));
                    route(ROUTE_PLAN_BUS, myBDLocation.getCity(), stNode, edNode);
                }
                break;

            case R.id.baidu_map_navigation:
                if (transitRouteLine != null) {
                    navigation(transitRouteLine.getStarting().getLocation().latitude,
                            transitRouteLine.getStarting().getLocation().longitude,
                            transitRouteLine.getTerminal().getLocation().latitude,
                            transitRouteLine.getTerminal().getLocation().longitude);
                } else if (drivingRouteLine != null) {
                    navigation(drivingRouteLine.getStarting().getLocation().latitude,
                            drivingRouteLine.getStarting().getLocation().longitude,
                            drivingRouteLine.getTerminal().getLocation().latitude,
                            drivingRouteLine.getTerminal().getLocation().longitude);
                } else if (walkingRouteLine != null) {
                    navigation(walkingRouteLine.getStarting().getLocation().latitude,
                            walkingRouteLine.getStarting().getLocation().longitude,
                            walkingRouteLine.getTerminal().getLocation().latitude,
                            walkingRouteLine.getTerminal().getLocation().longitude);
                }

                break;

        }
    }

    private LatLng bd09ToGcj02(double bdLat, double bdLon) {
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double ggLon = z * Math.cos(theta);
        double ggLat = z * Math.sin(theta);
        return new LatLng(ggLat, ggLon);
    }

    private OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getActivity(), getString(R.string.text_tip_route_plan_faild), Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap.getMap());
                baiduMap.getMap().setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                walkingRouteLine = result.getRouteLines().get(0);
                if (routePlanLay != null) {
                    routePlanLay.removeFromMap();
                }
                routePlanLay = overlay;
            }
        }

        public void onGetTransitRouteResult(TransitRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getActivity(), getString(R.string.text_tip_route_plan_faild), Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap.getMap());
                baiduMap.getMap().setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                transitRouteLine = result.getRouteLines().get(0);
                if (routePlanLay != null) {
                    routePlanLay.removeFromMap();
                }
                routePlanLay = overlay;
            }
        }

        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(getActivity(), getString(R.string.text_tip_route_plan_faild), Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap.getMap());
                baiduMap.getMap().setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                drivingRouteLine = result.getRouteLines().get(0);
                if (routePlanLay != null) {
                    routePlanLay.removeFromMap();
                }
                routePlanLay = overlay;
            }
        }
    };

    private class MyLocation implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                myBDLocation = bdLocation;
                LatLng myLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                baiduMap.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(myLocation));
                // 开启定位图层
                baiduMap.getMap().setMyLocationEnabled(true);
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher);
                OverlayOptions overlayOptions = new MarkerOptions().position(myLocation).icon(mCurrentMarker);
                if (localMarker != null) {
                    localMarker.remove();
                }
                localMarker = (Marker) baiduMap.getMap().addOverlay(overlayOptions);
                // 当不需要定位图层时关闭定位图层
                baiduMap.getMap().setMyLocationEnabled(false);
            }
        }
    }

    private class DreamRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mSBNRoutePlanNode = null;
        private BNRoutePlanNode mEBNRoutePlanNode = null;

        public DreamRoutePlanListener(BNRoutePlanNode snode, BNRoutePlanNode enode) {
            mSBNRoutePlanNode = snode;
            mEBNRoutePlanNode = enode;
        }

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(getActivity(), BaiduMapGuidActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(BaiduMapGuidActivity.ROUTE_PLAN_START_NODE, mSBNRoutePlanNode);
            bundle.putSerializable(BaiduMapGuidActivity.ROUTE_PLAN_END_NODE, mEBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(getActivity(), R.string.text_tip_navigation_faild, Toast.LENGTH_SHORT).show();
        }
    }

}
