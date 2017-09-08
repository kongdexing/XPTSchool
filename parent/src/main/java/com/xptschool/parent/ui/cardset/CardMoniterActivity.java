package com.xptschool.parent.ui.cardset;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.model.GreenDaoHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class CardMoniterActivity extends CardSetBaseActivity {

    @BindView(R.id.moniterCardView)
    MoniterCardView mMoniterCardView;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.btnCall)
    Button btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set);
        setTitle(R.string.label_card_moniter);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        CardType = CARD_MONITER;
        String stuId = bundle.getString(ExtraKey.STUDENT_ID);

        currentStudent = GreenDaoHelper.getInstance().getStudentByStuId(stuId);
        if (currentStudent == null) {
            Toast.makeText(this, R.string.toast_get_student_null, Toast.LENGTH_SHORT).show();
            return;
        }

        setViewData(currentStudent.getMonitor());

        getCardPhone();
    }

    @Override
    protected void setViewData(String value) {
        super.setViewData(value);
        try {
            if (value == null || value.isEmpty()) {
                value = "";
            }
            if (value == null) {
                return;
            }

            mMoniterCardView.bindData(value, contractChooseListener);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.btnOk, R.id.btnCall})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                try {
                    String val = (String) SharedPreferencesUtil.getData(this, spKey, "0");
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

                String phone = "";
                if (mMoniterCardView != null) {
                    phone = mMoniterCardView.getInputValue();
                }
                if (phone == null) {
                    return;
                }
                setCardPhone(phone);
                break;
            case R.id.btnCall:
//                CustomDialog dialog = new CustomDialog(this);
//                dialog.setTitle(R.string.label_cardset);
//                dialog.setMessage(R.string.tip_moniter_call_help);
//                dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
//                    @Override
//                    public void onPositiveClick() {
//                        //call phone
//                        String phone = currentStudent.getCard_phone();
//                        Log.i(TAG, "onPositiveClick phone: " + phone);
//                        if (phone == null || phone.isEmpty()) {
//                            Toast.makeText(CardMoniterActivity.this, "学生卡号码为空", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        try {
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_DIAL);
//                            intent.setData(Uri.parse("tel:" + currentStudent.getCard_phone()));
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            CardMoniterActivity.this.startActivity(intent);
//                        } catch (Exception ex) {
//                            Toast.makeText(CardMoniterActivity.this, R.string.toast_startcall_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
                break;
        }
    }

}
