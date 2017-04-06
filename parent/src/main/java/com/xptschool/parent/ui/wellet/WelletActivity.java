package com.xptschool.parent.ui.wellet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.ui.main.BaseActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class WelletActivity extends BaseActivity {

    private static final int SDK_PAY_FLAG = 1;
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2017032706425485";
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC8c05wA5eZPK224dGBWNMuDjw46YQSLdPnNwBluBNGgsAouQ3f6L0MJ5RIVPH5+7IkuwJ0lVF093LbML9LAJNyOmVZ0Rs5wtT4VgcTFWJgcfm+svSyCtfSNlN/dH3vM3PWW6Y1SaY2ceihf3HTb2YL4jNRQaWudgoOumriikGwDoa4N1E8goH6DYtt0ff+6keoSgRXDNjK8mCFqrQRyYN190CbsA+duog/oX/gboK0ZtnQoa6qHTUNZ4jpG4QtPdh+h/k3X1Y7hGTpKNPU4y0bXdo7GLVL/mjyLJDIBU6GqydrOKjIZ3s6ags5OE9abNYym6XYMR/JFNzZ7BRScatFAgMBAAECggEAaL7Ci1pDyiXC/JLZy0Ze4wuAh7Wr9hrI3IxiySced6O3QStSvfD0GyxorCei8+rloqrbe4d/Zj8f9RtMSFkCm4w/x0OGGX3kuD/A4OeS7b6MLWX0wn1qZmpR0NckJG955Fy+roHIRBzeS921m+sgUlyhX3nYqHbtsjAFtvNX/Y2xKPf5WNa4glFk7aij/iPX6pXrHdxdo/rpD6Zt11EpnAk8aGKDunDResJ98OcSA0myPFC51TumePCdDQJyvuwnrKgwQMrI6OZO8xeiiOxJRE0wMCE3RfJjMoCzmB8cwq5EWTiZjDILuLdSBG6Exj28zNYcAQHcdCHE+SfXwWmBwQKBgQDiAmqsmlLxFufeuAHlZR9ocAI+EwhB/ZwiJqzKjCbg5QJqcaVpdJET7kTMfrpL3CdhezdlbDS530SEwrKjKDt+FOHJ5ezF6PTOTxK4KrIE2ZPr7dL9xMKf2Alegj9vrR5yIkoKL9j6vaUu4ePy3mu0/7JgLoZymkHebBBl8xEvkQKBgQDVdQCnPf2abHaP92CzAsKZkmCZPYyLYvfMH7tJH1F1PF8WCP2ZBOp8euHdXNkDHGQduYhn8HkSf2e2ay/ShpZuSA61kclNadluEe0x8iaEEgn+EBfH2kMF68pH0iW5DlW8oer7E1kybL0G0ZEHyZ9S+r0wKf6T3nXTgQ8qxq0OdQKBgQDKcR26M5WdrEXPgoT4REcI1mO71HJuIcvL71aRK07b3WX3kIp41lfpQWDQx6b5sl53+9WX/H+SCoImZPt8F9qKSgwhO9mFQPCfJ8b9vgitPXM5PlLiym8GnI1v4T0PPENsOniVfVxe5KZkQyRadI6Hlw3hB2uYlcHwiF175Gh9cQKBgDBmUzuYpsQ5C7khEmAEpDNGKXkVp6SDUESMfV7bJxE6GyVX7IihwLlw833J67r02Q6UXwWSVSGIme+W5kUKF1nyJMOuxsIy2gZHMk085tbTcEiXRY0fREs3Z6pZUAxh37bhz/IWNQdl+IZvRj9JzEJ4cCVXoE3PB1Bp1xKP8fVxAoGBANOa07QOJ5CmngAKpDlzfsyKfW3CIoeJf/0pSV1LzLgI36lNaiLHEW7HkQAVCkc5E3fzi3LOSeaaQ6vvAe+MWQgkaSpa447HDVHWtXvmgtkSb3ntVP/kMnOtx60UPGfpO2F2xVelAoMv4C5BpFHj0FzeGgBEdhZqUHp23dc3C+tb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellet);
        setTitle(R.string.label_my_wellet);

    }

    @OnClick({R.id.txtPay, R.id.txtWeChatPay})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.txtPay:
                if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE))) {
                    new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                            .setPositiveButton("确定", null).show();
                    return;
                }

                boolean rsa2 = (RSA2_PRIVATE.length() > 0);
                Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
                String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

                String privateKey = RSA2_PRIVATE;
                String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
                final String orderInfo = orderParam + "&" + sign;
                Log.i(TAG, "orderInfo: " + orderInfo);
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(WelletActivity.this);
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
                break;
            case R.id.txtWeChatPay:
                IWXAPI api = WXAPIFactory.createWXAPI(this, XPTApplication.getInstance().WXAPP_ID);
                PayReq req = new PayReq();
//                req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                req.appId = XPTApplication.getInstance().WXAPP_ID;
                req.partnerId = "10000100";
                req.prepayId = "1101000000140415649af9fc314aa427";
                req.nonceStr = "a462b76e7436e98e0ed6e13c64b4fd1c";
                req.timeStamp = "1397527777";
                req.packageValue = "Sign=WXPay";
                req.sign = "582282D72DD2B03AD892830965F428CB16E7A256";
//                req.extData = "app data"; // optional
//                Toast.makeText(this, "正常调起支付", Toast.LENGTH_SHORT).show();
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

                api.registerApp(XPTApplication.getInstance().WXAPP_ID);
                boolean rst = api.sendReq(req);
                Toast.makeText(this, "sendReq result " + rst, Toast.LENGTH_SHORT).show();
                break;
        }
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
                        Toast.makeText(WelletActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(WelletActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

        ;
    };

    @OnClick({R.id.rl_balance, R.id.rlPocketMoney})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.rl_balance:

                break;
            case R.id.rlPocketMoney:
                startActivity(new Intent(this, PocketActivity.class));
                break;
        }
    }

//    //生成随机号，防重发
//    private String getNonceStr() {
//        // TODO Auto-generated method stub
//        Random random = new Random();
//
//        return CommonUtil.md5(String.valueOf(random.nextInt(10000))).getBytes();
//    }

}
