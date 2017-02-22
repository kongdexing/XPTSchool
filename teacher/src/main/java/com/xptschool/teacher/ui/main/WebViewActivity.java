package com.xptschool.teacher.ui.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.xptschool.teacher.R;
import com.xptschool.teacher.common.ExtraKey;

import butterknife.BindView;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.web_error)
    View web_error;
    @BindView(R.id.rl_progress)
    RelativeLayout rl_progress;
    @BindView(R.id.web_content)
    WebView web_content;
    @BindView(R.id.btn_refresh)
    View btn_refresh;

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
        web_content.requestFocus();
        MyWebClient wn = new MyWebClient();
        web_content.setWebViewClient(wn);
        web_content.loadUrl(webUrl);
    }

    private class MyWebClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (rl_progress != null) {
                rl_progress.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (rl_progress != null) {
                rl_progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (rl_progress != null) {
                rl_progress.setVisibility(View.GONE);
            }
            if (web_error != null) {
                web_error.setVisibility(View.VISIBLE);
            }
        }
    }

}
