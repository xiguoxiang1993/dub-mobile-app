# App 图标和启动画面配置

## 准备资源

### App 图标

准备一张 1024x1024 的 PNG 图片（透明背景或纯色背景）。

推荐使用在线工具生成各种尺寸：
- [App Icon Generator](https://appicon.co/)
- [MakeAppIcon](https://makeappicon.com/)

### 启动画面

准备一张 2732x2732 的 PNG 图片（建议居中放置 logo）。

## Android 图标配置

### 方法 1: 使用 Android Studio（推荐）

1. 打开 Android Studio
2. 右键点击 `android/app/src/main/res`
3. 选择 New → Image Asset
4. 配置：
   - Icon Type: Launcher Icons (Adaptive and Legacy)
   - Path: 选择你的 1024x1024 图标
   - Trim: Yes（自动裁剪透明边距）
   - Resize: 100%
5. 点击 Next → Finish

### 方法 2: 手动替换

将不同尺寸的图标放到对应目录：

```
android/app/src/main/res/
├── mipmap-hdpi/
│   └── ic_launcher.png (72x72)
├── mipmap-mdpi/
│   └── ic_launcher.png (48x48)
├── mipmap-xhdpi/
│   └── ic_launcher.png (96x96)
├── mipmap-xxhdpi/
│   └── ic_launcher.png (144x144)
└── mipmap-xxxhdpi/
    └── ic_launcher.png (192x192)
```

### Android 自适应图标（Android 8.0+）

创建 `android/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

定义背景色 `android/app/src/main/res/values/colors.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#667EEA</color>
</resources>
```

## iOS 图标配置（需要 Mac）

### 使用 Xcode

1. 打开 Xcode：`npm run ios`
2. 在左侧导航栏选择 `App` → `Assets.xcassets` → `AppIcon`
3. 将不同尺寸的图标拖拽到对应位置：
   - iPhone App: 60x60, 120x120, 180x180
   - iPad App: 76x76, 152x152, 167x167
   - App Store: 1024x1024

### 使用工具生成

使用 [App Icon Generator](https://appicon.co/) 生成 iOS 图标集，下载后：

1. 解压 zip 文件
2. 将 `AppIcon.appiconset` 文件夹复制到 `ios/App/App/Assets.xcassets/`
3. 替换原有的 `AppIcon.appiconset`

## Android 启动画面

### 1. 准备资源

将启动画面图片放到：

```
android/app/src/main/res/
├── drawable/
│   └── splash.png (通用)
├── drawable-land-hdpi/
│   └── splash.png (横屏 480x320)
├── drawable-land-mdpi/
│   └── splash.png (横屏 320x240)
├── drawable-land-xhdpi/
│   └── splash.png (横屏 640x480)
├── drawable-land-xxhdpi/
│   └── splash.png (横屏 960x720)
└── drawable-land-xxxhdpi/
    └── splash.png (横屏 1280x960)
```

### 2. 配置启动画面

编辑 `android/app/src/main/res/values/styles.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="AppTheme.NoActionBarLaunch" parent="AppTheme.NoActionBar">
        <item name="android:background">@drawable/splash</item>
    </style>
</resources>
```

### 3. 使用 Capacitor Splash Screen 插件

```bash
npm install @capacitor/splash-screen
npm run sync:android
```

在 `capacitor.config.ts` 中配置：

```typescript
plugins: {
  SplashScreen: {
    launchShowDuration: 2000,
    backgroundColor: "#667eea",
    androidScaleType: "CENTER_CROP",
    showSpinner: false,
    splashFullScreen: true,
    splashImmersive: true
  }
}
```

## iOS 启动画面（需要 Mac）

### 使用 Storyboard

1. 打开 Xcode：`npm run ios`
2. 选择 `App` → `LaunchScreen.storyboard`
3. 在 Interface Builder 中设计启动画面：
   - 添加 ImageView
   - 设置约束（居中）
   - 选择图片资源

### 添加启动图片资源

1. 在 `Assets.xcassets` 中创建新的 Image Set
2. 命名为 `Splash`
3. 添加不同分辨率的图片：
   - 1x: 1024x1024
   - 2x: 2048x2048
   - 3x: 3072x3072

## 修改 App 名称

### Android

编辑 `android/app/src/main/res/values/strings.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">互动视频编辑器</string>
    <string name="title_activity_main">互动视频编辑器</string>
    <string name="package_name">com.dub.editor</string>
    <string name="custom_url_scheme">dubeditor</string>
</resources>
```

### iOS

在 Xcode 中：
1. 选择项目 → General
2. 修改 Display Name: `互动视频编辑器`

或编辑 `ios/App/App/Info.plist`：

```xml
<key>CFBundleDisplayName</key>
<string>互动视频编辑器</string>
```

## 快速配置工具

### 使用 Capacitor Assets

```bash
npm install -g @capacitor/assets
```

准备资源文件：

```
resources/
├── icon.png (1024x1024)
└── splash.png (2732x2732)
```

生成所有尺寸：

```bash
npx capacitor-assets generate --iconBackgroundColor '#667eea' --splashBackgroundColor '#667eea'
```

这会自动生成 Android 和 iOS 所需的所有图标和启动画面。

## 验证配置

### Android

1. 卸载旧版本 App
2. 重新构建并安装
3. 检查桌面图标
4. 启动 App 查看启动画面

### iOS

1. 在 Xcode 中 Clean Build Folder (Cmd+Shift+K)
2. 重新运行
3. 检查主屏幕图标
4. 启动 App 查看启动画面

## 设计建议

### App 图标

- 使用简洁的设计，避免过多细节
- 确保在小尺寸下仍然清晰可辨
- 使用品牌色
- 避免使用文字（除非是 logo 的一部分）
- 测试在不同背景下的效果

### 启动画面

- 保持简洁，只显示 logo 或品牌名称
- 使用与 App 主题一致的颜色
- 避免显示过多信息
- 考虑深色模式适配
- 启动时间不宜过长（1-2 秒）

## 深色模式适配

### Android

创建 `android/app/src/main/res/values-night/colors.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#1a1a1a</color>
</resources>
```

### iOS

在 Assets.xcassets 中：
1. 选择图片资源
2. 在 Attributes Inspector 中
3. Appearances → Any, Dark
4. 为深色模式添加不同的图片

## 在线工具推荐

- [App Icon Generator](https://appicon.co/) - 生成所有平台图标
- [MakeAppIcon](https://makeappicon.com/) - iOS/Android 图标生成
- [Figma](https://figma.com) - 设计图标和启动画面
- [Canva](https://canva.com) - 快速设计工具
- [IconKitchen](https://icon.kitchen/) - Android 自适应图标生成器

## 注意事项

1. **图标透明度**: Android 自适应图标的前景层应该有透明背景
2. **安全区域**: iOS 图标会自动添加圆角，设计时预留边距
3. **文件大小**: 启动画面图片不宜过大（建议 < 500KB）
4. **版权**: 确保使用的图片和字体有合法授权
5. **测试**: 在真实设备上测试，模拟器可能显示不准确
