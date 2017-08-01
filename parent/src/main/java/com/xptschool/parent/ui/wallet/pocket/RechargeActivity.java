package com.xptschool.parent.ui.wallet.pocket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.SmoothCheckBox;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.wallet.alipay.PayResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 充值
 */

public class RechargeActivity extends BaseActivity {

    @BindView(R.id.txt_recharge_50)
    TextView txt_recharge_50;

    @BindView(R.id.txt_recharge_100)
    TextView txt_recharge_100;

    @BindView(R.id.txt_recharge_150)
    TextView txt_recharge_150;

    @BindView(R.id.txt_recharge_200)
    TextView txt_recharge_200;

    @BindView(R.id.txt_recharge_300)
    TextView txt_recharge_300;

    @BindView(R.id.txt_recharge_400)
    TextView txt_recharge_400;

    ArrayList<TextView> rechargeUI = new ArrayList<>();

    @BindView(R.id.cbx_alipay)
    SmoothCheckBox cbx_alipay;
    @BindView(R.id.cbx_wxpay)
    SmoothCheckBox cbx_wxpay;
    @BindView(R.id.cbx_uppay)
    SmoothCheckBox cbx_uppay;

    private int recharge_limit = 0;

    private static final int SDK_PAY_FLAG = 1;
    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        setTitle(R.string.label_recharge);
        initView();
    }

    private void initView() {
        rechargeUI.clear();
        rechargeUI.add(txt_recharge_50);
        rechargeUI.add(txt_recharge_100);
        rechargeUI.add(txt_recharge_150);
        rechargeUI.add(txt_recharge_200);
        rechargeUI.add(txt_recharge_300);
        rechargeUI.add(txt_recharge_400);
        cbx_alipay.setChecked(true);
        viewOnClick(txt_recharge_50);
    }

    @OnClick({R.id.rl_alipay, R.id.cbx_alipay,
            R.id.rl_wxpay, R.id.cbx_wxpay,
            R.id.rl_uppay, R.id.cbx_uppay,
            R.id.txt_recharge_50, R.id.txt_recharge_100, R.id.txt_recharge_150,
            R.id.txt_recharge_200, R.id.txt_recharge_300, R.id.txt_recharge_400, R.id.btn_recharge})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.txt_recharge_50:
            case R.id.txt_recharge_100:
            case R.id.txt_recharge_150:
            case R.id.txt_recharge_200:
            case R.id.txt_recharge_300:
            case R.id.txt_recharge_400:
                resetRechargeUI((TextView) view);
                break;
            case R.id.rl_alipay:
            case R.id.cbx_alipay:
                cbx_alipay.setChecked(true);
                cbx_wxpay.setChecked(false);
                cbx_uppay.setChecked(false);
                break;
            case R.id.rl_wxpay:
            case R.id.cbx_wxpay:
                cbx_alipay.setChecked(false);
                cbx_wxpay.setChecked(true);
                cbx_uppay.setChecked(false);
                break;
            case R.id.rl_uppay:
            case R.id.cbx_uppay:
                cbx_alipay.setChecked(false);
                cbx_wxpay.setChecked(false);
                cbx_uppay.setChecked(true);
                break;
            case R.id.btn_recharge:
                getOrderInfo();
                break;
        }
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
    }

    private void getOrderInfo() {
        String payment_id = "0";
        if (cbx_wxpay.isChecked()) {
            payment_id = "1";
        } else if (cbx_alipay.isChecked()) {
            payment_id = "0";
        } else {
            payment_id = "2";
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_OrderInfo, new VolleyHttpParamsEntity()
                .addParam("deal_price", recharge_limit + "")
//                .addParam("deal_price", "0.01")
                .addParam("num", "1")
                .addParam("payment_id", payment_id) //支付方式 0支付宝 1微信 2银联
                .addParam("type", "0") //充值
                .addParam("memo", "零钱充值")
                .addParam("token", CommonUtil.encryptToken(HttpAction.GET_OrderInfo)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress(R.string.label_recharge_progress);
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                hideProgress();
                if (cbx_wxpay.isChecked()) {
                    try {
                        JSONObject jsonObject = new JSONObject(volleyHttpResult.getData().toString());
                        IWXAPI api = WXAPIFactory.createWXAPI(RechargeActivity.this, XPTApplication.getInstance().WXAPP_ID);
                        boolean register = api.registerApp(XPTApplication.getInstance().WXAPP_ID);
                        PayReq req = new PayReq();
                        req.appId = XPTApplication.getInstance().WXAPP_ID;
                        req.partnerId = jsonObject.getString("partnerid");
                        req.prepayId = jsonObject.getString("prepayid");
                        req.nonceStr = jsonObject.getString("noncestr");
                        req.timeStamp = jsonObject.getString("timestamp");
                        req.packageValue = jsonObject.getString("package");
                        req.sign = jsonObject.getString("sign");
//                req.extData = "app data"; // optional
//                Toast.makeText(this, "正常调起支付", Toast.LENGTH_SHORT).show();
                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                        boolean rst = api.sendReq(req);
//                        Toast.makeText(RechargeActivity.this, "register:" + register + " sendReq result " + rst, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
//                    toWXpay();
                } else if (cbx_alipay.isChecked()) {
                    final String orderInfo = volleyHttpResult.getInfo();
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(RechargeActivity.this);
                            Map<String, String> result = alipay.payV2(orderInfo, true);
                            Log.i(TAG, "payV2:" + result.toString());
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    UPPayAssistEx.startPay(RechargeActivity.this, null, null, volleyHttpResult.getInfo().toString(), mMode);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                hideProgress();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.i(TAG, "handleMessage: " + resultInfo + " status :" + resultStatus);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        ;
    };

}
