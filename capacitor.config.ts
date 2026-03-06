import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.dub.editor',
  appName: 'Dub Editor',
  webDir: 'www',
  server: {
    // 开发模式：取消注释下面两行，填入你的本地 IP 地址
    // url: 'http://192.168.1.100:5173',
    // cleartext: true,

    // 生产模式：注释掉上面的 url，使用打包后的静态文件
    androidScheme: 'https'
  },
  ios: {
    contentInset: 'never',
    scrollEnabled: false,
    // 支持 URL Scheme
    scheme: 'Dub Editor'
  },
  android: {
    allowMixedContent: true,
    // 启用硬件加速
    webContentsDebuggingEnabled: true
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 0,
      backgroundColor: '#ffffff'
    }
  }
};

export default config;
