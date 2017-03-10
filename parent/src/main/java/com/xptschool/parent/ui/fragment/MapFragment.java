package com.xptschool.parent.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.android.widget.view.ArrowTextView;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanHTLocation;
import com.xptschool.parent.bean.BeanRTLocation;
import com.xptschool.parent.bean.BeanRail;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.alarm.AlarmActivity;
import com.xptschool.parent.ui.fence.FenceListActivity;
import com.xptschool.parent.view.TimePickerPopupWindow;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends MapBaseFragment {

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;
    @BindView(R.id.txtSDate)
    ArrowTextView txtSDate;
    @BindView(R.id.txtEDate)
    ArrowTextView txtEDate;
    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;
    @BindView(R.id.llLocation)
    LinearLayout llLocation;
    @BindView(R.id.llTrack)
    LinearLayout llTrack;
    @BindView(R.id.llRailings)
    LinearLayout llRailings;

    private Timer timer = null;
    private TimerTask task;
    TimePickerPopupWindow sDatePopup, eDatePopup;
    private String startTime, endTime;
    private int locationTime = 0;

    public MapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, mRootView);
        mMapView = (MapView) mRootView.findViewById(R.id.mapView);
        progress = (ProgressBar) mRootView.findViewById(R.id.progress);
        //初始化传感器
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        return mRootView;
    }

    @Override
    protected void initData() {
        super.initData();
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(true);
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(this);

//        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                .fromResource(R.mipmap.icon_geobl);
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(CommonUtil.getDateBefore(1));
        endTime = CommonUtil.getCurrentDateTime();
        txtSDate.setText(startTime);
        txtEDate.setText(endTime);

        if (GreenDaoHelper.getInstance().getStudents().size() == 0) {
            spnStudents.setText(R.string.title_no_student);
            spnStudents.setEnabled(false);
            return;
        }

        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        currentStudent = (BeanStudent) spnStudents.getSelectedItem();

        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<BeanStudent>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, BeanStudent item) {
                flTransparent.setVisibility(View.GONE);
                currentStudent = item;
                //获取不同数据
                reloadDataByStatus();
            }
        });

        spnStudents.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                flTransparent.setVisibility(View.GONE);
            }
        });

        llTrack.setTag(false);
        llLocation.setTag(false);
        llRailings.setTag(false);
    }

    @OnClick({R.id.txtSDate, R.id.txtEDate, R.id.llLocation, R.id.llTrack, R.id.llRailings, R.id.spnStudents, R.id.llAlarm, R.id.llMyLocation})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.txtSDate:
                showSDatePop();
                break;
            case R.id.txtEDate:
                showEDatePop();
                break;
            case R.id.spnStudents:
                if (spnStudents.getItems().size() != 1) {
                    flTransparent.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.llLocation:
                if (view.getTag() == null || !(Boolean) view.getTag()) {
                    if (currentStudent == null) {
                        Toast.makeText(mContext, R.string.toast_choose_student, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startRTLocationTimer();
                } else {
                    cancelRTLocationTimer();
                }
                resetDefaultBg(view);
                llTrack.setTag(false);
                llRailings.setTag(false);
                break;
            case R.id.llTrack:
                if (view.getTag() == null || !(Boolean) view.getTag()) {
                    if (currentStudent == null) {
                        Toast.makeText(mContext, R.string.toast_choose_student, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    cancelRTLocationTimer();
                    getHistoryTrackByStu(startTime, endTime);
                } else {
                    mHandler.removeCallbacksAndMessages(null);
                }
                resetDefaultBg(view);
                llLocation.setTag(false);
                llRailings.setTag(false);
                break;
            case R.id.llRailings:
                if (view.getTag() == null || !(Boolean) view.getTag()) {
                    if (currentStudent == null) {
                        Toast.makeText(mContext, R.string.toast_choose_student, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, "viewClick: llRailings " + false);
                    mHandler.removeCallbacksAndMessages(null);
                    cancelRTLocationTimer();
                    getStudentRail();
                }
                resetDefaultBg(view);
                llTrack.setTag(false);
                llLocation.setTag(false);
                break;
            case R.id.llAlarm:
                startActivity(new Intent(getContext(), AlarmActivity.class));
                break;
            case R.id.llMyLocation:
                isFirstLoc = true;
                break;
        }
    }

    private void resetDefaultBg(View currentView) {
        llLocation.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selector));
        llTrack.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selector));
        llRailings.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selector));

        if (currentView.getTag() == null || !(Boolean) currentView.getTag()) {
            currentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selected));
            currentView.setTag(true);
        } else {
            currentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_normal));
            currentView.setTag(false);
        }
        //设置其他按钮tag 为 false
    }

    private void reloadDataByStatus() {
        if ((Boolean) llLocation.getTag()) {
            getRealTimeLocationByStu();
        }

        if ((Boolean) llTrack.getTag()) {
            getHistoryTrackByStu(startTime, endTime);
        }

        if ((Boolean) llRailings.getTag()) {
            getStudentRail();
        }
    }

    private void showSDatePop() {
        if (sDatePopup == null) {
            sDatePopup = new TimePickerPopupWindow(this.getContext(), txtSDate.getText().toString(),
                    new TimePickerPopupWindow.OnTimePickerClickListener() {

                        @Override
                        public void onTimePickerResult(String result) {
                            if (!result.isEmpty()) {
                                txtSDate.setText(result);
                                startTime = result;
                            }
                            reloadDataByStatus();
                        }
                    });
            sDatePopup.setTouchable(true);
            sDatePopup.setBackgroundDrawable(new ColorDrawable());
            sDatePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        sDatePopup.showAsDropDown(txtSDate, 0, 3);
    }

    private void showEDatePop() {
        if (eDatePopup == null) {
            eDatePopup = new TimePickerPopupWindow(this.getContext(), txtEDate.getText().toString(),
                    new TimePickerPopupWindow.OnTimePickerClickListener() {

                        @Override
                        public void onTimePickerResult(String result) {
                            if (!result.isEmpty()) {
                                txtEDate.setText(result);
                                endTime = result;
                            }
                            reloadDataByStatus();
                        }
                    });
            eDatePopup.setTouchable(true);
            eDatePopup.setBackgroundDrawable(new ColorDrawable());
            eDatePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        eDatePopup.showAsDropDown(txtEDate, 0, 3);
    }

    private void startRTLocationTimer() {
        locationTime = 0;
        task = new TimerTask() {
            @Override
            public void run() {
                getRealTimeLocationByStu();
            }
        };
        if (timer == null) {
            timer = new Timer(true);
        }
        //start the timer
        timer.schedule(task, 1000, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRTLocationTimer();
    }

    private void cancelRTLocationTimer() {
        // Stop Timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
        }
    }

    private void getRealTimeLocationByStu() {
        locationTime++;
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_RealTime, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_RealTime)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        mHandler.sendEmptyMessage(SHOW_PROGRESS);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        mHandler.sendEmptyMessage(HIDE_PROGRESS);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    BeanRTLocation rtLocation = gson.fromJson(volleyHttpResult.getData().toString(), BeanRTLocation.class);

                                    Message message = new Message();
                                    message.what = DrawLocation;
                                    message.arg1 = locationTime;
                                    message.obj = rtLocation;
                                    mHandler.sendMessage(message);
                                } catch (Exception ex) {
                                    Toast.makeText(mContext, "解析失败：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                cancelRTLocationTimer();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mHandler.sendEmptyMessage(HIDE_PROGRESS);
                        Toast.makeText(mContext, "获取失败", Toast.LENGTH_SHORT).show();
                        cancelRTLocationTimer();
                    }
                });
    }

    private void getHistoryTrackByStu(String sDate, String eDate) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_HistoryTrack, new VolleyHttpParamsEntity()
                        .addParam("sdate", sDate + ":00")
                        .addParam("edate", eDate + ":00")
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_HistoryTrack)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanHTLocation> listLocations = gson.fromJson(volleyHttpResult.getData().toString(),
                                            new TypeToken<List<BeanHTLocation>>() {
                                            }.getType());
                                    drawTrack(listLocations);
                                } catch (Exception ex) {
                                    Toast.makeText(mContext, "解析错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(mContext, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getStudentRail() {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_StudentFence, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_StudentFence)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanRail> listFence = gson.fromJson(volleyHttpResult.getData().toString(),
                                            new TypeToken<List<BeanRail>>() {
                                            }.getType());
                                    if (listFence.size() == 0) {
                                        Toast.makeText(mContext, R.string.toast_railing_empty, Toast.LENGTH_SHORT).show();
                                    }
                                    drawRail(listFence);
                                } catch (Exception ex) {
                                    Toast.makeText(mContext, "解析错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(mContext, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
