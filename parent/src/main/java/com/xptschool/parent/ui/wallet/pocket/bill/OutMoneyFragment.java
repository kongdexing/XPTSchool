package com.xptschool.parent.ui.wallet.pocket.bill;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.xptschool.parent.R;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.fragment.BaseFragment;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;
import com.xptschool.parent.ui.wallet.pocket.BeanPocketRecord;
import com.xptschool.parent.ui.wallet.pocket.PocketHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/21.
 * No1
 */

public class OutMoneyFragment extends BaseFragment {

    @BindView(R.id.txt_money)
    TextView txt_money;

    @BindView(R.id.txt_status)
    TextView txt_status;

    @BindView(R.id.txt_pay_type)
    TextView txt_pay_type;

    @BindView(R.id.txt_pay_info)
    TextView txt_pay_info;

    @BindView(R.id.txt_submit_time)
    TextView txt_submit_time;

    @BindView(R.id.txt_pay_time)
    TextView txt_pay_time;
    @BindView(R.id.llPayTime)
    LinearLayout llPayTime;

    private BeanPocketRecord pocketRecord;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mRootView = inflater.inflate(R.layout.frag_money_out, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    public void setPocketRecord(BeanPocketRecord pocketRecord) {
        Log.i(TAG, "setPocketRecord: ");
        this.pocketRecord = pocketRecord;
        if (pocketRecord == null) {
            return;
        }
        getBillDetail(pocketRecord);
    }

    @Override
    protected void initData() {
        Log.i(TAG, "initData: ");
    }

    private void getBillDetail(final BeanPocketRecord record) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.POCKET_BILL_DETAIL, new VolleyHttpParamsEntity()
                        .addParam("id", record.getId()),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    if (record.getType().equals("提现")) {
                                        Gson gson = new Gson();
                                        BeanTakeMoney takeMoney = gson.fromJson(volleyHttpResult.getData().toString(), BeanTakeMoney.class);

                                        txt_money.setText(pocketRecord.getMoney());

                                        BeanBankCard bankCard = new BeanBankCard();
                                        bankCard.setCard_type(takeMoney.getCard_type());
                                        bankCard.setCard_no(takeMoney.getCard_no());
                                        bankCard.setBankname(takeMoney.getBankname());

                                        txt_pay_info.setText(takeMoney.getMemo());
                                        txt_submit_time.setText(takeMoney.getCreate_time());

                                        //0'未审核',1'通过',2'驳回',3'成功'
                                        if (takeMoney.getIs_pay().equals("0")) {
                                            txt_status.setText("正在处理");
                                            llPayTime.setVisibility(View.GONE);
                                        } else if (takeMoney.getIs_pay().equals("1")) {
                                            txt_status.setText("正在处理");
                                            llPayTime.setVisibility(View.GONE);
                                        } else if (takeMoney.getIs_pay().equals("2")) {
                                            txt_status.setText("交易取消");
                                            llPayTime.setVisibility(View.VISIBLE);
                                            txt_pay_time.setText(takeMoney.getPay_time());
                                        } else if (takeMoney.getIs_pay().equals("3")) {
                                            txt_status.setText("交易成功");
                                            llPayTime.setVisibility(View.VISIBLE);
                                            txt_pay_time.setText(takeMoney.getPay_time());
                                        }
                                        txt_pay_type.setText(PocketHelper.getBankShortName(mContext, bankCard));
                                    } else {

                                    }
                                } catch (Exception ex) {
//                                    ToastUtils.showToast(mContext.this, "");
                                }
                                break;
                            default:

                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
