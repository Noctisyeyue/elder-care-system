<template>
  <div>
    <el-form :inline="true" class="search-form">
      <el-form-item>
        <el-input v-model="searchName" placeholder="请输入管家姓名" clearable @clear="handleQuery" style="width: 200px; margin-right: 10px">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />
    <el-table :data="caregivers" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column prop="email" label="邮箱" width="250" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button type="primary" text @click="goToServiceConfig(scope.row)" style="padding: 0">
            设置服务客户
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 20px; text-align: right"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      :page-sizes="[5, 10, 20, 50]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCaregiverList } from '@/api/health'
import { Search } from '@element-plus/icons-vue'
const searchName = ref('')
const caregivers = ref([
  { id: 1, name: '李福星', phone: '2342534634', gender: '男', email: '20226927@stu.neu.edu.cn' },
])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const router = useRouter()

const fetchCaregivers = async () => {
  const res = await getCaregiverList({
    userName: searchName.value,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
  })
  caregivers.value = res.list || []
  total.value = res.total || 0
}

const handleQuery = () => {
  pageNum.value = 1
  fetchCaregivers()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchCaregivers()
}

const handleCurrentChange = (page) => {
  pageNum.value = page
  fetchCaregivers()
}

const goToServiceConfig = (caregiver) => {
  router.push({
    name: 'ServiceConfig',
  })
  if(localStorage.getItem('serviceConfigCaregiverId')){
    localStorage.removeItem('serviceConfigCaregiverId')
  }
  if(localStorage.getItem('serviceConfigCaregiverName')){
    localStorage.removeItem('serviceConfigCaregiverName')
  }
  localStorage.setItem('serviceConfigCaregiverId', caregiver.id)
  localStorage.setItem('serviceConfigCaregiverName', caregiver.name)
}

onMounted(fetchCaregivers)
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 286px);
}
</style>
