import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AppRouteRecord } from '@/types/router'

export const useMenuStore = defineStore('menu', () => {
  const menuList = ref<AppRouteRecord[]>([])
  const homePath = ref('/home/admin')

  function setMenuList(list: AppRouteRecord[], defaultHome?: string) {
    menuList.value = list
    if (defaultHome) {
      homePath.value = defaultHome
    }
  }

  function clearMenuList() {
    menuList.value = []
  }

  return {
    menuList,
    homePath,
    setMenuList,
    clearMenuList,
  }
})
