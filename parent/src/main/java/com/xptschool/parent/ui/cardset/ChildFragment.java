package com.xptschool.parent.ui.cardset;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
            imgHead.setImageResource(R.mipmap.student_boy);
        } else {
            imgHead.setImageResource(R.mipmap.student_girl);
        }
        //设置信息
        txtName.setText(currentStudent.getStu_name());
        txtClassName.setText(currentStudent.getG_name() + currentStudent.getC_name());
        if (currentStudent.getSex().equals("0")) {
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_girl);
            imgSex.setBackgroundResource(R.mipmap.female_w);
        } else {
            llInfoBg.setBackgroundResource(R.drawable.bg_student_info_boy);
            imgSex.setBackgroundResource(R.mipmap.male_w);
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
    }

    @Override
    public void onClick(View view) {
        if (currentStudent != null && currentStudent.getImei_id().isEmpty()) {
            Toast.makeText(mContext, R.string.toast_imei_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), CardSetActivity.class);
        switch (view.getId()) {
            case R.id.RLSOSSet:
                intent = new Intent(getContext(), CardSOSActivity.class);
                intent.putExtra(ExtraKey.STUDENT_ID, currentStudent.getStu_id());
                getContext().startActivity(intent);
                break;
            case R.id.RLWhitelistSet:
                intent.putExtra(CardSetActivity.CARD_TYPE, CardSetActivity.CARD_WHITELIST);
                intent.putExtra(ExtraKey.STUDENT_ID, currentStudent.getStu_id());
                getContext().startActivity(intent);
                break;
            case R.id.RLMoniterSet:
                intent.putExtra(CardSetActivity.CARD_TYPE, CardSetActivity.CARD_MONITER);
                intent.putExtra(ExtraKey.STUDENT_ID, currentStudent.getStu_id());
                getContext().startActivity(intent);
                break;
        }
    }
}
