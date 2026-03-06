# GitHub 上传指南

## 项目已准备就绪

项目已经初始化 git 仓库并完成首次提交，可以直接推送到 GitHub。

## 上传步骤

### 1. 在 GitHub 创建新仓库

1. 访问 https://github.com/new
2. 填写仓库信息：
   - **Repository name**: `dub-mobile-app`（或你喜欢的名字）
   - **Description**: `Dub Editor Mobile App - WebView container with Deep Link support`
   - **Public/Private**: 选择 Public（公开）或 Private（私有）
   - **不要勾选** "Initialize this repository with a README"（我们已经有了）

### 2. 推送代码到 GitHub

创建仓库后，GitHub 会显示推送命令，在本地执行：

```bash
cd /d/project/互动视频/dub-mobile-app

# 添加远程仓库（替换为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/dub-mobile-app.git

# 推送代码
git branch -M main
git push -u origin main
```

### 3. 启用 GitHub Actions

推送成功后：

1. 访问你的仓库页面
2. 点击 **Actions** 标签
3. 如果看到提示，点击 **I understand my workflows, go ahead and enable them**
4. GitHub Actions 会自动开始构建 APK

### 4. 下载构建的 APK

构建完成后（约 5-10 分钟）：

1. 在 **Actions** 标签页找到最新的 workflow run
2. 点击进入详情页
3. 在 **Artifacts** 部分下载 `app-release`
4. 解压得到 `app-release-unsigned.apk`

## 自动构建说明

### 触发条件

GitHub Actions 会在以下情况自动构建：

- 推送代码到 `main` 或 `master` 分支
- 创建 Pull Request
- 手动触发（在 Actions 页面点击 "Run workflow"）

### 构建配置

配置文件：`.github/workflows/android-build.yml`

构建环境：
- Ubuntu Latest
- Node.js 18
- JDK 11
- Gradle 自动版本

### 构建产物

- **文件名**: `app-release-unsigned.apk`
- **位置**: Actions → Workflow Run → Artifacts
- **保留时间**: 90 天

## 签名 APK（可选）

未签名的 APK 可以安装，但如果要发布到应用商店，需要签名：

### 1. 生成密钥

```bash
keytool -genkey -v -keystore dub-editor.keystore \
  -alias dub-editor \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### 2. 配置 GitHub Secrets

在仓库设置中添加以下 Secrets：

- `KEYSTORE_FILE`: 密钥文件的 Base64 编码
- `KEYSTORE_PASSWORD`: 密钥库密码
- `KEY_ALIAS`: 密钥别名
- `KEY_PASSWORD`: 密钥密码

### 3. 修改 workflow

更新 `.github/workflows/android-build.yml`，添加签名步骤（参考 GitHub Actions 文档）。

## 本地构建（备选方案）

如果不想使用 GitHub Actions，也可以本地构建：

### Windows

```bash
cd /d/project/互动视频/dub-mobile-app
npm install
npm run sync:android
cd android
gradlew.bat assembleRelease
```

APK 位置：`android/app/build/outputs/apk/release/`

### Mac/Linux

```bash
cd /d/project/互动视频/dub-mobile-app
npm install
npm run sync:android
cd android
./gradlew assembleRelease
```

## 分发 APK

### 方式一：直接下载

1. 将 APK 上传到你的服务器
2. 生成下载链接
3. 用户访问链接下载安装

### 方式二：GitHub Releases

1. 在 GitHub 仓库页面点击 **Releases**
2. 点击 **Create a new release**
3. 填写版本号（如 `v1.0.0`）
4. 上传 APK 文件
5. 发布 Release
6. 用户可以从 Releases 页面下载

### 方式三：应用商店

- **Google Play**: 需要开发者账号（$25 一次性）
- **华为应用市场**: 免费
- **小米应用商店**: 免费

## 更新 App

### 修改代码后

```bash
cd /d/project/互动视频/dub-mobile-app

# 修改代码...

# 提交并推送
git add .
git commit -m "描述你的修改"
git push

# GitHub Actions 会自动构建新版本
```

### 更新版本号

编辑 `android/app/build.gradle`：

```gradle
defaultConfig {
    versionCode 2        // 每次发布递增
    versionName "1.1.0"  // 显示给用户的版本号
}
```

## 常见问题

### Q: 构建失败怎么办？

A: 查看 Actions 页面的构建日志，通常是依赖问题或配置错误。

### Q: APK 无法安装？

A:
1. 确保允许"未知来源"安装
2. 检查 Android 版本是否支持（最低 API 24 / Android 7.0）
3. 尝试卸载旧版本后重新安装

### Q: 如何修改 App 名称和图标？

A: 参考项目中的 `ASSETS.md` 文档。

### Q: 如何修改 URL Scheme？

A: 参考项目中的 `DEEPLINK.md` 文档。

## 下一步

1. ✅ 推送代码到 GitHub
2. ✅ 启用 GitHub Actions
3. ✅ 等待构建完成
4. ✅ 下载 APK
5. ✅ 测试安装和 Deep Link 功能
6. ✅ 分发给用户

## 相关链接

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Capacitor 文档](https://capacitorjs.com/docs)
- [Android 开发文档](https://developer.android.com/)

## 技术支持

如有问题，请在 GitHub 仓库创建 Issue。
