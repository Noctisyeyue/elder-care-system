<!-- 管理端--子菜单--膳食配置 -->
<template>
  <div class="art-full-height">
    <!-- 搜索和日期选择区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="searchQuery" placeholder="请输入客户姓名搜索" clearable @clear="handleSearch"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="配餐日期">
          <el-date-picker v-model="selectedDate" type="date" placeholder="选择日期" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" :disabled-date="disablePastDates" @change="handleDateChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="openBatchPackageDialog">
            <SvgIcon icon="ri:settings-4-line" :size="16" />
            <span>套餐配置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 客户膳食列表 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">客户膳食列表</span>
        <div class="flex-c" style="gap: 8px;">
          <el-button :type="isMuslimRadio === null ? 'primary' : ''" @click="isMuslimRadio = null; handleSearch()">
            全部
          </el-button>
          <el-button :type="isMuslimRadio === 1 ? 'primary' : ''" @click="isMuslimRadio = 1; handleSearch()">
            清真
          </el-button>
          <el-button :type="isMuslimRadio === 0 ? 'primary' : ''" @click="isMuslimRadio = 0; handleSearch()">
            非清真
          </el-button>
        </div>
      </div>
      <el-table :data="customerDiets" height="100%" stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="nation" label="民族" width="100" />
      <el-table-column label="早餐" min-width="200">
        <template #default="scope">
          <div class="meal-tags">
            <el-tag
              v-for="meal in scope.row.breakfast"
              :key="meal.id"
              class="meal-tag"
              type="success"
            >
              {{ meal.name }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="午餐" min-width="200">
        <template #default="scope">
          <div class="meal-tags">
            <el-tag v-for="meal in scope.row.lunch" :key="meal.id" class="meal-tag" type="success">
              {{ meal.name }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="晚餐" min-width="200">
        <template #default="scope">
          <div class="meal-tags">
            <el-tag v-for="meal in scope.row.dinner" :key="meal.id" class="meal-tag" type="success">
              {{ meal.name }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <template v-if="isDateConfigurable(selectedDate)">
            <el-button type="primary" link @click="openConfigPackageDialog(scope.row)">
              配置套餐
            </el-button>
            <el-button type="danger" link @click="handleRemoveSetMeal(scope.row)">
              移除套餐
            </el-button>
          </template>
          <el-button v-else type="info" link disabled>仅可查看</el-button>
        </template>
      </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </el-card>

    <!-- 膳食配置对话框 -->
    <el-dialog v-model="configDialogVisible" :title="`${selectedCustomer?.customerName} - 膳食配置`"
      width="60%" align-center>
      <div class="meal-config">
        <el-tabs v-model="activeMealType">
          <el-tab-pane label="早餐" name="breakfast">
            <meal-selector
              :selected-meals="selectedCustomer?.breakfast || []"
              :available-meals="availableMeals"
              @update:selected="handleMealSelection('breakfast', $event)"
            />
          </el-tab-pane>
          <el-tab-pane label="午餐" name="lunch">
            <meal-selector
              :selected-meals="selectedCustomer?.lunch || []"
              :available-meals="availableMeals"
              @update:selected="handleMealSelection('lunch', $event)"
            />
          </el-tab-pane>
          <el-tab-pane label="晚餐" name="dinner">
            <meal-selector
              :selected-meals="selectedCustomer?.dinner || []"
              :available-meals="availableMeals"
              @update:selected="handleMealSelection('dinner', $event)"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="saveDietConfig">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量套餐配置弹窗 -->
    <el-dialog v-model="batchPackageDialogVisible" title="批量套餐配置" width="400px" align-center>
      <el-form label-width="80px">
        <el-form-item label="日期">
          <el-date-picker v-model="batchPackageDate" type="date" value-format="YYYY-MM-DD" disabled />
        </el-form-item>
        <el-form-item label="套餐">
          <el-select v-model="selectedPackageId" placeholder="请选择套餐">
            <el-option v-for="pkg in filteredPackagesForBatch" :key="pkg.setMealId" :label="pkg.setMealName" :value="pkg.setMealId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchPackageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBatchPackage">配置</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 单个客户套餐配置弹窗 -->
    <el-dialog v-model="singlePackageDialogVisible" title="套餐配置" width="400px" align-center>
      <el-form label-width="80px">
        <el-form-item label="日期">
          <el-date-picker v-model="selectedDate" type="date" value-format="YYYY-MM-DD" disabled />
        </el-form-item>
        <el-form-item label="套餐">
          <el-select v-model="selectedPackageId" placeholder="请选择套餐">
            <el-option v-for="pkg in filteredPackagesForSingle" :key="pkg.setMealId" :label="pkg.setMealName" :value="pkg.setMealId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="singlePackageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSinglePackage">配置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDietCustomerList, getDietAvailableDishes, saveDishes, saveCustomerSetMeal, removeCustomerSetMeal, getDailySetMealList } from '@/api/diet'
import MealSelector from '@/components/MealSelector.vue'

// 状态定义
const searchQuery = ref('')
function getTodayStr() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
const selectedDate = ref(getTodayStr())
const customerDiets = ref([])
const configDialogVisible = ref(false)
const selectedCustomer = ref(null)
const activeMealType = ref('breakfast')
const availableMeals = ref([])

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 批量套餐配置相关
const batchPackageDialogVisible = ref(false)
const batchPackageDate = ref('')
const batchPackageContent = ref({ breakfast: [], lunch: [], dinner: [] })
const isMuslimRadio = ref(null)
const allPackages = ref([
  { id: 1, name: '标准套餐A', isMuslim: false },
  { id: 2, name: '清真套餐B', isMuslim: true },
  { id: 3, name: '标准套餐C', isMuslim: false },
  { id: 4, name: '清真套餐D', isMuslim: true },
])
const selectedPackageId = ref(null)
const singlePackageDialogVisible = ref(false)

// 新增：每日套餐列表
const dailySetMeals = ref([])

// 方法定义
const disablePastDates = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchCustomerDiets()
}

const handleDateChange = () => {
  currentPage.value = 1
  fetchCustomerDiets()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCustomerDiets()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCustomerDiets()
}

const fetchCustomerDiets = async () => {
  try {
    const data = await getDietCustomerList({
      pork: isMuslimRadio.value,
      date: selectedDate.value,
      customerName: searchQuery.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    })
    customerDiets.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('获取客户膳食数据失败')
  }
}

const fetchAvailableMeals = async () => {
  try {
    const data = await getDietAvailableDishes()
    availableMeals.value = data
  } catch (error) {
    ElMessage.error('获取可用膳食数据失败')
  }
}

const handleConfigDiet = (customer) => {
  // 创建客户数据的深拷贝
  selectedCustomer.value = JSON.parse(JSON.stringify(customer))
  configDialogVisible.value = true
  fetchAvailableMeals()
}

const handleMealSelection = (mealType, selectedMeals) => {
  if (!selectedCustomer.value) return
  // 创建新的数组而不是直接修改
  selectedCustomer.value[mealType] = [...selectedMeals]
}

const handleCancel = () => {
  configDialogVisible.value = false
  selectedCustomer.value = null
}

const saveDietConfig = async () => {
  if (!selectedCustomer.value) return

  // 校验三餐是否都已选择
  const { breakfast, lunch, dinner } = selectedCustomer.value
  if (!breakfast?.length || !lunch?.length || !dinner?.length) {
    ElMessage.warning('请确保早餐、午餐、晚餐都已配置！')
    return
  }

  try {
    await saveDishes({
      customerId: selectedCustomer.value.id,
      date: selectedDate.value,
      breakfast: selectedCustomer.value.breakfast,
      lunch: selectedCustomer.value.lunch,
      dinner: selectedCustomer.value.dinner,
    })
    ElMessage.success('膳食配置保存成功')
    configDialogVisible.value = false
    fetchCustomerDiets()
  } catch (error) {
    ElMessage.error('保存膳食配置失败')
  }
}

// 在 script setup 部分添加日期检查函数
const isDateConfigurable = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const targetDate = new Date(date)
  targetDate.setHours(0, 0, 0, 0)

  const oneWeekLater = new Date(today)
  oneWeekLater.setDate(today.getDate() + 7)

  return targetDate >= today && targetDate <= oneWeekLater
}

// 生命周期钩子
onMounted(() => {
  fetchCustomerDiets()
})

async function handleRemoveSetMeal(row) {
  try {
    await ElMessageBox.confirm(`确定要移除 ${row.customerName} 在 ${selectedDate.value} 的套餐配置吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await removeCustomerSetMeal(row.customerId, selectedDate.value)
    ElMessage.success('套餐配置已移除')
    fetchCustomerDiets()
  } catch {
    ElMessage.error('移除套餐配置失败')
  }
}

// 批量套餐配置相关
const multipleSelection = ref([])
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 判断是否为回族
const isMuslim = (nation) => nation && nation.includes('回')

const filteredPackagesForBatch = computed(() => {
  if (multipleSelection.value.length === 0) return []
  const isAllMuslim = multipleSelection.value.every(item => isMuslim(item.nation))
  const isAllNonMuslim = multipleSelection.value.every(item => !isMuslim(item.nation))
  if (isAllMuslim) return dailySetMeals.value.filter(pkg => pkg.pork === '1' || pkg.pork === 1)
  if (isAllNonMuslim) return dailySetMeals.value.filter(pkg => pkg.pork === '0' || pkg.pork === 0)
  return []
})

const filteredPackagesForSingle = computed(() => {
  if (!selectedCustomer.value) return []
  if (isMuslim(selectedCustomer.value.nation)) {
    return dailySetMeals.value.filter(pkg => pkg.pork === '1' || pkg.pork === 1)
  }
  return dailySetMeals.value.filter(pkg => pkg.pork === '0' || pkg.pork === 0)
})

async function openBatchPackageDialog() {
  if (!multipleSelection.value.length) {
    ElMessage.info('请先选择要配置套餐的客户')
    return
  }
  const isAllMuslim = multipleSelection.value.every(item => isMuslim(item.nation))
  const isAllNonMuslim = multipleSelection.value.every(item => !isMuslim(item.nation))
  if (!(isAllMuslim || isAllNonMuslim)) {
    ElMessage.warning('回族和非回族不能统一配置套餐')
    return
  }
  batchPackageDate.value = selectedDate.value
  selectedPackageId.value = null
  await fetchDailySetMeals(selectedDate.value)
  batchPackageDialogVisible.value = true
}

async function confirmBatchPackage() {
  if (!selectedPackageId.value) {
    ElMessage.warning('请选择套餐')
    return
  }
  const customerIds = multipleSelection.value.map(item => item.customerId)
  try {
    await saveCustomerSetMeal({
      setMealId: selectedPackageId.value,
      customerIds,
      date: batchPackageDate.value,
    })
    ElMessage.success('批量套餐配置成功')
    batchPackageDialogVisible.value = false
    fetchCustomerDiets()
  } catch (error) {
    ElMessage.error('批量套餐配置失败')
  }
}

async function openConfigPackageDialog(row) {
  selectedCustomer.value = row
  selectedPackageId.value = null
  await fetchDailySetMeals(selectedDate.value)
  singlePackageDialogVisible.value = true
}

async function confirmSinglePackage() {
  if (!selectedPackageId.value) {
    ElMessage.warning('请选择套餐')
    return
  }
  try {
    await saveCustomerSetMeal({
      setMealId: selectedPackageId.value,
      customerIds: [selectedCustomer.value.customerId],
      date: selectedDate.value,
    })
    ElMessage.success('套餐配置成功')
    singlePackageDialogVisible.value = false
    fetchCustomerDiets()
  } catch (error) {
    ElMessage.error('套餐配置失败')
  }
}

// 获取每日套餐
const fetchDailySetMeals = async (date) => {
  try {
    const data = await getDailySetMealList(date)
    dailySetMeals.value = data || []
    console.log(dailySetMeals.value)
  } catch (error) {
    ElMessage.error('获取套餐列表失败')
  }
}
</script>

<style scoped>
.search-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.search-input {
  width: 220px;
}

.muslim-radio {
  margin-left: 20px;
}

.meal-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.meal-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.meal-config {
  padding: 20px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 305px);
}
</style>
