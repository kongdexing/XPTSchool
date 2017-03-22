package com.xptschool.teacher.ui.fragment;

import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanHTLocation;
import com.xptschool.teacher.bean.BeanRTLocation;
import com.xptschool.teacher.bean.BeanRail;
import com.xptschool.teacher.bean.BeanStudent;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.ui.alarm.AlarmInfoWindowView;
import com.xptschool.teacher.view.MarkerNumView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2016/12/6.
 * No1
 */

public class MapBaseFragment extends BaseFragment implements BDLocationListener, SensorEventListener {

    public BaiduMap mBaiduMap;
    public LocationClient mLocClient;
    boolean isFirstLoc = true; // 是否首次定位
    public Marker mGPSMarker;
    public SensorManager mSensorManager;
    public Sensor mSensor;
    private long lastTime = 0;
    public final int DrawTrack = 0;
    public final int DrawLocation = 1;
    public final int DrawRail = DrawLocation + 1;
    public final int SHOW_PROGRESS = DrawRail + 1;
    public final int HIDE_PROGRESS = SHOW_PROGRESS + 1;

    private final int TIME_SENSOR = 100;
    private float mAngle;

    public MapView mMapView;
    public ProgressBar progress;
    private Marker mMarkerStudent;
    private List<BeanHTLocation> htLocations = null;
    private List<BeanRail> listRail = null;
    private int MapInfoTop = 0;
    private int RailIndex = 0;

