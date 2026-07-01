<!-- 护工端--子菜单--退住申请--查看退住申请 -->
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
          <el-button @click="goBack">
            <SvgIcon icon="ri:arrow-left-line" :size="16" />
            <span>返回</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">我的退住申请</span>
      </div>
      <el-table :data="checkOutList" height="100%" stripe style="width: 100%" v-loading="loading" element-loading-text="加载中..."
        :empty-text="loading ? '加载中...' : '暂无退住申请记录'">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" width="100" />
        <el-table-column prop="checkOutType" label="退住类型" width="100" />
        <el-table-column prop="checkOutReason" label="退住原因" show-overflow-tooltip />
        <el-table-column prop="checkOutDate" label="退住时间" width="120" :formatter="formatDate" />
        <el-table-column prop="approvalStatus" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.approvalStatus)">
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.approvalStatus === '已提交'" type="danger" size="small" @click="cancelApplication(row)">
              撤销申请
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
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchCheckOutList(); }"
        @current-change="handlePageChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyCheckOutApplications, cancelCheckOut } from '@/api/customer'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

/** 查询表单。 */
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

/** 退住申请列表。 */
const checkOutList = ref([])
const total = ref(0)
const loading = ref(false)

/**
 * 查询退住申请列表。
 */
const fetchCheckOutList = async () => {
  loading.value = true
  try {
    const response = await getMyCheckOutApplications({
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

/** 执行查询。 */
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchCheckOutList()
}

/** 处理分页变化。 */
const handlePageChange = (page) => {
  searchForm.pageNum = page
  fetchCheckOutList()
}

/** 获取状态标签类型。 */
const getStatusType = (status) => {
  switch (status) {
    case '已提交': return 'warning'
    case '通过': return 'success'
    case '不通过': return 'danger'
    default: return 'info'
  }
}

/** 格式化日期。 */
const formatDate = (row, column, cellValue) => {
  if (!cellValue) return ''
  return cellValue.split(' ')[0]
}

/** 撤销申请。 */
const cancelApplication = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销此退住申请吗？', '确认撤销', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await cancelCheckOut(row.id)
    ElMessage.success('申请撤销成功')
    fetchCheckOutList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销申请失败:', error)
      ElMessage.error(error.message || '撤销申请失败，请重试')
    }
  }
}

/** 返回上一页。 */
const goBack = () => {
  router.push('/customer/checkOutApply')
}

onMounted(() => {
  fetchCheckOutList()
})
</script>

<style scoped>
/* 样式由 el-ui.scss + app.scss 全局管理 */
</style>
