#!/bin/bash

# 构建脚本 - 用于生成生产环境的 APK

set -e

echo "🚀 开始构建 Dub Editor Android App..."

# 1. 确保在正确的目录
cd "$(dirname "$0")"

# 2. 检查 www 目录
if [ ! -d "www" ]; then
  echo "❌ 错误: www 目录不存在"
  exit 1
fi

# 3. 同步到 Android
echo "📦 同步代码到 Android..."
npm run sync:android

echo "✅ 同步完成！"
echo ""
echo "📱 接下来的步骤："
echo "1. 运行: npm run android"
echo "2. 在 Android Studio 中点击 Build → Generate Signed Bundle / APK"
echo "3. 选择 APK，配置签名密钥"
echo "4. 生成的 APK 位于: android/app/build/outputs/apk/"
