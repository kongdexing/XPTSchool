package com.xptschool.teacher.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.CircularImageView;
import com.android.widget.view.KenBurnsView;
import com.google.gson.Gson;
import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.DividerItemDecoration;
import com.xptschool.teacher.adapter.WrapContentLinearLayoutManager;
import com.xptschool.teacher.bean.BeanStudent;
import com.xptschool.teacher.bean.BeanStudentDetail;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.ui.contact.ContactParentAdapter;
import com.xptschool.teacher.ui.main.BaseActivity;

import org.json.JSONArray;

import butterknife.BindView;

public class StudentDetailActivity extends BaseActivity {

    @BindView(R.id.imgHead)
    CircularImageView imgHead;

    @BindView(R.id.imgSex)
    ImageView imgSex;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.llInfoBg)
    LinearLayout llInfoBg;

    @BindView(R.id.txtAge)
    TextView txtAge;

    @BindView(R.id.txtClassName)
    TextView txtClassName;

    @BindView(R.id.txtBirthday)
    TextView txtBirthday;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.txtParentCount)
    TextView txtParentCount;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    private BeanStudent currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        setTitle(R.string.title_detail);

        KenBurnsView mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.bg_student, R.drawable.bg_student);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentStudent = bundle.getParcelable(ExtraKey.STUDENT_DETAIL);
        }
        if (currentStudent != null) {
            getStudentById();
        } else {
            //
            Toast.makeText(this, "无法查询学生信息", Toast.LENGTH_SHORT).show();
        }
    }

    private void call(String phone) {
        Log.i(TAG, "call: ");
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            Log.i(TAG, "toCallPhone: " + ex.getMessage());
            Toast.makeText(this, R.string.toast_startcall_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void getStudentById() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MyStudent_Detail, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.MyStudent_Detail)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (progress != null) {
                            progress.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    JSONArray array = new JSONArray(volleyHttpResult.getData().toString());
                                    if (array.length() == 0) {
                                        return;
                                    }
                                    BeanStudentDetail studentDetail = gson.fromJson(array.getJSONObject(0).toString(), BeanStudentDetail.class);

                                    txtName.setText(studentDetail.getStu_name());
                                    txtClassName.setText(studentDetail.getG_name() + studentDetail.getC_name());
                                    if (studentDetail.getSex().equals("0")) {
                                        imgHead.setImageResource(R.drawable.student_girl);
                                        llInfoBg.setBackgroundResource(R.drawable.bg_student_info_girl);
                                        imgSex.setBackgroundResource(R.drawable.female_w);
                                    } else {
                                        imgHead.setImageResource(R.drawable.student_boy);
                                        llInfoBg.setBackgroundResource(R.drawable.bg_student_info_boy);
                                        imgSex.setBackgroundResource(R.drawable.male_w);
                                    }
                                    txtAge.setText(studentDetail.getAge() + "岁");
                                    txtBirthday.setText(studentDetail.getBirth_date());

                                    if (studentDetail.getParent_phone().size() > 0) {
                                        txtParentCount.setText("家长");
                                        ContactParentAdapter adapter = new ContactParentAdapter(StudentDetailActivity.this);
                                        adapter.setPhoneClickListener(new ContactParentAdapter.OnParentPhoneClickListener() {
                                            @Override
                                            public void onPhoneClickListener(ContactParent parent) {
                                                call(parent.getPhone());
                                            }
                                        });
                                        adapter.refreshDate(studentDetail.getParent_phone());

                                        recycleView.setHasFixedSize(true);
                                        final WrapContentLinearLayoutManager mLayoutManager = new WrapContentLinearLayoutManager(StudentDetailActivity.this);
                                        recycleView.setLayoutManager(mLayoutManager);
                                        recycleView.addItemDecoration(new DividerItemDecoration(StudentDetailActivity.this,
                                                LinearLayoutManager.VERTICAL, R.drawable.line_dotted));
                                        recycleView.setAdapter(adapter);
                                    } else {
                                        txtParentCount.setText("无家长通讯信息");
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "onResponse: " + ex.getMessage());
                                    Toast.makeText(StudentDetailActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(StudentDetailActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                    }
                });

    }
}
