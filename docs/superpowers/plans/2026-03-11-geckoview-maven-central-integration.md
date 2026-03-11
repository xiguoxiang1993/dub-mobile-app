# GeckoView Maven Central 集成实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 GeckoView 从 Mozilla Maven 仓库切换到 Maven Central 第三方镜像，解决国内依赖下载问题

**Architecture:** 保持现有 MainActivity 的 GeckoView 实现，仅修改依赖配置。使用 Maven Central 上的 io.github.mhqz 第三方镜像（GeckoView 107），移除 Mozilla Maven 仓库依赖。

**Tech Stack:**
- GeckoView 107.0.20221005094233 (io.github.mhqz)
- Gradle 8.7.2
- Android SDK 24-35

---

## Chunk 1: 依赖配置修改

### Task 1: 修改项目级 build.gradle 仓库配置

**Files:**
- Modify: `android/build.gradle:23-32`

**目标：** 移除 Mozilla Maven 仓库，确保使用 Maven Central 和阿里云镜像

- [ ] **Step 1: 备份当前配置**

```bash
cd "D:/project/互动视频/dub-mobile-app"
git diff android/build.gradle
```

Expected: 查看当前配置状态

- [ ] **Step 2: 修改 allprojects 仓库配置**

在 `android/build.gradle` 的 `allprojects.repositories` 块中：

移除：
```gradle
maven { url 'https://maven.mozilla.org/maven2/' }
```

确保保留：
```gradle
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
        mavenCentral()
        google()
    }
}
```

- [ ] **Step 3: 验证配置**

```bash
cd android
./gradlew dependencies --configuration debugCompileClasspath | grep -i "maven"
```

Expected: 不应出现 maven.mozilla.org

- [ ] **Step 4: 提交更改**

```bash
git add android/build.gradle
git commit -m "refactor: 移除 Mozilla Maven 仓库，使用 Maven Central

准备切换到 Maven Central 第三方 GeckoView 镜像

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 2: 修改 app/build.gradle 依赖配置

**Files:**
- Modify: `android/app/build.gradle:35-55`

**目标：** 将 GeckoView 依赖从官方 beta 版本切换到 Maven Central 第三方镜像

- [ ] **Step 1: 注释掉当前 GeckoView 依赖**

在 `android/app/build.gradle` 的 `dependencies` 块中，找到：
```gradle
implementation "org.mozilla.geckoview:geckoview-beta:125.0.20240415091630"
```

注释掉：
```gradle
// implementation "org.mozilla.geckoview:geckoview-beta:125.0.20240415091630"
```

- [ ] **Step 2: 添加 Maven Central 第三方 GeckoView 依赖**

在注释的下方添加：
```gradle
// GeckoView - Maven Central 第三方镜像（国内可访问）
implementation 'io.github.mhqz:geckoview-default-omni-arm64-v8a:107.0.20221005094233'
```

- [ ] **Step 3: 移除 androidx.webkit 依赖（可选）**

找到：
```gradle
implementation "androidx.webkit:webkit:$androidxWebkitVersion"
```

注释掉（因为已使用 GeckoView）：
```gradle
// implementation "androidx.webkit:webkit:$androidxWebkitVersion"
```

- [ ] **Step 4: 同步 Gradle 依赖**

```bash
cd android
./gradlew clean
./gradlew app:dependencies --configuration debugCompileClasspath | grep geckoview
```

Expected: 应显示 `io.github.mhqz:geckoview-default-omni-arm64-v8a:107.0.20221005094233`

- [ ] **Step 5: 验证依赖下载成功**

```bash
./gradlew build --dry-run
```

Expected: 无错误，依赖解析成功

- [ ] **Step 6: 提交更改**

```bash
git add android/app/build.gradle
git commit -m "refactor: 切换到 Maven Central GeckoView 镜像

- 使用 io.github.mhqz:geckoview-default-omni-arm64-v8a:107.0.20221005094233
- 移除官方 Mozilla GeckoView beta 依赖
- 注释 androidx.webkit（已使用 GeckoView）

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## Chunk 2: 构建和测试验证

### Task 3: 构建 Debug APK

**Files:**
- Build output: `android/app/build/outputs/apk/debug/app-debug.apk`

**目标：** 验证新依赖配置能够成功构建 APK

- [ ] **Step 1: 清理构建缓存**

