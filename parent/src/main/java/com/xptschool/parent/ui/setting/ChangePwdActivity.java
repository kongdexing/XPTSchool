package com.xptschool.parent.ui.setting;

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
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.login.LoginActivity;
import com.xptschool.parent.ui.main.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePwdActivity extends BaseActivity {

    @BindView(R.id.edtOldPwd)
    EditText edtOldPwd;
    @BindView(R.id.edtNewPwd1)
    EditText edtNewPwd1;
    @BindView(R.id.edtNewPwd2)
    EditText edtNewPwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        setTitle(R.string.setting_change_pwd);
    }

    @OnClick({R.id.btnSubmit})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                String oldpwd = edtOldPwd.getText().toString().trim();
                String newPwd1 = edtNewPwd1.getText().toString().trim();
                String newPwd2 = edtNewPwd2.getText().toString().trim();

                if (oldpwd.isEmpty() || newPwd1.isEmpty() || newPwd2.isEmpty()) {
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
                updatePassword(oldpwd, newPwd1);
                break;
        }
    }

    private void updatePassword(String oldpwd, String newpwd) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.UPDATE_PASSWORD,
                new VolleyHttpParamsEntity()
                        .addParam("user_id", GreenDaoHelper.getInstance().getCurrentParent().getU_id())
                        .addParam("ypassword", oldpwd)
                        .addParam("password", newpwd),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress(R.string.progress_loading_cn);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(ChangePwdActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                            SharedPreferencesUtil.saveData(ChangePwdActivity.this, SharedPreferencesUtil.KEY_PWD, "");
                            Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
    }

}
