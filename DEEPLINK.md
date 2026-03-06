# Deep Link 配置指南

## 什么是 Deep Link

Deep Link 允许从 Web 页面或其他 App 直接打开你的 App，并传递参数。

## URL Scheme 格式

```
dubeditor://open?url=https://your-domain.com/editor?id=123&token=abc
```

## Android 配置

### 1. 配置 AndroidManifest.xml

编辑 `android/app/src/main/AndroidManifest.xml`，在 `<activity>` 标签内添加：

```xml
<activity
    android:name=".MainActivity"
    ...>

    <!-- 现有的 intent-filter -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>

    <!-- 添加 Deep Link 支持 -->
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!-- URL Scheme: dubeditor:// -->
        <data android:scheme="dubeditor" />
    </intent-filter>

    <!-- 可选：支持 HTTPS Universal Links -->
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data android:scheme="https" />
        <data android:host="your-domain.com" />
        <data android:pathPrefix="/app" />
    </intent-filter>

</activity>
```

### 2. 同步配置

```bash
npm run sync:android
```

## iOS 配置（需要 Mac）

### 1. 配置 Info.plist

编辑 `ios/App/App/Info.plist`，添加：

```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLName</key>
        <string>com.dub.editor</string>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>dubeditor</string>
        </array>
    </dict>
</array>
```

### 2. 配置 Universal Links（可选）

在 Xcode 中：
1. 选择项目 → Signing & Capabilities
2. 点击 + Capability → Associated Domains
3. 添加：`applinks:your-domain.com`

在服务器上配置 `https://your-domain.com/.well-known/apple-app-site-association`：

```json
{
  "applinks": {
    "apps": [],
    "details": [
      {
        "appID": "TEAM_ID.com.dub.editor",
        "paths": ["/app/*"]
      }
    ]
  }
}
```

### 3. 同步配置

```bash
npm run sync:ios
```

## Web 端调用示例

### 基础用法

```javascript
// 构建 Deep Link URL
const editorUrl = 'https://your-domain.com/editor?id=123&token=abc';
const deepLink = `dubeditor://open?url=${encodeURIComponent(editorUrl)}`;

// 尝试打开 App
window.location.href = deepLink;

// 2 秒后如果还在页面，说明没安装 App，跳转到下载页
setTimeout(() => {
  if (document.hidden === false) {
    window.location.href = 'https://your-domain.com/download';
  }
}, 2000);
```

### 优雅降级方案

```javascript
function openEditor(editorUrl) {
  const deepLink = `dubeditor://open?url=${encodeURIComponent(editorUrl)}`;
  const downloadUrl = 'https://your-domain.com/download';

  // 创建隐藏的 iframe 尝试打开 App
  const iframe = document.createElement('iframe');
  iframe.style.display = 'none';
  iframe.src = deepLink;
  document.body.appendChild(iframe);

  // 检测是否成功打开
  const start = Date.now();

  setTimeout(() => {
    const elapsed = Date.now() - start;

    // 如果时间差小于 2100ms，说明没有离开页面（App 未安装）
    if (elapsed < 2100) {
      // 询问用户是否下载
      if (confirm('检测到您未安装 Dub Editor App，是否前往下载？')) {
        window.location.href = downloadUrl;
      }
    }

    // 清理 iframe
    document.body.removeChild(iframe);
  }, 2000);
}

// 使用
openEditor('https://your-domain.com/editor?id=123');
```

### React 组件示例

```jsx
import { useState } from 'react';

function EditorButton({ editorUrl }) {
  const [showDownload, setShowDownload] = useState(false);

  const openEditor = () => {
    const deepLink = `dubeditor://open?url=${encodeURIComponent(editorUrl)}`;

    window.location.href = deepLink;

    setTimeout(() => {
      setShowDownload(true);
    }, 2000);
  };

  return (
    <div>
      <button onClick={openEditor}>
        在 App 中打开编辑器
      </button>

      {showDownload && (
        <div className="download-tip">
          <p>未检测到 App，<a href="/download">点击下载</a></p>
        </div>
      )}
    </div>
  );
}
```

## 测试 Deep Link

### Android 测试

使用 ADB 命令测试：

```bash
# 测试 URL Scheme
adb shell am start -W -a android.intent.action.VIEW -d "dubeditor://open?url=https://example.com/editor"

# 测试 Universal Link
adb shell am start -W -a android.intent.action.VIEW -d "https://your-domain.com/app/editor?id=123"
```

### iOS 测试

在 Safari 中输入：

```
dubeditor://open?url=https://example.com/editor
```

或使用 Xcode 的 URL Scheme 测试工具。

## 支持的参数

| 参数 | 说明 | 示例 |
|------|------|------|
| `url` | 编辑器完整 URL | `dubeditor://open?url=https://...` |
| `editor` | 编辑器路径（相对） | `dubeditor://open?editor=/edit/123` |
| `path` | 同 editor | `dubeditor://open?path=/edit/123` |

## 常见问题

### Q: Deep Link 不工作？

1. 检查 AndroidManifest.xml / Info.plist 配置是否正确
2. 重新安装 App（配置修改后需要重新安装）
3. 检查 URL Scheme 拼写是否正确
4. 查看 Logcat / Xcode Console 的错误日志

### Q: 如何调试 Deep Link？

**Android**:
```bash
adb logcat | grep -i "intent"
```

**iOS**:
在 Xcode 中查看 Console 输出

### Q: Universal Links vs URL Scheme 的区别？

| | URL Scheme | Universal Links |
|---|---|---|
| 格式 | `dubeditor://` | `https://your-domain.com/app` |
| 未安装 App | 显示错误 | 在浏览器中打开 |
| 配置复杂度 | 简单 | 需要服务器配置 |
| 用户体验 | 一般 | 更好 |

推荐：同时支持两种方式，优先使用 Universal Links。

## 安全建议

1. **验证 URL**: 在 App 中验证传入的 URL 是否来自可信域名
2. **HTTPS Only**: 生产环境只接受 HTTPS 的编辑器 URL
3. **参数校验**: 验证必要的参数（如 token）是否存在

```javascript
// 在 index.html 中添加 URL 验证
function isValidEditorUrl(url) {
  try {
    const parsed = new URL(url);
    // 只允许特定域名
    const allowedHosts = ['your-domain.com', 'editor.your-domain.com'];
    return parsed.protocol === 'https:' && allowedHosts.includes(parsed.host);
  } catch {
    return false;
  }
}

function getEditorUrl() {
  const params = new URLSearchParams(window.location.search);
  const url = params.get('url');

  if (url && isValidEditorUrl(url)) {
    return decodeURIComponent(url);
  }

  return DEFAULT_EDITOR_URL;
}
```
