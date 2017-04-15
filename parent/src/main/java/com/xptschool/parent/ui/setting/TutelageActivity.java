package com.xptschool.parent.ui.setting;

import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.android.widget.view.SmoothCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.spnRelation)
    MaterialSpinner spnRelation;

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
    //1爸爸2妈妈3爷爷4奶奶5外公6外婆0其它
    private List<String> relations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutelage);
        setTitle(R.string.setting_add_tutelage);

        relations.add("爸爸");
        relations.add("妈妈");
        relations.add("爷爷");
        relations.add("奶奶");
        relations.add("外公");
        relations.add("外婆");
        relations.add("其它");
        spnRelation.setItems(relations);

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
                if (name.isEmpty()) {
                    edt_name.setError("姓名不可为空");
                    return;
                }
                if (phone.isEmpty()) {
                    edt_phone.setError("手机号不可为空");
                    return;
                }
                int relation_int = 0;
                String relation = (String) spnRelation.getSelectedItem();
                for (int i = 0; i < relations.size(); i++) {
                    if (relation.equals(relations.get(i))) {
                        relation_int = i + 1;
                        if (i == relations.size() - 1) {
                            relation_int = 0;
                        }
                    }
                }
                putTutelageInfo(username, name, phone, email, relation_int + "", home_address, work_address, home_phone);
                break;
        }
    }

    private void putTutelageInfo(String username, String name, String phone, String email, String relation,
                                 String home_address, String work_address, String home_phone) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.ADD_TUTELAGE, new VolleyHttpParamsEntity()
                .addParam("stu_id", "")
                .addParam("username", username)
                .addParam("name", name)
                .addParam("phone", phone)
                .addParam("sex", cbx_male.isChecked() ? "1" : "0")
                .addParam("email", email)
                .addParam("relation", relation)
                .addParam("address", home_address)
                .addParam("work_unit", work_address)
                .addParam("family_tel", home_phone)
                .addParam("token", CommonUtil.encryptToken(HttpAction.ADD_TUTELAGE)), new MyVolleyRequestListener() {
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
