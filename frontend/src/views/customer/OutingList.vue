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

    <!-- 外出申请列表 -->
    <h3 style="margin-bottom: 10px">我的外出申请</h3>
    <el-table
      :data="outingList"
      border
      style="width: 100%"
      v-loading="loading"
      element-loading-text="加载中..."
      :empty-text="loading ? '加载中...' : '暂无外出申请记录'"
    >
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="customerName" label="客户姓名" width="100"></el-table-column>
      <el-table-column prop="outingReason" label="外出事由" show-overflow-tooltip></el-table-column>
      <el-table-column
        prop="outingDate"
        label="外出时间"
        width="120"
        :formatter="formatDate"
      ></el-table-column>
      <el-table-column
        prop="returnDate"
        label="预计回院时间"
        width="120"
        :formatter="formatDate"
      ></el-table-column>
      <el-table-column
        prop="actualReturnDate"
        label="实际回院时间"
        width="120"
        :formatter="formatDate"
      ></el-table-column>
      <el-table-column prop="companion" label="陪同人" width="100"></el-table-column>
      <el-table-column prop="relationship" label="与老人关系" width="100"></el-table-column>
      <el-table-column prop="companionPhone" label="陪同人电话" width="120"></el-table-column>
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
            v-if="row.approvalStatus === '通过' && !row.actualReturnDate"
            type="primary"
            size="small"
            @click="showReturnDialog(row)"
          >
            登记回院时间
          </el-button>
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
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchOutingList(); }"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 登记回院时间对话框 -->
    <el-dialog v-model="returnDialogVisible" title="登记回院时间" width="30%">
      <el-form :model="returnForm" :rules="returnRules" ref="returnFormRef" label-width="120px">
        <el-form-item label="实际回院时间" prop="actualReturnDate">
          <el-date-picker
            v-model="returnForm.actualReturnDate"
            type="date"
            placeholder="选择实际回院时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="returnDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReturnTime">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOutingApplications, cancelOuting, returnOuting } from '@/api/customer'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 查询表单数据
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

// 外出申请列表数据
const outingList = ref([
  {
    id: 1,
    customerName: '李福星',
    outingReason: '上厕所',
    outingDate: '2025-06-21',
    returnDate: '2025-06-22',
    actualReturnDate: '2025-06-21',
    companion: '小小立',
    relationship: '小弟',
    companionPhone: '4324324345',
    approvalStatus: '已提交',
  },
  {
    id: 2,
    customerName: '李福星',
    outingReason: '上厕所',
    outingDate: '2025-06-21',
    returnDate: '2025-06-22',
    actualReturnDate: '',
    companion: '小小立',
    relationship: '小弟',
    companionPhone: '4324324345',
    approvalStatus: '通过',
  },
  {
    id: 3,
    customerName: '李福星',
    outingReason: '上厕所',
    outingDate: '2025-06-21',
    returnDate: '2025-06-22',
    actualReturnDate: '',
    companion: '小小立',
    relationship: '小弟',
    companionPhone: '4324324345',
    approvalStatus: '已撤销',
  },
  {
    id: 4,
    customerName: '李福星',
    outingReason: '上厕所',
    outingDate: '2025-06-21',
    returnDate: '2025-06-22',
    actualReturnDate: '',
    companion: '小小立',
    relationship: '小弟',
    companionPhone: '4324324345',
    approvalStatus: '不通过',
  },
])
const total = ref(0)

// 回院时间表单数据
const returnForm = reactive({
  id: '',
  actualReturnDate: '',
})

// 表单验证规则
const returnRules = {
  actualReturnDate: [
    { required: true, message: '请选择实际回院时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        const currentApplication = outingList.value.find((item) => item.id === returnForm.id)
        if (
          currentApplication &&
          currentApplication.outingDate &&
          value &&
          value < currentApplication.outingDate.split(' ')[0]
        ) {
          callback(new Error('实际回院时间不能早于外出时间'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

// 对话框相关
const returnDialogVisible = ref(false)
const returnFormRef = ref()
const loading = ref(false)

// 获取外出申请列表
const fetchOutingList = async () => {
  loading.value = true
  try {
    const response = await getMyOutingApplications({
      pageNum: searchForm.pageNum,
      pageSize: searchForm.pageSize,
      customerName: searchForm.customerName,
    })
    outingList.value = response.records || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取外出申请列表失败:', error)
    ElMessage.error('获取外出申请列表失败')
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchOutingList()
}

// 分页变化
const handlePageChange = (page) => {
  searchForm.pageNum = page
  fetchOutingList()
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

// 显示登记回院时间对话框
const showReturnDialog = (row) => {
  returnForm.id = row.id
  returnForm.actualReturnDate = ''
  returnDialogVisible.value = true
}

// 提交回院时间
const submitReturnTime = async () => {
  try {
    await returnFormRef.value.validate()

    const response = await returnOuting(returnForm.id, {
      actualReturnDate: returnForm.actualReturnDate,
    })

    ElMessage.success('回院时间登记成功')
    returnDialogVisible.value = false
    fetchOutingList() // 刷新列表
  } catch (error) {
    console.error('登记回院时间失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('登记回院时间失败，请重试')
    }
  }
}

// 撤销申请
const cancelApplication = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销这个外出申请吗？', '确认撤销', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const response = await cancelOuting(row.id)
    ElMessage.success('申请撤销成功')
    fetchOutingList() // 刷新列表
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
  router.push('/customer/outApply')
}

// 页面加载时获取数据
onMounted(() => {
  fetchOutingList()
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
