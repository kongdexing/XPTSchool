package com.xptschool.teacher.ui.score;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanExam;
import com.xptschool.teacher.bean.BeanScore;
import com.xptschool.teacher.bean.ExamType;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 成绩管理
 */
public class ScoreActivity extends BaseListActivity {

    @BindView(R.id.spnDate)
    MaterialSpinner spnDate;

    @BindView(R.id.spnClass)
    MaterialSpinner spnClass;

    @BindView(R.id.spnExams)
    MaterialSpinner spnExams;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.item_scroll_title)
    SyncScrollHorizontalView headerScroll;

    @BindView(R.id.llScoreTitle)
    LinearLayout llScoreTitle;

    @BindView(R.id.hlistview_scroll_list)
    ListView hlistview_scroll_list;

    @BindView(R.id.txtStudnet)
    TextView txtStudnet;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;

    protected List<SyncScrollHorizontalView> mHScrollViews = new ArrayList<SyncScrollHorizontalView>();
    public HorizontalScrollView mTouchView;
    private int courseSize = 6;
    private ScoreHAdapter mAdapter;
    private BeanClass currentClass;
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
                flTransparent.setVisibility(View.GONE);
                getExamName();
            }
        });

        List<BeanClass> listClass = GreenDaoHelper.getInstance().getAllClass();
        if (listClass.size() == 0) {
            spnClass.setText("暂无班级");
        } else {
            spnClass.setItems(listClass);
            getExamName();
        }

        spnClass.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                flTransparent.setVisibility(View.GONE);
                getExamName();
            }
        });

        spnExams.setItems(listExams);
        spnExams.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<BeanExam>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, BeanExam item) {
                flTransparent.setVisibility(View.GONE);
                initTitleExam(item);
                getFirstPageData();
            }
        });

        spnClass.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnExams.setOnNothingSelectedListener(spinnerNothingSelectedListener);
        spnDate.setOnNothingSelectedListener(spinnerNothingSelectedListener);

        // 添加头滑动事件
        mHScrollViews.add(headerScroll);
        mAdapter = new ScoreHAdapter(this);
        hlistview_scroll_list.setAdapter(mAdapter);

    }

    public void initTitleExam(BeanExam item) {
        if (item == null) {
            setTitle(R.string.home_score);
            return;
        }
        String[] examName = item.getName().split("-");
        spnExams.setText(examName[0]);
        try {
            setTitle(item.getName().replace(examName[0] + "-", ""));
        } catch (Exception ex) {
            setTitle(item.getName());
        }
    }

    public void addHViews(final SyncScrollHorizontalView hScrollView) {
        if (!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            SyncScrollHorizontalView scrollView = mHScrollViews.get(size - 1);
            final int scrollX = scrollView.getScrollX();
            // 第一次满屏后，向下滑动，有一条数据在开始时未加入
            if (scrollX != 0) {
                hlistview_scroll_list.post(new Runnable() {
                    @Override
                    public void run() {
                        // 当listView刷新完成之后，把该条移动到最终位置
                        hScrollView.scrollTo(scrollX, 0);
                    }
                });
            }
        }
        mHScrollViews.add(hScrollView);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        for (SyncScrollHorizontalView scrollView : mHScrollViews) {
            // 防止重复滑动
            if (mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }

    private void getFirstPageData(){
        mAdapter.loadScore(new ArrayList<BeanScore>());
        llScoreTitle.removeAllViews();
        getScoreList();
    }

    @OnClick({R.id.spnDate, R.id.spnClass, R.id.spnExams})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.spnDate:
            case R.id.spnClass:
            case R.id.spnExams:
                flTransparent.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void getExamName() {
        this.currentClass = (BeanClass) spnClass.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GETExams, new VolleyHttpParamsEntity()
                .addParam("years", spnDate.getText().toString())
                .addParam("g_id", currentClass.getG_id())
                .addParam("c_id", currentClass.getC_id()), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                spnExams.setItems(listExams);
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            ArrayList<BeanExam> listExams = gson.fromJson(volleyHttpResult.getData().toString(), new TypeToken<List<BeanExam>>() {
                            }.getType());
                            if (listExams.size() > 0) {
                                spnExams.setItems(listExams);
                                initTitleExam(listExams.get(0));
                                getFirstPageData();
                            } else {
                                spnExams.setText("暂无考试");
                                initTitleExam(null);
                                txtStudnet.setVisibility(View.GONE);
                                mAdapter.loadScore(null);
                                llScoreTitle.removeAllViews();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(ScoreActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onResponse: exam name " + ex.getMessage());
                        }
                        break;
                    default:
                        spnExams.setItems(listExams);
                        Toast.makeText(ScoreActivity.this, "获取考试名称失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                spnExams.setItems(listExams);
                Toast.makeText(ScoreActivity.this, "获取考试名称失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getScoreList() {

        Log.i(TAG, "getScoreList: " + spnExams.getItems().size());
        BeanExam exam = null;
        try {
            exam = (BeanExam) spnExams.getSelectedItem();
        } catch (Exception ex) {
            exam = null;
        }
        if (exam == null) {
            Toast.makeText(this, "请先选择一场考试", Toast.LENGTH_SHORT).show();
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            return;
        }

        final String type = exam.getType();
        BeanClass beanClass = (BeanClass) spnClass.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.EXAM_QUERY, new VolleyHttpParamsEntity()
                        .addParam("type", type)
                        .addParam("e_id", exam.getE_id())
                        .addParam("g_id", beanClass != null ? beanClass.getG_id() : "")
                        .addParam("c_id", beanClass != null ? beanClass.getC_id() : "")
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
                                    //成绩
                                    List<BeanScore> datas = new ArrayList<BeanScore>();
                                    //头部课程
                                    List<String> strCourse = new ArrayList<String>();

                                    if (type.equals(ExamType.ALL.toString())) {
                                        JSONObject scoreDate = new JSONObject(httpResult.getData().toString());

                                        //解析课程
                                        JSONArray course = scoreDate.getJSONArray("course");
                                        if (course.length() > 0) {
                                            strCourse.add("总分");
                                        }
                                        for (int i = 0; i < course.length(); i++) {
                                            JSONObject object = course.getJSONObject(i);
                                            Iterator it = object.keys();
                                            while (it.hasNext()) {
                                                String key = it.next().toString();
                                                String val = object.getString(key);
                                                strCourse.add(val);
                                                Log.i(TAG, "course: key " + key + " val " + val);
                                            }
                                        }

                                        //解析学生成绩
                                        JSONArray students = scoreDate.getJSONArray("result");
                                        for (int i = 0; i < students.length(); i++) {
                                            JSONObject jsonScore = students.getJSONObject(i);
                                            BeanScore score = new BeanScore();
                                            score.setStudentName(jsonScore.getString("name"));
                                            LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
                                            data.put("总分", jsonScore.getString("sum"));

                                            Iterator itScore = jsonScore.keys();
                                            while (itScore.hasNext()) {
                                                String key = itScore.next().toString();
                                                String val = jsonScore.getString(key);
                                                if (strCourse.contains(key)) {
                                                    Log.i(TAG, "student: " + score.getStudentName() + " key " + key + " val " + val);
                                                    data.put(key, val);
                                                }
                                            }
                                            score.setScoreMap(data);
                                            datas.add(score);
                                        }
                                    } else {
                                        //单考成绩
                                        JSONArray jsonArray = new JSONArray(httpResult.getData().toString());
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            BeanScore score = new BeanScore();
                                            score.setStudentName(jsonObject.getString("stu_name"));
                                            LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
                                            String crs_name = jsonObject.getString("crs_name");
                                            data.put(crs_name, jsonObject.getString("scores"));

                                            if (!strCourse.contains(crs_name)) {
                                                strCourse.add(crs_name);
                                            }
                                            score.setScoreMap(data);
                                            datas.add(score);
                                        }
                                    }

                                    if (datas.size() > 0) {
                                        txtStudnet.setVisibility(View.VISIBLE);
                                    } else {
                                        txtStudnet.setVisibility(View.GONE);
                                    }

                                    mAdapter.loadScore(datas);
                                    llScoreTitle.removeAllViews();

                                    //头部课程
                                    for (int i = 0; i < strCourse.size(); i++) {
                                        TextView title = new TextView(ScoreActivity.this);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dp_60), ViewGroup.LayoutParams.MATCH_PARENT);
                                        title.setTextColor(getResources().getColor(R.color.text_black));
                                        title.setLayoutParams(lp);
                                        title.setText(strCourse.get(i));
                                        title.setGravity(Gravity.CENTER);
                                        llScoreTitle.addView(title);
                                    }

                                } catch (Exception ex) {
                                    Toast.makeText(ScoreActivity.this, "获取成绩失败", Toast.LENGTH_SHORT).show();
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
