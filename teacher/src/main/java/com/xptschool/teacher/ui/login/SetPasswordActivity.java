package com.xptschool.teacher.ui.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class SetPasswordActivity extends BaseActivity {


    @BindView(R.id.edtNewPwd1)
    EditText edtNewPwd1;
    @BindView(R.id.edtNewPwd2)
    EditText edtNewPwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        setTitle(R.string.title_forgot_password);

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
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.FORGOT_PWD_STEP1,
                new VolleyHttpParamsEntity()
                        .addParam("pass", password)
                        .addParam("password", password)
                        .addParam("username", ""), new MyVolleyRequestListener() {
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
