import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src') // 关键配置：让 @ 指向 src 目录
    }
  },
  server: {
    port: 5173, // 端口号
    open: true, // 启动自动打开浏览器
    proxy: {
       // 如果后端跨域，可以在这里配代理，目前我们用 cors 应该不需要
    }
  }
})
