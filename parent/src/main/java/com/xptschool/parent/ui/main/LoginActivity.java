package com.xptschool.parent.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.push.UpushTokenHelper;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

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
    @BindView(R.id.txtForgetPWD)
    TextView txtForgetPWD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String origin = bundle.getString(ExtraKey.LOGIN_ORIGIN);
            if (origin != null && origin.equals("0")) {
                showImgBack(false);
            }
        }

        String userName = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_USER_NAME, "");
        edtAccount.setText(userName);
        edtAccount.setSelection(edtAccount.getText().length());
    }

    private void initView() {
        llParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = llParent.getRootView().getHeight();
                int height = llParent.getHeight();
                int diff = heightDiff - height;
                Log.i(TAG, "onGlobalLayout  rootH " + heightDiff + "  height:" + height + " diff:" + diff);
                if (diff > 400) {
                    //键盘弹起
                    imgCompany.setVisibility(View.GONE);
                } else {
                    imgCompany.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick({R.id.imgDel, R.id.imgToggle, R.id.btnLogin, R.id.txtForgetPWD})
    void buttonOnclick(View view) {
        switch (view.getId()) {
            case R.id.imgDel:
                edtAccount.setText("");
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

                break;
        }
    }

    private void showPassword(boolean show) {
        if (show) {
            // 显示密码
            edtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgToggle.setImageResource(R.mipmap.login_reg_showpass);
        } else {
            // 隐藏密码
            edtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgToggle.setImageResource(R.mipmap.login_reg_hidepass);
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
                        progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        progress.setVisibility(View.INVISIBLE);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                if (!SharedPreferencesUtil.getData(LoginActivity.this, SharedPreferencesUtil.KEY_USER_NAME, "").equals(account)) {
                                    SharedPreferencesUtil.saveData(LoginActivity.this, SharedPreferencesUtil.KEY_USER_NAME, account);
                                    UpushTokenHelper.switchAccount();
                                }
                                SharedPreferencesUtil.saveData(LoginActivity.this, SharedPreferencesUtil.KEY_PWD, password);

                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.initBeanStudentByHttpResult(jsonData.getJSONArray("stuData").toString());
                                    CommonUtil.initParentInfoByHttpResult(jsonData.getJSONObject("login").toString());
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
                        progress.setVisibility(View.INVISIBLE);
                        btnLogin.setEnabled(true);
                        Log.i(TAG, "onErrorResponse: " + error.getMessage());
                    }
                });
    }
}
