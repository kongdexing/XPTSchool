package com.xptschool.parent.ui.question;

import android.content.Intent;
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
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanQuestion;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.view.CalendarOptionView;

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

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

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

        if (GreenDaoHelper.getInstance().getStudents().size() > 0) {
            setTxtRight("提问");
            setTextRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(QuestionActivity.this, AskQuestionActivity.class));
                }
            });
        }
    }

    private void initDate() {
        startTime = CommonUtil.getDate2StrBefore(7);
        endTime = CommonUtil.getCurrentDate();
        setTxtDate();

        if (GreenDaoHelper.getInstance().getStudents().size() == 0) {
            spnStudents.setText(R.string.title_no_student);
            txtDate.setEnabled(false);
            spnStudents.setEnabled(false);
            swipeRefreshLayout.setEnabled(false);
            return;
        }

        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                flTransparent.setVisibility(View.GONE);
                getQuestionList();
            }
        });
        spnStudents.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                flTransparent.setVisibility(View.GONE);
            }
        });

        getQuestionList();
    }

    private void setTxtDate() {
        txtDate.setText(startTime + "\n" + endTime);
    }

    @OnClick({R.id.txtDate, R.id.spnStudents})
    void noticeClick(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                showDatePop();
                break;
            case R.id.spnStudents:
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
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();
        if (student == null) {
            Toast.makeText(this, "请选择一名学生", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.setCurrentStudent(student);
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_QUERY,
                new VolleyHttpParamsEntity()
                        .addParam("g_id", student.getG_id())
                        .addParam("c_id", student.getC_id())
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
                                    Toast.makeText(QuestionActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
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
