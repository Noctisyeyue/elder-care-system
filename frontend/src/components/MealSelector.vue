<template>
  <div class="meal-selector">
    <el-transfer
      v-model="selectedMealIds"
      :data="transferData"
      :titles="['可选膳食', '已选膳食']"
      :props="{
        key: 'id',
        label: 'name',
      }"
      @change="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface Meal {
  id: number
  name: string
  category: string
}

const props = defineProps<{
  selectedMeals: Meal[]
  availableMeals: Meal[]
}>()

const emit = defineEmits<{
  (e: 'update:selected', meals: Meal[]): void
}>()

const selectedMealIds = ref<number[]>([])

// 初始化已选择的膳食ID
watch(
  () => props.selectedMeals,
  (newVal) => {
    selectedMealIds.value = newVal.map((meal) => meal.id)
  },
  { immediate: true },
)

// 转换数据格式为transfer组件所需格式
const transferData = computed(() => {
  return props.availableMeals.map((meal) => ({
    key: meal.id,
    label: meal.name,
    ...meal,
  }))
})

// 处理选择变化
const handleChange = (value: number[]) => {
  const selectedMeals = props.availableMeals.filter((meal) => value.includes(meal.id))
  emit('update:selected', selectedMeals)
}
</script>

<style scoped>
.meal-selector {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}

:deep(.el-transfer) {
  width: 100%;
  display: flex;
  justify-content: center;
}

:deep(.el-transfer-panel) {
  width: 38%;
}

:deep(.el-transfer-panel__body) {
  height: 400px;
}
</style>
