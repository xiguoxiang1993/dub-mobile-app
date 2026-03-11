# GeckoView 集成设计方案

**日期：** 2026-03-11
**状态：** 已批准
**目标：** 为 Android 7-11 旧设备提供现代浏览器内核支持

## 一、背景和问题

### 当前状况
- 项目使用 Capacitor + WebView 架构
- 目标用户设备：Android 7.1.1（Chrome 60）
- 系统 WebView 版本过旧，无法渲染现代 Web 应用

### 核心问题
用户的 Android 7.1.1 设备自带 Chrome 60，缺少现代 Web 特性：
- ES6+ JavaScript 语法支持不完整
- 缺少 WebRTC、WebGL、Canvas 等 API
- CSS Grid、现代 Flexbox 支持不足
- Service Worker、IndexedDB 等 PWA 特性缺失

### 需求
- 支持 Android 7-11 旧设备用户群
- 内置现代浏览器内核（Chrome 100+ 等效）
- 包体积不受限制（功能优先）

## 二、方案选型

### 评估的方案

**方案 A：GeckoView（采用）**
- 优势：Mozilla 持续维护，安全更新及时，隐私保护好
- 劣势：包体积 50-70MB，基于 Firefox 内核（非 Chromium）
- 依赖问题：Mozilla Maven 仓库国内无法访问

**方案 B：系统 WebView + androidx.webkit**
- 优势：官方方案，无额外包体积
- 劣势：依赖设备 WebView 版本，Android 7 设备仍然是旧版本
- 结论：无法解决核心问题

**方案 C：腾讯 X5 内核**
- 优势：基于 Chromium，国内优化
- 劣势：已于 2023 年停止维护，安全漏洞无人修复
- 结论：不推荐生产环境使用

### 最终选择：GeckoView + Maven Central 第三方镜像

**理由：**
1. 功能完整：支持所有现代 Web 特性
2. 持续维护：Mozilla 官方支持
3. 依赖可获取：通过 Maven Central 第三方镜像（io.github.mhqz）
4. 包体积可接受：用户选择功能优先

## 三、技术设计

### 3.1 架构概述

保持现有 Capacitor + WebView 架构，核心变更：
- MainActivity 使用 GeckoView 替代系统 WebView
- 通过 Maven Central 获取 GeckoView 依赖
- 降低对网络环境的依赖（国内可访问）

### 3.2 依赖配置

**GeckoView 版本：** 107.0.20221005094233
- 基于 Firefox 107（相当于 Chrome 90+）
- 支持所有现代 Web 特性
- 比 Chrome 60 新约 30 个大版本

**架构支持：**
- 主要：arm64-v8a（64位 ARM，覆盖大部分 Android 7+ 设备）
- 可选：armeabi-v7a（32位 ARM，支持极少数旧设备，增加约 50MB）

**Maven 仓库策略：**
```gradle
allprojects {
    repositories {
        // 阿里云镜像（优先，快速）
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }

        // Maven Central（GeckoView 第三方镜像）
        mavenCentral()

        // 官方仓库（备用）
        google()
    }
}
```

**依赖声明：**
```gradle
dependencies {
    // 移除系统 WebKit
    // implementation "androidx.webkit:webkit:$androidxWebkitVersion"

    // 添加 GeckoView（arm64-v8a）
    implementation 'io.github.mhqz:geckoview-default-omni-arm64-v8a:107.0.20221005094233'

    // 可选：支持 32位设备（增加约 50MB）
    // implementation 'io.github.mhqz:geckoview-ceno-omni-armeabi-v7a:107.0.20221005094233'
}
```

**ABI 过滤：**
```gradle
android {
    defaultConfig {
        ndk {
            abiFilters 'arm64-v8a'
            // 如需支持 32位：'armeabi-v7a'
        }
    }
}
```

### 3.3 代码实现

**MainActivity.java 结构：**
```java
public class MainActivity extends Activity {
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

    private void handleIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            String targetUrl = data.getQueryParameter("url");
            if (targetUrl != null) {
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
```

