import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ThemeMode, SETTING_DEFAULT_CONFIG } from '@/config/setting'

export const useSettingStore = defineStore(
  'setting',
  () => {
    const menuOpen = ref(SETTING_DEFAULT_CONFIG.menuOpen)
    const menuOpenWidth = ref(SETTING_DEFAULT_CONFIG.menuOpenWidth)
    const menuCloseWidth = ref(SETTING_DEFAULT_CONFIG.menuCloseWidth)
    const uniqueOpened = ref(SETTING_DEFAULT_CONFIG.uniqueOpened)
    const systemThemeMode = ref<ThemeMode>(SETTING_DEFAULT_CONFIG.systemThemeMode)
    const systemThemeColor = ref(SETTING_DEFAULT_CONFIG.systemThemeColor)
    const showMenuButton = ref(SETTING_DEFAULT_CONFIG.showMenuButton)
    const showRefreshButton = ref(SETTING_DEFAULT_CONFIG.showRefreshButton)
    const showCrumbs = ref(SETTING_DEFAULT_CONFIG.showCrumbs)
    const showWorkTab = ref(SETTING_DEFAULT_CONFIG.showWorkTab)
    const showGlobalSearch = ref(SETTING_DEFAULT_CONFIG.showGlobalSearch)
    const showFullscreen = ref(SETTING_DEFAULT_CONFIG.showFullscreen)
    const showNotification = ref(SETTING_DEFAULT_CONFIG.showNotification)
    const showThemeToggle = ref(SETTING_DEFAULT_CONFIG.showThemeToggle)

    const menuWidth = computed(() =>
      menuOpen.value ? `${menuOpenWidth.value}px` : `${menuCloseWidth.value}px`,
    )

    function setMenuOpen(open: boolean) {
      menuOpen.value = open
    }

    function toggleMenuOpen() {
      menuOpen.value = !menuOpen.value
    }

    function setSystemThemeMode(mode: ThemeMode) {
      systemThemeMode.value = mode
    }

    return {
      menuOpen,
      menuOpenWidth,
      menuCloseWidth,
      uniqueOpened,
      systemThemeMode,
      systemThemeColor,
      showMenuButton,
      showRefreshButton,
      showCrumbs,
      showWorkTab,
      showGlobalSearch,
      showFullscreen,
      showNotification,
      showThemeToggle,
      menuWidth,
      setMenuOpen,
      toggleMenuOpen,
      setSystemThemeMode,
    }
  },
  {
    persist: {
      key: 'elder-care-setting',
      pick: ['menuOpen', 'systemThemeMode', 'systemThemeColor'],
    },
  },
)
