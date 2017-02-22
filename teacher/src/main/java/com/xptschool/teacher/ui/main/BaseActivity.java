package com.xptschool.teacher.ui.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.common.ExtraKey;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class BaseActivity extends AppCompatActivity {

    public String TAG = "";
    //应用是否销毁标志
    protected boolean isDestroy;
    public LinearLayout llContent;  //主视图
    private RelativeLayout llActionBar;  //ActionBar
    private ImageView imgBack;
    private TextView txtTitle, txtRight;
    private Button btnRight;
    private Dialog progressDialog;
    public Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        isDestroy = false;
        PushAgent.getInstance(this).onAppStart();

        IntentFilter filter = new IntentFilter(BroadcastAction.RELOGIN);
        this.registerReceiver(ReLoginReceiver, filter);
        Log.i(TAG, "onCreate: " + this.getClass().getSimpleName());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null, false);
        View parentView = LayoutInflater.from(this).inflate(R.layout.activity_base, null, true);

        llActionBar = (RelativeLayout) parentView.findViewById(R.id.includeActionBar);
        imgBack = (ImageView) parentView.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtTitle = (TextView) parentView.findViewById(R.id.txtTitle);
        txtRight = (TextView) parentView.findViewById(R.id.txtRight);
        btnRight = (Button) parentView.findViewById(R.id.btnRight);
        llContent = (LinearLayout) parentView.findViewById(R.id.llContent);

        btnRight.setVisibility(View.GONE);

        txtTitle.setText(this.getTitle());
        llContent.addView(contentView);
        setContentView(parentView);
        unbinder = ButterKnife.bind(this);
    }

    public void setContentView(View view) {
        super.setContentView(view);
    }

    /**
     * 设置ActionBar名称
     *
     * @param strId
     */
    public void setTitle(int strId) {
        setTitle(getResources().getString(strId));
    }

    /**
     * 设置ActionBar名称
     *
     * @param str
     */
    public void setTitle(String str) {
        if (txtTitle != null) {
            txtTitle.setText(str);
        }
    }

    public void setTxtRight(int strId) {
        setTxtRight(getResources().getString(strId));
    }

    public void setTxtRight(String str) {
        if (txtRight != null) {
            txtRight.setText(str);
        }
    }

    public void setTextRightClickListener(View.OnClickListener listener) {
        if (txtRight != null)
            txtRight.setOnClickListener(listener);
    }

    public void setBtnRight(int strId) {
        setBtnRight(getResources().getString(strId));
    }

    public void setBtnRight(String str) {
        if (btnRight != null) {
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setText(str);
        }
    }

    public void setBtnRightClickListener(View.OnClickListener listener) {
        if (btnRight != null)
            btnRight.setOnClickListener(listener);
    }


    public void showActionBar(boolean show) {
        if (llActionBar != null) {
            llActionBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void showImgBack(boolean show) {
        if (imgBack != null) {
            imgBack.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    public void showProgress(int strId) {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, R.style.CustomDialog);
            progressDialog.setContentView(R.layout.layout_dialog);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        TextView msg = (TextView) progressDialog.findViewById(R.id.tv_load_dialog);
        msg.setText(strId);
        try {
            progressDialog.show();
        } catch (Exception ex) {
            Log.e(TAG, "showProgress: " + ex.getMessage());
        }
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.i(TAG, "onResume: " + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        Log.i(TAG, "onPause: " + this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: " + this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: " + this.getClass().getSimpleName());
        if (unbinder != null) {
            unbinder.unbind();
        }
        isDestroy = true;
        try {
            this.unregisterReceiver(ReLoginReceiver);
        } catch (Exception ex) {
            Log.i(TAG, "onDestroy: " + ex.getMessage());
        }
        super.onDestroy();
    }

    BroadcastReceiver ReLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.RELOGIN)) {
                intent = new Intent(BaseActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                startActivity(intent);
            }
        }
    };

}
