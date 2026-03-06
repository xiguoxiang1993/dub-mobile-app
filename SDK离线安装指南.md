# Android SDK 离线安装指南

由于网络问题，需要手动下载 SDK 组件。

## 需要下载的文件

请从以下镜像地址下载（选择一个速度快的）：

### 1. platform-tools（必需）
- 阿里云：https://mirrors.aliyun.com/android.googlesource.com/platform/prebuilts/fullsdk/platforms/android-35_r01.zip
- 或直接下载：https://dl.google.com/android/repository/platform-tools-latest-windows.zip

保存到：`D:\Android\sdk-downloads\platform-tools.zip`

### 2. build-tools 35.0.0（必需）
- 下载地址：https://dl.google.com/android/repository/build-tools_r35-windows.zip

保存到：`D:\Android\sdk-downloads\build-tools.zip`

### 3. android-35 platform（必需）
- 下载地址：https://dl.google.com/android/repository/platform-35_r01.zip

保存到：`D:\Android\sdk-downloads\platform-35.zip`

## 或者使用百度网盘

如果上述地址都很慢，可以搜索"Android SDK 离线包"，通常有人分享在百度网盘。

## 下载完成后

将三个 zip 文件放到 `D:\Android\sdk-downloads\` 目录，然后告诉我，我会帮你解压和配置。

---

## 备选方案：使用 Android Studio 的国内镜像

Android Studio 本身也有国内镜像：

- 中科大镜像：https://mirrors.ustc.edu.cn/android-studio/
- 腾讯镜像：https://mirrors.cloud.tencent.com/AndroidStudio/

下载后安装，在首次启动时配置 SDK 镜像：
1. 打开 Settings → Appearance & Behavior → System Settings → HTTP Proxy
2. 或者在 SDK Manager 中配置使用国内镜像源

这样 Android Studio 就能从国内镜像下载 SDK 了。
