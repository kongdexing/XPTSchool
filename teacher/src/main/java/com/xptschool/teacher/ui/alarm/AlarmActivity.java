package com.xptschool.teacher.ui.alarm;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.bean.BeanAlarm;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;
import com.xptschool.teacher.view.CalendarView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

//警报查询
public class AlarmActivity extends BaseListActivity {

    @BindView(R.id.llDate)
    LinearLayout llDate;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.spnClass)
    MaterialSpinner spnClass;

    @BindView(R.id.txtDate)
    TextView txtDate;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    @BindView(R.id.llCheckTitle)
    LinearLayout llCheckTitle;

    private PopupWindow datePopup;
    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setTitle(R.string.home_alarm);
        initView();
        initDate();
    }

    private void initView() {
        initRecyclerView(recycleView, swipeRefresh);

        adapter = new AlarmAdapter(this);
        recycleView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAlarmList(txtDate.getText().toString());
            }
        });

    }

    private void initDate() {
        txtDate.setText(CommonUtil.getCurrentDate());

        List<BeanClass> beanClasses = GreenDaoHelper.getInstance().getAllClass();
        if (beanClasses.size() == 0) {
            spnClass.setText(R.string.toast_class_empty);
            Toast.makeText(this, R.string.toast_class_empty, Toast.LENGTH_SHORT).show();
            return;
        } else {
            spnClass.setItems(beanClasses);
        }

        spnClass.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<BeanClass>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, BeanClass item) {
                flTransparent.setVisibility(View.GONE);
                getAlarmList(txtDate.getText().toString());
            }
        });
        spnClass.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        getAlarmList(txtDate.getText().toString());
    }

    @OnClick({R.id.llDate, R.id.spnClass})
    void homeWorkClick(View view) {
        switch (view.getId()) {
            case R.id.llDate:
                showDatePop();
                break;
            case R.id.spnClass:
                if (spnClass.getItems().size() > 0) {
                    flTransparent.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void getAlarmList(String date) {
        BeanClass beanClass = (BeanClass) spnClass.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_alarm,
                new VolleyHttpParamsEntity()
                        .addParam("dates", date)
                        .addParam("c_id", beanClass.getC_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_alarm)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanAlarm> beanAlarms = gson.fromJson(httpResult.getData().toString(), new TypeToken<List<BeanAlarm>>() {
                                    }.getType());
                                    if (beanAlarms.size() > 0) {
                                        llCheckTitle.setVisibility(View.VISIBLE);
                                    }
                                    adapter.refreshData(beanAlarms);
                                } catch (Exception ex) {
                                    adapter.clearData();
                                    llCheckTitle.setVisibility(View.GONE);
                                    Toast.makeText(AlarmActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                adapter.clearData();
                                llCheckTitle.setVisibility(View.GONE);
                                Toast.makeText(AlarmActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                        adapter.clearData();
                        llCheckTitle.setVisibility(View.GONE);
                    }
                });
    }

    private void showDatePop() {
        if (datePopup == null) {
            CalendarView calendarView = new CalendarView(this, CalendarView.SELECTION_MODE_SINGLE);
            calendarView.setContainerGravity(Gravity.CENTER);

            calendarView.setSelectedListener(new CalendarView.CalendarViewSelectedListener() {
                @Override
                public void onCalendarSelected(int mode, String... date) {
                    datePopup.dismiss();
                    String dateStr = txtDate.getText().toString();
                    if (mode == CalendarView.SELECTION_MODE_SINGLE) {
                        dateStr = date[0];
                    }
                    txtDate.setText(dateStr);
                    getAlarmList(txtDate.getText().toString());
                }
            });
            datePopup = new PopupWindow(calendarView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, CommonUtil.getPopDateHeight(), true);
            datePopup.setTouchable(true);
            datePopup.setBackgroundDrawable(new BitmapDrawable());
            datePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        datePopup.showAsDropDown(llDate, 0, 2);
    }

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };


}
