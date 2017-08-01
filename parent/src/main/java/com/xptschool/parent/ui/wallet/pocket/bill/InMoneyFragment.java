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
import com.xptschool.parent.ui.wallet.pocket.BeanPocketRecord;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/21.
 * No1
 */

public class InMoneyFragment extends BaseFragment {

    @BindView(R.id.txt_money)
    TextView txt_money;

    @BindView(R.id.txt_status)
    TextView txt_status;

    @BindView(R.id.txt_pay_type)
    TextView txt_pay_type;

    @BindView(R.id.txt_pay_info)
    TextView txt_pay_info;

    @BindView(R.id.txt_pay_time)
    TextView txt_pay_time;

    @BindView(R.id.txt_pay_tn)
    TextView txt_pay_tn;

    @BindView(R.id.lltnOrder)
    LinearLayout lltnOrder;

    private BeanPocketRecord pocketRecord;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_money_in, container, false);
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
                                    Gson gson = new Gson();
                                    BeanOrder beanOrder = gson.fromJson(volleyHttpResult.getData().toString(), BeanOrder.class);

                                    txt_money.setText(pocketRecord.getMoney());
                                    txt_status.setText(beanOrder.getOrder_status());
                                    txt_pay_type.setText(beanOrder.getPayment_id());
                                    txt_pay_info.setText(beanOrder.getMemo());
                                    txt_pay_time.setText(beanOrder.getCreate_time());
                                    txt_pay_tn.setText(beanOrder.getNotice_sn());
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
