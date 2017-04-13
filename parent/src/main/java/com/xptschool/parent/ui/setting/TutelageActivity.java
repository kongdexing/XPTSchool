package com.xptschool.parent.ui.setting;

import android.os.Bundle;
import android.view.View;

import com.android.widget.view.SmoothCheckBox;
import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加监护人
 */
public class TutelageActivity extends BaseActivity {

    @BindView(R.id.cbx_male)
    SmoothCheckBox cbx_male;
    @BindView(R.id.cbx_female)
    SmoothCheckBox cbx_female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutelage);
        setTitle(R.string.setting_add_tutelage);

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

                break;
        }
    }

}
