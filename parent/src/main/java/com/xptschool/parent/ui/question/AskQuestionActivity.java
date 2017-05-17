package com.xptschool.parent.ui.question;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.audiorecorder.Recorder;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.BeanTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AskQuestionActivity extends BaseActivity {

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.spnTeacher)
    MaterialSpinner spnTeacher;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.edtContent)
    EditText edtContent;
    @BindView(R.id.edtQTitle)
    EditText edtQTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        setTitle(R.string.label_ask_question);
        initData();
    }

    private void initData() {
        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                getTeacherByStudent();
            }
        });

        spnTeacher.setItems("正在获取老师信息");

        getTeacherByStudent();
    }

    private void getTeacherByStudent() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_TEACHER_BYCID,
                new VolleyHttpParamsEntity()
                        .addParam("c_id", student.getC_id())
                        .addParam("g_id", student.getG_id()),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanTeacher> teachers = gson.fromJson(volleyHttpResult.getData().toString(), new TypeToken<List<BeanTeacher>>() {
                                    }.getType());
                                    if (teachers.size() == 0) {
                                        spnTeacher.setItems("");
                                        Toast.makeText(AskQuestionActivity.this, "该学生所在班级无执教老师", Toast.LENGTH_SHORT).show();
                                    } else {
                                        spnTeacher.setItems(teachers);
                                    }
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: " + ex.getMessage());
                                }
                                break;
                            default:
                                spnTeacher.setItems("获取失败");
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        spnTeacher.setItems("获取失败");
                    }
                });
    }

    @OnClick({R.id.btnSubmit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (edtQTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.toast_question_title_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtContent.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.toast_question_content_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                createQuestion();
                break;
        }
    }

    private void createQuestion() {
        BeanTeacher teacher = (BeanTeacher) spnTeacher.getSelectedItem();
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_NEWADD,
                new VolleyHttpParamsEntity()
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_NEWADD))
                        .addParam("title", edtQTitle.getText().toString().trim())
                        .addParam("content", edtContent.getText().toString().trim())
                        .addParam("form_user", teacher.getU_id())
                        .addParam("a_id", student.getA_id())
                        .addParam("s_id", student.getS_id())
                        .addParam("c_id", student.getC_id())
                        .addParam("g_id", student.getG_id()),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        showProgress(R.string.progress_add_question);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(AskQuestionActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        if (volleyHttpResult.getStatus() == 1) {
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
    }

}
