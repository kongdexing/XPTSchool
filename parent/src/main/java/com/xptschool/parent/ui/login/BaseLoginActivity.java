package com.xptschool.parent.ui.login;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.imsdroid.ImsSipHelper;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.push.UpushTokenHelper;
import com.xptschool.parent.server.ServerManager;
import com.xptschool.parent.ui.main.BaseActivity;

import org.json.JSONObject;

/**
 * Created by dexing on 2017/6/5.
 * No1
 */
public class BaseLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void login(final String account, final String password, DefaultRetryPolicy retryPolicy) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.LOGIN,
                new VolleyHttpParamsEntity()
                        .addParam("username", account)
                        .addParam("password", password)
                        .addParam("type", "4"), retryPolicy,
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        onStartLogin();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                if (!SharedPreferencesUtil.getData(BaseLoginActivity.this, SharedPreferencesUtil.KEY_USER_NAME, "").equals(account)) {
                                    SharedPreferencesUtil.saveData(BaseLoginActivity.this, SharedPreferencesUtil.KEY_USER_NAME, account);
                                    ImsSipHelper.getInstance().stopSipServer();
                                    //切换账号
                                    UpushTokenHelper.exitAccount(GreenDaoHelper.getInstance().getCurrentParent());
                                }
                                SharedPreferencesUtil.saveData(BaseLoginActivity.this, SharedPreferencesUtil.KEY_PWD, password);

                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.initBeanStudentByHttpResult(jsonData.getJSONArray("stuData").toString());
                                    CommonUtil.initParentInfoByHttpResult(jsonData.getJSONObject("login").toString(), account);
                                    //删除联系人
                                    GreenDaoHelper.getInstance().deleteContact();

                                    onLoginSuccess();
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: exception " + ex.getMessage());
                                    onLoginFailed("登录失败");
                                }
                                break;
                            default:
                                onLoginFailed(httpResult.getInfo());
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onLoginFailed("登录失败");
                    }
                });
    }

    protected void onStartLogin() {

    }

    protected void onLoginSuccess() {
        ServerManager.getInstance().startService();
    }

    protected void onLoginFailed(String msg) {
        SharedPreferencesUtil.saveData(this, SharedPreferencesUtil.KEY_UID, "");
    }
}
