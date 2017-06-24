package com.xptschool.parent.ui.cardset;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.model.GreenDaoHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CardSOSActivity extends CardSetBaseActivity implements View.OnClickListener {

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    LinearLayout llContent;
    public List<CardSOSView> listSOSViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_sos);
        setTitle(R.string.label_card_sos);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        CardType = CARD_SOS;

        String stuId = bundle.getString(ExtraKey.STUDENT_ID);

        currentStudent = GreenDaoHelper.getInstance().getStudentByStuId(stuId);
        if (currentStudent == null) {
            Toast.makeText(this, R.string.toast_get_student_null, Toast.LENGTH_SHORT).show();
            return;
        }
        initView();
    }

    private void initView() {
        btnSubmit = new Button(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.dp_40));
        int margin = (int) getResources().getDimension(R.dimen.dp_10);
        lp.setMargins(margin, margin, margin, margin);
        btnSubmit.setLayoutParams(lp);
        btnSubmit.setBackground(getResources().getDrawable(R.drawable.btn_bg_normal));
        btnSubmit.setText(R.string.btn_confirm);
        btnSubmit.setTextColor(getResources().getColor(R.color.colorWhite));
        btnSubmit.setTextSize(16);
        btnSubmit.setOnClickListener(this);

        llContent = new LinearLayout(this);
        LinearLayout.LayoutParams contentLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        llContent.setOrientation(LinearLayout.VERTICAL);
        llContent.setLayoutParams(contentLP);

        setViewData(currentStudent.getSos());

        getCardPhone();
    }

    @Override
    protected void setViewData(String value) {
        super.setViewData(value);
        String[] sosPhones = new String[]{"", "", ""};
        if (value != null) {
            sosPhones = value.split(",");
        }

        llContent.removeAllViews();
        scrollView.removeAllViews();
        listSOSViews.clear();

        for (int i = 0; i < 3; i++) {
            CardSOSView sosView = new CardSOSView(this);
            String phone = "";
            if (sosPhones.length > i) {
                phone = sosPhones[i];
            }
            sosView.bindData(i + 1, phone, contractChooseListener);
            listSOSViews.add(sosView);
            llContent.addView(sosView);
        }
        llContent.addView(btnSubmit);
        scrollView.addView(llContent);
    }

    @Override
    public void onClick(View v) {
        try {
            String val = (String) SharedPreferencesUtil.getData(CardSOSActivity.this, spKey, "0");
            long spVal = Long.parseLong(val);

            long diff = (System.currentTimeMillis() - spVal) / 1000;
            if (60 >= diff) {
                Toast.makeText(this, (60 - diff) + "秒后再进行设置", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception ex) {
            Log.i(TAG, "onClick: " + ex.getMessage() + " spKey:" + spKey);
            return;
        }

        String sosPhones = "";
        int size = listSOSViews.size();
        for (int i =0; i < size; i++) {
            CardSOSView sosView = listSOSViews.get(i);
            String phone = sosView.getSOSPhone();
            if (phone == null) {
                //格式不正确
                return;
            }
            if (!phone.isEmpty()) {
                sosPhones += phone + ",";
            }
        }
        if (!sosPhones.isEmpty()) {
            sosPhones = sosPhones.substring(0, sosPhones.length() - 1);
        }
        setCardPhone(sosPhones);
    }

}
