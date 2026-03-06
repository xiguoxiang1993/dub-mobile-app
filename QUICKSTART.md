# 快速开始指南

## 前置要求

### 所有平台
- Node.js 16+
- npm 或 yarn

### Android 开发
- Android Studio（最新版本）
- Android SDK（API 24+）
- JDK 11+

### iOS 开发（可选）
- macOS 系统
- Xcode 14+
- CocoaPods
- Apple Developer 账号（用于真机测试和发布）

## 5 分钟快速上手

### 1. 配置编辑器地址

编辑 `www/index.html`，找到这一行：

```javascript
const DEFAULT_EDITOR_URL = 'https://your-editor-domain.com/editor';
```

替换为你的实际编辑器地址，例如：

```javascript
const DEFAULT_EDITOR_URL = 'https://editor.example.com/';
```

### 2. 安装依赖

```bash
cd mobile-app
npm install
```

### 3. 添加 Android 平台（已完成）

```bash
npm run add:android
```

### 4. 打开 Android Studio

```bash
npm run android
```

### 5. 运行到设备

在 Android Studio 中：
1. 连接 Android 设备（开启 USB 调试）或启动模拟器
2. 等待 Gradle 同步完成
3. 点击绿色的 Run 按钮 ▶️

完成！App 会安装到设备上并自动启动。

## 开发模式（热更新）

开发时可以让 App 加载本地开发服务器，实现实时预览：

### 1. 启动编辑器开发服务器

```bash
cd ../packages/playground
pnpm run dev
```

记下服务器地址，例如：`http://localhost:5173`

### 2. 获取本机 IP 地址

**Windows**:
```bash
ipconfig
```

**macOS/Linux**:
```bash
ifconfig
```

找到你的局域网 IP，例如：`192.168.1.100`

### 3. 配置 Capacitor

编辑 `capacitor.config.ts`：

```typescript
server: {
  url: 'http://192.168.1.100:5173',  // 替换为你的 IP
  cleartext: true
}
```

### 4. 同步并运行

```bash
npm run sync:android
npm run android
```

现在修改编辑器代码，App 会自动刷新！

## 生产构建

### 方案 1: 加载远程编辑器（推荐）

当前配置已经是这种方式，优点：
- App 体积小（约 10MB）
- 编辑器更新无需重新发布 App
- 需要网络连接

只需确保 `www/index.html` 中的 `DEFAULT_EDITOR_URL` 指向生产环境地址。

### 方案 2: 打包编辑器到 App

如果需要离线使用：

```bash
# 1. 构建编辑器
cd ../packages/playground
pnpm run build

# 2. 复制到 mobile-app
cp -r dist/* ../mobile-app/www/

# 3. 修改 index.html，直接加载本地文件
# 将 iframe.src = editorUrl 改为 iframe.src = './index.html'

# 4. 同步到 Android
cd ../mobile-app
npm run sync:android
```

## 构建 APK

### Debug APK（测试用）

在 Android Studio 中：
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. 等待构建完成
3. APK 位于：`android/app/build/outputs/apk/debug/app-debug.apk`

### Release APK（发布用）

#### 1. 生成签名密钥

```bash
keytool -genkey -v -keystore dub-editor.keystore -alias dub-editor -keyalg RSA -keysize 2048 -validity 10000
```

按提示输入密码和信息，生成 `dub-editor.keystore` 文件。

#### 2. 配置签名

创建 `android/key.properties`：

```properties
storePassword=你的密钥库密码
keyPassword=你的密钥密码
keyAlias=dub-editor
storeFile=../dub-editor.keystore
```

编辑 `android/app/build.gradle`，在 `android` 块中添加：

```gradle
signingConfigs {
    release {
        def keystorePropertiesFile = rootProject.file("key.properties")
        def keystoreProperties = new Properties()
        keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

        keyAlias keystoreProperties['keyAlias']
        keyPassword keystoreProperties['keyPassword']
        storeFile file(keystoreProperties['storeFile'])
        storePassword keystoreProperties['storePassword']
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled false
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }
}
```

#### 3. 构建 Release APK

在 Android Studio 中：
1. Build → Generate Signed Bundle / APK
2. 选择 APK
3. 选择密钥文件和别名
4. 输入密码
5. 选择 release 构建类型
6. 点击 Finish

生成的 APK 位于：`android/app/build/outputs/apk/release/app-release.apk`

或使用命令行：

```bash
cd android
./gradlew assembleRelease
```

## 分发 APK

### 直接分发

1. 将 `app-release.apk` 上传到你的服务器
2. 生成下载链接，例如：`https://your-domain.com/download/dub-editor.apk`
3. 用户访问链接即可下载安装

### 二维码分发

使用在线工具生成下载链接的二维码，用户扫码即可下载。

### 应用市场

- **Google Play**: 需要开发者账号（25 美元一次性费用）
- **华为应用市场**: 免费，需要企业认证
- **小米应用商店**: 免费
- **应用宝（腾讯）**: 免费

## iOS 开发（需要 Mac）

### 1. 添加 iOS 平台

```bash
npm run add:ios
cd ios/App
pod install
cd ../..
```

### 2. 打开 Xcode

```bash
npm run ios
```

### 3. 配置签名

在 Xcode 中：
1. 选择项目 → Signing & Capabilities
2. 勾选 "Automatically manage signing"
3. 选择你的 Team（需要登录 Apple ID）

### 4. 运行到设备

1. 连接 iPad
2. 在设备列表中选择你的 iPad
3. 点击 Run 按钮 ▶️

首次运行需要在 iPad 上信任开发者证书：
设置 → 通用 → VPN 与设备管理 → 信任

### 5. TestFlight 分发

1. 在 Xcode 中：Product → Archive
2. 等待归档完成
3. 点击 "Distribute App"
4. 选择 "App Store Connect"
5. 上传到 App Store Connect
6. 在 App Store Connect 中配置 TestFlight
7. 添加测试用户（最多 1 万人）
8. 用户通过 TestFlight App 安装

## 常见问题

### Android Studio Gradle 同步失败

**问题**: 下载依赖超时

**解决**:
1. 配置国内镜像，编辑 `android/build.gradle`：

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

2. 重新同步 Gradle

### App 白屏

**可能原因**:
1. 编辑器 URL 配置错误
2. 网络连接问题
3. CORS 跨域问题

**调试方法**:
1. 在 Android Studio 中打开 Logcat
2. 过滤 "chromium" 查看 WebView 日志
3. 检查是否有网络错误或 CORS 错误

### 无法连接到开发服务器

**检查清单**:
1. 电脑和手机在同一局域网
2. 防火墙允许 5173 端口
3. `capacitor.config.ts` 中的 IP 地址正确
4. 开发服务器正在运行

### iOS 签名错误

**问题**: "Failed to create provisioning profile"

**解决**:
1. 确保已登录 Apple ID
2. 修改 Bundle ID 为唯一值（例如：`com.yourcompany.dubeditor`）
3. 在 Xcode 中重新选择 Team

## 下一步

- 📖 阅读 [Deep Link 配置指南](./DEEPLINK.md)
- 🎨 自定义 App 图标和启动画面
- 📱 配置推送通知（可选）
- 🔐 添加生物识别认证（可选）

## 获取帮助

- [Capacitor 官方文档](https://capacitorjs.com/docs)
- [Android 开发文档](https://developer.android.com)
- [iOS 开发文档](https://developer.apple.com)
