package com.xptschool.teacher.ui.chat.video;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.ui.main.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class InCallActivity extends BaseActivity {

    @BindView(R.id.view_call_trying_textView_name)
    TextView view_call_trying_textView_name;
    @BindView(R.id.view_call_trying_imageView_avatar)
    ImageView view_call_trying_imageView_avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_call);
        showActionBar(false);
    }


    @OnClick({R.id.view_call_trying_imageButton_hang, R.id.view_call_trying_imageButton_accept})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.view_call_trying_imageButton_hang:

                break;
            case R.id.view_call_trying_imageButton_accept:

                break;
        }
    }

}