**关键点：**
- 保持 Deep Link 处理逻辑不变
- 支持返回键导航
- 正确管理生命周期

### 3.4 构建配置

**minSdkVersion：** 保持 24（Android 7.0）
- 覆盖目标用户群（Android 7-11）
- GeckoView 107 支持 Android 5.0+

**targetSdkVersion：** 保持 34
**compileSdkVersion：** 保持 35

## 四、实施计划

### 4.1 配置阶段
1. 修改项目级 build.gradle，添加 Maven 仓库配置
2. 修改 app/build.gradle，添加 GeckoView 依赖
3. 配置 ABI 过滤
4. 移除 androidx.webkit 依赖

### 4.2 代码改造
1. 确认 MainActivity.java 已使用 GeckoView
2. 验证 Deep Link 处理逻辑
3. 测试返回键和生命周期管理

### 4.3 构建测试
1. Gradle 同步依赖
2. 构建 Debug APK
3. 在 Android 7.1.1 设备上安装测试
4. 验证页面渲染和功能

### 4.4 验证标准
- [ ] 依赖成功下载
- [ ] APK 成功构建
- [ ] 应用正常启动
- [ ] Deep Link 跳转正常
- [ ] 页面完整渲染（现代 Web 特性可用）
- [ ] 返回键导航正常

## 五、风险和缓解

### 5.1 包体积增大
**风险：** APK 增加 50-70MB
**影响：** 下载时间增加，存储空间占用
**缓解：** 用户已接受（功能优先）

### 5.2 GeckoView 版本较旧
**风险：** 107 版本可能缺少最新 Web API
**影响：** 部分最新特性不可用
**缓解：**
- 107 版本已支持绝大部分现代特性
- 比 Chrome 60 新约 30 个版本
- 如遇问题，可考虑其他方案

### 5.3 架构兼容性
**风险：** 只支持 arm64-v8a，极少数旧设备不兼容
**影响：** 部分 32位设备无法使用
**缓解：**
- 大部分 Android 7+ 设备支持 64位
- 可按需添加 armeabi-v7a 支持

### 5.4 Maven Central 镜像可用性
**风险：** 第三方镜像可能停止维护
**影响：** 未来无法获取依赖
**缓解：**
- 当前版本可用
- 可保存 AAR 文件到项目本地
- 可寻找其他镜像源

## 六、后续优化

### 6.1 短期优化
- 测试 32位设备兼容性，按需添加 armeabi-v7a
- 优化 APK 体积（ProGuard、资源压缩）
- 添加错误处理和降级方案

### 6.2 长期优化
- 监控 GeckoView 更新，评估升级可行性
- 探索其他现代内核方案（如 Chromium 自编译）
- 考虑多内核策略（新设备用系统 WebView，旧设备用 GeckoView）

## 七、参考资料

- [GeckoView on Maven Central (io.github.mhqz)](https://central.sonatype.com/artifact/io.github.mhqz/geckoview-default-omni-arm64-v8a)
- [GeckoView Official Documentation](https://firefox-source-docs.mozilla.org/mobile/android/geckoview/consumer/geckoview-quick-start.html)
- [GeckoView GitHub Repository](https://github.com/mozilla/geckoview)

## 八、决策记录

| 决策点 | 选择 | 理由 |
|--------|------|------|
| 浏览器内核 | GeckoView | 持续维护，功能完整 |
| 依赖来源 | Maven Central 第三方镜像 | 国内可访问 |
| 版本选择 | 107.0.20221005094233 | 可用的最新版本 |
| 架构支持 | arm64-v8a | 覆盖大部分设备 |
| 包体积策略 | 功能优先 | 用户需求 |

## 九、批准记录

- **设计者：** Claude (Opus 4.6)
- **审核者：** 用户
- **批准日期：** 2026-03-11
- **状态：** 已批准，准备实施
