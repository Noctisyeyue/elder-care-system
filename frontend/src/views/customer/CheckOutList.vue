<template>
  <div>
    <!-- 客户姓名查询 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item>
        <el-input
          v-model="searchForm.customerName"
          placeholder="请输入客户姓名"
          clearable
          @clear="handleSearch"
          style="width: 200px; margin-right: 10px"
          ><template #prefix>
            <el-icon><Search /></el-icon> </template
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="info" @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />

    <!-- 退住申请列表 -->
    <h3 style="margin-bottom: 10px">我的退住申请</h3>
    <el-table
      :data="checkOutList"
      border
      style="width: 100%"
      v-loading="loading"
      element-loading-text="加载中..."
      :empty-text="loading ? '加载中...' : '暂无退住申请记录'"
    >
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="customerName" label="客户姓名" width="100"></el-table-column>
      <el-table-column prop="checkOutType" label="退住类型" width="100"></el-table-column>
      <el-table-column
        prop="checkOutReason"
        label="退住原因"
        show-overflow-tooltip
      ></el-table-column>
      <el-table-column
        prop="checkOutDate"
        label="退住时间"
        width="120"
        :formatter="formatDate"
      ></el-table-column>
      <el-table-column prop="approvalStatus" label="审批状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.approvalStatus)">
            {{ row.approvalStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.approvalStatus === '已提交'"
            type="danger"
            size="small"
            @click="cancelApplication(row)"
          >
            撤销申请
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination
        class="pagination-right"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="searchForm.pageSize"
        :current-page="searchForm.pageNum"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchCheckOutList(); }"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post } from '@/utils/request'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 查询表单数据
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

// 退住申请列表数据
const checkOutList = ref([
  {
    id: 1,
    customerName: '李福星',
    checkOutType: '正常退住',
    checkOutReason: '牛逼',
    checkOutDate: '2025-06-21',
    approvalStatus: '通过',
  },
  {
    id: 2,
    customerName: '李福星',
    checkOutType: '正常退住',
    checkOutReason: '牛逼',
    checkOutDate: '2025-06-21',
    approvalStatus: '不通过',
  },
  {
    id: 3,
    customerName: '李福星',
    checkOutType: '正常退住',
    checkOutReason: '牛逼',
    checkOutDate: '2025-06-21',
    approvalStatus: '已撤销',
  },
  {
    id: 4,
    customerName: '李福星',
    checkOutType: '正常退住',
    checkOutReason: '牛逼',
    checkOutDate: '2025-06-21',
    approvalStatus: '已提交',
  },
])
const total = ref(0)
const loading = ref(false)

// 获取退住申请列表
const fetchCheckOutList = async () => {
  loading.value = true
  try {
    const response = await get('/customer/checkout/myApplications', {
      pageNum: searchForm.pageNum,
      pageSize: searchForm.pageSize,
      customerName: searchForm.customerName,
    })
    checkOutList.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取退住申请列表失败:', error)
    ElMessage.error('获取退住申请列表失败')
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchCheckOutList()
}

// 分页变化
const handlePageChange = (page) => {
  searchForm.pageNum = page
  fetchCheckOutList()
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case '已提交':
      return 'warning'
    case '通过':
      return 'success'
    case '不通过':
      return 'danger'
    default:
      return 'info'
  }
}

// 格式化日期，只显示年月日
const formatDate = (row, column, cellValue) => {
  if (!cellValue) return ''
  return cellValue.split(' ')[0]
}

// 撤销申请
const cancelApplication = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销此退住申请吗？', '确认撤销', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const response = await post(`/customer/checkout/cancel/${row.id}`)
    ElMessage.success('申请撤销成功')
    fetchCheckOutList() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销申请失败:', error)
      if (error.message) {
        ElMessage.error(error.message)
      } else {
        ElMessage.error('撤销申请失败，请重试')
      }
    }
  }
}

// 返回上一页
const goBack = () => {
  router.push('/customer/checkOutApply')
}

// 页面加载时获取数据
onMounted(() => {
  fetchCheckOutList()
})
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 18px;
}

.el-date-picker {
  width: 100%;
}

.el-tag {
  margin-right: 5px;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 360px);
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