```bash
cd android
./gradlew clean
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 构建 Debug APK**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL，生成 APK 文件

- [ ] **Step 3: 验证 APK 生成**

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

Expected: 文件存在，大小约 50-80MB（包含 GeckoView）

- [ ] **Step 4: 检查 APK 内容**

```bash
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -i "gecko"
```

Expected: 应包含 GeckoView 相关的 .so 文件（arm64-v8a）

- [ ] **Step 5: 记录构建成功**

创建构建日志：
```bash
echo "Build Date: $(date)" > build-log.txt
echo "GeckoView Version: 107.0.20221005094233" >> build-log.txt
echo "APK Size: $(ls -lh app/build/outputs/apk/debug/app-debug.apk | awk '{print $5}')" >> build-log.txt
```

---

### Task 4: 设备测试验证

**Files:**
- Test device: Android 7.1.1 (API 25)

**目标：** 在目标设备上验证应用功能正常

- [ ] **Step 1: 安装 APK 到设备**

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Expected: Success

- [ ] **Step 2: 启动应用**

```bash
adb shell am start -n com.dub.editor/.MainActivity
```

Expected: 应用启动

- [ ] **Step 3: 检查应用日志**

```bash
adb logcat -c
adb logcat | grep -i "gecko"
```

Expected: 应看到 GeckoView 初始化日志

- [ ] **Step 4: 测试 Deep Link 跳转**

```bash
adb shell am start -W -a android.intent.action.VIEW -d "dubeditor://open?url=https://www.example.com"
```

Expected: 应用打开并加载指定 URL

- [ ] **Step 5: 验证页面渲染**

手动操作：
1. 观察页面是否正常渲染
2. 测试 JavaScript 功能
3. 测试返回键导航
4. 测试页面滚动和交互

Expected: 所有功能正常

- [ ] **Step 6: 记录测试结果**

```bash
echo "Test Date: $(date)" >> build-log.txt
echo "Device: Android 7.1.1" >> build-log.txt
echo "Test Result: PASS/FAIL" >> build-log.txt
git add build-log.txt
```

---

### Task 5: 最终提交和文档更新

**Files:**
- Modify: `README.md` (如需更新依赖说明)
- Create: `build-log.txt`

**目标：** 完成集成并更新文档

- [ ] **Step 1: 检查所有更改**

```bash
git status
git diff
```

Expected: 确认所有更改符合预期

- [ ] **Step 2: 提交构建日志**

```bash
git add build-log.txt
git commit -m "docs: 添加 GeckoView Maven Central 集成构建日志

记录切换到第三方镜像后的构建和测试结果

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

- [ ] **Step 3: 更新 README（如需要）**

如果 README 中有依赖说明，更新为：
```markdown
## 依赖说明

本项目使用 GeckoView 107（通过 Maven Central 第三方镜像）提供现代浏览器内核支持。

依赖来源：`io.github.mhqz:geckoview-default-omni-arm64-v8a:107.0.20221005094233`
```

- [ ] **Step 4: 最终提交**

```bash
git add README.md
git commit -m "docs: 更新 GeckoView 依赖说明

说明使用 Maven Central 第三方镜像

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

- [ ] **Step 5: 推送到远程（可选）**

```bash
git push origin main
```

Expected: 成功推送

---

## 验证清单

完成所有任务后，确认以下项目：

- [ ] `android/build.gradle` 不包含 Mozilla Maven 仓库
- [ ] `android/app/build.gradle` 使用 Maven Central GeckoView 依赖
- [ ] Gradle 同步成功，无依赖错误
- [ ] Debug APK 构建成功
- [ ] APK 包含 GeckoView arm64-v8a 库
- [ ] 应用在 Android 7.1.1 设备上正常启动
- [ ] Deep Link 功能正常
- [ ] 页面渲染正常，现代 Web 特性可用
- [ ] 所有更改已提交到 git

---

## 回滚方案

如果集成失败，可以回滚到之前的 Mozilla Maven 配置：

```bash
git revert HEAD~3  # 回滚最近 3 次提交
cd android
./gradlew clean
./gradlew assembleDebug
```

---

## 注意事项

1. **版本差异：** GeckoView 107 比之前的 125 beta 旧，可能缺少部分最新特性
2. **架构限制：** 仅支持 arm64-v8a，极少数 32位设备不兼容
3. **包体积：** APK 大小约增加 50-70MB
4. **依赖可用性：** 依赖第三方镜像，如镜像失效需要备用方案

---

## 相关文档

- 设计文档：`docs/superpowers/specs/2026-03-11-geckoview-integration-design.md`
- Maven Central 镜像：https://central.sonatype.com/artifact/io.github.mhqz/geckoview-default-omni-arm64-v8a
