package com.xptschool.parent.ui.alarm;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanAlarm;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

//警报查询
public class AlarmActivity extends BaseListActivity {

    @BindView(R.id.spnDate)
    MaterialSpinner spnDate;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    @BindView(R.id.llCheckTitle)
    LinearLayout llCheckTitle;

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
                getAlarmList();
            }
        });

    }

    private void initDate() {
        spnDate.setItems(CommonUtil.getExamDate());
        spnDate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                flTransparent.setVisibility(View.GONE);
                getAlarmList();
            }
        });
        spnDate.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        if (GreenDaoHelper.getInstance().getStudents().size() == 0) {
            spnStudents.setText(R.string.title_no_student);
            spnStudents.setEnabled(false);
            spnDate.setEnabled(false);
            swipeRefresh.setEnabled(false);
            return;
        }

        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                flTransparent.setVisibility(View.GONE);
                getAlarmList();
            }
        });
        spnStudents.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        getAlarmList();
    }

    @OnClick({R.id.spnStudents})
    void homeWorkClick(View view) {
        switch (view.getId()) {
            case R.id.spnStudents:
                if (spnStudents.getItems().size() > 0) {
                    flTransparent.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void getAlarmList() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_alarm,
                new VolleyHttpParamsEntity()
                        .addParam("dates", spnDate.getText().toString())
                        .addParam("stu_id", student.getStu_id())
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
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                        adapter.clearData();
                        llCheckTitle.setVisibility(View.GONE);
                    }
                });
    }

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };


}
