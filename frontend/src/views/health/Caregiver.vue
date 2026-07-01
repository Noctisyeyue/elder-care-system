<!-- 管理端--子菜单--设置服务对象 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="护工姓名">
          <el-input v-model="searchName" placeholder="请输入护工姓名" clearable @clear="handleQuery"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">护工列表</span>
      </div>
      <el-table :data="caregivers" height="100%" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="gender" label="性别" />
        <el-table-column prop="email" label="邮箱" min-width="250" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link @click="goToServiceConfig(scope.row)">
              <SvgIcon icon="ri:user-settings-line" :size="16" />
              <span>设置服务客户</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCaregiverList } from '@/api/health'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const router = useRouter()
const searchName = ref('')
const caregivers = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function handleQuery() {
  pageNum.value = 1
  fetchCaregivers()
}

async function fetchCaregivers() {
  const res = await getCaregiverList({
    userName: searchName.value,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
  })
  caregivers.value = res.list || []
  total.value = res.total || 0
}

function handleSizeChange(size) { pageSize.value = size; pageNum.value = 1; fetchCaregivers() }
function handleCurrentChange(page) { pageNum.value = page; fetchCaregivers() }

function goToServiceConfig(row) {
  localStorage.setItem('serviceConfigCaregiverId', row.id)
  localStorage.setItem('serviceConfigCaregiverName', row.name)
  router.push('/service/config')
}

onMounted(fetchCaregivers)
</script>

<style scoped>
/* 样式由全局管理 */
</style>
