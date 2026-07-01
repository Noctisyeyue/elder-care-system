<template>
  <aside class="sidebar-menu" :style="{ width: menuWidth }">
    <div class="sidebar-header" @click="goHome">
      <img src="@/assets/logo.svg" alt="logo" class="logo-image" />
      <span v-if="menuOpen" class="logo-text">{{ appName }}</span>
    </div>
    <el-scrollbar class="menu-scroll">
      <el-menu
        :collapse="!menuOpen"
        :default-active="activePath"
        :unique-opened="uniqueOpened"
        :collapse-transition="false"
      >
        <SidebarSubmenu
          v-for="item in menuList"
          :key="item.path"
          :item="item"
        />
      </el-menu>
    </el-scrollbar>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import AppConfig from '@/config'
import { useMenuStore } from '@/stores/menu'
import { useSettingStore } from '@/stores/setting'
import SidebarSubmenu from './widget/SidebarSubmenu.vue'

defineOptions({ name: 'SidebarMenu' })

const route = useRoute()
const router = useRouter()
const menuStore = useMenuStore()
const settingStore = useSettingStore()
const { menuOpen, menuWidth, uniqueOpened } = storeToRefs(settingStore)

const appName = AppConfig.systemInfo.name
const menuList = computed(() => menuStore.menuList)

const activePath = computed(() => {
  const path = route.path
  const parts = path.split('/').filter(Boolean)
  if (parts.length >= 2) {
    return `/${parts[0]}/${parts[1]}`
  }
  return path
})

function goHome() {
  router.push(menuStore.homePath)
}
</script>

<style lang="scss" scoped>
@use './style.scss';
</style>
