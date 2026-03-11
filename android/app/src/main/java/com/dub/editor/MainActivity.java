package com.dub.editor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.view.View;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建 WebView
        webView = new WebView(this);
        setContentView(webView);

        // 配置 WebView - 启用所有现代特性
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 启用现代 Web 特性
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // 性能优化
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // 启用硬件加速
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // 设置 WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                android.util.Log.d("MainActivity", "shouldOverrideUrlLoading: " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                android.util.Log.d("MainActivity", "onPageStarted: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                android.util.Log.d("MainActivity", "onPageFinished: " + url);
            }
        });

        // 设置 WebChromeClient 支持更多特性
        webView.setWebChromeClient(new WebChromeClient());

        // 处理 Deep Link
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        android.util.Log.d("MainActivity", "=== handleIntent called ===");
        android.util.Log.d("MainActivity", "Intent action: " + intent.getAction());
        android.util.Log.d("MainActivity", "Intent data: " + intent.getData());

        Uri data = intent.getData();

        if (data != null) {
            android.util.Log.d("MainActivity", "URI scheme: " + data.getScheme());
            android.util.Log.d("MainActivity", "URI host: " + data.getHost());
            android.util.Log.d("MainActivity", "URI path: " + data.getPath());
            android.util.Log.d("MainActivity", "URI query: " + data.getQuery());
            android.util.Log.d("MainActivity", "URI fragment: " + data.getFragment());

            // 从 dubeditor://open?url=xxx 中提取 url 参数
            String targetUrl = data.getQueryParameter("url");

            // 如果 URI 有 fragment，将其附加到目标 URL
            String fragment = data.getFragment();
            if (fragment != null && !fragment.isEmpty()) {
                if (targetUrl != null) {
                    targetUrl = targetUrl + "#" + fragment;
                }
            }

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
