package com.xptschool.parent.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class SetPasswordActivity extends BaseActivity {


    @BindView(R.id.edtNewPwd1)
    EditText edtNewPwd1;
    @BindView(R.id.edtNewPwd2)
    EditText edtNewPwd2;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        setTitle(R.string.title_forgot_password);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userName = bundle.getString("username");
        }
    }


    @OnClick({R.id.btnSubmit})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                String newPwd1 = edtNewPwd1.getText().toString().trim();
                String newPwd2 = edtNewPwd2.getText().toString().trim();
                if (newPwd1.isEmpty() || newPwd2.isEmpty()) {
                    Toast.makeText(this, R.string.toast_edit_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPwd1.length() < 6) {
                    Toast.makeText(this, R.string.toast_password_length_lt6, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPwd1.equals(newPwd2)) {
                    Toast.makeText(this, R.string.toast_pwd_not_equal, Toast.LENGTH_SHORT).show();
                    return;
                }

                resetPassword(newPwd1);

                break;
        }
    }

    private void resetPassword(String password) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.FORGOT_PWD_STEP4,
                new VolleyHttpParamsEntity()
                        .addParam("pass", password)
                        .addParam("password", password)
                        .addParam("username", userName), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                            //返回登录页面
                            startActivity(new Intent(SetPasswordActivity.this, LoginActivity.class));
                        }
                        ToastUtils.showToast(SetPasswordActivity.this, volleyHttpResult.getInfo());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

}
