package com.xptschool.parent.ui.wallet;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanLearningModule;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.cardset.ContractClickActivity;
import com.xptschool.parent.util.ToastUtils;
import com.xptschool.parent.view.CustomEditDialog;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class TelephoneFareActivity extends ContractClickActivity {

    @BindView(R.id.txt_recharge_30)
    TextView txt_recharge_30;

    @BindView(R.id.txt_recharge_50)
    TextView txt_recharge_50;

    @BindView(R.id.txt_recharge_100)
    TextView txt_recharge_100;

    @BindView(R.id.txt_recharge_200)
    TextView txt_recharge_200;

    @BindView(R.id.txt_recharge_300)
    TextView txt_recharge_300;

    @BindView(R.id.txt_recharge_500)
    TextView txt_recharge_500;

    @BindView(R.id.edtPhone)
    EditText edtPhone;

    ArrayList<TextView> rechargeUI = new ArrayList<>();

    private BeanLearningModule currentLearning = null;
    private int recharge_limit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_fare);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentLearning = (BeanLearningModule) bundle.get(ExtraKey.LEARNING_MODEL);
        }

        if (currentLearning == null) {
            return;
        }
        setTitle(currentLearning.getTitle());
        initView();
    }

    private void initView() {
        rechargeUI.clear();
        rechargeUI.add(txt_recharge_30);
        rechargeUI.add(txt_recharge_50);
        rechargeUI.add(txt_recharge_100);
        rechargeUI.add(txt_recharge_200);
        rechargeUI.add(txt_recharge_300);
        rechargeUI.add(txt_recharge_500);
    }

    @OnClick({R.id.txt_recharge_30, R.id.txt_recharge_50, R.id.txt_recharge_100,
            R.id.txt_recharge_200, R.id.txt_recharge_300, R.id.txt_recharge_500, R.id.imgSelect})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.txt_recharge_30:
            case R.id.txt_recharge_50:
            case R.id.txt_recharge_100:
            case R.id.txt_recharge_200:
            case R.id.txt_recharge_300:
            case R.id.txt_recharge_500:
                final String phone = edtPhone.getText().toString().trim();
                if (phone.isEmpty()) {
                    return;
                }

                if (!CommonUtil.isPhone(phone)) {
                    ToastUtils.showToast(this, R.string.input_error_phone);
                    return;
                }
                resetRechargeUI((TextView) view);
                CustomEditDialog editDialog = new CustomEditDialog(this);
                editDialog.setTitle(R.string.title_user_verify);
                editDialog.setEdtInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editDialog.setHintEdit(R.string.msg_input_password);
                editDialog.setAlertDialogClickListener(new CustomEditDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick(String value) {
                        String password = (String) SharedPreferencesUtil.getData(TelephoneFareActivity.this, SharedPreferencesUtil.KEY_PWD, "");
                        if (value.equals(password)) {
                            getTelFareOrder(phone);
                        } else {
                            Toast.makeText(TelephoneFareActivity.this, R.string.error_toast_password, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                break;
            case R.id.imgSelect:
                contractChooseListener.onContractClick();
                break;
        }
    }

    @Override
    public void onChooseContractResult(String[] contacts) {
        super.onChooseContractResult(contacts);
        if (contacts.length > 1) {
            edtPhone.setText(contacts[1]);
            edtPhone.setSelection(edtPhone.getText().toString().length());
        }
    }

    private void getTelFareOrder(String phone) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_TEL_RECHARGE_ORDER,
                new VolleyHttpParamsEntity()
                        .addParam("mobile", phone)
                        .addParam("money", recharge_limit + "")
                        .addParam("token", CommonUtil.encryptToken(HttpAction.GET_TEL_RECHARGE_ORDER)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress(R.string.progress_loading_cn);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject object = new JSONObject(volleyHttpResult.getData().toString());
                                    String notice_sn = object.getString("notice_sn");
                                    String in_price = object.getString("inprice");
                                    String access_token = CommonUtil.md5(notice_sn + "shuhaixinxi_phone_recharge_order" +
                                            GreenDaoHelper.getInstance().getCurrentParent().getU_id() + in_price);
                                    doTelTopUp(access_token, notice_sn);
                                } catch (Exception ex) {
                                    hideProgress();
                                    ToastUtils.showToast(TelephoneFareActivity.this, "充值失败");
                                }
                                break;
                            default:
                                hideProgress();
                                ToastUtils.showToast(TelephoneFareActivity.this, volleyHttpResult.getInfo());
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
    }

    private void doTelTopUp(String access_token, String notice_sn) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.TEL_RECHARGE,
                new VolleyHttpParamsEntity()
                        .addParam("access_token", access_token)
                        .addParam("notice_sn", notice_sn)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.TEL_RECHARGE)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        ToastUtils.showToast(TelephoneFareActivity.this, volleyHttpResult.getInfo());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
    }

    private void resetRechargeUI(TextView view) {
        for (int i = 0; i < rechargeUI.size(); i++) {
            TextView rechargeView = rechargeUI.get(i);
            rechargeView.setBackground(getResources().getDrawable(R.drawable.bg_recharge_money));
            rechargeView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        view.setBackground(getResources().getDrawable(R.color.colorPrimary));
        view.setTextColor(getResources().getColor(R.color.white));
        recharge_limit = Integer.parseInt(view.getTag().toString());
//        recharge_limit = 1;
    }
}
