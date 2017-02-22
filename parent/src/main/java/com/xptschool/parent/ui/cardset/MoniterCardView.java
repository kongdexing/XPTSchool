package com.xptschool.parent.ui.cardset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.util.CardWhiteListClickListener;
import com.xptschool.parent.view.CustomDialog;

/**
 * Created by dexing on 2017/1/10.
 * No1
 */

public class MoniterCardView extends LinearLayout implements View.OnClickListener {

    private TextView txtPhoneName1;
    private EditText edtPhoneName;
    private EditText edtPhone;
    private ImageView imgDel1;
    private ImageView imgDel2;
    private ImageView imgContract;
    private Button btnOk, btnCall;
    private BeanStudent currentStudent;
    private Context mContext;
    private CardWhiteListClickListener clickListener;

    public MoniterCardView(Context context) {
        this(context, null);
    }

    public MoniterCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.content_card_moniter1, this, true);
        txtPhoneName1 = (TextView) view.findViewById(R.id.txtPhoneName1);
        edtPhoneName = (EditText) view.findViewById(R.id.edtPhoneName);
        edtPhone = (EditText) view.findViewById(R.id.edtPhone);

        imgDel1 = (ImageView) view.findViewById(R.id.imgDel1);
        imgDel2 = (ImageView) view.findViewById(R.id.imgDel2);
        imgContract = (ImageView) view.findViewById(R.id.imgContract);
        btnOk = (Button) view.findViewById(R.id.btnOk);
        btnCall = (Button) view.findViewById(R.id.btnCall);

        imgDel1.setOnClickListener(this);
        imgDel2.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        imgContract.setOnClickListener(this);
    }

    public void bindData(BeanStudent student, CardWhiteListClickListener listener) {
        if (student == null) {
            return;
        }
        currentStudent = student;
        try {
            edtPhoneName.setHint("联系人");
            if (currentStudent.getMonitor().contains(",")) {
                String[] values = currentStudent.getMonitor().split(",");
                edtPhoneName.setText(values[0]);
                edtPhone.setText(values[1]);
                edtPhone.requestFocus();
                edtPhone.setSelection(edtPhone.getText().toString().length());
            }
        } catch (Exception ex) {

        }
        clickListener = listener;
    }

    public String getInputValue() {
        String name = edtPhoneName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty() && phone.isEmpty()) {
            return "";
        }

        if (name.isEmpty() && !phone.isEmpty()
                || !name.isEmpty() && phone.isEmpty()) {
            //其中一个为空
            Toast.makeText(mContext, R.string.toast_contract_other_empty, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (!CommonUtil.isPhone(phone)) {
            edtPhone.setError(mContext.getString(R.string.input_error_phone));
            return null;
        }

        return name + ":" + phone;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgDel1:
                edtPhoneName.setText("");
                break;
            case R.id.imgDel2:
                edtPhone.setText("");
                break;
            case R.id.imgContract:
                if (clickListener != null) {
                    clickListener.onContractChooseClick();
                    mContext.registerReceiver(WhiteListContractsReceiver, new IntentFilter(BroadcastAction.WHITELIST_CONTACTS));
                }
                break;
            case R.id.btnOk:
                String phone = edtPhone.getText().toString().trim();
                if (clickListener != null) {
                    clickListener.onBtnOkClick(edtPhoneName.getText().toString().trim() + "," + phone);
                }
                break;
            case R.id.btnCall:
                CustomDialog dialog = new CustomDialog(mContext);
                dialog.setTitle(R.string.label_cardset);
                dialog.setMessage(R.string.tip_moniter_call_help);
                dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //call phone
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + currentStudent.getStu_phone()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } catch (Exception ex) {
                            Toast.makeText(mContext, R.string.toast_startcall_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    BroadcastReceiver WhiteListContractsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.WHITELIST_CONTACTS)) {
                try {
                    String phone = intent.getStringExtra("phone");
                    String name = intent.getStringExtra("name");
                    edtPhoneName.setText(name.replace(" ", ""));
                    edtPhone.setText(phone.replace("-", "").replace(" ", ""));
                    edtPhone.requestFocus();
                    edtPhone.setSelection(edtPhone.getText().length());
                    mContext.unregisterReceiver(this);
                } catch (Exception ex) {
                    Toast.makeText(context, R.string.toast_get_phone_null, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
