package com.xptschool.parent.ui.checkin;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.android.widget.view.LoadMoreRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanCheckin;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 考勤管理
 */
public class CheckinActivity extends BaseListActivity {

    @BindView(R.id.spnDate)
    MaterialSpinner spnDate;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.spnType)
    MaterialSpinner spnType;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.llCheckTitle)
    LinearLayout llCheckTitle;

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    private CheckinAdapter adapter;
    //循序固定，勿乱动
    private static final String[] statuses = {"全部", "进校", "出校"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        setTitle(R.string.home_checkin);
        initView();
    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefresh);
        spnDate.setItems(CommonUtil.getExamDate());
        spnDate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                getFirstPageData();
            }
        });

        if (GreenDaoHelper.getInstance().getStudents().size() == 0) {
            spnStudents.setText(R.string.title_no_student);
            spnDate.setEnabled(false);
            spnStudents.setEnabled(false);
            swipeRefresh.setEnabled(false);
            return;
        }

        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                getFirstPageData();
            }
        });

        spnType.setItems(statuses);
        spnType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                getFirstPageData();
            }
        });

        spnStudents.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnDate.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnType.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        adapter = new CheckinAdapter(this);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFirstPageData();
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

        getFirstPageData();
    }

    private void getFirstPageData() {
        flTransparent.setVisibility(View.GONE);
        resultPage.setPage(1);
        adapter.refreshData(new ArrayList<BeanCheckin>());
        getCheckinList();
    }

    @OnClick({R.id.spnDate, R.id.spnStudents})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.spnDate:
            case R.id.spnStudents:
                flTransparent.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getCheckinList() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();
        int typeIndex = spnType.getSelectedIndex();
        String sign_type = "";
        if (typeIndex == 1) {
            sign_type = "1";
        } else if (typeIndex == 2) {
            sign_type = "0";
        }

        //sign_type '进校 1 出校 0'
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Attendance_QUERY, new VolleyHttpParamsEntity()
                        .addParam("dates", spnDate.getText().toString())
                        .addParam("page", resultPage.getPage() + "")
                        .addParam("sign_type", sign_type)
                        .addParam("stu_id", student.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Attendance_QUERY)),
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
                                    Toast.makeText(CheckinActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
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
                        if (swipeRefresh != null && resultPage.getPage() == 1) {
                            swipeRefresh.setRefreshing(false);
                        }
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
