<template>
  <button type="button" class="icon-btn" :title="title" @click="$emit('click')">
    <SvgIcon :icon="icon" :size="18" />
  </button>
</template>

<script setup lang="ts">
import SvgIcon from '@/components/base/svg-icon/index.vue'

defineOptions({ name: 'IconButton' })

defineProps<{
  icon: string
  title?: string
}>()

defineEmits<{ click: [] }>()
</script>

<style scoped>
.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 6px;
  background: transparent;
  cursor: pointer;
  color: var(--ui-gray-600);
  transition: background-color 0.2s;
}

.icon-btn:hover {
  background: var(--ui-hover-color);
  color: var(--theme-color);
}
</style>
