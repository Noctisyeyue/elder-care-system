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
    <el-table
      :data="customerList"
      border
      style="width: 100%; margin-bottom: 20px"
      v-loading="customerLoading"
      element-loading-text="加载中..."
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户姓名" min-width="100" />
      <el-table-column prop="age" label="年龄" min-width="60" />
      <el-table-column prop="gender" label="性别" min-width="60" />
      <el-table-column prop="buildingNumber" label="楼号" width="90" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="bedNumber" label="床号" width="80" />
      <el-table-column prop="tel" label="联系电话" min-width="140" />
      <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewNursingRecords(scope.row)"
            >查看护理记录</el-button
          >
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

    <!-- 护理记录弹窗 -->
    <el-dialog v-model="nursingRecordDialogVisible" title="护理记录" width="1000px">
      <div v-if="currentCustomer">
        <h4>客户信息</h4>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="客户姓名">{{
            currentCustomer.customerName
          }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentCustomer.age }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentCustomer.gender }}</el-descriptions-item>
          <el-descriptions-item label="楼号">{{ currentCustomer.buildingNumber }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentCustomer.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="床号">{{ currentCustomer.bedNumber }}</el-descriptions-item>
          <el-descriptions-item label="护理级别">{{
            currentCustomer.nursingLevel
          }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 20px">护理记录列表</h4>
        <el-table
          :data="nursingRecords"
          border
          style="width: 100%; margin-bottom: 20px"
          v-loading="nursingLoading"
          element-loading-text="加载中..."
        >
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="护理项目编号" min-width="120" />
          <el-table-column prop="name" label="护理项目名称" min-width="150" />
          <el-table-column prop="times" label="护理数量" min-width="100" />
          <el-table-column prop="nursingStaff" label="护理人员" min-width="100" />
          <el-table-column prop="staffPhone" label="护理人员手机" min-width="140" />
          <el-table-column prop="nursingTime" label="护理时间" min-width="180" />
        </el-table>
        <!-- 护理记录分页 -->
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="nursingTotal"
          :page-size="nursingPageSize.value"
          :current-page="nursingPage"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleNursingSizeChange"
          @current-change="handleNursingPageChange"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerList, getMyCustomers } from '@/api/customer'
import { getNursingRecordList } from '@/api/nursing'
import { Search } from '@element-plus/icons-vue'
// 查询参数
const queryName = ref('')
const customerList = ref([
  {
    id: 1,
    customerName: '李福星',
    age: 18,
    gender: '男',
    buildingNumber: '06',
    roomNumber: '1001',
    bedNumber: '1',
    nursingLevel: '一级 ',
  },
])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 护理记录相关
const nursingRecordDialogVisible = ref(false)
const currentCustomer = ref(null)
const nursingRecords = ref([
  {
    id: 1,
    code: '#1234',
    name: '踩背',
    times: 2,
    nursingStaff: '大大力',
    staffPhone: '1343254234',
    nursingTime: '2025-06-21',
  },
])
const nursingPage = ref(1)
const nursingPageSize = ref(10)
const nursingTotal = ref(0)

const customerLoading = ref(false)
const nursingLoading = ref(false)

// 查询客户列表
const fetchCustomerList = async () => {
  customerLoading.value = true
  try {
    const res = await getMyCustomers({
      customerName: queryName.value,
      pageNum: page.value,
      pageSize: pageSize.value,
    })
    customerList.value = res.list || []
    total.value = res.total || 0
  } finally {
    customerLoading.value = false
  }
}

// 分页切换
const handlePageChange = (val) => {
  page.value = val
  fetchCustomerList()
}

// 查看护理记录
const viewNursingRecords = async (customer) => {
  currentCustomer.value = customer
  nursingRecordDialogVisible.value = true
  await fetchNursingRecords()
}

// 获取护理记录列表
const fetchNursingRecords = async () => {
  if (!currentCustomer.value) return
  nursingLoading.value = true
  try {
    const res = await getNursingRecordList({
      customerId: currentCustomer.value.id,
      pageNum: nursingPage.value,
      pageSize: nursingPageSize.value,
    })
    nursingRecords.value = res.list || []
    nursingTotal.value = res.total || 0
  } finally {
    nursingLoading.value = false
  }
}

// 护理记录分页切换
const handleNursingPageChange = (val) => {
  nursingPage.value = val
  fetchNursingRecords()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  fetchCustomerList()
}

const handleNursingSizeChange = (size) => {
  nursingPageSize.value = size
  nursingPage.value = 1
  fetchNursingRecords()
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
