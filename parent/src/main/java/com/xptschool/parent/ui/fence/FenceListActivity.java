package com.xptschool.parent.ui.fence;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.groupexpandable.FloatingGroupExpandableListView;
import com.android.widget.groupexpandable.WrapperExpandableListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanRail;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class FenceListActivity extends BaseActivity {

    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.expandableview)
    FloatingGroupExpandableListView expandableview;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipe_refresh_widget;
    WrapperExpandableListAdapter wrapperAdapter;
    FencesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_list);
        setTitle(R.string.label_fence_list);
        initView();
        setTxtRight(R.string.app_name);

        setTxtRight(R.string.label_modify);
        setTextRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtVal = ((TextView) v).getText().toString();
                if (txtVal.equals(getString(R.string.label_modify))) {
                    Intent intent = new Intent();
                    intent.setAction(BroadcastAction.FENCE_MODIFY);
                    sendBroadcast(intent);
                    setTxtRight(R.string.btn_cancel);
                } else if (txtVal.equals(getString(R.string.btn_cancel))) {
                    Intent intent = new Intent();
                    intent.setAction(BroadcastAction.FENCE_CANCEL);
                    sendBroadcast(intent);
                    setTxtRight(R.string.label_modify);
                }
            }
        });
    }

    private void initView() {
        adapter = new FencesAdapter(this);
        wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        expandableview.setAdapter(wrapperAdapter);
        swipe_refresh_widget.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFenceList();
            }
        });
        getFenceList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                getFenceList();
            }
        }
    }

    private void getFenceList() {
        final List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
        if (students.size() == 0) {
            Toast.makeText(this, "您还未绑定需要监护的学生，请绑定后再操作", Toast.LENGTH_SHORT).show();
            return;
        }
        String stuIds = "";
        for (int i = 0; i < students.size(); i++) {
            stuIds += students.get(i).getStu_id() + ",";
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_StudentFence,
                new VolleyHttpParamsEntity()
                        .addParam("stu_id", stuIds)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_StudentFence)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipe_refresh_widget != null) {
                            swipe_refresh_widget.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (swipe_refresh_widget != null) {
                            swipe_refresh_widget.setRefreshing(false);
                        }
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanRail> listFence = gson.fromJson(volleyHttpResult.getData().toString(),
                                            new TypeToken<List<BeanRail>>() {
                                            }.getType());

                                    LinkedHashMap<String, ArrayList<Object>> stuFences = new LinkedHashMap<String, ArrayList<Object>>();
                                    for (int i = 0; i < students.size(); i++) {
                                        String stuName = students.get(i).getStu_name();
                                        ArrayList<Object> fences = new ArrayList<Object>();
                                        for (int j = 0; j < listFence.size(); j++) {
                                            if (students.get(i).getStu_id().equals(listFence.get(j).getStu_id())) {
                                                fences.add(listFence.get(j));
                                            }
                                        }
                                        if (5 > fences.size()) {
                                            //小于5个，可以添加围栏信息
                                            fences.add(students.get(i));
                                        }
                                        stuFences.put(stuName, fences);
                                    }
                                    adapter.loadContacts(stuFences);
                                    for (int i = 0; i < wrapperAdapter.getGroupCount(); i++) {
                                        expandableview.expandGroup(i);
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(FenceListActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(FenceListActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        if (swipe_refresh_widget != null) {
                            swipe_refresh_widget.setRefreshing(false);
                        }
                    }
                });

    }

}
