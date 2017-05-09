package com.xptschool.teacher.ui.mine;

import android.os.Bundle;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseActivity;

import butterknife.BindView;

public class TeacherInfoActivity extends BaseActivity {

    @BindView(R.id.imgHead)
    CircularImageView imgHead;

    @BindView(R.id.txtTeacherName)
    TextView txtTeacherName;

    @BindView(R.id.txtPhone)
    TextView txtPhone;

    @BindView(R.id.txtEducation)
    TextView txtEducation;

    @BindView(R.id.txtSchoolArea)
    TextView txtSchoolArea;

    @BindView(R.id.txtSchoolName)
    TextView txtSchoolName;

    @BindView(R.id.txtDepartmentName)
    TextView txtDepartmentName;

    @BindView(R.id.txtAdviser)
    TextView txtAdviser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        setTitle(R.string.mine_info);

        initData();
    }

    private void initData() {
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            return;
        }
        if (teacher.getSex().equals("1")) {
            imgHead.setImageResource(R.drawable.teacher_man);
        } else {
            imgHead.setImageResource(R.drawable.teacher_woman);
        }
        txtTeacherName.setText(teacher.getName());
        txtPhone.setText(teacher.getPhone());
        txtEducation.setText(teacher.getEducation());
        txtSchoolArea.setText(teacher.getA_name());
        txtSchoolName.setText(teacher.getS_name());
        txtDepartmentName.setText(teacher.getD_name());
        txtAdviser.setText(teacher.getCharge() == "1" ? "是" : "否");

    }
}
