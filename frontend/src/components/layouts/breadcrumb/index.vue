<template>
  <nav v-if="breadcrumbs.length" class="breadcrumb max-lg:!hidden">
    <span
      v-for="(item, index) in breadcrumbs"
      :key="index"
      class="breadcrumb-item"
      :class="{ active: index === breadcrumbs.length - 1 }"
    >
      <span v-if="index > 0" class="separator">/</span>
      {{ item }}
    </span>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { layoutChildren } from '@/router/modules'
import { getBreadcrumbTitles } from '@/utils/menu'

defineOptions({ name: 'Breadcrumb' })

const route = useRoute()

const breadcrumbs = computed(() => getBreadcrumbTitles(route.path, layoutChildren))
</script>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  margin-left: 12px;
  font-size: 14px;
  color: var(--ui-gray-500);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.breadcrumb-item.active {
  color: var(--ui-gray-800);
}

.separator {
  margin: 0 6px;
  color: var(--ui-gray-400);
}
</style>
