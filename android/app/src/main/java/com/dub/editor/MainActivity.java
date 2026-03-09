package com.dub.editor;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Bridge;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 配置 Bridge，允许所有导航
        Bridge bridge = getBridge();
        if (bridge != null) {
            bridge.getConfig().setAllowNavigation(new String[]{"*"});
        }
    }
}
