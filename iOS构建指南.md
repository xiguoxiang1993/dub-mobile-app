# iOS 构建指南

## 项目结构

iOS 项目已成功生成在 `mobile-app/ios/` 目录下，包含以下关键文件：

```
ios/
├── App/
│   ├── App/
│   │   ├── AppDelegate.swift          # 应用程序委托
│   │   ├── Info.plist                 # 应用配置文件
│   │   ├── Assets.xcassets/           # 图标和启动图资源
│   │   │   ├── AppIcon.appiconset/    # 应用图标
│   │   │   └── Splash.imageset/       # 启动屏幕
│   │   ├── Base.lproj/
│   │   │   ├── LaunchScreen.storyboard  # 启动屏幕界面
│   │   │   └── Main.storyboard          # 主界面
│   │   ├── public/                    # Web 资源目录
│   │   └── capacitor.config.json      # Capacitor 配置
│   ├── App.xcodeproj/                 # Xcode 项目文件
│   ├── App.xcworkspace/               # Xcode 工作空间
│   └── Podfile                        # CocoaPods 依赖配置
└── capacitor-cordova-ios-plugins/     # Cordova 插件目录
```

## 应用配置

### 基本信息
- **App ID**: `com.dub.editor`
- **App Name**: `Dub Editor`
- **最低 iOS 版本**: 14.0
- **支持方向**: 竖屏、横屏（左右）

### Capacitor 配置
配置文件位于 `capacitor.config.ts`，iOS 相关配置：
```typescript
ios: {
  contentInset: 'never',
  scrollEnabled: false,
  scheme: 'Dub Editor'
}
```

## 构建步骤

### 前置要求
1. macOS 系统（必需）
2. Xcode 14.0 或更高版本
3. CocoaPods（用于依赖管理）

### 安装 CocoaPods
```bash
# 使用 Homebrew 安装
brew install cocoapods

# 或使用 gem 安装
sudo gem install cocoapods
```

### 同步项目
```bash
cd mobile-app

# 同步 Web 资源到 iOS
npm run sync:ios

# 或手动执行
npx cap sync ios
```

### 安装依赖
```bash
cd ios/App
pod install
```

### 打开 Xcode 项目
```bash
# 在 mobile-app 目录下执行
npm run ios

# 或手动打开
open ios/App/App.xcworkspace
```

**注意**: 必须打开 `.xcworkspace` 文件，而不是 `.xcodeproj` 文件。

## 在 Xcode 中配置

### 1. 配置签名
1. 在 Xcode 中选择项目 `App`
2. 选择 `Signing & Capabilities` 标签
3. 选择你的开发团队（Team）
4. Xcode 会自动管理签名证书

### 2. 配置 Bundle Identifier
- 默认为 `com.dub.editor`
- 如需修改，在 Xcode 的 `General` 标签中修改

### 3. 配置版本号
- **Version**: 1.0（对应 CFBundleShortVersionString）
- **Build**: 1（对应 CFBundleVersion）

## 运行和调试

### 在模拟器中运行
1. 在 Xcode 中选择目标模拟器（如 iPhone 15 Pro）
2. 点击运行按钮（⌘R）

### 在真机上运行
1. 连接 iOS 设备到 Mac
2. 在设备上信任开发者证书
3. 在 Xcode 中选择你的设备
4. 点击运行按钮（⌘R）

### 使用命令行运行
```bash
# 在模拟器中运行
npx cap run ios

# 在指定设备上运行
npx cap run ios --target="iPhone 15 Pro"
```

## 开发模式

### 连接本地开发服务器
修改 `capacitor.config.ts`：
```typescript
server: {
  url: 'http://你的本地IP:5173',
  cleartext: true
}
```

然后重新同步：
```bash
npm run sync:ios
```

## 构建发布版本

### 1. Archive 构建
1. 在 Xcode 中选择 `Product` > `Archive`
2. 等待构建完成

### 2. 导出 IPA
1. 在 Organizer 窗口中选择刚才的 Archive
2. 点击 `Distribute App`
3. 选择分发方式：
   - **App Store Connect**: 上传到 App Store
   - **Ad Hoc**: 内部测试分发
   - **Enterprise**: 企业分发
   - **Development**: 开发测试

### 3. 上传到 App Store
1. 选择 `App Store Connect`
2. 按照向导完成上传
3. 在 App Store Connect 中提交审核

## 常见问题

### CocoaPods 依赖安装失败
```bash
# 清理缓存
pod cache clean --all
pod deintegrate
pod install --repo-update
```

### 签名错误
- 确保在 Xcode 中选择了正确的开发团队
- 检查 Bundle Identifier 是否唯一
- 确保证书和描述文件有效

### 模拟器无法启动
```bash
# 重置模拟器
xcrun simctl erase all

# 重启 Xcode
killall Xcode
```

### Web 资源未更新
```bash
# 清理并重新同步
rm -rf ios/App/App/public
npm run sync:ios
```

## 更新 iOS 项目

### 更新 Capacitor
```bash
npm install @capacitor/ios@latest
npm run sync:ios
```

### 更新依赖
```bash
cd ios/App
pod update
```

## 调试技巧

### 查看日志
在 Xcode 中打开 `Console`（⌘⇧C）查看应用日志

### Safari 调试 WebView
1. 在 iOS 设备上启用 `设置` > `Safari` > `高级` > `Web 检查器`
2. 在 Mac 上打开 Safari
3. 选择 `开发` > `[你的设备]` > `Dub Editor`

### 网络调试
在 `capacitor.config.ts` 中启用调试：
```typescript
ios: {
  webContentsDebuggingEnabled: true
}
```

## 资源文件

### 应用图标
替换 `ios/App/App/Assets.xcassets/AppIcon.appiconset/` 中的图标文件

### 启动屏幕
- 修改 `ios/App/App/Base.lproj/LaunchScreen.storyboard`
- 或替换 `Assets.xcassets/Splash.imageset/` 中的图片

## 与 Android 版本的差异

| 特性 | Android | iOS |
|------|---------|-----|
| 最低版本 | API 24 (Android 7.0) | iOS 14.0 |
| 开发环境 | Windows/Mac/Linux | 仅 Mac |
| 构建工具 | Gradle | Xcode + CocoaPods |
| 包格式 | APK/AAB | IPA |
| 签名 | Keystore | 证书 + 描述文件 |

## 下一步

1. 在 macOS 上安装 Xcode
2. 安装 CocoaPods
3. 执行 `pod install`
4. 在 Xcode 中打开项目
5. 配置签名并运行

## 参考资源

- [Capacitor iOS 文档](https://capacitorjs.com/docs/ios)
- [Xcode 文档](https://developer.apple.com/xcode/)
- [CocoaPods 文档](https://cocoapods.org/)
- [App Store Connect](https://appstoreconnect.apple.com/)
