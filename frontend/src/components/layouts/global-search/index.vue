<template>
  <el-dialog v-model="visible" title="搜索菜单" width="480px" :show-close="true">
    <el-input
      v-model="keyword"
      placeholder="输入菜单名称..."
      clearable
      autofocus
      @input="onSearch"
    >
      <template #prefix>
        <SvgIcon icon="ri:search-line" />
      </template>
    </el-input>
    <ul class="search-results">
      <li
        v-for="item in results"
        :key="item.path"
        class="search-item"
        @click="goTo(item.path)"
      >
        <SvgIcon v-if="item.meta?.icon" :icon="item.meta.icon" :size="16" />
        <span>{{ item.meta?.title }}</span>
        <span class="search-path">{{ item.path }}</span>
      </li>
      <li v-if="keyword && results.length === 0" class="empty">无匹配结果</li>
    </ul>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useMenuStore } from '@/stores/menu'
import { flattenMenus } from '@/utils/menu'
import { mittBus } from '@/utils/mitt'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import type { AppRouteRecord } from '@/types/router'

defineOptions({ name: 'GlobalSearch' })

const router = useRouter()
const menuStore = useMenuStore()
const { menuList } = storeToRefs(menuStore)

const visible = ref(false)
const keyword = ref('')
const results = ref<AppRouteRecord[]>([])

const flatMenus = computed(() => flattenMenus(menuList.value))

function onSearch() {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) {
    results.value = flatMenus.value.slice(0, 10)
    return
  }
  results.value = flatMenus.value.filter((item) =>
    item.meta?.title?.toLowerCase().includes(kw),
  )
}

function goTo(path: string) {
  visible.value = false
  keyword.value = ''
  router.push(path)
}

function open() {
  visible.value = true
  onSearch()
}

function onKeydown(e: KeyboardEvent) {
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
    e.preventDefault()
    open()
  }
}

onMounted(() => {
  mittBus.on('openSearch', open)
  document.addEventListener('keydown', onKeydown)
})

onUnmounted(() => {
  mittBus.off('openSearch', open)
  document.removeEventListener('keydown', onKeydown)
})

defineExpose({ open })
</script>

<style scoped>
.search-results {
  list-style: none;
  margin: 12px 0 0;
  padding: 0;
  max-height: 320px;
  overflow-y: auto;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.search-item:hover {
  background: var(--ui-hover-color);
}

.search-path {
  margin-left: auto;
  font-size: 12px;
  color: var(--ui-gray-500);
}

.empty {
  text-align: center;
  padding: 20px;
  color: var(--ui-gray-500);
}
</style>
