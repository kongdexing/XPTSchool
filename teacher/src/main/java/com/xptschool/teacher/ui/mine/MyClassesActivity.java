package com.xptschool.teacher.ui.mine;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanMyClass;
import com.xptschool.teacher.ui.main.BaseListActivity;

import java.util.List;

import butterknife.BindView;

/**
 * 我的班级
 */
public class MyClassesActivity extends BaseListActivity {

    @BindView(R.id.llTitle)
    LinearLayout llTitle;

    @BindView(R.id.recycleView)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;
    MyClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        setTitle(R.string.mine_class);
        initView();
    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefresh);

        adapter = new MyClassAdapter(this);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getClassesList();
            }
        });

        getClassesList();
    }

    private void getClassesList() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MyClass_QUERY, new VolleyHttpParamsEntity()
                        .addParam("token", CommonUtil.encryptToken(HttpAction.MyClass_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanMyClass> listClasses = gson.fromJson(volleyHttpResult.getData().toString(),
                                            new TypeToken<List<BeanMyClass>>() {
                                            }.getType());
                                    if (listClasses.size() > 0) {
                                        llTitle.setVisibility(View.VISIBLE);
                                    } else {
                                        llTitle.setVisibility(View.GONE);
                                    }
                                    adapter.refreshData(listClasses);
                                } catch (Exception ex) {
                                    Toast.makeText(MyClassesActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(MyClassesActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                    }
                }

        );
    }

}
