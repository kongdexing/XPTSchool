package com.xptschool.parent.ui.wallet.bankcard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xptschool.parent.R;

public class BankBtmPopView extends LinearLayout implements View.OnClickListener {

    private String TAG = BankBtmPopView.class.getSimpleName();
    private LinearLayout llDel;
    private LinearLayout llCancel;
    private BankCrdPopClickListener clickListener;

    public BankBtmPopView(Context context) {
        this(context, null);
    }

    public BankBtmPopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_bankcard_btmpop, this, true);
        try {
            llDel = (LinearLayout) view.findViewById(R.id.llDel);
            llCancel = (LinearLayout) view.findViewById(R.id.llCancel);
            llDel.setOnClickListener(this);
            llCancel.setOnClickListener(this);
        } catch (Exception ex) {
            Log.i(TAG, "AlbumSourceView: " + ex.getMessage());
        }
    }

    public void setBankCardPopClickListener(BankCrdPopClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llDel:
                if (clickListener != null) {
                    clickListener.onCardDeleteClick();
                }
                break;
            case R.id.llCancel:
                if (clickListener != null) {
                    clickListener.onBack();
                }
                break;
        }
    }

    public interface BankCrdPopClickListener {

        void onCardDeleteClick();

        void onBack();
    }

}
