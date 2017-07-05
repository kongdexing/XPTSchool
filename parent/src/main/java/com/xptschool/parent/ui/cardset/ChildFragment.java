package com.xptschool.parent.ui.cardset;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.widget.view.CircularImageView;
import com.android.widget.view.KenBurnsView;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.ui.fragment.BaseFragment;
import com.xptschool.parent.util.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChildFragment extends BaseFragment implements View.OnClickListener {

    private static String TAG = ChildFragment.class.getSimpleName();

    CircularImageView imgHead;
    ImageView imgSex;
    TextView txtName;
    LinearLayout llInfoBg;
    TextView txtAge;
    TextView txtClassName;
    TextView txtBirthday;
    TextView txtSchoolDate;
    TextView txtSchoolName;
    TextView txtIMEI;
    ProgressBar progress;
    TextView txtCardPhone;

    private BeanStudent currentStudent;

    public ChildFragment() {
        // Required empty public constructor
    }

    public void setStudent(BeanStudent student) {
        Log.i(TAG, "setStudent: ");
        currentStudent = student;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child, container, false);
        imgHead = (CircularImageView) view.findViewById(R.id.imgHead);
        imgSex = (ImageView) view.findViewById(R.id.imgSex);
        KenBurnsView mHeaderPicture = (KenBurnsView) view.findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.bg_student, R.drawable.bg_student);
        txtName = (TextView) view.findViewById(R.id.txtName);
        llInfoBg = (LinearLayout) view.findViewById(R.id.llInfoBg);
        txtAge = (TextView) view.findViewById(R.id.txtAge);
        txtClassName = (TextView) view.findViewById(R.id.txtClassName);
        txtBirthday = (TextView) view.findViewById(R.id.txtBirthday);
        txtSchoolDate = (TextView) view.findViewById(R.id.txtSchoolDate);
        txtSchoolName = (TextView) view.findViewById(R.id.txtSchoolName);
        txtIMEI = (TextView) view.findViewById(R.id.txtIMEI);

        RelativeLayout rlCardPhone = (RelativeLayout) view.findViewById(R.id.rlCardPhone);
        txtCardPhone = (TextView) view.findViewById(R.id.txtCardPhone);
        rlCardPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardPhone = txtCardPhone.getText().toString();
                if (cardPhone.isEmpty()) {
                    return;
                }
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + cardPhone));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception ex) {
                    ToastUtils.showToast(ChildFragment.this.getContext(), R.string.toast_startcall_error);
                }
            }
        });

        RelativeLayout RLSOSSet = (RelativeLayout) view.findViewById(R.id.RLSOSSet);
        RelativeLayout RLWhitelistSet = (RelativeLayout) view.findViewById(R.id.RLWhitelistSet);
        RelativeLayout RLMoniterSet = (RelativeLayout) view.findViewById(R.id.RLMoniterSet);

        RLSOSSet.setOnClickListener(this);
        RLWhitelistSet.setOnClickListener(this);
        RLMoniterSet.setOnClickListener(this);

        return view;
    }

    @Override
    protected void initData() {
        if (currentStudent == null || txtName == null) {
            Log.i(TAG, "bindingData: " + txtName.hashCode());
            return;
        }
        if (currentStudent.getSex().equals("1")) {
            imgHead.setImageResource(R.drawable.student_boy);
        } else {
            imgHead.setImageResource(R.drawable.student_girl);
        }
        //设置信息
        txtName.setText(currentStudent.getStu_name());
        txtClassName.setText(currentStudent.getG_name() + currentStudent.getC_name());
        if (currentStudent.getSex().equals("0")) {
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_girl);
            imgSex.setBackgroundResource(R.drawable.female_w);
        } else {
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_boy);
            imgSex.setBackgroundResource(R.drawable.male_w);
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dbDate = (Date) dateFormat.parse(currentStudent.getBirth_date());
            txtAge.setText(CommonUtil.getAge(dbDate) + "岁");
        } catch (Exception ex) {
            txtAge.setText("未知");
        }
        txtSchoolDate.setText(currentStudent.getRx_date());
        txtBirthday.setText(currentStudent.getBirth_date());
        txtSchoolName.setText(currentStudent.getS_name() + currentStudent.getA_name());
        txtIMEI.setText(currentStudent.getImei_id());
        txtCardPhone.setText(currentStudent.getCard_phone());
    }

    @Override
    public void onClick(View view) {
        if (currentStudent != null && currentStudent.getImei_id().isEmpty()) {
            Toast.makeText(mContext, R.string.toast_imei_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.RLSOSSet:
                intent = new Intent(getContext(), CardSOSActivity.class);
                intent.putExtra(ExtraKey.STUDENT_ID, currentStudent.getStu_id());
                getContext().startActivity(intent);
                break;
            case R.id.RLWhitelistSet:
                intent = new Intent(getContext(), CardWhiteListActivity.class);
                intent.putExtra(ExtraKey.STUDENT_ID, currentStudent.getStu_id());
                getContext().startActivity(intent);
                break;
            case R.id.RLMoniterSet:
                intent = new Intent(getContext(), CardMoniterActivity.class);
                intent.putExtra(ExtraKey.STUDENT_ID, currentStudent.getStu_id());
                getContext().startActivity(intent);
                break;
        }
    }
}
