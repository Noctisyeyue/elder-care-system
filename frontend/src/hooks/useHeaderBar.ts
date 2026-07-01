import { computed } from 'vue'
import { useSettingStore } from '@/stores/setting'
import { headerBarConfig } from '@/config/headerBar'

export function useHeaderBar() {
  const settingStore = useSettingStore()

  const shouldShowMenuButton = computed(
    () => headerBarConfig.showMenuButton && settingStore.showMenuButton,
  )
  const shouldShowRefreshButton = computed(
    () => headerBarConfig.showRefreshButton && settingStore.showRefreshButton,
  )
  const shouldShowBreadcrumb = computed(
    () => headerBarConfig.showCrumbs && settingStore.showCrumbs,
  )
  const shouldShowWorkTab = computed(
    () => headerBarConfig.showWorkTab && settingStore.showWorkTab,
  )
  const shouldShowGlobalSearch = computed(
    () => headerBarConfig.showGlobalSearch && settingStore.showGlobalSearch,
  )
  const shouldShowFullscreen = computed(
    () => headerBarConfig.showFullscreen && settingStore.showFullscreen,
  )
  const shouldShowNotification = computed(
    () => headerBarConfig.showNotification && settingStore.showNotification,
  )
  const shouldShowChat = computed(() => headerBarConfig.showChat && settingStore.showChat)
  const shouldShowThemeToggle = computed(
    () => headerBarConfig.showThemeToggle && settingStore.showThemeToggle,
  )

  return {
    shouldShowMenuButton,
    shouldShowRefreshButton,
    shouldShowBreadcrumb,
    shouldShowWorkTab,
    shouldShowGlobalSearch,
    shouldShowFullscreen,
    shouldShowNotification,
    shouldShowChat,
    shouldShowThemeToggle,
  }
}
