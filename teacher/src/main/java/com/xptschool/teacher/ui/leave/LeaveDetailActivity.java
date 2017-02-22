package com.xptschool.teacher.ui.leave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanLeave;
import com.xptschool.teacher.common.ActivityResultCode;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseActivity;
import com.xptschool.teacher.view.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class LeaveDetailActivity extends BaseActivity {

    @BindView(R.id.txtClassName)
    TextView txtClassName;

    @BindView(R.id.txtStudentName)
    TextView txtStudentName;

    @BindView(R.id.txtTeacher)
    TextView txtTeacher;

    @BindView(R.id.txtType)
    TextView txtType;

    @BindView(R.id.txtStatus)
    TextView txtStatus;

    @BindView(R.id.txtSTime)
    TextView txtSTime;

    @BindView(R.id.txtETime)
    TextView txtETime;

    @BindView(R.id.txtLeave)
    TextView txtLeave;

    @BindView(R.id.edtReply)
    EditText edtReply;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.btnRebut)
    Button btnRebut;
    private BeanLeave currentLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentLeave = bundle.getParcelable(ExtraKey.LEAVE_DETAIL);
        }
        if (currentLeave != null) {
            initData();
        }
    }

    private void initData() {
        txtClassName.setText(GreenDaoHelper.getInstance().getClassNameById(currentLeave.getC_id()));
        txtStudentName.setText(currentLeave.getStu_name());
        txtType.setText(currentLeave.getLeave_name());
        txtStatus.setText(currentLeave.getStatus_name());
        if (currentLeave.getStatus().equals("0")) {
            btnSubmit.setVisibility(View.VISIBLE);
            btnRebut.setVisibility(View.VISIBLE);
        } else {
            btnSubmit.setVisibility(View.GONE);
            btnRebut.setVisibility(View.GONE);
            edtReply.setEnabled(false);
            edtReply.clearFocus();
        }
        txtSTime.setText(currentLeave.getStart_time());
        txtETime.setText(currentLeave.getEnd_time());
        txtLeave.setText(currentLeave.getLeave_memo());
        edtReply.setText(currentLeave.getReply());
    }

    @OnClick({R.id.btnSubmit, R.id.btnRebut})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                currentLeave.setStatus("1");
                currentLeave.setReply(edtReply.getText().toString().trim());
                putLeaveStatus();
                break;
            case R.id.btnRebut:
                CustomDialog dialog = new CustomDialog(this);
                dialog.setTitle("请假条");
                dialog.setMessage("确定要驳回【" + currentLeave.getStu_name() + "】的请假条吗？");
                dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        currentLeave.setStatus("2");
                        currentLeave.setReply(edtReply.getText().toString().trim());
                        putLeaveStatus();
                    }
                });
                break;
        }
    }

    private void putLeaveStatus() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Leave_Edit, new VolleyHttpParamsEntity()
                        .addParam("status", currentLeave.getStatus())
                        .addParam("reply", currentLeave.getReply())
                        .addParam("id", currentLeave.getId())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Leave_Edit)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        showProgress(R.string.progress_loading_cn);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(LeaveDetailActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                            Intent intent = new Intent();
                            intent.putExtra(ExtraKey.LEAVE_DETAIL, currentLeave);
                            setResult(ActivityResultCode.Leave_Edit, intent);
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
    }

}
