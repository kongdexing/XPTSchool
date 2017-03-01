package com.xptschool.teacher.ui.alarm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanAlarm;
import com.xptschool.teacher.bean.BeanHTLocation;
import com.xptschool.teacher.bean.BeanRTLocation;
import com.xptschool.teacher.bean.BeanStudent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/9.
 */

public class AlarmInfoWindowView extends RelativeLayout implements OnGetGeoCoderResultListener {

    @BindView(R.id.txtStudentName)
    TextView txtStudentName;

    @BindView(R.id.txtIMEI)
    TextView txtIMEI;

    @BindView(R.id.txtTime)
    TextView txtTime;

    @BindView(R.id.txtLocationType)
    TextView txtLocationType;

    @BindView(R.id.txtAlarmType)
    TextView txtAlarmType;

    @BindView(R.id.txtLocation)
    TextView txtLocation;

    @BindView(R.id.trStudentName)
    LinearLayout trStudentName;
    @BindView(R.id.trAlarmType)
    LinearLayout trAlarmType;
    @BindView(R.id.trLocationType)
    LinearLayout trLocationType;

    GeoCoder mSearch = null;
    MyOnGetGeoCoderResultListener myOnGetGeoCoderResultListener;

    public AlarmInfoWindowView(Context context) {
        this(context, null);
    }

    public AlarmInfoWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_alarm_infowindow, this, true);
        ButterKnife.bind(view);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    public void setAlarmData(BeanAlarm alarm, MyOnGetGeoCoderResultListener listener) {
        myOnGetGeoCoderResultListener = listener;
        if (alarm.getLatLng() != null) {
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(alarm.getLatLng()));
        }

        trStudentName.setVisibility(GONE);
        trAlarmType.setVisibility(View.VISIBLE);
        trLocationType.setVisibility(GONE);
        txtIMEI.setText(alarm.getImei());
        txtAlarmType.setText(alarm.getWar_type());
        txtTime.setText(alarm.getCreate_time());
    }

    public void setHistoryData(BeanHTLocation location, BeanStudent student, MyOnGetGeoCoderResultListener listener) {
        myOnGetGeoCoderResultListener = listener;
        if (location.getLatLng() != null) {
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(location.getLatLng()));
        }
        trAlarmType.setVisibility(View.GONE);
        trLocationType.setVisibility(VISIBLE);

        txtStudentName.setText(student.getStu_name());
        txtIMEI.setText(location.getImei());
        txtTime.setText(location.getCreate_time());
        txtLocationType.setText(location.getGps_type());
    }

    public void setData(BeanRTLocation location, BeanStudent student, MyOnGetGeoCoderResultListener listener) {
        myOnGetGeoCoderResultListener = listener;
        if (location.getLatLng() != null) {
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(location.getLatLng()));
        }
        trAlarmType.setVisibility(View.GONE);
        trLocationType.setVisibility(VISIBLE);

        txtStudentName.setText(student.getStu_name());
        txtIMEI.setText(location.getImei());
        txtTime.setText(location.getTime());
        txtLocationType.setText(location.getGps_type());
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
