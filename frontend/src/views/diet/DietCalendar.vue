<template>
  <div>
    <el-calendar v-model="selectedDate">
      <template #header="{ date }">
        <span style="font-size: 20px">{{ date }}</span>
        <el-button-group>
          <el-button type="primary" @click="selectDate('prev-month')">上个月</el-button>
          <el-button type="primary" @click="selectDate('today')">今天</el-button>
          <el-button type="primary" @click="selectDate('next-month')">下个月</el-button>
        </el-button-group>
      </template>
      <template #date-cell="{ data }">
        <div class="date-cell" @click="handleDayClick(data.day)">
          <p :class="data.isSelected ? 'is-selected' : ''">
            {{ data.day.split('-').slice(2).join('') }}
          </p>
          <div v-if="dailyMeals[data.day] && dailyMeals[data.day].setMeals" class="meal-content">
            <div v-for="pkg in dailyMeals[data.day].setMeals" :key="pkg.setMealId">
              <el-tag size="small" :type="(pkg.pork==='1'||pkg.pork===1)?'success':'info'">
                {{ pkg.name }}
              </el-tag>
            </div>
          </div>
        </div>
      </template>
    </el-calendar>

    <el-dialog
      v-model="dialogVisible"
      :title="`${selectedDay} 膳食套餐配置`"
      width="50%"
      @close="resetForm"
    >
      <el-form :model="mealForm" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker
            v-model="mealForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            disabled
          />
        </el-form-item>
        <el-form-item label="清真套餐">
          <el-select
            v-model="mealForm.muslimPackageId"
            filterable
            placeholder="请选择清真套餐"
            style="width: 100%"
          >
            <el-option
              v-for="pkg in muslimPackages"
              :key="pkg.setMealId"
              :label="pkg.name"
              :value="pkg.setMealId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="非清真套餐">
          <el-select
            v-model="mealForm.nonMuslimPackageId"
            filterable
            placeholder="请选择非清真套餐"
            style="width: 100%"
          >
            <el-option
              v-for="pkg in nonMuslimPackages"
              :key="pkg.setMealId"
              :label="pkg.name"
              :value="pkg.setMealId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="package-preview-row">
        <div class="package-preview-block">
          <div class="package-title">清真套餐</div>
          <div class="package-meals-scroll">
            <template v-if="selectedMuslimPackage">
              <div class="package-meal-group">
                <div class="package-meal-title">早餐</div>
                <div class="package-meal-list">
                  <div v-for="item in selectedMuslimPackage.breakfast" :key="item.dishId" class="meal-item">
                    <img :src="item.img" class="meal-img" />
                    <div class="meal-name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
              <div class="package-meal-group">
                <div class="package-meal-title">午餐</div>
                <div class="package-meal-list">
                  <div v-for="item in selectedMuslimPackage.lunch" :key="item.dishId" class="meal-item">
                    <img :src="item.img" class="meal-img" />
                    <div class="meal-name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
              <div class="package-meal-group">
                <div class="package-meal-title">晚餐</div>
                <div class="package-meal-list">
                  <div v-for="item in selectedMuslimPackage.dinner" :key="item.dishId" class="meal-item">
                    <img :src="item.img" class="meal-img" />
                    <div class="meal-name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
            </template>
            <div v-else class="package-empty">请选择清真套餐</div>
          </div>
        </div>
        <div class="package-preview-block">
          <div class="package-title">非清真套餐</div>
          <div class="package-meals-scroll">
            <template v-if="selectedNonMuslimPackage">
              <div class="package-meal-group">
                <div class="package-meal-title">早餐</div>
                <div class="package-meal-list">
                  <div v-for="item in selectedNonMuslimPackage.breakfast" :key="item.dishId" class="meal-item">
                    <img :src="item.img" class="meal-img" />
                    <div class="meal-name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
              <div class="package-meal-group">
                <div class="package-meal-title">午餐</div>
                <div class="package-meal-list">
                  <div v-for="item in selectedNonMuslimPackage.lunch" :key="item.dishId" class="meal-item">
                    <img :src="item.img" class="meal-img" />
                    <div class="meal-name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
              <div class="package-meal-group">
                <div class="package-meal-title">晚餐</div>
                <div class="package-meal-list">
                  <div v-for="item in selectedNonMuslimPackage.dinner" :key="item.dishId" class="meal-item">
                    <img :src="item.img" class="meal-img" />
                    <div class="meal-name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
            </template>
            <div v-else class="package-empty">请选择非清真套餐</div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveMealConfig">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { get, post } from '@/utils/request'

const selectedDate = ref(new Date())
const dialogVisible = ref(false)
const selectedDay = ref('')
const dialogReadOnly = ref(false)

const mealForm = ref({
  date: '',
  muslimPackageId: null,
  nonMuslimPackageId: null,
})

const dailyMeals = ref({})
const foodOptions = ref([])

const muslimPackages = ref([])
const nonMuslimPackages = ref([])

