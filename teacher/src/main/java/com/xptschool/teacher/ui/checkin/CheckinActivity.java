package com.xptschool.teacher.ui.checkin;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
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
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanCheckin;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;
import com.xptschool.teacher.view.CalendarView;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 考勤管理
 */
public class CheckinActivity extends BaseListActivity {

    @BindView(R.id.txtDate)
    ArrowTextView txtDate;

    @BindView(R.id.spnClass)
    MaterialSpinner spnClass;

    @BindView(R.id.spnType)
    MaterialSpinner spnType;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.llCheckTitle)
    LinearLayout llCheckTitle;

    @BindView(R.id.recyclerView)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    private PopupWindow datePopup;
    private CheckinAdapter adapter;

    //循序固定，勿乱动
    private static final String[] statuses = {"全部", "进校", "出校"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        setTitle(R.string.home_checkin);
        initView();
        initDate();
    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefresh);

        adapter = new CheckinAdapter(this);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                getCheckinList();
            }
        });
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getCheckinList();
                }
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
                getCheckinList();
            }
        });

        spnType.setItems(statuses);
        spnType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                flTransparent.setVisibility(View.GONE);
                getCheckinList();
            }
        });

        spnClass.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnType.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        getCheckinList();
    }

    @OnClick({R.id.txtDate, R.id.spnClass})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                showDatePop();
                break;
            case R.id.spnClass:
                flTransparent.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getCheckinList() {
        BeanClass currentClass = (BeanClass) spnClass.getSelectedItem();
        int typeIndex = spnType.getSelectedIndex();
        String sign_type = "";
        if (typeIndex == 1) {
            sign_type = "1";
        } else if (typeIndex == 2) {
            sign_type = "0";
        }

        //sign_type '进校 1 出校 0'
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Attendance_QUERY, new VolleyHttpParamsEntity()
                        .addParam("dates", txtDate.getText().toString())
                        .addParam("g_id", currentClass.getG_id())
                        .addParam("c_id", currentClass.getC_id())
                        .addParam("sign_type", sign_type)
                        .addParam("page", resultPage.getPage() + "")
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Attendance_QUERY)),
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
                                    List<BeanCheckin> listCheckins = gson.fromJson(jsonObject.getJSONArray("content").toString(),
                                            new TypeToken<List<BeanCheckin>>() {
                                            }.getType());

                                    if (listCheckins.size() == 0) {
                                        llCheckTitle.setVisibility(View.GONE);
                                        Toast.makeText(CheckinActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                    } else {
                                        llCheckTitle.setVisibility(View.VISIBLE);
                                    }

                                    if (resultPage.getPage() > 1) {
                                        adapter.appendData(listCheckins);
                                    } else {
                                        //第一页数据
                                        recyclerView.removeAllViews();
                                        adapter.refreshData(listCheckins);
                                    }
                                    recyclerView.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                                } catch (Exception ex) {
                                    Toast.makeText(CheckinActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(CheckinActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
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
                    getCheckinList();
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
                    txtDate.collapse();
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        datePopup.showAsDropDown(txtDate, 0, 5);
    }

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };

}
