# Dub Editor Mobile App

基于 Capacitor 的移动端 WebView 容器，支持通过 Deep Link 从浏览器跳转到 App 并加载指定网址。

## ✨ 功能特性

- ✅ Android 和 iOS 双平台支持
- ✅ Deep Link 支持（从浏览器跳转到 App）
- ✅ WebView 容器加载任意网址
- ✅ 启动屏幕和加载动画
- ✅ 网络状态检测
- ✅ 错误处理和重试机制
- ✅ GitHub Actions 自动构建 APK

## 使用方式

### Web 端调用

从你的 Web 页面通过 Deep Link 打开 App：

```javascript
// 构建 Deep Link
const editorUrl = 'https://your-domain.com/editor?id=123&token=abc';
const deepLink = `dubeditor://open?url=${encodeURIComponent(editorUrl)}`;

// 打开 App
window.location.href = deepLink;

// 2 秒后如果还在页面，说明没安装 App
setTimeout(() => {
  if (confirm('未安装 App，是否下载？')) {
    window.location.href = 'https://your-domain.com/download';
  }
}, 2000);
```

### 测试示例

打开 `web-example.html` 查看完整的调用示例和代码。

## 📚 文档导航

- **[快速开始](./QUICKSTART.md)** - 5 分钟上手指南
- **[Deep Link 配置](./DEEPLINK.md)** - URL Scheme 和 Universal Links
- **[图标和启动画面](./ASSETS.md)** - 自定义 App 外观

## 项目结构

```
mobile-app/
├── android/              # Android 原生项目（已生成）
├── ios/                  # iOS 原生项目（需要 Mac 生成）
├── www/                  # Web 资源目录
│   └── index.html       # 启动页面（加载编辑器）
├── capacitor.config.ts  # Capacitor 配置
├── package.json
├── README.md            # 本文件
├── QUICKSTART.md        # 快速开始指南
├── DEEPLINK.md          # Deep Link 配置
└── ASSETS.md            # 图标和启动画面配置
```

## 快速开始

### 1. 构建 Android APK

```bash
cd mobile-app
npm install
npm run android
```

在 Android Studio 中：
1. 等待 Gradle 同步完成
2. Build → Build Bundle(s) / APK(s) → Build APK(s)
3. APK 位于：`android/app/build/outputs/apk/debug/`

### 2. 分发 APK

将 APK 上传到服务器，生成下载链接供用户下载安装。

### 3. Web 端集成

在你的 Web 页面中添加"在 App 中打开"按钮：

```html
<button onclick="openInApp()">在 App 中打开编辑器</button>

<script>
function openInApp() {
  // 当前编辑器的完整 URL
  const editorUrl = window.location.href;

  // 构建 Deep Link
  const deepLink = `dubeditor://open?url=${encodeURIComponent(editorUrl)}`;

  // 打开 App
  window.location.href = deepLink;

  // 未安装提示
  setTimeout(() => {
    if (confirm('未检测到 App，是否下载？')) {
      window.location.href = '/download/app.apk';
    }
  }, 2000);
}
</script>
```

详细示例请查看 `web-example.html`。

## 核心优势

### 解决的问题

根据 `docs/编辑器全屏方案分析及选型.md` 第八节，原生壳子方案能从根本上解决以下所有 Web 平台限制：

✅ iPad Safari 下滑退出手势冲突
✅ iPad Safari 左上角关闭按钮干扰
✅ `requestFullscreen` 的用户手势要求
✅ iPad Safari 重入全屏导致页面触控失效
✅ 笔迹书写、拖拽、滚动等手指操作与浏览器手势冲突
✅ 多次 `requestFullscreen` 切换目标的兼容性问题

### 与 Web 方案对比

| 特性 | 原生壳子 | Web 全屏 |
|------|---------|---------|
| 隐藏浏览器 UI | 完全隐藏 | 需要 requestFullscreen |
| iPad 手势冲突 | 无冲突 | 有冲突 |
| 用户手势要求 | 无要求 | 必须用户点击 |
| 安装门槛 | 需安装 App | 无需安装 |
| 更新方式 | 远程更新（无需重装） | 即时更新 |

## 开发模式

如果需要在开发时测试 Deep Link，可以使用 ADB 命令：

```bash
# 测试打开编辑器
adb shell am start -W -a android.intent.action.VIEW -d "dubeditor://open?url=https://example.com/editor?id=123"
```

## 生产构建

### 构建 Release APK

1. 生成签名密钥：
```bash
keytool -genkey -v -keystore dub-editor.keystore -alias dub-editor -keyalg RSA -keysize 2048 -validity 10000
```

2. 在 Android Studio 中：
   - Build → Generate Signed Bundle / APK
   - 选择 APK，配置签名
   - 生成 Release APK

详细步骤请查看 [快速开始指南](./QUICKSTART.md)。

## URL Scheme 跳转

支持从 Web 页面跳转到 App：

```javascript
// Web 端代码
const editorUrl = 'https://your-domain.com/editor?id=123&token=abc';
const deepLink = `dubeditor://open?url=${encodeURIComponent(editorUrl)}`;

