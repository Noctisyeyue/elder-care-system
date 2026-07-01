<template>
  <div v-if="shouldShowWorkTab" class="work-tab">
    <div class="tab-scroll" ref="scrollRef">
      <div
        v-for="tab in opened"
        :key="tab.path"
        :class="['tab-item', { active: current.path === tab.path }]"
        @click="onTabClick(tab)"
        @contextmenu.prevent="onContextMenu($event, tab)"
      >
        <span class="tab-title">{{ tab.title }}</span>
        <el-icon
          v-if="!tab.fixedTab"
          class="tab-close"
          @click.stop="closeTab(tab.path)"
        >
          <Close />
        </el-icon>
      </div>
    </div>
    <el-dropdown trigger="click" @command="handleCommand">
      <el-icon class="tab-more"><ArrowDown /></el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="refresh">刷新页面</el-dropdown-item>
          <el-dropdown-item command="closeAll">关闭所有页面</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <ul
      v-show="contextMenu.visible"
      class="context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
    >
      <li @click="closeTab(contextMenu.path)">关闭</li>
      <li @click="closeOther(contextMenu.path)">关闭其他</li>
      <li @click="closeAll">关闭全部</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { Close, ArrowDown } from '@element-plus/icons-vue'
import { useWorktabStore } from '@/stores/worktab'
import { useMenuStore } from '@/stores/menu'
import { useUserStore } from '@/stores/user'
import { useHeaderBar } from '@/hooks/useHeaderBar'
import { layoutChildren } from '@/router/modules'
import { getRouteTitle } from '@/utils/menu'
import type { WorkTab } from '@/types/worktab'

defineOptions({ name: 'WorkTab' })

const route = useRoute()
const router = useRouter()
const worktabStore = useWorktabStore()
const menuStore = useMenuStore()
const userStore = useUserStore()
const { shouldShowWorkTab } = useHeaderBar()
const { opened, current } = storeToRefs(worktabStore)

const scrollRef = ref<HTMLElement>()
const contextMenu = ref({ visible: false, x: 0, y: 0, path: '' })

function getFallbackHome() {
  return menuStore.homePath || (userStore.isCaregiver ? '/home/caregiver' : '/home/admin')
}

function resolveTitle(path: string): string {
  const parts = path.split('/').filter(Boolean)
  let mapPath = path
  if (/\d/.test(path) && parts.length >= 2) {
    mapPath = `/${parts[0]}/${parts[1]}`
  }
  return getRouteTitle(mapPath, layoutChildren) || '未知'
}

function syncTab(path: string) {
  if (path === '/' || path === '') {
    const home = getFallbackHome()
    router.replace(home)
    return
  }
  const title = resolveTitle(path)
  worktabStore.openTab({ title, path, name: route.name as string })
}

watch(
  () => route.path,
  (path) => syncTab(path),
  { immediate: true },
)

function onTabClick(tab: WorkTab) {
  if (tab.path !== route.path) {
    router.push(tab.path)
  }
}

function closeTab(path: string) {
  worktabStore.removeTab(path, getFallbackHome())
}

function closeOther(path: string) {
  worktabStore.closeOtherTabs(path)
  contextMenu.value.visible = false
}

function closeAll() {
  const home = getFallbackHome()
  worktabStore.removeAllTabs(home, resolveTitle(home))
  contextMenu.value.visible = false
}

function handleCommand(cmd: string) {
  if (cmd === 'refresh') {
    router.go(0)
  } else if (cmd === 'closeAll') {
    closeAll()
  }
}

function onContextMenu(e: MouseEvent, tab: WorkTab) {
  contextMenu.value = { visible: true, x: e.clientX, y: e.clientY, path: tab.path }
}

function hideContextMenu() {
  contextMenu.value.visible = false
}

onMounted(() => document.addEventListener('click', hideContextMenu))
onUnmounted(() => document.removeEventListener('click', hideContextMenu))
</script>

<style scoped>
.work-tab {
  display: flex;
  align-items: center;
  padding: 0 12px;
  height: 40px;
  background: var(--default-box-color);
}

.tab-scroll {
  flex: 1;
  display: flex;
  overflow-x: auto;
  gap: 6px;
  scrollbar-width: none;
}

.tab-scroll::-webkit-scrollbar {
  display: none;
}

.tab-item {
  display: flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  background: var(--ui-gray-100);
  color: var(--ui-gray-600);
  transition: background-color 0.2s;
}

.tab-item.active {
  background: var(--ui-active-color);
  color: var(--theme-color);
}

.tab-close {
  margin-left: 6px;
  font-size: 12px;
}

.tab-close:hover {
  color: #f56c6c;
}

.tab-more {
  margin-left: 8px;
  cursor: pointer;
  color: var(--ui-gray-500);
}

.context-menu {
  position: fixed;
  z-index: 3000;
  background: var(--default-box-color);
  border: 1px solid var(--default-border);
  border-radius: 4px;
  padding: 4px 0;
  list-style: none;
  margin: 0;
  min-width: 120px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.context-menu li {
  padding: 8px 16px;
  font-size: 13px;
  cursor: pointer;
}

.context-menu li:hover {
  background: var(--ui-hover-color);
}
</style>
