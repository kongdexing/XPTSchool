package com.xptschool.parent.ui.wallet.bankcard;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.SmoothCheckBox;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.view.CustomDialog;
import com.xptschool.parent.view.CustomEditDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加银行卡
 */
public class AddBankCardActivity extends BaseActivity {

    @BindView(R.id.edt_username)
    EditText edt_username;

    @BindView(R.id.edt_card)
    EditText edt_card;

    @BindView(R.id.edt_bankname)
    EditText edt_bankname;

    @BindView(R.id.cbx_card1)
    SmoothCheckBox cbx_card1;

    @BindView(R.id.cbx_card2)
    SmoothCheckBox cbx_card2;
    @BindView(R.id.txtCard1)
    TextView txtCard1;
    @BindView(R.id.txtCard2)
    TextView txtCard2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        setTitle(R.string.label_bankcard_addcard);

        cbx_card1.setChecked(true);
        edt_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.ll_type1, R.id.cbx_card1, R.id.txtCard1, R.id.ll_type2, R.id.cbx_card2, R.id.txtCard2, R.id.btnSubmit})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.ll_type1:
            case R.id.cbx_card1:
            case R.id.txtCard1:
                cbx_card1.setChecked(true);
                cbx_card2.setChecked(false);
                break;
            case R.id.ll_type2:
            case R.id.cbx_card2:
            case R.id.txtCard2:
                cbx_card1.setChecked(false);
                cbx_card2.setChecked(true);
                break;
            case R.id.btnSubmit:
                final String cardNum = edt_card.getText().toString().trim();
                final String cardName = edt_username.getText().toString().trim();
                final String bankName = edt_bankname.getText().toString().trim();
                if (cardNum.isEmpty() || cardName.isEmpty() || bankName.isEmpty()) {
                    Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!checkBankCard(cardNum)) {
                    CustomDialog dialog = new CustomDialog(AddBankCardActivity.this);
                    dialog.setTitle("银行卡验证");
                    dialog.setMessage("您输入的银行卡有误，请重新输入，继续添加？");
                    dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            addBankCardInfo(cardName, cardNum, bankName, true);
                        }
                    });
                } else {
                    addBankCardInfo(cardName, cardNum, bankName, true);
                }
                break;
        }
    }

    private void addBankCardInfo(final String username, final String card, final String bankname, boolean verify) {

        if (verify) {
            CustomEditDialog editDialog = new CustomEditDialog(this);
            editDialog.setTitle("用户验证");
            editDialog.setEdtInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editDialog.setHintEdit("请输入当前用户登录密码");
            editDialog.setAlertDialogClickListener(new CustomEditDialog.DialogClickListener() {
                @Override
                public void onPositiveClick(String value) {
                    String password = (String) SharedPreferencesUtil.getData(AddBankCardActivity.this, SharedPreferencesUtil.KEY_PWD, "");
                    if (value.equals(password)) {
                        addBankCardInfo(username, card, bankname, false);
                    } else {
                        Toast.makeText(AddBankCardActivity.this, "密码输入错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        } else {
            String cardType = "";
            if (cbx_card1.isChecked()) {
                cardType = "0";
            } else {
                cardType = "1";
            }

            VolleyHttpService.getInstance().sendPostRequest(HttpAction.Add_BankCard, new VolleyHttpParamsEntity()
                            .addParam("cardholder", username)
                            .addParam("card_no", card)
                            .addParam("card_type", cardType)
                            .addParam("bankname", bankname)
                            .addParam("token", CommonUtil.encryptToken(HttpAction.Add_BankCard)), new MyVolleyRequestListener() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            showProgress(R.string.progress_loading_cn);
                        }

                        @Override
                        public void onResponse(VolleyHttpResult volleyHttpResult) {
                            super.onResponse(volleyHttpResult);
                            hideProgress();
                            Toast.makeText(AddBankCardActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                            switch (volleyHttpResult.getStatus()) {
                                case HttpAction.SUCCESS:
                                    finish();
                                    break;
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            super.onErrorResponse(volleyError);
                            hideProgress();
                        }
                    }
            );
        }
    }

    public static boolean checkBankCard(String bankCard) {
        if (bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * 该校验的过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，则将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+") || nonCheckCodeCardId.trim().length() < 15
                || nonCheckCodeCardId.trim().length() > 18) {
            //如果传的数据不合法返回N
            System.out.println("银行卡号不合法！");
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        // 执行luh算法
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {  //偶数位处理
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


}
