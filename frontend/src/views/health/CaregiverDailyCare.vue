<template>
  <div>
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item>
        <el-input
          v-model="searchForm.customerName"
          placeholder="请输入客户姓名"
          clearable
          @clear="handleSearch"
          style="width: 200px; margin-right: 10px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />

    <!-- 客户信息表格 -->
    <el-table :data="customerList" border style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户姓名" min-width="100" />
      <el-table-column prop="age" label="年龄" min-width="60" />
      <el-table-column prop="gender" label="性别" min-width="60" />
      <el-table-column prop="buildingNumber" label="所属楼房" min-width="100" />
      <el-table-column prop="roomNumber" label="房间号" min-width="80" />
      <el-table-column prop="bedNumber" label="床号" min-width="60" />
      <el-table-column prop="tel" label="联系电话" min-width="140" />
      <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="goToCareItems(row)">客户护理</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pagination-right"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="searchForm.pageSize"
      :current-page="searchForm.pageNum"
      :page-sizes="[5, 10, 20, 50]"
      @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchCustomers(); }"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyCustomers } from '@/api/customer'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})
const customerList = ref([
  {
    id: 1,
    customerName: '李福星',
    age: 10,
    gender: '男',
    bedNumber: '1001-1',
    nursingLevel: '一级',
  },
])
const total = ref(0)
const loading = ref(false)

const fetchCustomers = async () => {
  loading.value = true
  try {
    const res = await getMyCustomers(searchForm)
    customerList.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchCustomers()
}
const handlePageChange = (page) => {
  searchForm.pageNum = page
  fetchCustomers()
}
const goToCareItems = (row) => {
  router.push({
    path: '/service/items',
  })
  if(localStorage.getItem('caregiverCustomerCareItemsCustomerId')){
    localStorage.removeItem('caregiverCustomerCareItemsCustomerId')
  }
  localStorage.setItem('caregiverCustomerCareItemsCustomerId', row.id)
}
onMounted(fetchCustomers)
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
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
