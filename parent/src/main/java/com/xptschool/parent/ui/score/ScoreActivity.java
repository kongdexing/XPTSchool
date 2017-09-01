package com.xptschool.parent.ui.score;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.groupexpandable.FloatingGroupExpandableListView;
import com.android.widget.groupexpandable.WrapperExpandableListAdapter;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanExam;
import com.xptschool.parent.bean.BeanScore;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 成绩管理
 */
public class ScoreActivity extends BaseListActivity {

    @BindView(R.id.spnDate)
    MaterialSpinner spnDate;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.expandableview)
    FloatingGroupExpandableListView expandableview;

    WrapperExpandableListAdapter wrapperAdapter;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    ScoreAdapter adapter;

    private String[] listExams = new String[]{"正在加载"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setTitle(R.string.home_score);
        initView();
        initDate();
    }

    private void initView() {
        adapter = new ScoreAdapter(this);
        wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        expandableview.setAdapter(wrapperAdapter);

        swipeRefresh.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFirstPageData();
            }
        });
    }

    private void initDate() {
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
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                getFirstPageData();
            }
        });

        spnStudents.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnDate.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        getFirstPageData();
    }

    private void getFirstPageData(){
        adapter.initData(new ArrayList<BeanExam>());
        flTransparent.setVisibility(View.GONE);
        getScoreList();
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

    private void getScoreList() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.EXAM_QUERY, new VolleyHttpParamsEntity()
                        .addParam("stu_id", student.getStu_id())
                        .addParam("dates", spnDate.getText().toString())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.EXAM_QUERY)),
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
                                    List<BeanExam> listExams = gson.fromJson(httpResult.getData().toString(),
                                            new TypeToken<List<BeanExam>>() {
                                            }.getType());
                                    for (int i = 0; i < listExams.size(); i++) {
                                        if (listExams.get(i).getExam_com_type().equals("统考")) {
                                            //设置总分
                                            List<BeanScore> scores = listExams.get(i).getScores();
                                            BeanScore score = new BeanScore();
                                            float temScore = 0;
                                            for (int j = 0; j < scores.size(); j++) {
                                                temScore += Float.parseFloat(scores.get(j).getCourse_score());
                                            }
                                            score.setCourse_score(temScore + "");
                                            score.setCourse_name("总分");
//                                            scores.add(score);
                                            scores.add(0, score);
                                            listExams.get(i).setScores(scores);
                                        }
                                    }
                                    adapter.initData(listExams);

                                    for (int i = 0; i < listExams.size(); i++) {
                                        expandableview.expandGroup(i);
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(ScoreActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(ScoreActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
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

    MaterialSpinner.OnNothingSelectedListener spinnerNothingSelectedListener = new MaterialSpinner.OnNothingSelectedListener() {
        @Override
        public void onNothingSelected(MaterialSpinner spinner) {
            flTransparent.setVisibility(View.GONE);
        }
    };

}
