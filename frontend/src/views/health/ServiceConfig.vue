<template>
  <div>
    <el-form :inline="true" @submit.prevent>
      <el-form-item>
        <el-input v-model="customerSearchName" placeholder="客户姓名" clearable style="width: 200px; margin-right: 10px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleNoCaregiverQuery">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
    <div style="display: flex; gap: 20px; margin-top: 20px">
      <!-- 无管家客户 -->
      <div style="flex: 1">
        <div class="table-title">无管家客户信息</div>
        <el-table :data="noCaregiverCustomers">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="customerName" label="姓名" />
          <el-table-column prop="age" label="年龄" />
          <el-table-column prop="gender" label="性别" />
          <el-table-column prop="bedNumber" label="床号" />
          <el-table-column prop="nursingLevel" label="护理级别" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="primary" text @click="addCustomer(scope.row)" style="padding: 0"
                >添加</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          style="margin-top: 20px; display: flex; justify-content: flex-end;"
          layout="total, sizes, prev, pager, next, jumper"
          :total="noCaregiverTotal"
          :page-size="noCaregiverPageSize"
          :current-page="noCaregiverPageNum"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleNoCaregiverSizeChange"
          @current-change="handleNoCaregiverCurrentChange"
        />
      </div>
      <!-- 当前管家服务的客户 -->
      <div style="flex: 1">
        <div class="table-title">{{ caregiverName }}服务的客户</div>
        <el-table :data="myCustomers">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="customerName" label="姓名" />
          <el-table-column prop="age" label="年龄" />
          <el-table-column prop="gender" label="性别" />
          <el-table-column prop="bedNumber" label="床号" />
          <el-table-column prop="nursingLevel" label="护理级别" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" text @click="removeCustomer(scope.row)" style="padding: 0"
                >移除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          style="margin-top: 20px; display: flex; justify-content: flex-end;"
          layout="total, sizes, prev, pager, next, jumper"
          :total="myCustomersTotal"
          :page-size="myCustomersPageSize"
          :current-page="myCustomersPageNum"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleMyCustomersSizeChange"
          @current-change="handleMyCustomersCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { getCustomersNoCaregiver, setCaregiverCustomers, getCaregiverCustomers } from '@/api/health'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const caregiverId = localStorage.getItem('serviceConfigCaregiverId')
const caregiverName = localStorage.getItem('serviceConfigCaregiverName')

const customerSearchName = ref('')

// 全量数据
const allNoCaregiverCustomers = ref([]) // 全部无管家客户
const allMyCustomers = ref([]) // 全部我的客户
const noCaregiverCustomers = ref([]) // 当前页无管家客户
const myCustomers = ref([]) // 当前页我的客户
const noCaregiverPageNum = ref(1)
const noCaregiverPageSize = ref(10)
const noCaregiverTotal = ref(0)
const myCustomersPageNum = ref(1)
const myCustomersPageSize = ref(10)
const myCustomersTotal = ref(0)

const isChanged = ref(false)

// 获取全部无管家客户（带查询条件）
const fetchNoCaregiverCustomers = async () => {
  const res = await getCustomersNoCaregiver({
    customerName: customerSearchName.value,
    pageNum: 1,
    pageSize: 9999,
  })
  allNoCaregiverCustomers.value = res.list || []
  noCaregiverPageNum.value = 1
  updateNoCaregiverPage()
}

// 获取全部我的客户
const fetchMyCustomers = async () => {
  const res = await getCaregiverCustomers({
    caregiverId,
    pageNum: 1,
    pageSize: 9999,
  })
  allMyCustomers.value = res.list || []
  myCustomersPageNum.value = 1
  updateMyCustomersPage()
}

// 更新无管家客户当前页
const updateNoCaregiverPage = () => {
  const start = (noCaregiverPageNum.value - 1) * noCaregiverPageSize.value
  const end = start + noCaregiverPageSize.value
  noCaregiverCustomers.value = allNoCaregiverCustomers.value.slice(start, end)
  noCaregiverTotal.value = allNoCaregiverCustomers.value.length
}

// 更新我的客户当前页
const updateMyCustomersPage = () => {
  const start = (myCustomersPageNum.value - 1) * myCustomersPageSize.value
  const end = start + myCustomersPageSize.value
  myCustomers.value = allMyCustomers.value.slice(start, end)
  myCustomersTotal.value = allMyCustomers.value.length
}

const handleNoCaregiverQuery = () => {
  if (!customerSearchName.value.trim()) {
    ElMessage.warning('请输入查询信息')
    return
  }
  fetchNoCaregiverCustomers()
}

const handleNoCaregiverCurrentChange = (page) => {
  noCaregiverPageNum.value = page
  updateNoCaregiverPage()
}

const handleMyCustomersCurrentChange = (page) => {
  myCustomersPageNum.value = page
  updateMyCustomersPage()
}

const handleNoCaregiverSizeChange = (size) => {
  noCaregiverPageSize.value = size
  noCaregiverPageNum.value = 1
  updateNoCaregiverPage()
}
const handleMyCustomersSizeChange = (size) => {
  myCustomersPageSize.value = size
  myCustomersPageNum.value = 1
  updateMyCustomersPage()
}

const addCustomer = (customer) => {
  // 从无管家客户中移除，添加到myCustomers
  allNoCaregiverCustomers.value = allNoCaregiverCustomers.value.filter((c) => c.id !== customer.id)
  allMyCustomers.value.push(customer)
  updateNoCaregiverPage()
  updateMyCustomersPage()
  isChanged.value = true
}

const removeCustomer = (customer) => {
  // 从myCustomers中移除，添加到无管家客户
  allMyCustomers.value = allMyCustomers.value.filter((c) => c.id !== customer.id)
  allNoCaregiverCustomers.value.push(customer)
  updateNoCaregiverPage()
  updateMyCustomersPage()
  isChanged.value = true
}

// 保存操作：将myCustomers的id列表提交到后端
const handleSave = async () => {
  const customerIds = allMyCustomers.value.map((c) => c.id)
  await setCaregiverCustomers({ caregiverId, customerIds })
  isChanged.value = false
  ElMessage.success('保存成功！')
}

const goBack = () => {
  router.back()
}

const handleBeforeUnload = (e) => {
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

onUnmounted(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

onBeforeRouteLeave((to, from, next) => {
  if (isChanged.value) {
    ElMessageBox.confirm('有未保存的变更，确定要离开吗？', '提示', {
      type: 'warning',
    })
      .then(() => {
        next()
      })
      .catch(() => {
        next(false)
      })
  } else {
    next()
  }
})
</script>

<style scoped>
.table-title {
  color: #48484d;
  padding: 8px;
  margin-bottom: 8px;
  font-weight: bold;
}
</style>
