package com.xptschool.parent.ui.homework;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.android.widget.view.ArrowTextView;
import com.android.widget.view.LoadMoreRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanHomeWork;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.view.CalendarOptionView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeWorkActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.txtDate)
    ArrowTextView txtDate;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    private PopupWindow datePopup;
    private List<BeanHomeWork> homeWorks_filter = new ArrayList<>();
    private String startTime, endTime;
    private HomeWorkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work);
        setTitle(R.string.home_homework);
        initView();
        initDate();
    }

    private void initView() {

        initRecyclerView(recyclerView, swipeRefreshLayout);

        adapter = new HomeWorkAdapter(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                getHomeWorkList();
            }
        });
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getHomeWorkList();
                }
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void initDate() {
        startTime = CommonUtil.getDate2StrBefore(7);
        endTime = CommonUtil.getCurrentDate();
        setTxtDate();

        if (GreenDaoHelper.getInstance().getStudents().size() == 0) {
            spnStudents.setText(R.string.title_no_student);
            txtDate.setEnabled(false);
            spnStudents.setEnabled(false);
            swipeRefreshLayout.setEnabled(false);
            return;
        }
        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                flTransparent.setVisibility(View.GONE);
                getHomeWorkList();
            }
        });
        spnStudents.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        getHomeWorkList();
    }

    private void setTxtDate() {
        txtDate.setText(startTime + "\n" + endTime);
    }

    @OnClick({R.id.txtDate, R.id.spnStudents})
    void homeWorkClick(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                showDatePop();
                break;
            case R.id.spnStudents:
                if (spnStudents.getItems().size() != 1) {
                    flTransparent.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void getHomeWorkList() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.HOMEWORK_QUERY,
                new VolleyHttpParamsEntity()
                        .addParam("s_id", student.getS_id())
                        .addParam("a_id", student.getA_id())
                        .addParam("g_id", student.getG_id())
                        .addParam("c_id", student.getC_id())
                        .addParam("sdate", startTime)
                        .addParam("edate", endTime)
                        .addParam("page", resultPage.getPage() + "")
                        .addParam("token", CommonUtil.encryptToken(HttpAction.HOMEWORK_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject jsonObject = new JSONObject(httpResult.getData().toString());
                                    resultPage.setPage(jsonObject.getInt("page"));
                                    resultPage.setTotal_page(jsonObject.getInt("total_page"));
                                    resultPage.setTotal_count(jsonObject.getInt("total_count"));

                                    if (resultPage.getTotal_page() > resultPage.getPage()) {
                                        recyclerView.setAutoLoadMoreEnable(true);
                                    } else {
                                        recyclerView.setAutoLoadMoreEnable(false);
                                    }

                                    Gson gson = new Gson();
                                    List<BeanHomeWork> homeWorks = gson.fromJson(jsonObject.getJSONArray("content").toString(),
                                            new TypeToken<List<BeanHomeWork>>() {
                                            }.getType());

                                    if (resultPage.getPage() > 1) {
                                        adapter.appendData(homeWorks);
                                    } else {
                                        //第一页数据
                                        if (homeWorks.size() == 0) {
                                            Toast.makeText(HomeWorkActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                        }
                                        recyclerView.removeAllViews();
                                        adapter.refreshData(homeWorks);
                                    }
                                    recyclerView.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: " + ex.getMessage());
                                    Toast.makeText(HomeWorkActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(HomeWorkActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(HomeWorkActivity.this, "error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePop() {
        if (datePopup == null) {
            CalendarOptionView calendarView = new CalendarOptionView(this);

            calendarView.setSelectedListener(new CalendarOptionView.CalendarViewSelectedListener() {
                @Override
                public void onCalendarSelected(String sDate, String eDate) {
                    datePopup.dismiss();
                    startTime = sDate;
                    endTime = eDate;
                    setTxtDate();
                    getHomeWorkList();
                }
            });

            datePopup = new PopupWindow(calendarView,
                    LinearLayout.LayoutParams.MATCH_PARENT, CommonUtil.getPopDateHeight(), true);
            datePopup.setTouchable(true);
            datePopup.setBackgroundDrawable(new BitmapDrawable());
            datePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                    txtDate.collapse();
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        datePopup.showAsDropDown(txtDate, 0, 2);
    }

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };

}
