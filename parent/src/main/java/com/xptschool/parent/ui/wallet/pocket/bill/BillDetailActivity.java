package com.xptschool.parent.ui.wallet.pocket.bill;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.wallet.pocket.BeanPocketRecord;

/**
 * 账单详情
 */
public class BillDetailActivity extends BaseActivity {

    private BeanPocketRecord currentRecord;
    private FragmentManager mFgtManager;
    private FragmentTransaction mFgtTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        setTitle(R.string.label_pocket_bill_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentRecord = bundle.getParcelable("record");
        }
        if (currentRecord != null) {
//            getBillDetail(currentRecord);
            initData();
        }
    }

    private void initData() {
        mFgtManager = getSupportFragmentManager();
        mFgtTransaction = mFgtManager.beginTransaction();
        if (currentRecord.getType().equals("提现")) {
            OutMoneyFragment outFragment = new OutMoneyFragment();
            mFgtTransaction.add(R.id.fl_Content, outFragment).commit();
            outFragment.setPocketRecord(currentRecord);
        }else{
            InMoneyFragment inFragment = new InMoneyFragment();
            mFgtTransaction.add(R.id.fl_Content, inFragment).commit();
            inFragment.setPocketRecord(currentRecord);
        }
    }

}
