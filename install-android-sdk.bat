@echo off
chcp 65001 >nul
echo ========================================
echo Android SDK 命令行工具安装脚本
echo ========================================
echo.

set SDK_DIR=D:\Android\sdk
set CMDLINE_TOOLS_URL=https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip
set CMDLINE_TOOLS_ZIP=%TEMP%\commandlinetools.zip

echo [1/5] 创建 SDK 目录...
if not exist "%SDK_DIR%" mkdir "%SDK_DIR%"
if not exist "%SDK_DIR%\cmdline-tools" mkdir "%SDK_DIR%\cmdline-tools"

echo.
echo [2/5] 下载 Android SDK 命令行工具...
echo 下载地址: %CMDLINE_TOOLS_URL%
echo 保存位置: %CMDLINE_TOOLS_ZIP%
echo.
echo 请手动下载上述文件并保存到: %CMDLINE_TOOLS_ZIP%
echo 或者使用浏览器下载后复制到该位置
echo.
echo 下载完成后按任意键继续...
pause >nul

if not exist "%CMDLINE_TOOLS_ZIP%" (
    echo 错误: 未找到下载的文件
    echo 请确保文件已下载到: %CMDLINE_TOOLS_ZIP%
    pause
    exit /b 1
)

echo.
echo [3/5] 解压文件...
powershell -Command "Expand-Archive -Path '%CMDLINE_TOOLS_ZIP%' -DestinationPath '%SDK_DIR%\cmdline-tools' -Force"

echo.
echo [4/5] 重命名目录...
if exist "%SDK_DIR%\cmdline-tools\cmdline-tools" (
    move "%SDK_DIR%\cmdline-tools\cmdline-tools" "%SDK_DIR%\cmdline-tools\latest"
)

echo.
echo [5/5] 安装必要的 SDK 组件...
echo 这可能需要几分钟时间...
echo.

set PATH=%SDK_DIR%\cmdline-tools\latest\bin;%PATH%

echo y | sdkmanager --sdk_root="%SDK_DIR%" "platform-tools"
echo y | sdkmanager --sdk_root="%SDK_DIR%" "platforms;android-35"
echo y | sdkmanager --sdk_root="%SDK_DIR%" "build-tools;35.0.0"

echo.
echo [6/6] 创建 local.properties 文件...
echo sdk.dir=%SDK_DIR:\=\\% > "%~dp0android\local.properties"

echo.
echo ========================================
echo 安装完成！
echo ========================================
echo.
echo SDK 位置: %SDK_DIR%
echo local.properties 已创建
echo.
echo 现在可以运行构建命令了：
echo cd "%~dp0android"
echo gradlew assembleDebug
echo.
pause
