package com.xptschool.parent.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;

import butterknife.BindView;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.web_error)
    View web_error;
    @BindView(R.id.web_content)
    WebView web_content;
    @BindView(R.id.btn_refresh)
    View btn_refresh;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            web_error.setVisibility(View.VISIBLE);
            return;
        }
        final String webUrl = bundle.getString(ExtraKey.WEB_URL);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl(webUrl);
            }
        });
        loadUrl(webUrl);
    }

    private void loadUrl(String webUrl) {
        if (!webUrl.contains("http://") && !webUrl.contains("https://")) {
            webUrl = "http://" + webUrl;
        }
        web_error.setVisibility(View.GONE);
        web_content.clearCache(true);
        web_content.clearView();
        web_content.setBackgroundColor(Color.WHITE);
        WebSettings webSettings = web_content.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        //判断是否有网

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        // 开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);

        web_content.requestFocus();
        web_content.setWebViewClient(new MyWebClient());

        MyWebChromeClient wn = new MyWebChromeClient();
        web_content.setWebChromeClient(wn);
        web_content.loadUrl(webUrl);
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (progressBar1 == null) {
                return;
            }
            progressBar1.setProgress(newProgress);
            if (newProgress == 100) {
                progressBar1.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (web_content == null) {
            return false;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && web_content.canGoBack()) {
            web_content.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        } else {
            finish();
        }
        return false;
    }

    /***
     * 防止WebView加载内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (web_content != null) {
            web_content.removeAllViews();
            web_content.destroy();
        }
    }

    private class MyWebClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

}
