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

        // 初始化 GeckoRuntime（全局单例）
        if (sRuntime == null) {
            sRuntime = GeckoRuntime.create(this);
        }

        // 创建 GeckoSession
        geckoSession = new GeckoSession();
        geckoSession.open(sRuntime);

        // 将 session 绑定到 view
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
        Log.d(TAG, "Intent action: " + intent.getAction());
        Log.d(TAG, "Intent data: " + intent.getData());
        Log.d(TAG, "Intent dataString: " + intent.getDataString());

        // 打印所有 extras
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Log.d(TAG, "Extra: " + key + " = " + intent.getExtras().get(key));
            }
        }

        Uri data = intent.getData();

        if (data != null) {
            Log.d(TAG, "URI scheme: " + data.getScheme());
            Log.d(TAG, "URI host: " + data.getHost());
            Log.d(TAG, "URI path: " + data.getPath());
            Log.d(TAG, "URI query: " + data.getQuery());
            Log.d(TAG, "URI fragment: " + data.getFragment());

            // 从 dubeditor://open?url=xxx 中提取 url 参数
            String targetUrl = data.getQueryParameter("url");

            // 如果 URI 有 fragment，将其附加到目标 URL
            String fragment = data.getFragment();
            if (fragment != null && !fragment.isEmpty()) {
                if (targetUrl != null) {
                    targetUrl = targetUrl + "#" + fragment;
                }
            }

            Log.d(TAG, "Extracted URL: " + targetUrl);

            if (targetUrl != null && !targetUrl.isEmpty()) {
                Log.d(TAG, "Loading URL: " + targetUrl);
                geckoSession.loadUri(targetUrl);
            } else {
                Log.e(TAG, "No URL parameter found");
            }
        } else {
            Log.e(TAG, "Intent data is null");
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
