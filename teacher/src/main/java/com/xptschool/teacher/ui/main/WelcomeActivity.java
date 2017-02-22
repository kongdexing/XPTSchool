package com.xptschool.teacher.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.mine.ChangePwdActivity;

import org.json.JSONObject;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        showActionBar(false);

        final Intent intent = new Intent();

        String userName = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_USER_NAME, "");
        String password = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_PWD, "");
        String splash_init = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_SPLASH_INIT, "0");

        if (splash_init.equals("0")) {
            Intent[] intents = new Intent[2];
            intents[1] = new Intent(this, SplashActivity.class);
            intents[0] = new Intent(this, LoginActivity.class);
            intents[0].putExtra(ExtraKey.LOGIN_ORIGIN, "0");
            startActivities(intents);
            finish();
            return;
        }

        if (password.isEmpty() || userName.isEmpty()) {
            intent.setClass(this, LoginActivity.class);
            intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
            startActivity(intent);
            finish();
        } else {
            //login
            login(userName, password);
        }
    }

    private void login(final String account, final String password) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.LOGIN,
                new VolleyHttpParamsEntity()
                        .addParam("username", account)
                        .addParam("password", password)
                        .addParam("type", "3"),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.getBeanClassesByHttpResult(jsonData.getJSONArray("class").toString());
                                    CommonUtil.getBeanCoursesByHttpResult(jsonData.getJSONArray("course").toString());
                                    JSONObject jsonLogin = jsonData.getJSONObject("login");
                                    Gson gson = new Gson();
                                    BeanTeacher teacher = gson.fromJson(jsonLogin.toString(), BeanTeacher.class);
                                    GreenDaoHelper.getInstance().insertTeacher(teacher);
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    finish();
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: error " + ex.getMessage());
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                                    startActivity(intent);
                                    finish();
                                }
                                break;
                            default:
                                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                                startActivity(intent);
                                finish();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                        startActivity(intent);
                        finish();
                    }
                });
    }

}
