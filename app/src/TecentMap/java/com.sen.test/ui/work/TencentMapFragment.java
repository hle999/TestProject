package com.sen.test.ui.work;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sen.test.R;
import com.sen.test.ui.fragment.BaseFragment;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.param.DrivingParam;
import com.tencent.lbssearch.object.param.RoutePlanningParam;
import com.tencent.lbssearch.object.param.TransitParam;
import com.tencent.lbssearch.object.param.WalkingParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;
import com.tencent.lbssearch.object.result.DrivingResultObject;
import com.tencent.lbssearch.object.result.TransitResultObject;
import com.tencent.lbssearch.object.result.WalkingResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptor;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.mapsdk.raster.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/11/17.
 */
public class TencentMapFragment extends BaseFragment implements TencentLocationListener,
        View.OnClickListener{

    private final static int DIRECTION_WALKING = 0x01;
    private final static int DIRECTION_DRIVING = DIRECTION_WALKING + 1;
    private final static int DIRECTION_TRANSIT = DIRECTION_DRIVING + 1;

    private TencentLocation mLocation;
    private MapView mapview=null;
    private Toast toast;

    private Location currentLocation;
    private TencentSearch tencentSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tencent_map, null);
        mapview=(MapView)root.findViewById(R.id.mapview);
        mapview.onCreate(savedInstanceState);

        root.findViewById(R.id.fragment_tencent_map_location).setOnClickListener(this);
        root.findViewById(R.id.fragment_tencent_map_route).setOnClickListener(this);
        tencentSearch = new TencentSearch(getActivity());

        return root;
    }

    @Override
    public void onDestroyView() {
        TencentLocationManager locationManager = TencentLocationManager.getInstance(getActivity());
        locationManager.removeUpdates(this);
        mapview.onDestroyView();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mapview.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        mapview.onStop();
        super.onStop();
    }

    private void tencentRouteSearch(int type, Location fromLocation, Location toLocation) {
        RoutePlanningParam routePlanningParam = null;
        switch (type) {

            case DIRECTION_WALKING:
                routePlanningParam = new WalkingParam();
                routePlanningParam.from(fromLocation);
                routePlanningParam.to(toLocation);
                break;

            case DIRECTION_DRIVING:
                routePlanningParam = new DrivingParam();
                routePlanningParam.from(fromLocation);
                routePlanningParam.to(toLocation);
                //策略
                ((DrivingParam) routePlanningParam).policy(RoutePlanningParam.DrivingPolicy.LEAST_DISTANCE);
                break;

            case DIRECTION_TRANSIT:
                routePlanningParam = new TransitParam();
                routePlanningParam.from(fromLocation);
                routePlanningParam.to(toLocation);
                //策略
                ((TransitParam) routePlanningParam).policy(RoutePlanningParam.TransitPolicy.LEAST_TIME);
                break;

        }
        if (routePlanningParam != null) {
            DirectionReponseListener directionReponseListener = new DirectionReponseListener();
            tencentSearch.getDirection(routePlanningParam, directionReponseListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fragment_tencent_map_location:
                TencentLocationRequest request = TencentLocationRequest.create();
                request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO);
                request.setInterval(60 * 1000l);
                TencentLocationManager locationManager = TencentLocationManager.getInstance(getActivity());
                int error = locationManager.requestLocationUpdates(request, this);
                if (error == TencentLocation.ERROR_OK) {
                    toast = Toast.makeText(getActivity(), "Start to loacation!", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    toast = Toast.makeText(getActivity(), "Failed to start loacation by " + error, Toast.LENGTH_LONG);
                    toast.show();
                }
                break;

            case R.id.fragment_tencent_map_route:
                if (currentLocation != null) {
                    Address2GeoParam param = new Address2GeoParam()
                            .address(getString(R.string.text_zhu_gong))
                            .region(getString(R.string.text_zhu_hai));
                    tencentSearch.address2geo(param, new HttpResponseListener() {
                        @Override
                        public void onSuccess(int i, Header[] headers, BaseObject baseObject) {
                            if (baseObject != null) {
                                Address2GeoResultObject oj = (Address2GeoResultObject)baseObject;
                                TencentMapFragment.this.tencentRouteSearch(DIRECTION_TRANSIT, currentLocation, oj.result.location);
                                System.out.println("TencentMapSearch success");
                            } else {
                                System.out.println("TencentMapSearch success but null!");
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            System.out.println("TencentMapSearch onFailed!");
                        }
                    });
                }
                break;

        }
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String s) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            toast.setText("Location success!");
            mLocation = tencentLocation;

            /**
             * location
             */
            Bitmap bmpMarker = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            LatLng latLng = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmpMarker);
            if (marker != null) {
                marker.remove();
            }
            marker = mapview.getMap().addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptor).draggable(true));
            mapview.getMap().animateTo(latLng);
            currentLocation = new Location();
            currentLocation.lat((float) tencentLocation.getLatitude());
            currentLocation.lng((float) tencentLocation.getLongitude());
        } else {
            // 定位失败
            toast.setText("Location failed!");
        }
    }

    private Marker marker;

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    protected List<LatLng> getLatLngs(List<Location> locations) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        for (Location location : locations) {
            latLngs.add(new LatLng(location.lat, location.lng));
        }
        return latLngs;
    }

    protected void drawSolidLine(List<Location> locations) {
        mapview.getMap().addPolyline(new PolylineOptions().
                addAll(getLatLngs(locations)).
                color(0xff2200ff));
    }

    protected void drawDotLine(List<Location> locations) {
        mapview.getMap().addPolyline(new PolylineOptions().
                addAll(getLatLngs(locations)).
                color(0xff2200ff).
                setDottedLine(true));
    }

    private class DirectionReponseListener implements HttpResponseListener {

        @Override
        public void onSuccess(int i, Header[] headers, BaseObject baseObject) {
            if (baseObject instanceof WalkingResultObject) {
                List<WalkingResultObject.Route> walkRouteList = ((WalkingResultObject) baseObject).result.routes;
                drawSolidLine(walkRouteList.get(0).polyline);
            } else if (baseObject instanceof TransitResultObject) {
                List<TransitResultObject.Route> transitRouteList = ((TransitResultObject) baseObject).result.routes;
                List<TransitResultObject.Segment> segments =
                        transitRouteList.get(0).steps;
                for (TransitResultObject.Segment segment : segments) {
                    if (segment instanceof TransitResultObject.Walking) {
                        drawDotLine(((TransitResultObject.Walking) segment).polyline);
                    } if (segment instanceof TransitResultObject.Transit) {
                        drawSolidLine(((TransitResultObject.Transit) segment).lines.get(0).polyline);
                    }
                }
            } else if (baseObject instanceof DrivingResultObject) {
                List<DrivingResultObject.Route> drivingRouteList = ((DrivingResultObject) baseObject).result.routes;;
            }
            System.out.println("TencentMapRoute success!");
        }

        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            System.out.println("TencentMapRoute failed!");
        }
    }
}


