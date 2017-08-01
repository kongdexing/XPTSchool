package com.xptschool.parent.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;

/**
 * Created by Administrator on 2016/10/28.
 */

public class CustomEditDialog implements View.OnClickListener {

    private Context mContext;
    private TextView txtTitle;
    private EditText edtMessage;
    private Button btnConfirm, btnCancel;
    private DialogClickListener clickListener;
    private AlertDialog alertDialog;

    public CustomEditDialog(Context context) {
        mContext = context;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        alertDialog.getWindow().setLayout(XPTApplication.getInstance().getWindowWidth() * 4 / 5,
                XPTApplication.getInstance().getWindowHeight() / 3);

        alertDialog.setCanceledOnTouchOutside(false);

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.popup_edit_dialog);

        txtTitle = (TextView) window.findViewById(R.id.title);
        edtMessage = (EditText) window.findViewById(R.id.content);
//        edtMessage.requestFocus();
        btnConfirm = (Button) window.findViewById(R.id.ok);
        btnCancel = (Button) window.findViewById(R.id.cancel);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    public void setTitle(int strId) {
        setTitle(mContext.getString(strId));
    }

    public void setTitle(String title) {
        if (txtTitle != null) {
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(title);
        }
    }

    public void setHintEdit(int strId) {
        edtMessage.setHint(strId);
    }

    public void setHintEdit(String message) {
        edtMessage.setHint(message);
    }

    public void setEdtInputType(int type) {
        edtMessage.setInputType(type);
    }

    public void setAlertDialogClickListener(DialogClickListener listener) {
        clickListener = listener;
    }

    public void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok:
                if (clickListener != null) {
                    clickListener.onPositiveClick(edtMessage.getText().toString());
                }
                break;
            case R.id.cancel:
                break;
        }
        dismiss();
    }

    public interface DialogClickListener {
        void onPositiveClick(String value);
    }

}
