package com.xptschool.parent.ui.wallet.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.wallet.bankcard.AddBankCardActivity;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;
import com.xptschool.parent.view.CustomEditDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提现
 */
public class TakeOutMoneyActivity extends BaseActivity {

//    @BindView(R.id.spnBankCard)
//    MaterialSpinner spnBankCard;

    @BindView(R.id.txt_rest_money)
    TextView txt_rest_money;

    @BindView(R.id.edt_money)
    EditText edt_money;

    @BindView(R.id.btn_takeout)
    Button btn_takeout;

    @BindView(R.id.txtBankName)
    TextView txtBankName;

    private BeanBankCard currentCard = null;
    private ArrayList<BeanBankCard> listCards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_out_money);
        setTitle(R.string.label_takeout);
        getBankList();

        txt_rest_money.setText(BalanceUtil.getParentBalance() + ",");
        edt_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String money = edt_money.getText().toString();
                if (money.contains(".")) {
                    int dotLength = money.length() - (money.indexOf(".") + 1);
                    if (dotLength > 2) {
                        String _money = money.substring(0, money.length() - 1);
                        edt_money.setText(_money);
                    }
                }

                if (!money.isEmpty() && Double.parseDouble(money) > BalanceUtil.getParentBalance()) {
                    edt_money.setText(money.substring(0, money.length() - 1));
                }
                edt_money.setSelection(edt_money.getText().toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.txtBankName, R.id.btn_takeout, R.id.txt_all_money})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.txtBankName:
                if (currentCard == null) {
                    Intent intent = new Intent(this, AddBankCardActivity.class);
                    intent.putExtra("card", "get");
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(this, BankCardChooseActivity.class);
                    intent.putParcelableArrayListExtra("listCard", listCards);
                    intent.putExtra("card", currentCard);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.txt_all_money:
                edt_money.setText(BalanceUtil.getParentBalance() + "");
                edt_money.setSelection(edt_money.getText().length());
                break;
            case R.id.btn_takeout:
                final String money = edt_money.getText().toString().trim();
                if (money.isEmpty()) {
                    Toast.makeText(this, "请输入提款金额", Toast.LENGTH_SHORT).show();
                }
                final BeanBankCard bankCard = currentCard;
                CustomEditDialog editDialog = new CustomEditDialog(this);
                editDialog.setTitle("用户验证");
                editDialog.setEdtInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editDialog.setHintEdit("请输入当前用户登录密码");
                editDialog.setAlertDialogClickListener(new CustomEditDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick(String value) {
                        String password = (String) SharedPreferencesUtil.getData(TakeOutMoneyActivity.this, SharedPreferencesUtil.KEY_PWD, "");
                        if (value.equals(password)) {
                            takeoutMoney(money, bankCard.getId());
                        } else {
                            Toast.makeText(TakeOutMoneyActivity.this, R.string.error_toast_password, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 1) {
                currentCard = data.getParcelableExtra("card");
                setTxtBankName();
            }
        }
    }

    private void getBankList() {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_BankCards, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.GET_BankCards)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            ArrayList<BeanBankCard> bankCards = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<List<BeanBankCard>>() {
                                    }.getType());

                            if (bankCards.size() == 0) {
                                txtBankName.setText("点击绑定银行卡");
                                btn_takeout.setEnabled(false);
                            } else {
                                listCards = bankCards;
                                currentCard = bankCards.get(0);
                                setTxtBankName();
                            }
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: " + ex.getMessage());
                        }
                        break;
                    default:
                        Toast.makeText(TakeOutMoneyActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }
        });
    }

    private void setTxtBankName() {
        if (currentCard != null) {
            btn_takeout.setEnabled(true);
            txtBankName.setText(PocketHelper.getBankShortName(this, currentCard));
        }
    }

    private void takeoutMoney(String money, String bankId) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.REFUND_ADD, new VolleyHttpParamsEntity()
                .addParam("money", money)
                .addParam("memo", "零钱提款")
                .addParam("bank_id", bankId)
                .addParam("token", CommonUtil.encryptToken(HttpAction.REFUND_ADD)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress(R.string.progress_loading_cn);
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                hideProgress();
                Toast.makeText(TakeOutMoneyActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                    finish();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                hideProgress();
            }
        });
    }
}
