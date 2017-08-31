package com.xptschool.teacher.ui.leave;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.xptschool.teacher.bean.BeanLeave;
import com.xptschool.teacher.common.ActivityResultCode;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;
import com.xptschool.teacher.util.ToastUtils;
import com.xptschool.teacher.view.CalendarView;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

//请假管理
public class LeaveActivity extends BaseListActivity {

    @BindView(R.id.txtDate)
    ArrowTextView txtDate;

    @BindView(R.id.spnClass)
    MaterialSpinner spnClass;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipe_refresh_widget;

    @BindView(R.id.recycleView)
    LoadMoreRecyclerView recycleView;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    private PopupWindow datePopup;
    private LeaveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        setTitle(R.string.home_leave);
        initView();
        initDate();
    }

    private void initView() {
        initRecyclerView(recycleView, swipe_refresh_widget);

        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                if (GreenDaoHelper.getInstance().getAllClass().size() > 0) {
                    getLeaveList();
                } else {
                    swipe_refresh_widget.setRefreshing(false);
                    ToastUtils.showToast(LeaveActivity.this, R.string.toast_class_empty);
                }
            }
        });
        recycleView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getLeaveList();
                }
            }
        });

        adapter = new LeaveAdapter(this);
        recycleView.setAdapter(adapter);

        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLeaveList();
            }
        });

    }

    private void initDate() {
        txtDate.setText(CommonUtil.getCurrentDate());

        List<BeanClass> allClass = GreenDaoHelper.getInstance().getAllClass();

        if (allClass.size() == 0) {
            spnClass.setText(getString(R.string.toast_class_empty));
            spnClass.setEnabled(false);
        } else {
            spnClass.setItems(allClass);
            spnClass.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<BeanClass>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, BeanClass item) {
                    flTransparent.setVisibility(View.GONE);
                    resultPage.setPage(1);
                    getLeaveList();
                }
            });
            spnClass.setOnNothingSelectedListener(spinnerNothingSelectedListener);
            getLeaveList();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getExtras() == null) {
            Log.i(TAG, "onActivityResult: data.getExtras() is null");
            return;
        }
        BeanLeave beanLeave = data.getExtras().getParcelable(ExtraKey.LEAVE_DETAIL);
        if (beanLeave == null) {
            return;
        }

        if (requestCode == 1) {
            if (resultCode == ActivityResultCode.Leave_Edit) {
                recycleView.updateItem(adapter.updateBeanLeave(beanLeave));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getLeaveList() {
        BeanClass currentClass = null;
        try {
            currentClass = (BeanClass) spnClass.getSelectedItem();
        } catch (Exception ex) {
            currentClass = null;
        }

        if (currentClass == null) {
            ToastUtils.showToast(this, R.string.toast_class_empty);
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Leave_QUERY, new VolleyHttpParamsEntity()
                        .addParam("dates", txtDate.getText().toString())
                        .addParam("g_id", currentClass.getG_id())
                        .addParam("c_id", currentClass.getC_id())
                        .addParam("page", resultPage.getPage() + "")
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Leave_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipe_refresh_widget != null && resultPage.getPage() == 1) {
                            swipe_refresh_widget.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        if (swipe_refresh_widget != null && resultPage.getPage() == 1) {
                            swipe_refresh_widget.setRefreshing(false);
                        }
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject jsonObject = new JSONObject(httpResult.getData().toString());
                                    resultPage.setPage(jsonObject.getInt("page"));
                                    resultPage.setTotal_page(jsonObject.getInt("total_page"));
                                    resultPage.setTotal_count(jsonObject.getInt("total_count"));

                                    if (resultPage.getTotal_page() > resultPage.getPage()) {
                                        recycleView.setAutoLoadMoreEnable(true);
                                    } else {
                                        recycleView.setAutoLoadMoreEnable(false);
                                    }

                                    Gson gson = new Gson();
                                    List<BeanLeave> beanLeaves = gson.fromJson(jsonObject.getJSONArray("content").toString(), new TypeToken<List<BeanLeave>>() {
                                    }.getType());

                                    if (resultPage.getPage() > 1) {
                                        adapter.appendData(beanLeaves);
                                    } else {
                                        //第一页数据
                                        if (beanLeaves.size() == 0) {
                                            Toast.makeText(LeaveActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                        }
                                        recycleView.removeAllViews();
                                        adapter.refreshData(beanLeaves);
                                    }
                                    recycleView.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: " + ex.getMessage());
                                    Toast.makeText(LeaveActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(LeaveActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (swipe_refresh_widget != null && resultPage.getPage() == 1) {
                            swipe_refresh_widget.setRefreshing(false);
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
                    if (mode == CalendarView.SELECTION_MODE_SINGLE) {
                        txtDate.setText(date[0]);
                    }
                    resultPage.setPage(1);
                    getLeaveList();
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
