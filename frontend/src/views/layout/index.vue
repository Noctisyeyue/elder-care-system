<template>
  <div class="app-layout">
    <SidebarMenu />
    <main class="app-main">
      <HeaderBar />
      <PageContent />
    </main>
    <GlobalComponent />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { buildMenuList } from '@/utils/menu'
import SidebarMenu from '@/components/layouts/sidebar-menu/index.vue'
import HeaderBar from '@/components/layouts/header-bar/index.vue'
import PageContent from '@/components/layouts/page-content/index.vue'
import GlobalComponent from '@/components/layouts/global-component/index.vue'

defineOptions({ name: 'AppLayout' })

const userStore = useUserStore()
const menuStore = useMenuStore()

onMounted(() => {
  const home =
    userStore.isCaregiver ? '/home/caregiver' : '/home/admin'
  menuStore.setMenuList(buildMenuList(userStore.roleKey), home)
})
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.app-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}
</style>
