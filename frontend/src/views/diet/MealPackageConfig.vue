<template>
  <div>
    <!-- 顶部搜索区域 -->
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="请输入套餐名称搜索"
        class="search-input"
        clearable
        @clear="handleSearch"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-radio-group v-model="isMuslim" class="muslim-radio" @change="handleSearch">
        <el-radio :label="null">全部</el-radio>
        <el-radio :label="1">清真套餐</el-radio>
        <el-radio :label="0">非清真套餐</el-radio>
      </el-radio-group>
      <!-- <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        :disabled-date="disablePastDates"
        @change="handleSearch"
      /> -->
      <el-button type="primary" @click="openAddDialog">添加套餐</el-button>
    </div>

    <!-- 套餐表格 -->
    <el-table :data="packageList" style="width: 100%" border>
      <el-table-column prop="name" label="套餐名" min-width="120" />
      <el-table-column label="套餐类型" min-width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.isMuslim==='1'" type="success">清真套餐</el-tag>
          <el-tag v-else type="info">普通套餐</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="早餐" min-width="160">
        <template #default="scope">
          <div class="meal-tags">
            <el-tag v-for="meal in scope.row.breakfast || []" :key="meal.id || meal.name" class="meal-tag" type="success">
              {{ meal.name }}
            </el-tag>
            <span v-if="!(scope.row.breakfast && scope.row.breakfast.length)">-</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="午餐" min-width="160">
        <template #default="scope">
          <div class="meal-tags">
            <el-tag v-for="meal in scope.row.lunch || []" :key="meal.id || meal.name" class="meal-tag" type="success">
              {{ meal.name }}
            </el-tag>
            <span v-if="!(scope.row.lunch && scope.row.lunch.length)">-</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="晚餐" min-width="160">
        <template #default="scope">
          <div class="meal-tags">
            <el-tag v-for="meal in scope.row.dinner || []" :key="meal.id || meal.name" class="meal-tag" type="success">
              {{ meal.name }}
            </el-tag>
            <span v-if="!(scope.row.dinner && scope.row.dinner.length)">-</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 'enabled'" type="success">启用</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180">
        <template #default="scope">
          <el-button type="primary" link @click="handleConfig(scope.row)">配置套餐</el-button>
          <el-button type="warning" link @click="openEditDialog(scope.row)">修改</el-button>
          <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination
        class="pagination-right"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加套餐弹窗 -->
    <el-dialog v-model="addDialogVisible" title="添加套餐" width="400px">
      <el-form label-width="80px">
        <el-form-item label="日期">
          <el-date-picker v-model="selectedDate" type="date" value-format="YYYY-MM-DD" disabled />
        </el-form-item>
        <el-form-item label="套餐名">
          <el-input v-model="addForm.name" placeholder="请输入套餐名" />
        </el-form-item>
        <el-form-item label="套餐类型">
          <el-select v-model="addForm.isMuslim" placeholder="请选择">
            <el-option :value="1" label="清真套餐" />
            <el-option :value="0" label="普通套餐" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 配置套餐对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      :title="`${selectedPackage?.name || ''} - 套餐配置`"
      width="60%"
    >
      <div class="meal-config">
        <el-tabs v-model="activeMealType">
          <el-tab-pane label="早餐" name="breakfast">
            <meal-selector
              :selected-meals="selectedPackage?.breakfast || []"
              :available-meals="availableMeals.breakfast"
              @update:selected="handleMealSelection('breakfast', $event)"
            />
          </el-tab-pane>
          <el-tab-pane label="午餐" name="lunch">
            <meal-selector
              :selected-meals="selectedPackage?.lunch || []"
              :available-meals="availableMeals.lunch"
              @update:selected="handleMealSelection('lunch', $event)"
            />
          </el-tab-pane>
          <el-tab-pane label="晚餐" name="dinner">
            <meal-selector
              :selected-meals="selectedPackage?.dinner || []"
              :available-meals="availableMeals.dinner"
              @update:selected="handleMealSelection('dinner', $event)"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="savePackageConfig">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改套餐弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改套餐" width="400px">
      <el-form label-width="80px">
        <el-form-item label="套餐名">
          <el-input v-model="editForm.name" placeholder="请输入套餐名" />
        </el-form-item>
        <el-form-item label="套餐类型">
          <el-select v-model="editForm.isMuslim" placeholder="请选择" disabled>
            <el-option :value="'1'" label="清真套餐" />
            <el-option :value="'0'" label="普通套餐" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="请选择">
            <el-option value="enabled" label="启用" />
            <el-option value="disabled" label="停用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditSave">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MealSelector from '@/components/MealSelector.vue'
import { getAvailableDishes, getSetMealList, saveSetMealDishes, updateSetMeal, removeSetMeal } from '@/api/diet'

const searchQuery = ref('')
const isMuslim = ref(null)
const selectedDate = ref(new Date().toISOString().split('T')[0])
const packageList = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

const addDialogVisible = ref(false)
const addForm = ref({ name: '', isMuslim: null })

