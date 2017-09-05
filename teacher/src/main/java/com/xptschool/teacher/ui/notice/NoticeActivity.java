package com.xptschool.teacher.ui.notice;

import android.content.Intent;
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
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanNotice;
import com.xptschool.teacher.common.ActivityResultCode;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;
import com.xptschool.teacher.view.CalendarOptionView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NoticeActivity extends BaseListActivity {

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.rclNotices)
    LoadMoreRecyclerView rclNotices;

    @BindView(R.id.spnClass)
    MaterialSpinner spnClass;

    @BindView(R.id.spnState)
    MaterialSpinner spnState;

    @BindView(R.id.txtDate)
    ArrowTextView txtDate;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    //循序固定，勿乱动
    private static final String[] statuses = {"全部", "已接收", "已发送"};

    private PopupWindow datePopup;
    private String startTime, endTime;
    private NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        setTitle(R.string.home_notice);
        setTxtRight(R.string.push);
        initView();
        initDate();
    }

    private void initView() {
        initRecyclerView(rclNotices, swipeRefresh);

        adapter = new NoticeAdapter(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFirstPageData();
            }
        });
        rclNotices.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getNoticeList();
                }
            }
        });
        rclNotices.setAdapter(adapter);

        setTextRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoticeActivity.this, NoticeDetailActivity.class));
            }
        });
    }

    private void initDate() {
        spnClass.setItems(GreenDaoHelper.getInstance().getAllClassNameAppend());
        spnClass.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<BeanClass>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, BeanClass item) {
                getFirstPageData();
            }
        });

        spnState.setItems(statuses);
        spnState.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                getFirstPageData();
            }
        });

        spnClass.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnState.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        startTime = CommonUtil.getDate2StrBefore(6);
        endTime = CommonUtil.getCurrentDate();

        setTxtDate();
        getFirstPageData();
    }

    private void getFirstPageData() {
        flTransparent.setVisibility(View.GONE);
        resultPage.setPage(1);
        adapter.refreshData(new ArrayList<BeanNotice>());
        getNoticeList();
    }

    //新增，删除，修改
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + resultCode);
        if (data == null || data.getExtras() == null) {
            Log.i(TAG, "onActivityResult: data.getExtras() is null");
            return;
        }
        BeanNotice notice = data.getExtras().getParcelable(ExtraKey.NOTICE_DETAIL);
        if (notice == null) {
            Log.i(TAG, "onActivityResult: notice is null");
            return;
        }

        switch (resultCode) {
            case ActivityResultCode.Notice_delete:
                int position = adapter.deleteData(notice);
                Log.i(TAG, "onActivityResult: deleteData position " + position);
                if (position != -1) {
                    rclNotices.deleteByPosition(position);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.txtDate, R.id.spnClass, R.id.spnState})
    void noticeClick(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                showDatePop();
                break;
            case R.id.spnClass:
            case R.id.spnState:
                flTransparent.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getNoticeList() {
        //type 1接收的公告  2是发送的公告
        int type = 0;
        for (int i = 0; i < statuses.length; i++) {
            if (spnState.getText().toString().equals(statuses[i])) {
                type = i;
                break;
            }
        }
        BeanClass currentClass = (BeanClass) spnClass.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.NOTICE_QUERY,
                new VolleyHttpParamsEntity()
                        .addParam("c_id", currentClass == null ? "" : currentClass.getC_id())
                        .addParam("sdate", startTime)
                        .addParam("edate", endTime)
                        .addParam("page", resultPage.getPage() + "")
                        .addParam("type", type == 0 ? "" : type + "")
                        .addParam("token", CommonUtil.encryptToken(HttpAction.NOTICE_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipeRefresh != null && resultPage.getPage() == 1) {
                            swipeRefresh.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        if (swipeRefresh != null && resultPage.getPage() == 1) {
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
                                        rclNotices.setAutoLoadMoreEnable(true);
                                    } else {
                                        rclNotices.setAutoLoadMoreEnable(false);
                                    }

                                    Gson gson = new Gson();
                                    List<BeanNotice> notices = gson.fromJson(jsonObject.getJSONArray("content").toString(), new TypeToken<List<BeanNotice>>() {
                                    }.getType());

                                    if (resultPage.getPage() > 1) {
                                        adapter.appendData(notices);
                                    } else {
                                        //第一页数据
                                        if (notices.size() == 0) {
                                            Toast.makeText(NoticeActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                        }
                                        rclNotices.removeAllViews();
                                        adapter.refreshData(notices);
                                    }
                                    rclNotices.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                                } catch (Exception ex) {
                                    Toast.makeText(NoticeActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (swipeRefresh != null && resultPage.getPage() == 1) {
                            swipeRefresh.setRefreshing(false);
                        }
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
                    getFirstPageData();
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
        datePopup.showAsDropDown(txtDate, 0, 2);
    }

    private void setTxtDate() {
        txtDate.setText(startTime + "\n" + endTime);
    }

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };

}
