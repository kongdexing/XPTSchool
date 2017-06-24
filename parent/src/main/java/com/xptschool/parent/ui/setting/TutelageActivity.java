package com.xptschool.parent.ui.setting;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.android.widget.view.SmoothCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.util.ParentUtil;
import com.xptschool.parent.view.CustomDialog;
import com.xptschool.parent.view.CustomEditDialog;

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
    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

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
        spnRelation.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                if (i == 1 || i == 3 || i == 5) {
                    cbx_female.setChecked(true);
                    cbx_male.setChecked(false);
                } else if (i == 0 || i == 2 || i == 4) {
                    cbx_female.setChecked(false);
                    cbx_male.setChecked(true);
                }
            }
        });

        List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
        BeanStudent allStu = new BeanStudent();
        allStu.setStu_name("全部");
        students.add(allStu);
        spnStudents.setItems(students);

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
                final String username = edt_username.getText().toString().trim();
                final String name = edt_name.getText().toString().trim();
                final String phone = edt_phone.getText().toString().trim();
                final String email = edt_email.getText().toString().trim();
                final String home_address = edt_home_address.getText().toString().trim();
                final String home_phone = edt_home_phone.getText().toString().trim();
                final String work_address = edt_work_address.getText().toString().trim();

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
                final String relation_str = relation_int + "";

                CustomEditDialog editDialog = new CustomEditDialog(this);
                editDialog.setTitle(R.string.title_user_verify);
                editDialog.setEdtInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editDialog.setHintEdit(R.string.msg_input_password);
                editDialog.setAlertDialogClickListener(new CustomEditDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick(String value) {
                        String password = (String) SharedPreferencesUtil.getData(TutelageActivity.this, SharedPreferencesUtil.KEY_PWD, "");
                        if (value.equals(password)) {
                            putTutelageInfo(username, name, phone, email, relation_str, home_address, work_address, home_phone);
                        } else {
                            Toast.makeText(TutelageActivity.this, R.string.error_toast_password, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                break;
        }
    }

    private void putTutelageInfo(final String username, String name, String phone, String email, String relation,
                                 String home_address, String work_address, String home_phone) {

        String stuIds = "";
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();
        if (student.getStu_id() == null || student.getStu_id().isEmpty()) {
            stuIds = ParentUtil.getStuId();
        } else {
            stuIds = student.getStu_id();
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.ADD_TUTELAGE, new VolleyHttpParamsEntity()
                .addParam("stu_id", stuIds)
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
                showProgress(R.string.progress_loading_cn);
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                hideProgress();
                Toast.makeText(TutelageActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();

                if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                    CustomDialog dialog = new CustomDialog(TutelageActivity.this);
                    dialog.setTitle("添加监护人");
                    dialog.setMessage("添加监护人成功。\n用户名：" + username + "\n密码：123456");
                    dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            finish();
                        }
                    });
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
