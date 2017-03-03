package com.xptschool.parent.ui.cardset;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CardSOSActivity extends CardSetBaseActivity implements View.OnClickListener {

    @BindView(R.id.llContent)
    LinearLayout llContent;
    private Button btnSubmit;

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

        String sos = currentStudent.getSos();
        String[] sosPhones = new String[]{"", "", ""};
        if (sos != null) {
            sosPhones = sos.split(",");
        }
        for (int i = 0; i < 3; i++) {
            CardSOSView sosView = new CardSOSView(this);
            String phone = "";
            if (sosPhones.length > i) {
                phone = sosPhones[i];
            }
            sosView.bindData(i + 1, phone, sosContractChooseListener);
            listSOSViews.add(sosView);
            llContent.addView(sosView);
        }
        llContent.addView(btnSubmit);

        getCardPhone();
    }

    @Override
    public void onClick(View v) {
        String sosPhones = "";
        int size = llContent.getChildCount();
        for (int i = 1; i < size - 1; i++) {
            CardSOSView sosView = (CardSOSView) llContent.getChildAt(i);
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
