package com.xptschool.parent.ui.leave;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.xptschool.parent.bean.BeanLeave;
import com.xptschool.parent.common.ActivityResultCode;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

//请假管理
public class LeaveActivity extends BaseListActivity {

    @BindView(R.id.spnDate)
    MaterialSpinner spnDate;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipe_refresh_widget;

    @BindView(R.id.recycleView)
    LoadMoreRecyclerView recycleView;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

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
                getLeaveList();
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

        if (GreenDaoHelper.getInstance().getStudents().size() > 0) {
            setTxtRight(R.string.label_leave);
            setTextRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LeaveActivity.this, LeaveDetailActivity.class));
                }
            });
        }
    }

    private void initDate() {
        spnDate.setItems(CommonUtil.getExamDate());
        spnDate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                flTransparent.setVisibility(View.GONE);
                resultPage.setPage(1);
                getLeaveList();
            }
        });
        spnDate.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        if (GreenDaoHelper.getInstance().getStudents().size() == 0) {
            spnStudents.setText(R.string.title_no_student);
            spnDate.setEnabled(false);
            spnStudents.setEnabled(false);
            swipe_refresh_widget.setEnabled(false);
            return;
        }

        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                flTransparent.setVisibility(View.GONE);
                resultPage.setPage(1);
                getLeaveList();
            }
        });
        spnStudents.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        getLeaveList();
    }

    @OnClick({R.id.spnDate, R.id.spnStudents})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.spnStudents:
                if (spnStudents.getItems().size() > 0) {
                    flTransparent.setVisibility(View.VISIBLE);
                }
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
            int position = 0;
            if (resultCode == ActivityResultCode.Leave_Edit) {
                position = adapter.updateBeanLeave(beanLeave);
                if (position == -1) {
                    position = adapter.deleteData(beanLeave);
                    if (position != -1) {
                        recycleView.deleteByPosition(position);
                    }
                } else {
                    recycleView.updateItem(position);
                }
            } else if (resultCode == ActivityResultCode.Leave_Del) {
                position = adapter.deleteData(beanLeave);
                if (position != -1) {
                    recycleView.deleteByPosition(position);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getLeaveList() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Leave_QUERY, new VolleyHttpParamsEntity()
                        .addParam("dates", spnDate.getText().toString())
                        .addParam("stu_id", student.getStu_id())
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
                                    Toast.makeText(LeaveActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
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

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };

}