const configDialogVisible = ref(false)
const selectedPackage = ref(null)
const activeMealType = ref('breakfast')
const availableMeals = ref({ breakfast: [], lunch: [], dinner: [] })

const editDialogVisible = ref(false)
const editForm = ref({ name: '', isMuslim: null, status: 'enabled', index: -1 })

const disablePastDates = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPackageList()
}
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchPackageList()
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchPackageList()
}
const openAddDialog = () => {
  addForm.value = { name: '', isMuslim: null }
  addDialogVisible.value = true
}
const handleAdd = async () => {
  if (!addForm.value.name) {
    ElMessage.warning('请输入套餐名')
    return
  }
  if (addForm.value.isMuslim === null) {
    ElMessage.warning('请选择套餐类型')
    return
  }
  try {
    await updateSetMeal({
      pork: addForm.value.isMuslim,
      setMealName: addForm.value.name,
      status: '0',
    })
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    fetchPackageList()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}
const handleConfig = async (pkg) => {
  // 深拷贝套餐，确保breakfast/lunch/dinner里的每个菜品都带有id字段
  selectedPackage.value = {
    ...JSON.parse(JSON.stringify(pkg)),
    breakfast: (pkg.breakfast || []).map(item => ({ ...item, id: item.dishId })),
    lunch: (pkg.lunch || []).map(item => ({ ...item, id: item.dishId })),
    dinner: (pkg.dinner || []).map(item => ({ ...item, id: item.dishId })),
  }
  configDialogVisible.value = true
  // pork: 1 表示清真，0 表示普通
  const pork = (pkg.pork === '清真' || pkg.isMuslim === '清真') ? 1 : 0
  const breakfastRes = await getAvailableDishes('0', pork)
  const lunchRes = await getAvailableDishes('1', pork)
  const dinnerRes = await getAvailableDishes('2', pork)
  availableMeals.value = {
    breakfast: (breakfastRes || []).map(item => ({ ...item, id: item.dishId })),
    lunch: (lunchRes || []).map(item => ({ ...item, id: item.dishId })),
    dinner: (dinnerRes || []).map(item => ({ ...item, id: item.dishId })),
  }
}
const fetchPackageList = async () => {
  try {
    const params = {
      pork: isMuslim.value === null ? "2" : String(isMuslim.value),
      setMealName: searchQuery.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    }
    const res = await getSetMealList(params)
    packageList.value = (res.records || []).map(item => ({
      ...item,
      isMuslim: item.pork === '清真'? '1' : '0',
      status: item.status === '0' ? 'enabled' : 'disabled',
    }))
    total.value = res.total || 0
  } catch (e) {
    ElMessage.error('获取套餐失败')
  }
}
const handleMealSelection = (mealType, selectedMeals) => {
  if (!selectedPackage.value) return
  selectedPackage.value[mealType] = [...selectedMeals]
}
const handleCancel = () => {
  configDialogVisible.value = false
  selectedPackage.value = null
}
const savePackageConfig = async () => {
  if (!selectedPackage.value) return
  const { breakfast, lunch, dinner, setMealId } = selectedPackage.value
  if (!breakfast?.length || !lunch?.length || !dinner?.length) {
    ElMessage.warning('请确保早餐、午餐、晚餐都已配置！')
    return
  }
  try {
    await saveSetMealDishes({
      setMealId: setMealId,
      breakfastIds: breakfast.map(item => item.dishId || item.id),
      lunchIds: lunch.map(item => item.dishId || item.id),
      dinnerIds: dinner.map(item => item.dishId || item.id),
    })
    ElMessage.success('套餐配置保存成功')
    configDialogVisible.value = false
    selectedPackage.value = null
    fetchPackageList()
  } catch (e) {
    ElMessage.error('套餐配置保存失败')
  }
}
const openEditDialog = (row) => {
  const idx = packageList.value.findIndex(pkg => pkg.name === row.name)
  editForm.value = { name: row.name, isMuslim: row.isMuslim, status: row.status, index: idx, setMealId: row.setMealId }
  editDialogVisible.value = true
}
const handleEditSave = async () => {
  if (!editForm.value.name) {
    ElMessage.warning('请输入套餐名')
    return
  }
  if (editForm.value.isMuslim === null) {
    ElMessage.warning('请选择套餐类型')
    return
  }
  if (!editForm.value.status) {
    ElMessage.warning('请选择套餐状态')
    return
  }
  try {
    await updateSetMeal({
      setMealId: editForm.value.setMealId,
      pork: editForm.value.isMuslim,
      setMealName: editForm.value.name,
      status: editForm.value.status === 'enabled' ? '0' : '1',
    })
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchPackageList()
  } catch (e) {
    ElMessage.error('修改失败')
  }
}
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除套餐"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await removeSetMeal(row.setMealId )
      ElMessage.success('删除成功')
      fetchPackageList()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}
onMounted(() => {
  fetchPackageList()
})
</script>

<style scoped>
.search-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: center;
}
.search-input {
  width: 220px;
}
.muslim-radio {
  margin-left: 10px;
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
</style>
