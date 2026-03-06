# 手动构建 APK 指南

由于网络问题导致 Gradle 自动下载失败，这里提供手动构建的方案。

## 方案 1：手动下载 Gradle（推荐）

### 步骤 1：下载 Gradle

从以下任一地址下载 `gradle-8.11.1-all.zip`：

1. **阿里云镜像**（推荐）：
   https://mirrors.aliyun.com/macports/distfiles/gradle/gradle-8.11.1-all.zip

2. **华为云镜像**：
   https://repo.huaweicloud.com/gradle/gradle-8.11.1-all.zip

3. **官方地址**（如果网络好）：
   https://services.gradle.org/distributions/gradle-8.11.1-all.zip

### 步骤 2：放置文件

将下载的 `gradle-8.11.1-all.zip` 文件（不要解压）复制到：

```
C:\Users\gxxi\.gradle\wrapper\dists\gradle-8.11.1-all\2qik7nd48slq1ooc2496ixf4i\
```

如果目录不存在，请手动创建。

### 步骤 3：构建 APK

打开命令行，运行：

```bash
cd "D:\project\互动视频\Dub\mobile-app\android"
gradlew assembleDebug
```

构建完成后，APK 位于：
```
D:\project\互动视频\Dub\mobile-app\android\app\build\outputs\apk\debug\app-debug.apk
```

---

## 方案 2：使用 Android Studio（最简单）

### 步骤 1：安装 Android Studio

下载地址：https://developer.android.com/studio

### 步骤 2：打开项目

1. 启动 Android Studio
2. 选择 "Open an existing project"
3. 打开目录：`D:\project\互动视频\Dub\mobile-app\android`

### 步骤 3：等待同步

Android Studio 会自动下载 Gradle 和依赖（它的下载机制更稳定）

### 步骤 4：构建 APK

点击菜单：**Build → Build Bundle(s) / APK(s) → Build APK(s)**

构建完成后会弹出提示，点击 "locate" 可以找到 APK 文件。

---

## 方案 3：使用已安装的 Gradle（如果有）

如果你的系统已经安装了 Gradle，可以直接使用：

```bash
cd "D:\project\互动视频\Dub\mobile-app\android"
gradle assembleDebug
```

---

## 构建 Release 版本（用于发布）

Debug 版本只用于测试。如果要发布，需要构建 Release 版本：

### 1. 生成签名密钥

```bash
keytool -genkey -v -keystore dub-editor.keystore -alias dub-editor -keyalg RSA -keysize 2048 -validity 10000
```

### 2. 配置签名

在 `android/app/build.gradle` 中添加：

```gradle
android {
    signingConfigs {
        release {
            storeFile file("../../dub-editor.keystore")
            storePassword "你的密码"
            keyAlias "dub-editor"
            keyPassword "你的密码"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 3. 构建 Release APK

```bash
gradlew assembleRelease
```

APK 位于：`android/app/build/outputs/apk/release/app-release.apk`

---

## 常见问题

### Q: Gradle 下载太慢怎么办？

使用方案 1 手动下载，或者使用方案 2 的 Android Studio。

### Q: 构建失败提示缺少 SDK？

使用 Android Studio 打开项目，它会自动提示并下载所需的 SDK。

### Q: 如何减小 APK 体积？

在 `build.gradle` 中启用代码压缩：

```gradle
buildTypes {
    release {
        minifyEnabled true
        shrinkResources true
    }
}
```

### Q: 如何查看构建日志？

添加 `--info` 或 `--debug` 参数：

```bash
gradlew assembleDebug --info
```
