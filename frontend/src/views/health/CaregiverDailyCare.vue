<!-- 护工端--子菜单--日常护理 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户姓名" clearable @clear="handleSearch"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
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
      <el-table :data="customerList" height="100%" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="tel" label="联系电话" min-width="140" />
        <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="goToCareItems(row)">
              <SvgIcon icon="ri:nurse-line" :size="14" />
              <span>客户护理</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyCustomers } from '@/api/customer'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const router = useRouter()
const searchForm = reactive({ customerName: '', pageNum: 1, pageSize: 10 })
const customerList = ref([])
const total = ref(0)
const loading = ref(false)

async function fetchCustomers() {
  loading.value = true
  try {
    const res = await getMyCustomers(searchForm)
    customerList.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  searchForm.pageNum = 1
  fetchCustomers()
}

function handlePageChange(page) {
  searchForm.pageNum = page
  fetchCustomers()
}

function handleSizeChange(size) {
  searchForm.pageSize = size
  searchForm.pageNum = 1
  fetchCustomers()
}

function goToCareItems(row) {
  localStorage.removeItem('caregiverCustomerCareItemsCustomerId')
  localStorage.setItem('caregiverCustomerCareItemsCustomerId', row.id)
  router.push('/service/items')
}

onMounted(fetchCustomers)
</script>

<style scoped>
/* 样式由全局管理 */
</style>
