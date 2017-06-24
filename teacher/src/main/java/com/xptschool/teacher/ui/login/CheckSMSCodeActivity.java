package com.xptschool.teacher.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.teacher.R;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.ui.main.BaseActivity;
import com.xptschool.teacher.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class CheckSMSCodeActivity extends BaseActivity {

    @BindView(R.id.edtPhone)
    EditText edtPhone;
    @BindView(R.id.edtCode)
    EditText edtCode;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_smscode);
        setTitle(R.string.title_forgot_password);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userName = bundle.getString("username");
        }

    }

    @OnClick({R.id.btnSend, R.id.btnNext})
    void viewOnClick(View view) {
        String phone = edtPhone.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btnSend:
                if (phone.isEmpty()) {
                    ToastUtils.showToast(this, R.string.hint_phone);
                    return;
                }

                getVerifyCode(phone);

                break;
            case R.id.btnNext:
                String code = edtCode.getText().toString().trim();
                if (code.isEmpty()) {
                    ToastUtils.showToast(CheckSMSCodeActivity.this, R.string.hint_code);
                    return;
                }
                checkVerifyCode(code);
                break;
        }
    }

    private void getVerifyCode(String phone) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.FORGOT_PWD_STEP1,
                new VolleyHttpParamsEntity()
                        .addParam("phone", phone)
                        .addParam("username", userName), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

    private void checkVerifyCode(String code) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.FORGOT_PWD_STEP1,
                new VolleyHttpParamsEntity()
                        .addParam("code", code)
                        .addParam("username", userName), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

}