window.location.href = deepLink;

// 2 秒后如果还在页面，跳转到下载页
setTimeout(() => {
  window.location.href = 'https://your-domain.com/download';
}, 2000);
```

详细配置请查看 [Deep Link 配置指南](./DEEPLINK.md)。

## 分发方式

### Android

- **APK 直装**: 将 APK 放到网站供下载，零审核，随时更新
- **应用市场**: Google Play、华为、小米等应用商店

### iOS

- **TestFlight**: 适合内部测试，支持 1 万用户，90 天有效期
- **App Store**: 正式发布，需要审核（3-7 天）
- **企业证书**: 仅限企业内部分发（299 美元/年）

## 常用命令

```bash
# 同步所有平台
npm run sync

# 同步单个平台
npm run sync:android
npm run sync:ios

# 打开原生 IDE
npm run android
npm run ios

# 添加平台
npm run add:android
npm run add:ios

# 复制 web 资源
npm run copy

# 更新依赖
npm run update
```

## 费用说明

### Android

- **开发**: 免费
- **分发**: APK 直装免费，Google Play 需 25 美元（一次性）

### iOS

- **开发**: 需要 Mac 电脑
- **Apple Developer Program**: 99 美元/年（必需）
- **分发**: TestFlight 和 App Store 包含在开发者账号中

## 注意事项

1. **iOS 开发必须使用 Mac**，Windows 无法构建 iOS 应用
2. **网络权限**: 确保 App 有网络访问权限（加载远程编辑器）
3. **HTTPS**: iOS 要求加载的网页必须使用 HTTPS（开发模式除外）
4. **Android 无需任何费用**，可直接分发 APK
5. **首次安装**: Android 需要允许"未知来源"安装

## 故障排查

### Android Studio Gradle 同步失败

配置国内镜像，编辑 `android/build.gradle`：

```gradle
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        google()
        mavenCentral()
    }
}
```

### App 白屏

1. 检查 `www/index.html` 中的编辑器 URL 是否正确
2. 打开 Android Studio Logcat 查看错误
3. 确保网络连接正常
4. 检查是否有 CORS 跨域问题

### 无法连接到开发服务器

1. 电脑和手机在同一局域网
2. 防火墙允许 5173 端口
3. `capacitor.config.ts` 中的 IP 地址正确
4. 开发服务器正在运行

## 推荐策略

**最务实的落地方案**：

1. **Android Pad**: 打包 APK 放到网站上供下载，零审核，随时更新
2. **iPad**: 用 TestFlight 分发，支持 1 万用户，审核宽松
3. **Web 端作为降级**: 未安装 App 的用户仍然可以用浏览器访问

形成**三层降级策略**：壳子 App（最优体验）→ 浏览器全屏（可用体验）→ 非全屏（基础体验）。

## 相关文档

- [Capacitor 官方文档](https://capacitorjs.com/docs)
- [Android 开发指南](https://capacitorjs.com/docs/android)
- [iOS 开发指南](https://capacitorjs.com/docs/ios)
- [编辑器全屏方案分析](../docs/编辑器全屏方案分析及选型.md)

## License

ISC
