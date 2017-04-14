package com.xptschool.parent.ui.setting;

import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.SmoothCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xptschool.parent.R;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加监护人
 */
public class TutelageActivity extends BaseActivity {

    @BindView(R.id.edt_username)
    MaterialEditText edt_username;

    @BindView(R.id.edt_name)
    MaterialEditText edt_name;

    @BindView(R.id.edt_phone)
    MaterialEditText edt_phone;

    @BindView(R.id.edt_email)
    MaterialEditText edt_email;

    @BindView(R.id.edt_home_address)
    MaterialEditText edt_home_address;

    @BindView(R.id.edt_home_phone)
    MaterialEditText edt_home_phone;

    @BindView(R.id.edt_work_address)
    MaterialEditText edt_work_address;

    @BindView(R.id.cbx_male)
    SmoothCheckBox cbx_male;
    @BindView(R.id.cbx_female)
    SmoothCheckBox cbx_female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutelage);
        setTitle(R.string.setting_add_tutelage);

        cbx_male.setChecked(true);
    }

    @OnClick({R.id.ll_male, R.id.cbx_male, R.id.ll_female, R.id.cbx_female, R.id.btn_submit})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.ll_male:
            case R.id.cbx_male:
                cbx_male.setChecked(true);
                cbx_female.setChecked(false);
                break;
            case R.id.ll_female:
            case R.id.cbx_female:
                cbx_male.setChecked(false);
                cbx_female.setChecked(true);
                break;
            case R.id.btn_submit:
                String username = edt_username.getText().toString().trim();
                String name = edt_name.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                String email = edt_email.getText().toString().trim();
                String home_address = edt_home_address.getText().toString().trim();
                String home_phone = edt_home_phone.getText().toString().trim();
                String work_address = edt_work_address.getText().toString().trim();

                if (username.isEmpty()) {
                    edt_username.setError("用户名不可为空");
                    return;
                }

                putTutelageInfo(username, name, phone, email, home_address, work_address);
                break;
        }
    }

    private void putTutelageInfo(String username, String name, String phone, String email, String home_address, String work_address) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.ADD_TUTELAGE, new VolleyHttpParamsEntity()
                .addParam("", ""), new MyVolleyRequestListener() {
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
