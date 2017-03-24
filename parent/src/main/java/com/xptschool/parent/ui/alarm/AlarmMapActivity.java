package com.xptschool.parent.ui.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanAlarm;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.view.MarkerStudentView;

import butterknife.BindView;

public class AlarmMapActivity extends BaseActivity {

    @BindView(R.id.bmapView)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private InfoWindow mInfoWindow;
    private BeanAlarm currentAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_map);
        setTitle(R.string.home_alarm);

        mBaiduMap = mMapView.getMap();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentAlarm = bundle.getParcelable(ExtraKey.ALARM_DETAIL);
        }

        if (currentAlarm != null) {
            setTitle(currentAlarm.getStu_name());
            initOverlay();
            if (currentAlarm.getWar_status().equals("0")) {
                putAlarm();
            }
        }
    }

    private void initOverlay() {
        // add marker overlay
        final LatLng llA = currentAlarm.getLatLng();
        if (llA == null) {
            Log.i(TAG, "initOverlay: " + currentAlarm.getLongitude() + " -- " + currentAlarm.getLatitude());
            Toast.makeText(this, R.string.toast_point_null, Toast.LENGTH_SHORT).show();
            return;
        }

        MarkerStudentView studentView = new MarkerStudentView(this);
        studentView.isBoy(currentAlarm.getStu_sex().equals("1"));
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(studentView);

        MarkerOptions ooA = new MarkerOptions().position(llA).icon(descriptor).zIndex(-1);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        float max = mBaiduMap.getMaxZoomLevel();
        float min = mBaiduMap.getMinZoomLevel();

        Log.i(TAG, "initOverlay: maxZoomLevel " + max + " minZoomLevel " + min);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, max - 6);
        mBaiduMap.animateMapStatus(u);

        final int MapInfoTop = -(getResources().getDimensionPixelOffset(R.dimen.dp_45));

        final AlarmInfoWindowView alarmInfoWindowView = new AlarmInfoWindowView(this);
        alarmInfoWindowView.setAlarmData(currentAlarm, new AlarmInfoWindowView.MyOnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                final InfoWindow infoWindow = new InfoWindow(alarmInfoWindowView, llA, MapInfoTop);
                mBaiduMap.showInfoWindow(infoWindow);

                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                    boolean isShowing = true;

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker == mMarkerA) {
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

    private void putAlarm() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_Alarm_edit, new VolleyHttpParamsEntity()
                        .addParam("wm_id", currentAlarm.getWm_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_Alarm_edit)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                currentAlarm.setWar_status("1");
                                Intent intent = new Intent();
                                intent.setAction(BroadcastAction.ALARM_AMEND);
                                intent.putExtra(ExtraKey.ALARM_DETAIL, currentAlarm);
                                sendBroadcast(intent);
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
    }

}
