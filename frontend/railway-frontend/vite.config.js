import process from "node:process";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    // Docker bind mounts on Windows/macOS don't deliver file-change events,
    // so poll when running in the container (set in docker-compose).
    watch: { usePolling: process.env.VITE_USE_POLLING === "true" },
    proxy: {
      "/api": {
        // In Docker the backend is reachable by its service name (set in docker-compose)
        target: process.env.API_PROXY_TARGET || "http://127.0.0.1:8080",
        changeOrigin: true,
      },
    },
  },
});