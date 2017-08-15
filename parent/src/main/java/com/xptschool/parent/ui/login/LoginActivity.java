package com.xptschool.parent.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.meizu.cloud.pushsdk.PushManager;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.push.UpushTokenHelper;
import com.xptschool.parent.server.ServerManager;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.main.BaseMainActivity;
import com.xptschool.parent.ui.main.MainActivity;
import com.xptschool.parent.ui.setting.SettingActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    boolean showPassword = false;
    @BindView(R.id.llParent)
    LinearLayout llParent;
    @BindView(R.id.edtAccount)
    EditText edtAccount;
    @BindView(R.id.edtPwd)
    EditText edtPwd;
    @BindView(R.id.imgToggle)
    ImageView imgToggle;
    @BindView(R.id.btnLogin)
    TextView btnLogin;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.imgCompany)
    ImageView imgCompany;

    HuaweiApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String origin = bundle.getString(ExtraKey.LOGIN_ORIGIN);
            if (origin != null && origin.equals("0")) {
                showImgBack(false);
                //推送不可用
                //拒收通知
                String model = android.os.Build.MODEL;
                String carrier = android.os.Build.MANUFACTURER;
                Log.i(TAG, "onCreate: " + model + "  " + carrier);

                if (carrier.toUpperCase().equals("XIAOMI")) {
                    //推送不可用
                    MiPushClient.disablePush(this);
                } else if (carrier.toUpperCase().equals("HUAWEI")) {
                    client = new HuaweiApiClient.Builder(this)
                            .addApi(HuaweiPush.PUSH_API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                    client.connect();
                    Log.i(TAG, "HUAWEI disable ");
                }
//                else if (carrier.toUpperCase().equals("MEIZU")) {
//                    PushManager.unRegister(this, XPTApplication.MZ_APP_ID, XPTApplication.MZ_APP_KEY);
//                }
                else {
                    PushAgent mPushAgent = PushAgent.getInstance(this);
                    mPushAgent.disable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            Log.i(TAG, "PushAgent disable onSuccess: ");
                        }

                        @Override
                        public void onFailure(String s, String s1) {
                            Log.i(TAG, "PushAgent disable onFailure: " + s + " s1 " + s1);
                        }
                    });
                }

            }
        }

        String userName = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_USER_NAME, "");
        edtAccount.setText(userName);
        edtAccount.setSelection(edtAccount.getText().length());
    }

    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        Log.i(TAG, "HuaweiApiClient 连接成功");
        new Thread() {
            public void run() {
                HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(client, false);
                HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(client, false);
            }
        }.start();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //HuaweiApiClient断开连接的时候，业务可以处理自己的事件
        Log.i(TAG, "HuaweiApiClient 连接断开");
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "HuaweiApiClient连接失败，错误码：" + result.getErrorCode());
    }

    private void initView() {
//        llParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int heightDiff = llParent.getRootView().getHeight();
//                int height = llParent.getHeight();
//                int diff = heightDiff - height;
//                Log.i(TAG, "onGlobalLayout  rootH " + heightDiff + "  height:" + height + " diff:" + diff);
//                if (diff > 400) {
//                    //键盘弹起
//                    imgCompany.setVisibility(View.GONE);
//                } else {
//                    imgCompany.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    @OnClick({R.id.imgDel, R.id.imgToggle, R.id.btnLogin, R.id.txtForgetPWD})
    void buttonOnclick(View view) {
        switch (view.getId()) {
            case R.id.imgDel:
                edtAccount.setText("");
                edtAccount.requestFocus();
                break;
            case R.id.imgToggle:
                showPassword(!showPassword);
                break;
            case R.id.btnLogin:
                String account = edtAccount.getText().toString();
                String password = edtPwd.getText().toString();

                if ((!TextUtils.isEmpty(account)) && (!TextUtils.isEmpty(password))) {
                    btnLogin.setEnabled(false);
                    login(account, password);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_empty_login, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txtForgetPWD:
                startActivity(new Intent(this, CheckUserActivity.class));
                break;
        }
    }

    private void showPassword(boolean show) {
        if (show) {
            // 显示密码
            edtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgToggle.setImageResource(R.drawable.login_reg_showpass);
        } else {
            // 隐藏密码
            edtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgToggle.setImageResource(R.drawable.login_reg_hidepass);
        }
        edtPwd.setSelection(edtPwd.getText().length());
        showPassword = show;
    }

    private void login(final String account, final String password) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.LOGIN,
                new VolleyHttpParamsEntity()
                        .addParam("username", account)
                        .addParam("password", password)
                        .addParam("type", "4"),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (progress != null)
                            progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        if (progress != null)
                            progress.setVisibility(View.INVISIBLE);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                if (!SharedPreferencesUtil.getData(LoginActivity.this, SharedPreferencesUtil.KEY_USER_NAME, "").equals(account)) {
                                    SharedPreferencesUtil.saveData(LoginActivity.this, SharedPreferencesUtil.KEY_USER_NAME, account);
                                    UpushTokenHelper.exitAccount(GreenDaoHelper.getInstance().getCurrentParent());
                                }
                                SharedPreferencesUtil.saveData(LoginActivity.this, SharedPreferencesUtil.KEY_PWD, password);

                                ServerManager.getInstance().stopService(LoginActivity.this);

                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.initBeanStudentByHttpResult(jsonData.getJSONArray("stuData").toString());
                                    CommonUtil.initParentInfoByHttpResult(jsonData.getJSONObject("login").toString(), account);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: exception " + ex.getMessage());
                                    Toast.makeText(LoginActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(LoginActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        btnLogin.setEnabled(true);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        if (progress != null)
                            progress.setVisibility(View.INVISIBLE);
                        btnLogin.setEnabled(true);
                        Log.i(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });
    }
}
