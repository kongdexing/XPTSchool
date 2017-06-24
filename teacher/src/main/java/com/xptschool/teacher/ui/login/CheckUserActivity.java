package com.xptschool.teacher.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.ui.main.BaseActivity;

import butterknife.BindView;

public class CheckUserActivity extends BaseActivity {

    @BindView(R.id.edtUserName)
    EditText edtUserName;
    @BindView(R.id.btnNext)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_username);
        setTitle(R.string.title_forgot_password);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUserName.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(CheckUserActivity.this, R.string.hint_username, Toast.LENGTH_SHORT).show();
                    return;
                }
                checkUserName(username);
            }
        });

    }

    private void checkUserName(String username) {
        Intent intent = new Intent(this, CheckSMSCodeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);

        if (true) {
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.FORGOT_PWD_STEP1,
                new VolleyHttpParamsEntity()
                        .addParam("username", username), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

}
