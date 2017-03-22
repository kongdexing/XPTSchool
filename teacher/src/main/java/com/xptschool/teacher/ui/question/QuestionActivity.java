package com.xptschool.teacher.ui.question;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.xptschool.teacher.bean.BeanQuestion;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;
import com.xptschool.teacher.view.CalendarOptionView;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
//问题列表

public class QuestionActivity extends BaseListActivity {

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rclQuestions)
    LoadMoreRecyclerView rclQuestions;

    @BindView(R.id.spnClass)
    MaterialSpinner spnClass;

    @BindView(R.id.txtDate)
    ArrowTextView txtDate;

    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;
    private String startTime, endTime;

    private PopupWindow datePopup;
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        setTitle(R.string.home_question);
        initView();
        initDate();
    }

    private void initView() {
        initRecyclerView(rclQuestions, swipeRefreshLayout);

        adapter = new QuestionAdapter(this);
        rclQuestions.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                getQuestionList();
            }
        });
        rclQuestions.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getQuestionList();
                }
            }
        });

    }

    private void initDate() {
        spnClass.setItems(GreenDaoHelper.getInstance().getAllClassNameAppend());
        spnClass.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<BeanClass>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, BeanClass item) {
                flTransparent.setVisibility(View.GONE);
                getQuestionList();
            }
        });
        spnClass.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                flTransparent.setVisibility(View.GONE);
            }
        });
        startTime = CommonUtil.getDate2StrBefore(6);
        endTime = CommonUtil.getCurrentDate();

        setTxtDate();
        getQuestionList();
    }

    private void setTxtDate() {
        txtDate.setText(startTime + "\n" + endTime);
    }

    @OnClick({R.id.txtDate, R.id.spnClass})
    void noticeClick(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                showDatePop();
                break;
            case R.id.spnClass:
                flTransparent.setVisibility(View.VISIBLE);
                break;
        }
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
                    getQuestionList();
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

    private void getQuestionList() {
        BeanClass currentClass = (BeanClass) spnClass.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_QUERY,
                new VolleyHttpParamsEntity()
                        .addParam("c_id", currentClass == null ? "" : currentClass.getC_id())
                        .addParam("sdate", startTime)
                        .addParam("edate", endTime)
                        .addParam("page", resultPage.getPage() + "")
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_QUERY)),
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
                                        rclQuestions.setAutoLoadMoreEnable(true);
                                    } else {
                                        rclQuestions.setAutoLoadMoreEnable(false);
                                    }

                                    Gson gson = new Gson();
                                    List<BeanQuestion> questions = gson.fromJson(jsonObject.getJSONArray("content").toString(), new TypeToken<List<BeanQuestion>>() {
                                    }.getType());

                                    if (resultPage.getPage() > 1) {
                                        adapter.appendData(questions);
                                    } else {
                                        //第一页数据
                                        if (questions.size() == 0) {
                                            Toast.makeText(QuestionActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                        }
                                        rclQuestions.removeAllViews();
                                        adapter.refreshData(questions);
                                    }
                                    rclQuestions.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                                } catch (Exception ex) {
                                    Toast.makeText(QuestionActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(QuestionActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

}
