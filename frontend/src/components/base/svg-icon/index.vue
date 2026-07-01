<template>
  <Icon :icon="icon" :width="size" :height="size" :class="className" />
</template>

<script setup lang="ts">
import { Icon } from '@iconify/vue'

defineOptions({ name: 'SvgIcon' })

withDefaults(
  defineProps<{
    icon: string
    size?: number | string
    className?: string
  }>(),
  {
    size: 18,
    className: '',
  },
)
</script>
