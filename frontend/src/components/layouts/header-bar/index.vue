<template>
  <header class="header-bar" :class="{ scrolled: isScrolled }">
    <div class="header-top">
      <div class="header-left">
        <IconButton
          v-if="shouldShowMenuButton"
          :icon="menuOpen ? 'ri:menu-fold-line' : 'ri:menu-unfold-line'"
          title="折叠菜单"
          @click="settingStore.toggleMenuOpen()"
        />
        <IconButton
          v-if="shouldShowRefreshButton"
          icon="ri:refresh-line"
          title="刷新"
          class="max-sm:!hidden"
          @click="reload"
        />
        <Breadcrumb v-if="shouldShowBreadcrumb" />
      </div>

      <div
        v-if="shouldShowGlobalSearch"
        class="search-box max-md:!hidden"
        @click="openSearch"
      >
        <SvgIcon icon="ri:search-line" :size="14" />
        <span>搜索菜单</span>
        <kbd>Ctrl+K</kbd>
      </div>

      <div class="header-right">
        <IconButton
          v-if="shouldShowFullscreen"
          :icon="isFullscreen ? 'ri:fullscreen-exit-line' : 'ri:fullscreen-fill'"
          title="全屏"
          class="max-md:!hidden"
          @click="toggleFullscreen"
        />
        <Notification v-if="shouldShowNotification" />
        <div v-if="shouldShowChat" class="chat-btn-wrap" @click="openChat">
          <IconButton icon="ri:message-3-line" title="AI 助手" />
          <span class="bg-success-dot chat-dot"></span>
        </div>
        <IconButton
          v-if="shouldShowThemeToggle"
          :icon="isDark ? 'ri:sun-fill' : 'ri:moon-line'"
          title="切换主题"
          @click="toggleTheme"
        />
        <UserMenu />
      </div>
    </div>
    <WorkTab />
    <GlobalSearch ref="searchRef" />
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useFullscreen } from '@vueuse/core'
import { useSettingStore } from '@/stores/setting'
import { useTheme } from '@/hooks/useTheme'
import { useHeaderBar } from '@/hooks/useHeaderBar'
import { mittBus } from '@/utils/mitt'
import IconButton from '@/components/base/icon-button/index.vue'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import Breadcrumb from '@/components/layouts/breadcrumb/index.vue'
import WorkTab from '@/components/layouts/work-tab/index.vue'
import Notification from '@/components/layouts/notification/index.vue'
import GlobalSearch from '@/components/layouts/global-search/index.vue'
import UserMenu from './widget/UserMenu.vue'

defineOptions({ name: 'HeaderBar' })

const router = useRouter()
const settingStore = useSettingStore()
const { menuOpen } = storeToRefs(settingStore)
const { toggleTheme, isDark: getIsDark } = useTheme()
const isDark = computed(() => getIsDark())

const {
  shouldShowMenuButton,
  shouldShowRefreshButton,
  shouldShowBreadcrumb,
  shouldShowGlobalSearch,
  shouldShowFullscreen,
  shouldShowNotification,
  shouldShowChat,
  shouldShowThemeToggle,
} = useHeaderBar()

const { isFullscreen, toggle: toggleFullscreen } = useFullscreen()
const searchRef = ref<InstanceType<typeof GlobalSearch>>()
const isScrolled = ref(false)

function reload() {
  router.go(0)
}

function openSearch() {
  mittBus.emit('openSearch')
}

function openChat() {
  mittBus.emit('openChat')
}

function onScroll() {
  isScrolled.value = window.scrollY > 0
}

onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.header-bar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--default-box-color);
  border-bottom: 1px solid var(--default-border);
  transition: box-shadow 0.2s;
}

.header-bar.scrolled {
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: var(--header-height);
  padding: 0 16px;
  gap: 12px;
  border-bottom: 1px solid var(--default-border);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-left {
  flex: 1;
  min-width: 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  height: 36px;
  border: 1px solid var(--ui-gray-400);
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  color: var(--ui-gray-500);
  min-width: 160px;
}

.search-box kbd {
  margin-left: auto;
  font-size: 11px;
  padding: 2px 6px;
  border: 1px solid var(--ui-gray-400);
  border-radius: 4px;
  background: var(--default-bg-color);
}

.chat-btn-wrap {
  position: relative;
  display: inline-flex;
}

.chat-dot {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  animation: breathing 2s ease-in-out infinite;
}

@keyframes breathing {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}
</style>
