import { watch } from 'vue'
import { usePreferredDark } from '@vueuse/core'
import { useSettingStore } from '@/stores/setting'
import { ThemeMode } from '@/config/setting'

function applyDarkClass(isDark: boolean) {
  const html = document.documentElement
  if (isDark) {
    html.classList.add('dark')
  } else {
    html.classList.remove('dark')
  }
}

function setElementPrimary(color: string) {
  document.documentElement.style.setProperty('--el-color-primary', color)
  document.documentElement.style.setProperty('--theme-color', color)
  document.documentElement.style.setProperty('--main-color', color)
}

export function useTheme() {
  const settingStore = useSettingStore()
  const prefersDark = usePreferredDark()

  function resolveIsDark(): boolean {
    const mode = settingStore.systemThemeMode
    if (mode === ThemeMode.DARK) return true
    if (mode === ThemeMode.LIGHT) return false
    return prefersDark.value
  }

  function applyTheme() {
    applyDarkClass(resolveIsDark())
    setElementPrimary(settingStore.systemThemeColor)
  }

  function toggleTheme() {
    const modes = [ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.AUTO]
    const idx = modes.indexOf(settingStore.systemThemeMode)
    settingStore.setSystemThemeMode(modes[(idx + 1) % modes.length])
    applyTheme()
  }

  function initTheme() {
    applyTheme()
    watch(
      () => [settingStore.systemThemeMode, settingStore.systemThemeColor, prefersDark.value],
      () => applyTheme(),
    )
  }

  return {
    toggleTheme,
    initTheme,
    isDark: () => resolveIsDark(),
    isDarkMode: () => document.documentElement.classList.contains('dark'),
  }
}
