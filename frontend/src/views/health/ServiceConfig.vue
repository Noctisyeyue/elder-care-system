<!-- 管理端--子菜单--设置服务客户 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" @submit.prevent class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="customerSearchName" placeholder="请输入客户姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleNoCaregiverQuery">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
          <el-button @click="goBack">
            <SvgIcon icon="ri:arrow-left-line" :size="16" />
            <span>返回</span>
          </el-button>
          <el-button type="success" @click="handleSave">
            <SvgIcon icon="ri:save-line" :size="16" />
            <span>保存</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 双栏表格 -->
    <div style="display: flex; gap: 16px; flex: 1; min-height: 0;">
      <!-- 无管家客户 -->
      <el-card class="art-table-card" shadow="never" style="flex: 1; margin-top: 0;">
        <div class="table-header">
          <span class="table-header-title">无管家客户信息</span>
        </div>
        <el-table :data="noCaregiverCustomers" height="100%" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="customerName" label="姓名" />
          <el-table-column prop="age" label="年龄" />
          <el-table-column prop="gender" label="性别" />
          <el-table-column prop="buildingNumber" label="楼号" width="90" />
          <el-table-column prop="roomNumber" label="房间号" width="100" />
          <el-table-column prop="bedNumber" label="床号" width="80" />
          <el-table-column prop="nursingLevel" label="护理级别" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="addCustomer(scope.row)">
                <SvgIcon icon="ri:arrow-right-circle-line" :size="16" />
                <span>添加</span>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="table-pagination" background v-model:current-page="noCaregiverPageNum"
          v-model:page-size="noCaregiverPageSize" layout="total, prev, pager, next, sizes, jumper"
          :total="noCaregiverTotal" :page-sizes="[5, 10, 20, 50]" @size-change="handleNoCaregiverSizeChange"
          @current-change="handleNoCaregiverCurrentChange" />
      </el-card>

      <!-- 管家服务的客户 -->
      <el-card class="art-table-card" shadow="never" style="flex: 1; margin-top: 0;">
        <div class="table-header">
          <span class="table-header-title">{{ caregiverName }}服务的客户</span>
        </div>
        <el-table :data="myCustomers" height="100%" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="customerName" label="姓名" />
          <el-table-column prop="age" label="年龄" />
          <el-table-column prop="gender" label="性别" />
          <el-table-column prop="buildingNumber" label="楼号" width="90" />
          <el-table-column prop="roomNumber" label="房间号" width="100" />
          <el-table-column prop="bedNumber" label="床号" width="80" />
          <el-table-column prop="nursingLevel" label="护理级别" />
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="scope">
              <el-button link type="danger" @click="removeCustomer(scope.row)">
                <SvgIcon icon="ri:arrow-left-circle-line" :size="16" />
                <span>移除</span>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="table-pagination" background v-model:current-page="myCustomersPageNum"
          v-model:page-size="myCustomersPageSize" layout="total, prev, pager, next, sizes, jumper"
          :total="myCustomersTotal" :page-sizes="[5, 10, 20, 50]" @size-change="handleMyCustomersSizeChange"
          @current-change="handleMyCustomersCurrentChange" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import {
  getCustomersNoCaregiver,
  setCaregiverCustomers,
  getCaregiverCustomers,
} from '@/api/health'
import { ElMessageBox, ElMessage } from 'element-plus'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const router = useRouter()
const caregiverId = localStorage.getItem('serviceConfigCaregiverId')
const caregiverName = localStorage.getItem('serviceConfigCaregiverName')

const customerSearchName = ref('')
const allNoCaregiverCustomers = ref([])
const allMyCustomers = ref([])
const noCaregiverCustomers = ref([])
const myCustomers = ref([])
const noCaregiverPageNum = ref(1)
const noCaregiverPageSize = ref(10)
const noCaregiverTotal = ref(0)
const myCustomersPageNum = ref(1)
const myCustomersPageSize = ref(10)
const myCustomersTotal = ref(0)
const isChanged = ref(false)

