package com.xptschool.parent.ui.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanRail;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2016/12/12.
 */

public class RailInfoWindowView extends RelativeLayout implements OnGetGeoCoderResultListener {

    @BindView(R.id.txtRailName)
    TextView txtRailName;

    @BindView(R.id.txtRadius)
    TextView txtRadius;

    @BindView(R.id.txtLocation)
    TextView txtLocation;

    GeoCoder mSearch = null;
    MyOnGetGeoCoderResultListener myOnGetGeoCoderResultListener;

    public RailInfoWindowView(Context context) {
        this(context, null);
    }

    public RailInfoWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_rail_infowindow, this, true);
        ButterKnife.bind(view);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    public void setData(BeanRail rail, LatLng latLng, MyOnGetGeoCoderResultListener listener) {
        myOnGetGeoCoderResultListener = listener;
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng));
        txtRailName.setText(rail.getName());
        txtRadius.setText(rail.getCreate_time());
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this.getContext(), "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        txtLocation.setText(result.getAddress());
        if (myOnGetGeoCoderResultListener != null) {
            myOnGetGeoCoderResultListener.onGetReverseGeoCodeResult(result);
            mSearch = null;
        }
    }

    public interface MyOnGetGeoCoderResultListener {
        void onGetReverseGeoCodeResult(ReverseGeoCodeResult result);
    }

}
