package com.dub.editor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建 WebView
        webView = new WebView(this);
        setContentView(webView);

        // 配置 WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 设置 WebViewClient，在应用内加载所有链接
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 处理 Deep Link
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // 更新当前 Intent
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Uri data = intent.getData();
        android.util.Log.d("MainActivity", "handleIntent called, data: " + data);

        if (data != null) {
            android.util.Log.d("MainActivity", "URI: " + data.toString());

            // 从 dubeditor://open?url=xxx 中提取 url 参数
            String targetUrl = data.getQueryParameter("url");
            android.util.Log.d("MainActivity", "Extracted URL: " + targetUrl);

            if (targetUrl != null && !targetUrl.isEmpty()) {
                android.util.Log.d("MainActivity", "Loading URL: " + targetUrl);
                webView.loadUrl(targetUrl);
            } else {
                android.util.Log.e("MainActivity", "No URL parameter found");
            }
        } else {
            android.util.Log.e("MainActivity", "Intent data is null");
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
