package com.xptschool.parent.ui.cardset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.ui.cardset.CardSetActivity;

/**
 * Created by dexing on 2017/1/10.
 * No1
 */

public class CardSOSView extends LinearLayout implements View.OnClickListener {

    private TextView txtSOS1;
    private EditText edtSOS1;
    private ImageView imgDel;
    private ImageView imgSOS1;
    private Context mContext;
    private CardSetActivity.ContractClickListener clickListener;

    public CardSOSView(Context context) {
        this(context, null);
    }

    public CardSOSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.content_card_sos, this, true);
        txtSOS1 = (TextView) view.findViewById(R.id.txtSOS1);
        edtSOS1 = (EditText) view.findViewById(R.id.edtSOS1);
        imgDel = (ImageView) view.findViewById(R.id.imgDel);
        imgSOS1 = (ImageView) view.findViewById(R.id.imgSOS1);
        imgDel.setOnClickListener(this);
        imgSOS1.setOnClickListener(this);
    }

    public void bindData(int index, String phoneNum, CardSetActivity.ContractClickListener listener) {
        txtSOS1.setText("SOS号码" + index);
        edtSOS1.setText(phoneNum);
        edtSOS1.setSelection(edtSOS1.getText().toString().length());
        clickListener = listener;
    }

    public String getSOSPhone() {
        String phone = edtSOS1.getText().toString().trim().replace("-", "");
        if (!phone.isEmpty() && !CommonUtil.isPhone(phone)) {
            edtSOS1.setError(mContext.getString(R.string.input_error_phone));
            return null;
        }
        return phone;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgDel:
                edtSOS1.setText("");
                break;
            case R.id.imgSOS1:
                if (clickListener != null) {
                    clickListener.onContractClick();
                    mContext.registerReceiver(SosContractsReceiver, new IntentFilter(BroadcastAction.SOS_CONTACTS));
                }
                break;
        }
    }

    BroadcastReceiver SosContractsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.SOS_CONTACTS)) {
                try {
                    String phone = intent.getStringExtra("phone");
                    edtSOS1.setText(phone.replace("-", "").replace(" ", ""));
                    edtSOS1.setSelection(edtSOS1.getText().length());
                    mContext.unregisterReceiver(this);
                } catch (Exception ex) {
                    Toast.makeText(context, R.string.toast_get_phone_null, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
