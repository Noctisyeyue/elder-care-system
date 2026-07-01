<template>
  <template v-if="item.children && item.children.length">
    <el-sub-menu :index="item.path">
      <template #title>
        <SvgIcon v-if="item.meta?.icon" :icon="item.meta.icon" class="menu-icon" />
        <span>{{ item.meta?.title }}</span>
      </template>
      <SidebarSubmenu
        v-for="child in item.children"
        :key="child.path"
        :item="child"
      />
    </el-sub-menu>
  </template>
  <el-menu-item v-else :index="item.path" @click="handleClick">
    <SvgIcon v-if="item.meta?.icon" :icon="item.meta.icon" class="menu-icon" />
    <template #title>{{ item.meta?.title }}</template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { AppRouteRecord } from '@/types/router'
import SvgIcon from '@/components/base/svg-icon/index.vue'

defineOptions({ name: 'SidebarSubmenu' })

const props = defineProps<{
  item: AppRouteRecord
}>()

const router = useRouter()

function handleClick() {
  if (props.item.path) {
    router.push(props.item.path)
  }
}
</script>

<style scoped>
.menu-icon {
  margin-right: 8px;
  flex-shrink: 0;
}
</style>
