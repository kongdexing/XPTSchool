package com.xptschool.parent.ui.wallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

/**
 * 清单
 */
public class BillActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        setTitle(R.string.label_bill);

    }
}
