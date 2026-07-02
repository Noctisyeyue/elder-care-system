<!-- 管理端--子菜单--膳食日历 -->
<template>
  <div class="diet-calendar-page">
    <el-calendar v-model="selectedDate">
      <template #header="{ date }">
        <div class="calendar-header">
          <span class="calendar-title">{{ date }}</span>
          <el-button-group>
            <el-button type="primary" @click="selectDate('prev-month')">
              <SvgIcon icon="ri:arrow-left-s-line" :size="16" />
              <span>上个月</span>
            </el-button>
            <el-button type="primary" @click="selectDate('today')">
              <SvgIcon icon="ri:calendar-line" :size="16" />
              <span>今天</span>
            </el-button>
            <el-button type="primary" @click="selectDate('next-month')">
              <SvgIcon icon="ri:arrow-right-s-line" :size="16" />
              <span>下个月</span>
            </el-button>
          </el-button-group>
        </div>
      </template>
      <template #date-cell="{ data }">
        <div class="date-cell" @click="handleDayClick(data.day)">
          <p :class="{ 'is-selected': data.isSelected, 'is-today': data.day === todayStr }">
            {{ data.day.split('-').slice(2).join('') }}
          </p>
          <div v-if="dailyMeals[data.day] && dailyMeals[data.day].setMeals" class="meal-content">
            <div v-for="pkg in dailyMeals[data.day].setMeals" :key="pkg.setMealId">
              <div class="meal-tag" :class="(pkg.pork==='1'||pkg.pork===1)?'tag-muslim':'tag-normal'">
                {{ pkg.name }}
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-calendar>

    <el-dialog
      v-model="dialogVisible"
      :title="`${selectedDay} 膳食套餐配置`"
      width="600px"
      align-center
      @close="resetForm"
    >
      <!-- 表单区域 -->
      <div class="form-card">
        <el-form ref="mealFormRef" :model="mealForm" :rules="mealRules" label-width="100px">
          <el-form-item label="日期">
            <el-date-picker
              v-model="mealForm.date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              disabled
            />
          </el-form-item>
          <div class="form-row">
            <el-form-item label="清真套餐" prop="muslimPackageId">
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
            <el-form-item label="非清真套餐" prop="nonMuslimPackageId">
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
          </div>
        </el-form>
      </div>

      <!-- 套餐预览区域 -->
      <div class="preview-section">
        <div class="section-title">套餐详情预览</div>
        <div class="package-preview-row">
          <div class="package-preview-block">
            <div class="package-title muslim-title">清真套餐</div>
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
            <div class="package-title normal-title">非清真套餐</div>
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
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveMealConfig">保存配置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getDietMonthList, getSetMealList, saveDietCalendar } from '@/api/diet'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const selectedDate = ref(new Date())
const dialogVisible = ref(false)
const selectedDay = ref('')
const dialogReadOnly = ref(false)

const mealFormRef = ref(null)
const mealForm = ref({
  date: '',
  muslimPackageId: null,
  nonMuslimPackageId: null,
})

/** 套餐选择校验规则 */
const mealRules = {
  muslimPackageId: [{ required: true, message: '请选择清真套餐', trigger: 'change' }],
  nonMuslimPackageId: [{ required: true, message: '请选择非清真套餐', trigger: 'change' }],
}

const dailyMeals = ref({})
const foodOptions = ref([])

const muslimPackages = ref([])
const nonMuslimPackages = ref([])

/** 今日日期字符串，用于高亮。 */
const todayStr = computed(() => {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
})

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

/**
 * 获取指定月份的膳食数据。
 *
 * @param date 日期
 */
const fetchMealsForMonth = async (date) => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const data = await getDietMonthList(year, month)
  dailyMeals.value = data.reduce((acc, day) => {
    acc[day.date] = day
    return acc
  }, {})
}

/** 获取全部套餐列表。 */
const fetchAllPackages = async () => {
  const [muslimRes, nonMuslimRes] = await Promise.all([
    getSetMealList({ pork: '1' }),
    getSetMealList({ pork: '0' })
  ])
  muslimPackages.value = (muslimRes.records || [])
  nonMuslimPackages.value = (nonMuslimRes.records || [])
}

/**
 * 点击日期单元格。
 *
 * @param day 日期字符串
 */
