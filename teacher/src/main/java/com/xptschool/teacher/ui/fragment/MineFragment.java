package com.xptschool.teacher.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.contact.ContactsActivity;
import com.xptschool.teacher.ui.course.CourseActivity;
import com.xptschool.teacher.ui.main.LoginActivity;
import com.xptschool.teacher.ui.mine.MyClassesActivity;
import com.xptschool.teacher.ui.mine.SettingActivity;
import com.xptschool.teacher.ui.mine.StudentsActivity;
import com.xptschool.teacher.ui.mine.TeacherInfoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MineFragment extends BaseFragment {

    @BindView(R.id.imgHead)
    CircularImageView imgHead;

    @BindView(R.id.txtUserName)
    TextView txtUserName;

    @BindView(R.id.txtChangeAccount)
    TextView txtMineInfo;

    @BindView(R.id.txtDot)
    View txtDot;

    private Unbinder unbinder;

    DisplayImageOptions options;

    public MineFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        initView();
        return mRootView;
    }

    private void initView() {
//        imgHead = (ImageView) mRootView.findViewById(R.id.imgHead);
//        imgHead.setOnClickListener(this);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.user_defaulthead)
                .showImageOnFail(R.drawable.user_defaulthead)
                .showImageOnLoading(R.drawable.user_defaulthead)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher != null) {
            txtUserName.setText(teacher.getName());
            if (teacher.getSex().equals("1")) {
                imgHead.setImageResource(R.drawable.teacher_man);
            } else {
                imgHead.setImageResource(R.drawable.teacher_woman);
            }
        }
        int num = GreenDaoHelper.getInstance().getUnReadChats().size();
        if (num > 0) {
            txtDot.setVisibility(View.VISIBLE);
        } else {
            txtDot.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.imgHead, R.id.txtChangeAccount, R.id.rlMyClass, R.id.rlMyCourse, R.id.rlMyStudents, R.id.rlMyContacts,
            R.id.rlSetting})
    void knifeClick(View view) {
        switch (view.getId()) {
            case R.id.imgHead:
                startActivity(new Intent(getContext(), TeacherInfoActivity.class));
                break;
            case R.id.rlMyClass:
                startActivity(new Intent(getContext(), MyClassesActivity.class));
                break;
            case R.id.rlMyCourse:
                startActivity(new Intent(getContext(), CourseActivity.class));
                break;
            case R.id.txtChangeAccount:
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.rlMyStudents:
                startActivity(new Intent(getContext(), StudentsActivity.class));
                break;
            case R.id.rlMyContacts:
                startActivity(new Intent(getContext(), ContactsActivity.class));
                break;
            case R.id.rlSetting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
