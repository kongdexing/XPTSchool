package com.xptschool.parent.ui.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanClassInfo;
import com.xptschool.parent.bean.BeanNotice;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.List;

import butterknife.BindView;

public class NoticeDetailActivity extends BaseActivity {

    @BindView(R.id.txtClassName)
    TextView txtClassName;

    @BindView(R.id.txtTime)
    TextView txtTime;

    @BindView(R.id.txtNoticeTitle)
    TextView txtNoticeTitle;

    @BindView(R.id.txtContent)
    TextView txtContent;

    private BeanNotice currentNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        setTitle(R.string.home_notice);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentNotice = bundle.getParcelable(ExtraKey.NOTICE_DETAIL);
        }
        initData();
    }

    private void initData() {
        if (currentNotice != null) {
            List<BeanClassInfo> classInfos = currentNotice.getClassInfo();
            if (classInfos.size() > 0) {
                BeanClassInfo classInfo = classInfos.get(0);
                String className = classInfo.getG_name() + classInfo.getC_name();
                if (classInfos.size() > 1) {
                    className += "...";
                }
                txtClassName.setText(className);
            } else {
                txtClassName.setVisibility(View.GONE);
            }

//            List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
//            for (int i = 0; i < students.size(); i++) {
//                BeanStudent student = students.get(i);
//                if (student.getC_id().equals(currentNotice.getC_id())
//                        && student.getG_id().equals(currentNotice.getG_id())) {
//                    txtClassName.setText(student.getG_name()+student.getC_name());
//                    break;
//                }
//            }

            txtNoticeTitle.setText(currentNotice.getTitle());
            txtTime.setText(currentNotice.getCreate_time());
            txtContent.setText(currentNotice.getContent());
        }

    }

}
