package com.xptschool.parent.ui.fence;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanRail;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.ui.fragment.RailInfoWindowView;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FenceShowActivity extends BaseActivity implements BDLocationListener, SensorEventListener {

    @BindView(R.id.llMyLocation)
    LinearLayout llMyLocation;
    @BindView(R.id.mapView)
    MapView mMapView;

    private BaiduMap mBaiduMap;
    private List<LatLng> listLatLng = new ArrayList<>();
    private BeanRail rail = null;
    public SensorManager mSensorManager;
    public Sensor mSensor;
    public LocationClient mLocClient;
    boolean isFirstLoc = false; // 是否首次定位
    public Marker mGPSMarker;
    private long lastTime = 0;
    private final int TIME_SENSOR = 100;
    private float mAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_show);
        setTitle("查看围栏");
        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        rail = (BeanRail) bundle.getSerializable(ExtraKey.FENCE_ID);
        if (rail == null) {
            return;
        }
        showFence();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // 开启定位图层
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(true);
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(this);

//        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_geobl);
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @OnClick({R.id.llMyLocation})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.llMyLocation:
                isFirstLoc = true;
                if (mLocClient != null) {
                    mLocClient.start();
                }
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
            return;
        }
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION: {
                float x = event.values[0];
                System.out.println(x);
                x += CommonUtil.getScreenRotationOnPhone(this);
                x %= 360.0F;
                if (x > 180.0F)
                    x -= 360.0F;
                else if (x < -180.0F)
                    x += 360.0F;
                if (Math.abs(mAngle - 90 + x) < 3.0f) {
                    break;
                }
                mAngle = x;
                if (mGPSMarker != null) {
                    mGPSMarker.setRotate(-mAngle);
                }
                lastTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();

        mBaiduMap.setMyLocationData(locData);

        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        Log.i(TAG, "onReceiveLocation: " + location.getAddrStr() + location.getLatitude() + " longitude " + location.getLongitude());

        if (isFirstLoc) {
            isFirstLoc = false;
            MarkerOptions markerOptions = new MarkerOptions().position(ll).icon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_marker)));
            if (mGPSMarker != null) {
                mGPSMarker.remove();
            }
            mGPSMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

        if (mGPSMarker != null) {
            mGPSMarker.setPosition(ll);
        }
    }

    private void showFence() {
        final List<LatLng> latlngs = rail.getLatLngs();
        OverlayOptions ooPolygon = new PolygonOptions().points(latlngs)
                .stroke(new Stroke(5, getResources().getColor(R.color.colorPrimary)))
                .fillColor(getResources().getColor(R.color.transparent_gray_map));
        mBaiduMap.addOverlay(ooPolygon);

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (int i = 0; i < latlngs.size(); i++) {
            bounds.include(latlngs.get(i));
        }
        final LatLng latlng = bounds.build().getCenter();
        MarkerOptions option = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding))
                .zIndex(9).draggable(false);
        option.animateType(MarkerOptions.MarkerAnimateType.grow);
        final Marker _marker = (Marker) (mBaiduMap.addOverlay(option));
        //隐藏其他infoWindow
        mBaiduMap.hideInfoWindow();
        //弹出当前infowindow

        final RailInfoWindowView railInfoWindowView = new RailInfoWindowView(FenceShowActivity.this);
        railInfoWindowView.setData(rail, latlng, new RailInfoWindowView.MyOnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                int MapInfoTop = -(getResources().getDimensionPixelOffset(R.dimen.dp_25));
                final InfoWindow infoWindow = new InfoWindow(railInfoWindowView, latlng, MapInfoTop);
                mBaiduMap.showInfoWindow(infoWindow);

                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                    boolean isShowing = true;

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.equals(_marker)) {
                            if (isShowing) {
                                mBaiduMap.hideInfoWindow();
                            } else {
                                mBaiduMap.showInfoWindow(infoWindow);
                            }
                            isShowing = !isShowing;
                        }
                        return true;
                    }
                });
            }
        });
        //地图移动到当前marker
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latlng).zoom(17.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

}
