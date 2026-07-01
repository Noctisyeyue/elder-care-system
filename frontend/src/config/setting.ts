/** 主题模式 */
export enum ThemeMode {
  LIGHT = 'light',
  DARK = 'dark',
  AUTO = 'auto',
}

/** 布局默认配置 */
export const SETTING_DEFAULT_CONFIG = {
  menuOpen: true,
  menuOpenWidth: 230,
  menuCloseWidth: 64,
  uniqueOpened: true,
  systemThemeMode: ThemeMode.LIGHT,
  systemThemeColor: '#409EFF',
  showMenuButton: true,
  showRefreshButton: true,
  showCrumbs: true,
  showWorkTab: true,
  showGlobalSearch: true,
  showFullscreen: true,
  showNotification: true,
  showChat: true,
  showThemeToggle: true,
}

export function getSettingDefaults() {
  return { ...SETTING_DEFAULT_CONFIG }
}
