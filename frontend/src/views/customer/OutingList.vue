<!-- 护工端--子菜单--外出申请--查看外出申请 -->
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
        <span class="table-header-title">我的外出申请</span>
      </div>
      <el-table :data="outingList" height="100%" stripe style="width: 100%" v-loading="loading" element-loading-text="加载中..."
        :empty-text="loading ? '加载中...' : '暂无外出申请记录'">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" width="100" />
        <el-table-column prop="outingReason" label="外出事由" show-overflow-tooltip />
        <el-table-column prop="outingDate" label="外出时间" width="120" :formatter="formatDate" />
        <el-table-column prop="returnDate" label="预计回院时间" width="120" :formatter="formatDate" />
        <el-table-column prop="actualReturnDate" label="实际回院时间" width="120" :formatter="formatDate" />
        <el-table-column prop="companion" label="陪同人" width="100" />
        <el-table-column prop="relationship" label="与老人关系" width="100" />
        <el-table-column prop="companionPhone" label="陪同人电话" width="120" />
        <el-table-column prop="approvalStatus" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.approvalStatus)">
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.approvalStatus === '通过' && !row.actualReturnDate" type="primary" size="small"
              @click="showReturnDialog(row)">
              登记回院时间
            </el-button>
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
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchOutingList(); }"
        @current-change="handlePageChange" />
    </el-card>

    <!-- 登记回院时间对话框 -->
    <el-dialog v-model="returnDialogVisible" title="登记回院时间" width="450px" align-center
      @close="returnFormRef?.resetFields()">
      <el-form :model="returnForm" :rules="returnRules" ref="returnFormRef" label-width="120px">
        <el-form-item label="实际回院时间" prop="actualReturnDate">
          <el-date-picker v-model="returnForm.actualReturnDate" type="date" placeholder="选择实际回院时间" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturnTime">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOutingApplications, cancelOuting, returnOuting } from '@/api/customer'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

/** 查询表单。 */
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

/** 外出申请列表。 */
const outingList = ref([])
const total = ref(0)

/** 回院时间表单。 */
const returnForm = reactive({
  id: '',
  actualReturnDate: '',
})

/** 回院时间校验规则。 */
const returnRules = {
  actualReturnDate: [
    { required: true, message: '请选择实际回院时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        const currentApplication = outingList.value.find((item) => item.id === returnForm.id)
        if (currentApplication && currentApplication.outingDate && value && value < currentApplication.outingDate.split(' ')[0]) {
          callback(new Error('实际回院时间不能早于外出时间'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

const returnDialogVisible = ref(false)
const returnFormRef = ref()
const loading = ref(false)

/** 查询外出申请列表。 */
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

/** 执行查询。 */
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchOutingList()
}

/** 处理分页变化。 */
const handlePageChange = (page) => {
  searchForm.pageNum = page
  fetchOutingList()
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

/** 打开回院时间弹窗。 */
const showReturnDialog = (row) => {
  returnForm.id = row.id
  returnForm.actualReturnDate = ''
  returnDialogVisible.value = true
}

/** 提交回院时间。 */
const submitReturnTime = async () => {
  try {
    await returnFormRef.value.validate()
    await returnOuting(returnForm.id, { actualReturnDate: returnForm.actualReturnDate })
    ElMessage.success('回院时间登记成功')
    returnDialogVisible.value = false
    fetchOutingList()
  } catch (error) {
    console.error('登记回院时间失败:', error)
    ElMessage.error(error.message || '登记回院时间失败，请重试')
  }
}

/** 撤销申请。 */
const cancelApplication = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销这个外出申请吗？', '确认撤销', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await cancelOuting(row.id)
    ElMessage.success('申请撤销成功')
    fetchOutingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('撤销申请失败:', error)
      ElMessage.error(error.message || '撤销申请失败，请重试')
    }
  }
}

/** 返回上一页。 */
const goBack = () => {
  router.push('/customer/outApply')
}

onMounted(() => {
  fetchOutingList()
})
</script>

<style scoped>
/* 样式由 el-ui.scss + app.scss 全局管理 */
</style>
