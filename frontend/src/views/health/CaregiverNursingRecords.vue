<!-- 护工端--子菜单--护理记录 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="queryName" placeholder="请输入客户姓名" clearable @clear="fetchCustomerList"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchCustomerList">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">服务客户列表</span>
      </div>
      <el-table :data="customerList" height="100%" stripe style="width: 100%" v-loading="customerLoading"
        element-loading-text="加载中...">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="tel" label="联系电话" min-width="140" />
        <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewNursingRecords(scope.row)">
              <SvgIcon icon="ri:file-list-3-line" :size="14" />
              <span>查看护理记录</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="page"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </el-card>

    <!-- 护理记录弹窗 -->
    <el-dialog v-model="nursingRecordDialogVisible" title="护理记录" width="900px" align-center>
      <div v-if="currentCustomer">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="客户姓名">{{ currentCustomer.customerName }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentCustomer.age }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentCustomer.gender }}</el-descriptions-item>
          <el-descriptions-item label="楼号">{{ currentCustomer.buildingNumber }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentCustomer.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="床号">{{ currentCustomer.bedNumber }}</el-descriptions-item>
          <el-descriptions-item label="护理级别">{{ currentCustomer.nursingLevel }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 20px;">护理记录列表</h4>
        <el-table :data="nursingRecords" stripe style="width: 100%" v-loading="nursingLoading"
          element-loading-text="加载中...">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="护理项目编号" min-width="120" />
          <el-table-column prop="name" label="护理项目名称" min-width="150" />
          <el-table-column prop="times" label="护理数量" width="100" />
          <el-table-column prop="nursingStaff" label="护理人员" min-width="100" />
          <el-table-column prop="staffPhone" label="护理人员手机" min-width="140" />
          <el-table-column prop="nursingTime" label="护理时间" min-width="180" />
        </el-table>
        <el-pagination class="table-pagination" background style="justify-content: center; padding-top: 10px;"
          v-model:current-page="nursingPage"
          v-model:page-size="nursingPageSize"
          layout="total, prev, pager, next, sizes, jumper"
          :total="nursingTotal"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleNursingSizeChange"
          @current-change="handleNursingPageChange" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyCustomers } from '@/api/customer'
import { getNursingRecordList } from '@/api/nursing'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const queryName = ref('')
const customerList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const nursingRecordDialogVisible = ref(false)
const currentCustomer = ref(null)
const nursingRecords = ref([])
const nursingPage = ref(1)
const nursingPageSize = ref(10)
const nursingTotal = ref(0)
const customerLoading = ref(false)
const nursingLoading = ref(false)

// 护工端只查询自己负责的客户。
async function fetchCustomerList() {
  customerLoading.value = true
  try {
    const res = await getMyCustomers({ customerName: queryName.value, pageNum: page.value, pageSize: pageSize.value })
    customerList.value = res.list || []
    total.value = res.total || 0
  } finally {
    customerLoading.value = false
  }
}

function handlePageChange(val) {
  page.value = val
  fetchCustomerList()
}

function handleSizeChange(size) {
  pageSize.value = size
  page.value = 1
  fetchCustomerList()
}

async function viewNursingRecords(customer) {
  currentCustomer.value = customer
  nursingPage.value = 1
  nursingRecordDialogVisible.value = true
  await fetchNursingRecords()
}

async function fetchNursingRecords() {
  if (!currentCustomer.value) return
  nursingLoading.value = true
  try {
    const res = await getNursingRecordList({
      customerId: currentCustomer.value.id, pageNum: nursingPage.value, pageSize: nursingPageSize.value,
    })
    nursingRecords.value = res.list || []
    nursingTotal.value = res.total || 0
  } finally {
    nursingLoading.value = false
  }
}

function handleNursingPageChange(val) {
  nursingPage.value = val
  fetchNursingRecords()
}

function handleNursingSizeChange(size) {
  nursingPageSize.value = size
  nursingPage.value = 1
  fetchNursingRecords()
}

onMounted(fetchCustomerList)
</script>

<style scoped>
/* 样式由全局管理 */
</style>
