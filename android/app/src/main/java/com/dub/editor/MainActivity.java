package com.dub.editor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private GeckoView geckoView;
    private GeckoSession geckoSession;
    private static GeckoRuntime sRuntime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建 GeckoView
        geckoView = new GeckoView(this);
        setContentView(geckoView);

        // 初始化 GeckoRuntime
        if (sRuntime == null) {
            sRuntime = GeckoRuntime.create(this);
        }

        // 创建 GeckoSession
        geckoSession = new GeckoSession();
        geckoSession.open(sRuntime);
        geckoView.setSession(geckoSession);

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
        Log.d(TAG, "=== handleIntent called ===");
        Uri data = intent.getData();

        if (data != null) {
            String targetUrl = data.getQueryParameter("url");
            String fragment = data.getFragment();

            if (fragment != null && !fragment.isEmpty() && targetUrl != null) {
                targetUrl = targetUrl + "#" + fragment;
            }

            Log.d(TAG, "Loading URL: " + targetUrl);
            if (targetUrl != null && !targetUrl.isEmpty()) {
                geckoSession.loadUri(targetUrl);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (geckoSession != null && geckoSession.canGoBack()) {
            geckoSession.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geckoSession != null) {
            geckoSession.close();
        }
    }
}
