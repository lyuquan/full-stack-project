import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// Vite 开发服务器配置。
// 前端运行在 5173，后端运行在 8080，所以开发时需要代理 /api 请求到后端。
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 当前端请求 /api/system/hello 时，Vite 会转发到 http://localhost:8080/api/system/hello
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
