package com.xptschool.parent.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.view.CustomDialog;

import butterknife.BindView;

public class WebViewActivity extends BaseActivity {

    private String TAG = WebViewActivity.class.getSimpleName();
    protected AgentWeb mAgentWeb;
    @BindView(R.id.container)
    LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agentweb);

        if (getSupportActionBar() != null)
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .setReceivedTitleCallback(mCallback)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
                .setWebLayout(new WebLayout(this))
                .createAgentWeb()//
                .ready()
                .go(getUrl());
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
//            Log.i("Info","progress:"+newProgress);
        }
    };

    public String getUrl() {

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return "";
        }
        String webUrl = bundle.getString(ExtraKey.WEB_URL);
        String title = bundle.getString(ExtraKey.WEB_TITLE);
        if (title != null && !title.isEmpty()) {
            setTitle(title);
        }
        try {
            webUrl += "?user_id=" + GreenDaoHelper.getInstance().getCurrentParent().getU_id();
        } catch (Exception ex) {
            Log.i(TAG, "loadUrl: user_id is null");
        }
        Log.i(TAG, "getUrl: " + webUrl);
        return webUrl;
    }

    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
//            setTitle(title);
        }
    };

    @Override
    public void onBackPressed() {
        CustomDialog dialog = new CustomDialog(WebViewActivity.this);
        dialog.setTitle(R.string.label_tip);
        dialog.setMessage("您确定要关闭该页面吗?");
        dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                WebViewActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Info", "result:" + requestCode + " result:" + resultCode);
        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAgentWeb.destroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

}
