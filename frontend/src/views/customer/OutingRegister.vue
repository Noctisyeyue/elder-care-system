<!-- 管理端--子菜单--外出审核 -->
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

    <!-- 外出申请表格 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">外出申请</span>
      </div>
      <el-table :data="outingApplicationList" height="100%" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" width="90" />
        <el-table-column prop="outingDate" label="外出时间" width="120" />
        <el-table-column prop="returnDate" label="预计返回时间" width="120" />
        <el-table-column prop="actualReturnDate" label="实际返回时间" width="120" />
        <el-table-column prop="outingReason" label="外出原因" min-width="150" />
        <el-table-column prop="approvalTime" label="审批时间" width="120" />
        <el-table-column prop="approvalPerson" label="审批人" width="90" />
        <el-table-column prop="approvalStatus" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.approvalStatus === '已提交' ? 'warning' : row.approvalStatus === '通过' ? 'success' : 'danger'">
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="60" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleApprove(row)" :disabled="row.approvalStatus !== '已提交'">
              审批
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="outingApplicationTotal"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchOutingApplications(); }"
        @current-change="handleOutingApplicationPageChange" />
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" title="外出申请审批" width="450px" align-center>
      <p style="text-align: center; font-size: 15px;">确定要通过或不通过此外出申请吗？</p>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmApprove('reject')">不通过</el-button>
        <el-button type="primary" @click="confirmApprove('pass')">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOutingList, approveOuting } from '@/api/customer'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

const outingApplicationList = ref([])
const outingApplicationTotal = ref(0)

const dialogVisible = ref(false)
const currentApplication = ref(null)

const fetchOutingApplications = async () => {
  const response = await getOutingList({
    pageNum: searchForm.pageNum,
    pageSize: searchForm.pageSize,
    customerName: searchForm.customerName,
  })
  outingApplicationList.value = response.records || []
  outingApplicationTotal.value = response.total || 0
}

const handleSearch = () => {
  searchForm.pageNum = 1
  fetchOutingApplications()
}

const handleOutingApplicationPageChange = (page) => {
  searchForm.pageNum = page
  fetchOutingApplications()
}

const handleApprove = (row) => {
  currentApplication.value = row
  dialogVisible.value = true
}

const confirmApprove = async (action) => {
  ElMessageBox.confirm(
    `您确定要${action === 'pass' ? '通过' : '不通过'}此外出申请吗？`,
    '确认审批',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: action === 'pass' ? 'success' : 'warning',
    },
  ).then(async () => {
    try {
      await approveOuting(currentApplication.value.id, {
        approvalStatus: action === 'pass' ? '通过' : '不通过',
        buildingNumber: currentApplication.value.buildingNumber,
        roomNumber: currentApplication.value.roomNumber,
        bedNumber: currentApplication.value.bedNumber,
      })
      ElMessage.success('审批成功')
      fetchOutingApplications()
    } finally {
      dialogVisible.value = false
    }
  })
}

onMounted(() => {
  fetchOutingApplications()
})
</script>

<style scoped>
/* 样式由 el-ui.scss + app.scss 全局管理 */
</style>
