package com.xptschool.parent.ui.alarm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.roundcornerprogressbar.RoundCornerProgressBar;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanAlarm;
import com.xptschool.parent.bean.BeanHTLocation;
import com.xptschool.parent.bean.BeanRTLocation;
import com.xptschool.parent.model.BeanStudent;

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

    @BindView(R.id.rlStudentName)
    LinearLayout trStudentName;
    @BindView(R.id.trAlarmType)
    LinearLayout trAlarmType;
    @BindView(R.id.trLocationType)
    LinearLayout trLocationType;

    @BindView(R.id.trPower)
    LinearLayout trPower;
    @BindView(R.id.txtPower)
    TextView txtPower;
    @BindView(R.id.powerBar)
    RoundCornerProgressBar powerBar;

    @BindView(R.id.trSignal)
    LinearLayout trSignal;
    @BindView(R.id.txtSignal)
    TextView txtSignal;
    @BindView(R.id.signalBar)
    RoundCornerProgressBar signalBar;

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
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(alarm.getLatLng()));

        trStudentName.setVisibility(GONE);
        trAlarmType.setVisibility(View.VISIBLE);
        trLocationType.setVisibility(GONE);
        txtIMEI.setText(alarm.getImei());
        txtAlarmType.setText(alarm.getWar_type());
        txtTime.setText(alarm.getCreate_time());
    }

    public void setHistoryData(BeanHTLocation location, LatLng latLng, BeanStudent student, MyOnGetGeoCoderResultListener listener) {
        myOnGetGeoCoderResultListener = listener;
        if (latLng != null) {
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(latLng));
        }
        trAlarmType.setVisibility(View.GONE);
        trLocationType.setVisibility(VISIBLE);
        txtStudentName.setText(student.getStu_name());
        txtIMEI.setText(location.getImei());
        txtTime.setText(location.getCreate_time());
        txtLocationType.setText(location.getGps_type());
    }

    /**
     * 实时位置
     *
     * @param location
     * @param student
     * @param listener
     */
    public void setData(BeanRTLocation location, BeanStudent student, MyOnGetGeoCoderResultListener listener) {
        myOnGetGeoCoderResultListener = listener;
        if (location.getLatLng() != null) {
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(location.getLatLng()));
        }
        trAlarmType.setVisibility(View.GONE);
        trLocationType.setVisibility(VISIBLE);
        trStudentName.setVisibility(GONE);

        txtIMEI.setText(location.getImei());
        txtTime.setText(location.getTime());
        txtLocationType.setText(location.getGps_type());

        trPower.setVisibility(VISIBLE);
        trSignal.setVisibility(VISIBLE);

        txtPower.setText(Integer.parseInt(location.getPower()) + "%");
        int signal = Integer.parseInt(location.getSignal1());

        txtSignal.setText(signal + "%");

        powerBar.setProgressBackgroundColor(this.getContext().getResources().getColor(R.color.color_power));
        powerBar.setMax(100);
        powerBar.setSecondaryProgress(powerBar.getMax());
        powerBar.setSecondaryProgressColor(this.getContext().getResources().getColor(R.color.white));
        powerBar.setProgressColor(this.getContext().getResources().getColor(R.color.color_power));
        powerBar.setProgress(Float.parseFloat(location.getPower()));

        signalBar.setProgressBackgroundColor(this.getContext().getResources().getColor(R.color.color_signal));
        signalBar.setMax(100);
        signalBar.setSecondaryProgress(powerBar.getMax());
        signalBar.setSecondaryProgressColor(this.getContext().getResources().getColor(R.color.white));
        signalBar.setProgressColor(this.getContext().getResources().getColor(R.color.color_signal));
        signalBar.setProgress(signal);

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(final ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
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
