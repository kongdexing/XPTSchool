package com.xptschool.parent.ui.cardset;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.GreenDaoHelper;

import java.util.ArrayList;

import butterknife.BindView;

public class CardWhiteListActivity extends CardSetBaseActivity implements View.OnClickListener {

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    LinearLayout llContent;
    private ArrayList<WhiteCardView> whiteCardViews = new ArrayList<>();
    private int maxLength = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_white_list);
        setTitle(R.string.label_card_whitelist);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        CardType = CARD_WHITELIST;
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

        setViewData(currentStudent.getWhitelist());
        getCardPhone();
    }

    @Override
    protected void setViewData(String value) {
        super.setViewData(value);
        String[] whitelists = null;
        if (value != null) {
            whitelists = value.split(",");
        }

        scrollView.removeAllViews();
        for (int i = 0; i < maxLength; i++) {
            WhiteCardView cardView = new WhiteCardView(this);
            String nameNum = "";
            if (whitelists != null && whitelists.length > i) {
                nameNum = whitelists[i];
            }
            cardView.bindData(i + 1, nameNum, contractChooseListener);
            llContent.addView(cardView);
            whiteCardViews.add(cardView);
        }

        llContent.addView(btnSubmit);
        scrollView.addView(llContent);
    }

    @Override
    public void onClick(View v) {
        String values = "";
        for (int i = 0; i < whiteCardViews.size(); i++) {
            WhiteCardView cardView = whiteCardViews.get(i);
            String value = cardView.getInputValue();
            if (value == null) {
                return;
            }
            if (!value.isEmpty()) {
                values += value + ",";
            }
        }

        if (values.length() > 0) {
            values = values.substring(0, values.length() - 1);
        }
        setCardPhone(values);
    }
}
