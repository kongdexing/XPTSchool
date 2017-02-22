package com.xptschool.teacher.ui.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.widget.view.CircularImageView;
import com.android.widget.view.KenBurnsView;
import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.DividerItemDecoration;
import com.xptschool.teacher.adapter.WrapContentLinearLayoutManager;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.ContactStudent;
import com.xptschool.teacher.model.ContactTeacher;
import com.xptschool.teacher.ui.main.BaseActivity;

import java.util.List;

import butterknife.BindView;

public class ContactsDetailActivity extends BaseActivity {

    @BindView(R.id.imgHead)
    CircularImageView imgHead;

    @BindView(R.id.txtAreaName)
    TextView txtAreaName;

    @BindView(R.id.txtSchoolName)
    TextView txtSchoolName;

    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.rlTeacherPhone)
    RelativeLayout rlTeacherPhone;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtClassName)
    TextView txtClassName;

    @BindView(R.id.txtBirth_date)
    TextView txtBirth_date;

    @BindView(R.id.llTeacher)
    LinearLayout llTeacher;

    @BindView(R.id.llStudent)
    LinearLayout llStudent;

    @BindView(R.id.imgSex)
    ImageView imgSex;

    @BindView(R.id.llInfoBg)
    LinearLayout llInfoBg;

    @BindView(R.id.txtAge)
    TextView txtAge;


    @BindView(R.id.txtParentCount)
    TextView txtParentCount;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    String currentPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_detial);

        KenBurnsView mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.bg_student, R.drawable.bg_student);

        String type = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString(ExtraKey.CONTACT_TYPE);
            if (type.equals(ExtraKey.CONTACT_TEACHER)) {
                llTeacher.setVisibility(View.VISIBLE);
                llStudent.setVisibility(View.GONE);
                setTeacherInfo((ContactTeacher) bundle.get(ExtraKey.CONTACT));
            } else {
                llTeacher.setVisibility(View.GONE);
                llStudent.setVisibility(View.VISIBLE);
                setStudentInfo((ContactStudent) bundle.get(ExtraKey.CONTACT));
            }
        }
    }

    private void setTeacherInfo(final ContactTeacher teacher) {
        if (teacher == null) {
            return;
        }
        setTitle(teacher.getName());

        if (teacher.getSex().equals("1")) {
            imgHead.setImageResource(R.mipmap.teacher_man);
        } else {
            imgHead.setImageResource(R.mipmap.teacher_woman);
        }
        txtName.setText(teacher.getName());
        txtAreaName.setText(teacher.getArea_name());
        txtSchoolName.setText(teacher.getSchool_name());
        txtPhone.setText(teacher.getPhone());
        rlTeacherPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPhone = teacher.getPhone();
                call();
            }
        });
    }

    private void setStudentInfo(ContactStudent student) {
        if (student == null) {
            return;
        }
        setTitle(student.getStu_name());

        if (student.getSex().equals("1")) {
            imgHead.setImageResource(R.mipmap.student_boy);
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_boy);
            imgSex.setBackgroundResource(R.mipmap.male_w);
        } else {
            imgHead.setImageResource(R.mipmap.student_girl);
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_girl);
            imgSex.setBackgroundResource(R.mipmap.female_w);
        }

        txtAge.setText(student.getAge() + "岁");

        txtName.setText(student.getStu_name());
        txtBirth_date.setText(student.getBirth_date());
        txtClassName.setText(student.getG_name() + student.getC_name());
        List<ContactParent> parents = student.getParent();
        if (parents == null || parents.size() == 0) {
            txtParentCount.setText("无家长通讯信息");
            return;
        }

        ContactParentAdapter adapter = new ContactParentAdapter(this);
        adapter.refreshDate(parents);
        adapter.setPhoneClickListener(new ContactParentAdapter.OnParentPhoneClickListener() {
            @Override
            public void onPhoneClickListener(String phone) {
                currentPhone = phone;
                call();
            }
        });

        recycleView.setHasFixedSize(true);
        final WrapContentLinearLayoutManager mLayoutManager = new WrapContentLinearLayoutManager(this);
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.addItemDecoration(new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.line_dotted));
        recycleView.setAdapter(adapter);
    }

    private void call() {
        Log.i(TAG, "call: ");
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + currentPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            Log.i(TAG, "toCallPhone: " + ex.getMessage());
            Toast.makeText(this, R.string.toast_startcall_error, Toast.LENGTH_SHORT).show();
        }
    }

}
