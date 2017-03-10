package com.xptschool.teacher.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.xptschool.teacher.R;

/**
 * Created by Administrator on 2016/10/28.
 */

public class CustomDialog implements View.OnClickListener {

    private Context mContext;
    private AlertDialog alertDialog;
    private TextView txtTitle;
    private TextView txtMessage;
    private Button btnConfirm, btnCancel;
    private DialogClickListener clickListener;

    public CustomDialog(Context context) {
        mContext = context;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.popup_dialog);
        txtTitle = (TextView) window.findViewById(R.id.title);
        txtMessage = (TextView) window.findViewById(R.id.content);
        btnConfirm = (Button) window.findViewById(R.id.ok);
        btnCancel = (Button) window.findViewById(R.id.cancel);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        txtTitle.setVisibility(View.GONE);
    }

    public void setTitle(int strId) {
        txtTitle.setText(strId);
    }

    public void setTitle(String title) {
        txtTitle.setText(title);
    }

    public void setMessage(int strId) {
        txtMessage.setText(strId);
    }

    public void setMessage(String message) {
        txtMessage.setText(message);
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
                if (clickListener!=null){
                    clickListener.onPositiveClick();
                }
                break;
            case R.id.cancel:
                break;
        }
        dismiss();
    }

    public interface DialogClickListener {
        void onPositiveClick();
    }

}
