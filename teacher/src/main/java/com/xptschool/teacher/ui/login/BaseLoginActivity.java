package com.xptschool.teacher.ui.login;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.imsdroid.ImsSipHelper;
import com.xptschool.teacher.imsdroid.NetWorkStatusChangeHelper;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.push.UpushTokenHelper;
import com.xptschool.teacher.server.ServerManager;
import com.xptschool.teacher.ui.main.BaseActivity;

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
                        .addParam("type", "3"), retryPolicy,
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
                                    UpushTokenHelper.exitAccount();
                                }
                                SharedPreferencesUtil.saveData(BaseLoginActivity.this, SharedPreferencesUtil.KEY_PWD, password);
                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.getBeanClassesByHttpResult(jsonData.getJSONArray("class").toString());
                                    CommonUtil.getBeanCoursesByHttpResult(jsonData.getJSONArray("course").toString());
                                    JSONObject jsonLogin = jsonData.getJSONObject("login");
                                    Gson gson = new Gson();
                                    BeanTeacher teacher = gson.fromJson(jsonLogin.toString(), BeanTeacher.class);
                                    teacher.setLogin_name(account);
                                    GreenDaoHelper.getInstance().insertTeacher(teacher);
                                    //删除联系人
                                    GreenDaoHelper.getInstance().deleteContacts();
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
        ServerManager.getInstance().startSocketServer(this);
    }

    protected void onLoginFailed(String msg) {

    }
}
