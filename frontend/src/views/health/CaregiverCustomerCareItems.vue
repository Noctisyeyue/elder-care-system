<template>
  <div>
    <!-- 搜索与返回栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item>
        <el-input v-model="searchForm.itemName" placeholder="请输入项目名称" clearable @clear="handleSearch"
          style="width: 200px; margin-right: 10px">
          <template #prefix>
            <el-icon>
              <Search />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="info" @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />

    <!-- 护理项目表格 -->
    <el-table :data="nursingItems" border style="width: 100%" max-height="600" v-loading="loading"
      element-loading-text="加载中..." :empty-text="loading ? '加载中...' : '暂无护理项目'">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户" width="120" />
      <el-table-column prop="code" label="项目编号" width="120" />
      <el-table-column prop="name" label="项目名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="price" label="价格" width="120" />
      <el-table-column prop="remain" label="剩余次数" width="120" />
      <el-table-column prop="expireDate" label="服务到期时间" width="120">
        <template #default="{ row }">
          {{ formatDate(row.expireDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="150">
        <template #default="{ row }">
          <div class="status-tags">
            <el-tag :type="new Date(row.expireDate) < new Date() ? 'danger' : 'primary'" size="small">
              {{ new Date(row.expireDate) < new Date() ? '到期' : '未到期' }} </el-tag>
                <el-tag :type="row.remain <= 0 ? 'danger' : 'primary'" size="small">
                  {{ row.remain <= 0 ? '欠费' : '数量正常' }} </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openNursingDialog(scope.row)">添加护理</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination layout="total, sizes, prev, pager, next, jumper" :total="total" :page-size="pageSize"
        :current-page="pageNum" :page-sizes="[5, 10, 20, 50]" @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </div>

    <!-- 护理记录弹窗 -->
    <el-dialog v-model="nursingDialogVisible" title="添加护理记录" width="30%">
      <el-form :model="nursingForm" label-width="100px">
        <el-form-item label="护理时间" required>
          <el-date-picker v-model="nursingForm.nursingTime" type="date" value-format="YYYY-MM-DD" disabled />
        </el-form-item>
        <el-form-item label="已护理次数" required>
          <el-input-number v-model="nursingForm.times" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nursingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNursingRecord">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCustomerList, getMyCustomers } from '@/api/customer'
import { getPurchasedItems, addNursingRecord } from '@/api/health'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const customerId = localStorage.getItem('caregiverCustomerCareItemsCustomerId')
const allNursingItems = ref([]) // 全部护理项目
const nursingItems = ref([]) // 当前页护理项目
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const nursingDialogVisible = ref(false)
const nursingForm = reactive({
  nursingTime: '',
  times: 1,
  itemId: '',
  itemName: '',
  itemCode: '',
  itemPrice: '',
})
const searchForm = reactive({
  itemName: '',
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const updateNursingItemsPage = () => {
  const start = (pageNum.value - 1) * pageSize.value
  const end = start + pageSize.value
  nursingItems.value = allNursingItems.value.slice(start, end)
  total.value = allNursingItems.value.length
}

const fetchNursingItems = async () => {
  loading.value = true
  try {
    const res = await getPurchasedItems({
      customerId,
      pageNum: 1,
      pageSize: 9999,
      itemName: searchForm.itemName,
    })
    allNursingItems.value = res.list || []
    pageNum.value = 1
    updateNursingItemsPage()
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  pageNum.value = val
  updateNursingItemsPage()
}
const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  updateNursingItemsPage()
}

const handleSearch = () => {
  fetchNursingItems()
}

const openNursingDialog = (item) => {
  const isExpired = new Date(item.expireDate) < new Date()
  const isOwed = item.remain <= 0
  if (isExpired || isOwed) {
    ElMessage.error('该护理项目已到期或欠费，请联系管理员续费')
    return
  }
  nursingForm.nursingTime = new Date().toISOString().slice(0, 10)
  nursingForm.times = 1
  nursingForm.itemId = item.id
  nursingForm.itemName = item.name
  nursingForm.itemCode = item.code
  nursingForm.itemPrice = item.price
  nursingDialogVisible.value = true
}

const submitNursingRecord = async () => {
  if (!nursingForm.nursingTime || !nursingForm.times) {
    ElMessage.warning('请填写完整信息')
    return
  }
  await addNursingRecord({
    customerId,
    itemId: nursingForm.itemId,
    name: nursingForm.itemName,
    code: nursingForm.itemCode,
    price: nursingForm.itemPrice,
    nursingTime: nursingForm.nursingTime,
    times: nursingForm.times,
  })
  ElMessage.success('护理记录添加成功')
  nursingDialogVisible.value = false
  fetchNursingItems()
}

const goBack = () => {
  router.back()
}

onMounted(fetchNursingItems)
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}

.pagination-container {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.el-table {
  margin-bottom: 20px;
}

.status-tags {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
}
</style>
