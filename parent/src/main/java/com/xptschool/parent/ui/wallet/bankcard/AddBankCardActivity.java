package com.xptschool.parent.ui.wallet.bankcard;

import android.os.Bundle;
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
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;

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
                String cardNum = edt_card.getText().toString().trim();
                String cardName = edt_username.getText().toString().trim();
                String bankName = edt_bankname.getText().toString().trim();
                if (cardNum.isEmpty() || cardName.isEmpty() || bankName.isEmpty()) {
                    Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                String cardType = "";
                if (cbx_card1.isChecked()) {
                    cardType = "0";
                } else {
                    cardType = "1";
                }

                addBankCardInfo(cardName, cardNum, cardType, bankName);
                break;
        }
    }

    private void addBankCardInfo(String username, String card, String cardType, String bankname) {
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