const handleDayClick = async (day) => {
  selectedDay.value = day
  await fetchAllPackages()
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

/** 保存膳食配置。 */
const saveMealConfig = async () => {
  if (!mealFormRef.value) return
  try {
    await mealFormRef.value.validate()
  } catch { return }
  try {
    const dataToSend = {
      date: mealForm.value.date,
      huiSetMealId: mealForm.value.muslimPackageId,
      notHuiSetMealId: mealForm.value.nonMuslimPackageId,
    }
    await saveDietCalendar(dataToSend)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchMealsForMonth(selectedDate.value)
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

/** 重置表单。 */
const resetForm = () => {
  mealForm.value = {
    date: '',
    muslimPackageId: null,
    nonMuslimPackageId: null,
  }
}

/**
 * 切换月份或回到今天。
 *
 * @param type 操作类型
 */
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
.diet-calendar-page {
  padding: 4px;
  height: 100%;
  overflow-y: auto;
}

/* 日历头部 */
.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 0 8px;
}

.calendar-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--ui-gray-800);
}

/* 日历单元格 */
:deep(.el-calendar-table .el-calendar-day) {
  height: 110px;
  padding: 0;
  transition: all 0.2s ease;
}

:deep(.el-calendar-table .el-calendar-day:hover) {
  background-color: var(--ui-hover-color);
}

:deep(.el-calendar__button-group .el-button-group) {
  border-radius: 8px;
  overflow: hidden;
}

.date-cell {
  height: 100%;
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.date-cell p {
  margin: 0 0 8px 0;
  text-align: right;
  font-size: 14px;
  font-weight: 500;
  color: var(--ui-gray-700);
  line-height: 22px;
  height: 22px;
}

.date-cell .is-selected {
  color: var(--theme-color);
  font-weight: 600;
}

.date-cell .is-today {
  display: inline-block;
  width: 22px;
  height: 22px;
  line-height: 22px;
  text-align: center;
  border-radius: 50%;
  background-color: var(--theme-color);
  color: #fff !important;
  font-weight: 600;
}

/* 套餐标签 */
.meal-content {
  flex: 1;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  overflow-y: auto;
}

.meal-content::-webkit-scrollbar {
  width: 4px;
}
.meal-content::-webkit-scrollbar-thumb {
  background: var(--ui-gray-300);
  border-radius: 2px;
}

.meal-tag {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.5;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-muslim {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border: 1px solid rgba(34, 197, 94, 0.2);
}

.tag-normal {
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

/* 弹窗表单卡片 */
.form-card {
  background: var(--art-gray-100);
  border-radius: 8px;
  padding: 16px 20px 4px 20px;
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 20px;
}

/* 预览区域 */
.preview-section .section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--ui-gray-800);
  margin-bottom: 12px;
  padding-left: 4px;
  border-left: 3px solid var(--theme-color);
}

.package-preview-row {
  display: flex;
  gap: 16px;
}

.package-preview-block {
  flex: 1;
  border-radius: 8px;
  border: 1px solid var(--default-border);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: 300px;
}

.package-title {
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.muslim-title {
  background: linear-gradient(90deg, #22c55e, #16a34a);
}

.normal-title {
  background: linear-gradient(90deg, #3b82f6, #2563eb);
}

.package-meals-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  background: var(--default-box-color);
}

.package-meals-scroll::-webkit-scrollbar {
  width: 4px;
}
.package-meals-scroll::-webkit-scrollbar-thumb {
  background: var(--ui-gray-300);
  border-radius: 2px;
}

.package-meal-group {
  margin-bottom: 14px;
}

.package-meal-group:last-child {
  margin-bottom: 0;
}

.package-meal-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--ui-gray-700);
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px dashed var(--ui-gray-200);
}

.package-meal-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.meal-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 60px;
  transition: transform 0.2s ease;
}

.meal-item:hover {
  transform: translateY(-2px);
}

.meal-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 4px;
  background: var(--ui-gray-100);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);
}

.meal-name {
  font-size: 12px;
  text-align: center;
  color: var(--ui-gray-700);
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.package-empty {
  color: var(--ui-gray-400);
  text-align: center;
  margin-top: 60px;
  font-size: 13px;
}
</style>