    public BeanStudent currentStudent;
    private BitmapDescriptor mBlueTexture = null;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
            return;
        }
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION: {
                float x = event.values[0];
                System.out.println(x);
                x += CommonUtil.getScreenRotationOnPhone(getContext());
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

        if (isFirstLoc) {
            isFirstLoc = false;
            MarkerOptions markerOptions = new MarkerOptions().position(ll).icon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location_marker)));
            mGPSMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

        mGPSMarker.setPosition(ll);
    }

    @Override
    protected void initData() {
        MapInfoTop = -(getResources().getDimensionPixelOffset(R.dimen.dp_30));
        Log.i(TAG, "initData: MapInfoTop " + MapInfoTop);
    }

    public void drawLocation(final BeanRTLocation newPt, int timer) {
        mBaiduMap.clear();
//        LatLng pt1 = new LatLng(39.93923, 116.357428);
        final LatLng latLng = newPt.getLatLng();
        if (latLng == null) {
            Toast.makeText(this.getContext(), R.string.toast_point_null, Toast.LENGTH_SHORT).show();
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_gcoding)));
        mMarkerStudent = (Marker) mBaiduMap.addOverlay(markerOptions);

        final AlarmInfoWindowView alarmInfoWindowView = new AlarmInfoWindowView(getContext());
        alarmInfoWindowView.setData(newPt, currentStudent, new AlarmInfoWindowView.MyOnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                final InfoWindow infoWindow = new InfoWindow(alarmInfoWindowView, latLng, MapInfoTop);
                mBaiduMap.showInfoWindow(infoWindow);

                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                    boolean isShowing = true;

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker == mMarkerStudent) {
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

        if (timer == 1) {
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(15.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    public void drawTrack(List<BeanHTLocation> locations) {
        if (locations.size() > 0) {
            mBaiduMap.clear();
            htLocations = locations;
            mHandler.removeCallbacksAndMessages(null);
            //marker初始化
            mHandler.sendEmptyMessage(DrawTrack);
        } else {
            Toast.makeText(mContext, "无轨迹信息", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 画电子围栏
     */
    public void drawRail(List<BeanRail> listFences) {
        mBaiduMap.clear();
        listRail = listFences;

        RailIndex = 0;
        mHandler.removeCallbacksAndMessages(null);
        //marker初始化
        mHandler.sendEmptyMessageDelayed(DrawRail, 0);
    }

    Handler mHandler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS:
                    if (progress != null) {
                        progress.setVisibility(View.VISIBLE);
                    }
                    break;
                case HIDE_PROGRESS:
                    if (progress != null) {
                        progress.setVisibility(View.GONE);
                    }
                    break;
                case DrawLocation:
                    drawLocation((BeanRTLocation) msg.obj, msg.arg1);
                    break;
                case DrawTrack:
                    try {
                        List<LatLng> points = new ArrayList<LatLng>();
                        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                        Log.i(TAG, "handleMessage: htLocation size " + htLocations.size());

                        //起点
                        final LatLng bdStartLatlng = htLocations.get(0).getLatLng();
                        if (bdStartLatlng != null) {
                            MarkerOptions markerStartOptions = new MarkerOptions().position(bdStartLatlng).icon(
                                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_walk_start)));
                            final Marker startMarker = (Marker) mBaiduMap.addOverlay(markerStartOptions);
                            final AlarmInfoWindowView alarmInfoWindowView = new AlarmInfoWindowView(getContext());
                            alarmInfoWindowView.setHistoryData(htLocations.get(0), currentStudent, new AlarmInfoWindowView.MyOnGetGeoCoderResultListener() {
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                                    final InfoWindow infoWindow = new InfoWindow(alarmInfoWindowView, bdStartLatlng, MapInfoTop);
                                    mBaiduMap.showInfoWindow(infoWindow);

                                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                                        boolean isShowing = true;

                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            Log.i(TAG, "onMarkerClick: start marker");
                                            if (marker == startMarker) {
                                                Log.i(TAG, "onMarkerClick: equal startMarker " + isShowing);
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
                        }
                        //终点
                        final LatLng bdEndLatlng = htLocations.get(htLocations.size() - 1).getLatLng();
                        if (bdEndLatlng != null) {
                            MarkerOptions markerEndOptions = new MarkerOptions().position(bdEndLatlng).icon(
                                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_walk_end)));
                            final Marker endMarker = (Marker) mBaiduMap.addOverlay(markerEndOptions);

                            final AlarmInfoWindowView endInfoWindowView = new AlarmInfoWindowView(getContext());
                            endInfoWindowView.setHistoryData(htLocations.get(htLocations.size() - 1), currentStudent, new AlarmInfoWindowView.MyOnGetGeoCoderResultListener() {
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                                    final InfoWindow infoWindow = new InfoWindow(endInfoWindowView, bdEndLatlng, MapInfoTop);
                                    mBaiduMap.showInfoWindow(infoWindow);

                                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                                        boolean isShowing = true;

                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            Log.i(TAG, "onMarkerClick: end marker");
                                            if (marker == endMarker) {
                                                Log.i(TAG, "onMarkerClick: equal endMarker " + isShowing);
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
                        }

                        for (int i = 0; i < htLocations.size(); i++) {
                            LatLng latLngBD = htLocations.get(i).getLatLng();
                            if (latLngBD != null) {
                                points.add(latLngBD);
                                bounds.include(latLngBD);
                            }
                        }
                        if (points.size() > 0) {
                            if (mBlueTexture == null) {
                                mBlueTexture = BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png");
                            }
                            List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
                            textureList.add(mBlueTexture);
                            List<Integer> textureIndexs = new ArrayList<Integer>();
                            textureIndexs.add(0);

                            //.color(0xAAFF0000)
                            OverlayOptions ooPolyline1 = new PolylineOptions().width(16)
                                    .points(points).dottedLine(true).customTextureList(textureList).textureIndex(textureIndexs);

                            Log.i(TAG, "handleMessage: points size " + points.size());
                            mBaiduMap.addOverlay(ooPolyline1);
                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds.build()));
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "handleMessage DrawTrack : " + ex.getMessage());
                    }
                    break;
                case DrawRail:
                    if (RailIndex >= listRail.size()) {
                        RailIndex = 0;
                        return;
                    }
                    try {
                        final List<LatLng> latlngs = listRail.get(RailIndex).getLatLngs();
                        MarkerNumView numView = new MarkerNumView(getContext());
                        numView.setNumber(RailIndex + 1);
                        BitmapDescriptor free_view = BitmapDescriptorFactory.fromView(numView);
                        if (free_view == null) {
                            Log.e(TAG, "handleMessage: freeview is null");
                        }
                        OverlayOptions ooPolygon = new PolygonOptions().points(latlngs)
                                .stroke(new Stroke(5, getResources().getColor(R.color.colorPrimary)))
                                .fillColor(getResources().getColor(R.color.transparent_gray_map));
                        mBaiduMap.addOverlay(ooPolygon);

                        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                        for (int i = 0; i < latlngs.size(); i++) {
                            bounds.include(latlngs.get(i));
                        }
                        final LatLng latlng = bounds.build().getCenter();
                        MarkerOptions option = new MarkerOptions().position(latlng).icon(free_view).zIndex(9).draggable(false);
                        option.animateType(MarkerOptions.MarkerAnimateType.grow);
                        final Marker _marker = (Marker) (mBaiduMap.addOverlay(option));
                        //隐藏其他infoWindow
                        mBaiduMap.hideInfoWindow();
                        //弹出当前infowindow

                        final RailInfoWindowView railInfoWindowView = new RailInfoWindowView(getContext());
                        railInfoWindowView.setData(listRail.get(RailIndex), latlng, new RailInfoWindowView.MyOnGetGeoCoderResultListener() {
                            @Override
                            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
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
                        builder.target(latlng);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        RailIndex++;
                        sendEmptyMessageDelayed(DrawRail, 1500);
                    } catch (Exception ex) {
                        Log.e(TAG, "handleMessage DrawTrack : " + ex.getMessage());
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume() {
        mMapView.onResume();
        registerSensorListener();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        deactivate();
        mHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 停止定位
     */
    public void deactivate() {
//        mListener = null;
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//            locationManager.destory();
//        }
//        locationManager = null;
        unRegisterSensorListener();
    }

    public void registerSensorListener() {
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterSensorListener() {
        mSensorManager.unregisterListener(this, mSensor);
    }


}
