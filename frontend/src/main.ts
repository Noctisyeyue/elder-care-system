import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import './styles/auth.css'
import '@/assets/styles/core/tailwind.css'
import '@/assets/styles/index.scss'
import { useTheme } from '@/hooks/useTheme'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.use(pinia)
app.mount('#app')

useTheme().initTheme()
