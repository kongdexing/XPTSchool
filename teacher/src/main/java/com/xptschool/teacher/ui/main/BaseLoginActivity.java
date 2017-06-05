package com.xptschool.teacher.ui.main;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.push.UpushTokenHelper;

import org.json.JSONObject;

/**
 * Created by dexing on 2017/6/5.
 * No1
 */
public class BaseLoginActivity extends BaseActivity {

    //login video chat server

    

    public void login(final String account, final String password) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.LOGIN,
                new VolleyHttpParamsEntity()
                        .addParam("username", account)
                        .addParam("password", password)
                        .addParam("type", "3"),
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
                                    UpushTokenHelper.switchAccount();
                                }
                                SharedPreferencesUtil.saveData(BaseLoginActivity.this, SharedPreferencesUtil.KEY_PWD, password);
                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.getBeanClassesByHttpResult(jsonData.getJSONArray("class").toString());
                                    CommonUtil.getBeanCoursesByHttpResult(jsonData.getJSONArray("course").toString());
                                    JSONObject jsonLogin = jsonData.getJSONObject("login");
                                    Gson gson = new Gson();
                                    BeanTeacher teacher = gson.fromJson(jsonLogin.toString(), BeanTeacher.class);
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

    }

    protected void onLoginFailed(String msg) {

    }
}