// 左侧列表：当前未分配管家的客户。
async function fetchNoCaregiverCustomers() {
  const res = await getCustomersNoCaregiver({ customerName: customerSearchName.value, pageNum: 1, pageSize: 9999 })
  allNoCaregiverCustomers.value = res.list || []
  noCaregiverPageNum.value = 1
  updateNoCaregiverPage()
}

// 右侧列表：当前护工已经服务的客户。
async function fetchMyCustomers() {
  const res = await getCaregiverCustomers({ caregiverId, pageNum: 1, pageSize: 9999 })
  allMyCustomers.value = res.list || []
  myCustomersPageNum.value = 1
  updateMyCustomersPage()
}

function updateNoCaregiverPage() {
  const start = (noCaregiverPageNum.value - 1) * noCaregiverPageSize.value
  noCaregiverCustomers.value = allNoCaregiverCustomers.value.slice(start, start + noCaregiverPageSize.value)
  noCaregiverTotal.value = allNoCaregiverCustomers.value.length
}

function updateMyCustomersPage() {
  const start = (myCustomersPageNum.value - 1) * myCustomersPageSize.value
  myCustomers.value = allMyCustomers.value.slice(start, start + myCustomersPageSize.value)
  myCustomersTotal.value = allMyCustomers.value.length
}

function handleNoCaregiverQuery() {
  if (!customerSearchName.value.trim()) {
    ElMessage.warning('请输入查询信息')
    return
  }

  fetchNoCaregiverCustomers()
}

function handleNoCaregiverCurrentChange(page) {
  noCaregiverPageNum.value = page
  updateNoCaregiverPage()
}

function handleMyCustomersCurrentChange(page) {
  myCustomersPageNum.value = page
  updateMyCustomersPage()
}

function handleNoCaregiverSizeChange(size) {
  noCaregiverPageSize.value = size
  noCaregiverPageNum.value = 1
  updateNoCaregiverPage()
}

function handleMyCustomersSizeChange(size) {
  myCustomersPageSize.value = size
  myCustomersPageNum.value = 1
  updateMyCustomersPage()
}

function addCustomer(customer) {
  allNoCaregiverCustomers.value = allNoCaregiverCustomers.value.filter(c => c.id !== customer.id)
  allMyCustomers.value.push(customer)
  updateNoCaregiverPage()
  updateMyCustomersPage()
  isChanged.value = true
}

function removeCustomer(customer) {
  allMyCustomers.value = allMyCustomers.value.filter(c => c.id !== customer.id)
  allNoCaregiverCustomers.value.push(customer)
  updateNoCaregiverPage()
  updateMyCustomersPage()
  isChanged.value = true
}

async function handleSave() {
  try {
    // 批量保存当前护工的服务客户，请求体格式与后端 SetCustomersRequest 一致
    await setCaregiverCustomers({
      caregiverId: Number(caregiverId),
      customerIds: allMyCustomers.value.map((c) => c.id),
    })
    isChanged.value = false
    ElMessage.success('保存成功！')
  } catch (error) {
    console.error('保存服务对象失败:', error)
    ElMessage.error('保存服务对象失败')
  }
}

function goBack() {
  router.back()
}

function handleBeforeUnload(e) {
  if (isChanged.value) {
    e.preventDefault()
    e.returnValue = ''
  }
}

onMounted(() => {
  fetchNoCaregiverCustomers()
  fetchMyCustomers()
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onUnmounted(() => window.removeEventListener('beforeunload', handleBeforeUnload))

onBeforeRouteLeave((to, from, next) => {
  if (isChanged.value) {
    ElMessageBox.confirm('有未保存的变更，确定要离开吗？', '提示', { type: 'warning' })
      .then(() => next()).catch(() => next(false))
  } else {
    next()
  }
})
</script>

<style scoped>
/* 样式由全局管理 */
</style>
