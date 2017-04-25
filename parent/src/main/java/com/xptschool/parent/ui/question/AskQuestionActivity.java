package com.xptschool.parent.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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

    protected void connectServerWithTCPSocket() {

        Socket socket;
        try {// 创建一个Socket对象，并指定服务端的IP及端口号
            socket = new Socket("192.168.1.32", 1989);
            // 创建一个InputStream用户读取要发送的文件。
            InputStream inputStream = new FileInputStream("e://a.txt");
            // 获取Socket的OutputStream对象用于发送数据。
            OutputStream outputStream = socket.getOutputStream();
            // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            // 循环读取文件
            while ((temp = inputStream.read(buffer)) != -1) {
                // 把数据写入到OuputStream对象中
                outputStream.write(buffer, 0, temp);
            }
            // 发送读取的数据到服务端
            outputStream.flush();

            /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
//          String socketData = "[2143213;21343fjks;213]";
//          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                  socket.getOutputStream()));
//          writer.write(socketData.replace("\n", " ") + "\n");
//          writer.flush();
            /************************************************/
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
