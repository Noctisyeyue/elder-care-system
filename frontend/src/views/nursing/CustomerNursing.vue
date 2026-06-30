<template>
  <div>
    <!-- 查询栏 -->
    <el-form :inline="true" class="search-form">
      <el-form-item>
        <el-input
          v-model="queryName"
          placeholder="请输入客户姓名"
          clearable
          @clear="fetchCustomerList"
          style="width: 200px; margin-right: 10px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchCustomerList">查询</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />

    <!-- 客户信息表格 -->
    <el-table :data="customerList" border style="width: 100%; margin-bottom: 20px">
      <el-table-column type="index" label="序号" width="90" />
      <el-table-column prop="customerName" label="客户姓名" width="120" />
      <el-table-column prop="age" label="年龄" width="90" />
      <el-table-column prop="gender" label="性别" width="90" />
      <el-table-column prop="buildingNumber" label="所属楼房" width="120" />
      <el-table-column prop="roomNumber" label="房间号" width="120" />
      <el-table-column prop="bedNumber" label="床号" width="90" />
      <el-table-column prop="tel" label="联系电话"/>
      <el-table-column prop="nursingLevel" label="护理级别" width="120" />
      <el-table-column label="操作" width="240">
        <template #default="scope">
          <div class="operation-buttons">
            <el-button type="primary" size="small" @click="openSetNursingDialog(scope.row)"
              >设置护理级别</el-button
            >
            <el-button
              v-if="scope.row.levelId"
              type="danger"
              size="small"
              @click="removeNursingLevel(scope.row)"
              >移除护理级别</el-button
            >
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pagination-right"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize.value"
      :current-page="page"
      :page-sizes="[5, 10, 20, 50]"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />

    <!-- 设置护理级别弹窗 -->
    <el-dialog v-model="setNursingDialogVisible" title="客户护理设置" width="1200px">
      <el-form label-width="100px">
        <el-form-item label="当前护理级别" v-if="currentCustomer?.levelId">
          <el-tag type="success">{{ currentCustomer.nursingLevel }}</el-tag>
        </el-form-item>
        <el-form-item label="修改护理级别">
          <el-select
            v-model="nursingForm.levelId"
            placeholder="请选择护理级别"
            @change="fetchNursingItems"
          >
            <el-option
              v-for="item in nursingLevelList"
              :key="item.id"
              :label="item.level"
              :value="item.id"
            />
          </el-select>

        </el-form-item>
      </el-form>

      <!-- 当前护理项目列表 -->
      <div v-if="currentCustomer?.levelId" style="margin-bottom: 20px">
        <h4>当前护理项目</h4>
        <el-table :data="currentNursingItems" border style="margin-bottom: 20px">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="frequency" label="执行周次" />
          <el-table-column prop="count" label="执行次数" />
          <el-table-column prop="buyDate" label="购买日期" />
          <el-table-column prop="buyCount" label="购买数量" />
          <el-table-column prop="expireDate" label="到期日期" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.expireDate < getToday() ? 'danger' : 'success'">
                {{ scope.row.expireDate < getToday() ? '已过期' : '使用中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <h4>新增护理项目</h4>
      <el-table :data="nursingForm.items" border style="margin-bottom: 10px">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="code" label="编号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="frequency" label="执行周次" />
        <el-table-column prop="count" label="执行次数" />
        <el-table-column prop="buyDate" label="服务购买日期" width="150">
          <template #default="scope">
            <el-date-picker
              v-model="scope.row.buyDate"
              type="date"
              placeholder="选择日期"
              style="width: 130px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="buyCount" label="购买数量" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.buyCount" :min="0" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="服务到期日期" width="150">
          <template #default="scope">
            <el-date-picker
              v-model="scope.row.expireDate"
              type="date"
              placeholder="选择日期"
              style="width: 130px"
            />
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增护理项目分页组件 -->
      <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="itemTotal"
          :page-size="itemPageSize"
          :current-page="itemPage"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleItemSizeChange"
          @current-change="handleItemPageChange"
          style="margin-top: 10px"
       />
      <template #footer>
        <el-button @click="setNursingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePreSubmitNursingSetting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCustomerList } from '@/api/customer'
import { getNursingLevelList, getNursingLevelItems, getCustomerNursingItems, addCustomerNursingLevel, removeCustomerNursingLevel } from '@/api/nursing'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 查询参数
const queryName = ref('')
const customerList = ref([
  {
    id: 1,
    customerName: '李福星',
    age: 18,
    gender: '男',
    roomNumber: '1001',
    bedNumber: '1',
    buildingNumber: '606',
    contactPhone: '1386663233',
    nursingLevel: '一级 ',
  },
])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 护理级别相关
const nursingLevelList = ref([])
const setNursingDialogVisible = ref(false)
const currentCustomer = ref(null)
const nursingForm = reactive({
  levelId: null,
  items: [],
})
const currentNursingItems = ref([])

// 新增护理级别分页相关变量
const levelPage = ref(1)
const levelPageSize = ref(10)
const levelTotal = ref(0)

// 新增护理项目分页相关变量
const itemPage = ref(1)
const itemPageSize = ref(10)
const itemTotal = ref(0)

// 预提交护理设置
const handlePreSubmitNursingSetting = () => {
  submitNursingSetting()
}

// 查询客户列表
const fetchCustomerList = async () => {
  const res = await getCustomerList({
    customerName: queryName.value,
    pageNum: page.value,
    pageSize: pageSize.value,
  })
  customerList.value = res.records || []
  total.value = res.total || 0
}

// 分页切换
const handlePageChange = (val) => {
  page.value = val
  fetchCustomerList()
}

// 查询启用的护理级别（带分页）
const fetchNursingLevels = async () => {
  const res = await getNursingLevelList({
    status: '启用',
    pageNum: levelPage.value,
    pageSize: levelPageSize.value,
  })

  nursingLevelList.value = res.records || []
  levelTotal.value = res.total || 0
}

// 查询护理级别下的项目（带分页）
const fetchNursingItems = async (levelId = nursingForm.levelId) => {
  if (!levelId) return
  const res = await getNursingLevelItems(levelId)
  nursingForm.items = (res.records || []).map((item) => ({
    ...item,
    buyDate: getToday(),
    buyCount: 1,
    expireDate: getDefaultExpireDate(),
  }))
  itemTotal.value = res.total || 0
}

// 打开设置护理级别弹窗
const openSetNursingDialog = async (customer) => {
  currentCustomer.value = customer
  setNursingDialogVisible.value = true
  nursingForm.levelId = null
  nursingForm.items = []
  levelPage.value = 1 // 弹窗打开时重置分页
  itemPage.value = 1 // 弹窗打开时重置护理项目分页
  await fetchNursingLevels()
  if (customer.levelId) {
    const res = await getCustomerNursingItems(customer.id)
    currentNursingItems.value = res
  }
}

// 提交护理级别设置
const submitNursingSetting = async () => {
  if (!nursingForm.levelId) {
    ElMessage.error('请选择护理级别')
    return
  }
  if (!nursingForm.items.length) {
    ElMessage.error('该护理级别下无护理项目')
    return
  }

  await addCustomerNursingLevel({
    customerId: currentCustomer.value.id,
    levelId: nursingForm.levelId,
    items: nursingForm.items
      .filter(item => item.buyCount > 0)
      .map((item) => ({
        itemId: item.id,
        buyDate: item.buyDate,
        buyCount: item.buyCount,
        expireDate: item.expireDate,
      })),
  })

  ElMessage.success('设置成功')
  setNursingDialogVisible.value = false
  fetchCustomerList()
}

// 移除护理级别
const removeNursingLevel = (customer) => {
  ElMessageBox.confirm('移除后将删除该客户所有当前级别的护理项目，是否继续？', '警告', {
    type: 'warning',
  }).then(async () => {
    await removeCustomerNursingLevel(customer.id, customer.levelId)
    ElMessage.success('移除成功')
    fetchCustomerList()
  })
}

// 获取今天日期
function getToday() {
  const date = new Date()
  return date.toISOString().slice(0, 10)
}
// 默认到期日期（3个月后）
function getDefaultExpireDate() {
  const date = new Date()
  date.setMonth(date.getMonth() + 3)
  return date.toISOString().slice(0, 10)
}

const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  fetchCustomerList()
}

// 分页事件处理
const handleLevelPageChange = (val) => {
  levelPage.value = val
  fetchNursingLevels()
}
const handleLevelSizeChange = (size) => {
  levelPageSize.value = size
  levelPage.value = 1
  fetchNursingLevels()
}

// 新增护理项目分页事件处理
const handleItemPageChange = (val) => {
  itemPage.value = val
  fetchNursingItems()
}
const handleItemSizeChange = (size) => {
  itemPageSize.value = size
  itemPage.value = 1
  fetchNursingItems()
}

onMounted(() => {
  fetchCustomerList()
})
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}
.operation-buttons {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
}
.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 286px);
}
</style>