const selectedMuslimPackage = computed(() => {
  return muslimPackages.value.find(pkg => pkg.setMealId === mealForm.value.muslimPackageId)
})
const selectedNonMuslimPackage = computed(() => {
  return nonMuslimPackages.value.find(pkg => pkg.setMealId === mealForm.value.nonMuslimPackageId)
})

onMounted(() => {
  fetchMealsForMonth(selectedDate.value)
  fetchAllPackages()
})

watch(selectedDate, (newDate, oldDate) => {
  if (
    newDate.getMonth() !== oldDate.getMonth() ||
    newDate.getFullYear() !== oldDate.getFullYear()
  ) {
    fetchMealsForMonth(newDate)
  }
})

const fetchMealsForMonth = async (date) => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const data = await get('/diet/monthList', { year, month })
  // data: [{date, setMeals: [...]}, ...]
  dailyMeals.value = data.reduce((acc, day) => {
    acc[day.date] = day
    return acc
  }, {})
}

const fetchAllPackages = async () => {
  // 获取所有套餐，分清真和非清真
  const [muslimRes, nonMuslimRes] = await Promise.all([
    get('/diet/setMeal/list', { pork: '1' }),
    get('/diet/setMeal/list', { pork: '0' })
  ])
  muslimPackages.value = (muslimRes.records || [])
  nonMuslimPackages.value = (nonMuslimRes.records || [])
}

const handleDayClick = async (day) => {
  selectedDay.value = day
  await fetchAllPackages()
  // 预填套餐id
  const dayData = dailyMeals.value[day]
  let muslimId = null, nonMuslimId = null
  if (dayData && dayData.setMeals) {
    for (const pkg of dayData.setMeals) {
      if (pkg.pork === '1' || pkg.pork === 1) muslimId = pkg.setMealId
      if (pkg.pork === '0' || pkg.pork === 0) nonMuslimId = pkg.setMealId
    }
  }
  mealForm.value = {
    date: day,
    muslimPackageId: muslimId,
    nonMuslimPackageId: nonMuslimId,
  }
  dialogVisible.value = true
}

const saveMealConfig = async () => {
  try {
    const dataToSend = {
      date: mealForm.value.date,
      huiSetMealId: mealForm.value.muslimPackageId,
      notHuiSetMealId: mealForm.value.nonMuslimPackageId,
    }
    await post('/diet/saveDietCalendar', dataToSend)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchMealsForMonth(selectedDate.value)
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  mealForm.value = {
    date: '',
    muslimPackageId: null,
    nonMuslimPackageId: null,
  }
}

const selectDate = (type) => {
  const date = selectedDate.value
  if (type === 'prev-month') {
    selectedDate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1)
  } else if (type === 'next-month') {
    selectedDate.value = new Date(date.getFullYear(), date.getMonth() + 1, 1)
  } else {
    selectedDate.value = new Date()
  }
}
</script>

<style scoped>
:deep(.el-calendar-table .el-calendar-day) {
  height: 10em;
}

.date-cell {
  height: 100%;
  padding: 8px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  cursor: pointer;
}

.date-cell p {
  margin: 0;
  text-align: right;
  font-size: 14px;
}

.date-cell .is-selected {
  color: #409eff;
}

.meal-content {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
  text-align: left !important;
  white-space: normal;
  text-overflow: clip;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  overflow-y: auto;
  height: 144px;
}

/* 隐藏滚动条（仅支持 Webkit 浏览器） */
.meal-content::-webkit-scrollbar {
  display: none;
}

.meal-tag,
.el-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.el-calendar__header .el-button-group .el-button--primary {
  background-color: #fff;
  color: #409eff;
  border: 1px solid #409eff !important;
  border-radius: 0 !important;
}

.el-calendar__header .el-button-group .el-button--primary:hover,
.el-calendar__header .el-button-group .el-button--primary:focus {
  background-color: #e6f7ff !important;
  color: #409eff !important;
  border-color: #409eff !important;
}

.el-calendar__header .el-button-group .el-button--primary + .el-button--primary {
  margin-left: 0 !important;
}

.el-calendar__header .el-button-group .el-button--primary:first-child {
  border-top-left-radius: var(--el-border-radius-base) !important;
  border-bottom-left-radius: var(--el-border-radius-base) !important;
}

.el-calendar__header .el-button-group .el-button--primary:last-child {
  border-top-right-radius: var(--el-border-radius-base) !important;
  border-bottom-right-radius: var(--el-border-radius-base) !important;
}

.package-preview-row {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}
.package-preview-block {
  flex: 1;
  background: #fafbfc;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px 8px 8px 8px;
  min-width: 220px;
  max-height: 260px;
  display: flex;
  flex-direction: column;
}
.package-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}
.package-meals-scroll {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}
.package-meal-group {
  margin-bottom: 8px;
}
.package-meal-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}
.package-meal-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.meal-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 64px;
  margin-bottom: 4px;
}
.meal-img {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 2px;
  background: #f5f5f5;
}
.meal-name {
  font-size: 12px;
  text-align: center;
  color: #333;
}
.package-empty {
  color: #bbb;
  text-align: center;
  margin-top: 32px;
}
</style>
