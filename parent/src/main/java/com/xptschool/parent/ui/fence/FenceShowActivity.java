package com.xptschool.parent.ui.fence;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanRail;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.ui.fragment.RailInfoWindowView;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.view.MarkerNumView;

import java.util.ArrayList;
import java.util.List;

public class FenceShowActivity extends BaseActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> listLatLng = new ArrayList<>();
    private BeanRail rail = null;

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
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
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
        MarkerOptions option = new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding))
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
