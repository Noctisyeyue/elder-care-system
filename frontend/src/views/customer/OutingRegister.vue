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
            <el-icon><Search /></el-icon> 
            </template>
          </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="showOutingApplications">查看外出申请</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />
    <!-- 客户信息表格 -->
    <h3 style="margin-bottom: 10px">客户信息</h3>
    <div class="outer">
      <el-table :data="customerInfoList" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="age" label="年龄" min-width="60" />
        <el-table-column prop="gender" label="性别" min-width="60" />
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="tel" label="联系电话" min-width="140" />
        <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
      </el-table>
      <div class="pagination-container">
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="customerInfoTotal"
          :page-size="searchForm.pageSize"
          :current-page="searchForm.pageNum"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchCustomerInfo(); }"
          @current-change="handleCustomerInfoPageChange"
        />
      </div>
    </div>

    <!-- 外出申请表格 - 移至抽屉中 -->
    <el-drawer v-model="drawerVisible" title="外出申请" direction="rtl" size="70%">
      <el-table
        :data="outingApplicationList"
        border
        style="width: 100%"
        max-height="calc(100vh - 150px)"
      >
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名" width="90"></el-table-column>
        <el-table-column prop="outingDate" label="外出时间" width="120"></el-table-column>
        <el-table-column prop="returnDate" label="预计返回时间" width="120"></el-table-column>
        <el-table-column prop="actualReturnDate" label="实际返回时间" width="120"></el-table-column>
        <el-table-column prop="outingReason" label="外出原因" width="150"></el-table-column>
        <el-table-column prop="approvalTime" label="审批时间" width="120"></el-table-column>
        <el-table-column prop="approvalPerson" label="审批人" width="90"></el-table-column>
        <el-table-column prop="approvalStatus" label="审批状态" width="90"></el-table-column>
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column label="操作" width="90">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleApprove(row)"
              :disabled="row.approvalStatus !== '已提交'">
              审批
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="outingApplicationTotal"
          :page-size="searchForm.pageSize"
          :current-page="searchForm.pageNum"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchOutingApplications(); }"
          @current-change="handleOutingApplicationPageChange"
        />
      </div>
    </el-drawer>

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" title="外出申请审批" width="30%">
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmApprove('reject')">不通过</el-button>
          <el-button type="primary" @click="confirmApprove('pass')">通过</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList, getOutingList, approveOuting } from '@/api/customer'
import { Search } from '@element-plus/icons-vue'
// 查询表单数据
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

// 客户信息列表数据
const customerInfoList = ref([])
const customerInfoTotal = ref(0)

/** 外出申请列表。 */
const outingApplicationList = ref([])
const outingApplicationTotal = ref(0)

/** 审批弹窗状态。 */
const dialogVisible = ref(false)
const currentApplication = ref(null)
const approvalAction = ref('pass')

/** 抽屉是否显示。 */
const drawerVisible = ref(false)

/**
 * 查询客户列表。
 *
 * @returns 无返回值
 */
const fetchCustomerInfo = async () => {
  const response = await getCustomerList({
    pageNum: searchForm.pageNum,
    pageSize: searchForm.pageSize,
    customerName: searchForm.customerName,
  })
  customerInfoList.value = response.records || []
  customerInfoTotal.value = response.total || 0
}

/**
 * 查询外出申请列表。
 *
 * @returns 无返回值
 */
const fetchOutingApplications = async () => {
  const response = await getOutingList({
    pageNum: searchForm.pageNum,
    pageSize: searchForm.pageSize,
    customerName: searchForm.customerName,
  })
  outingApplicationList.value = response.records || []
  outingApplicationTotal.value = response.total || 0
}

/**
 * 执行查询。
 *
 * @returns 无返回值
 */
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchCustomerInfo()
  fetchOutingApplications()
}

/**
 * 处理客户列表分页变化。
 *
 * @param page 页码
 * @returns 无返回值
 */
const handleCustomerInfoPageChange = (page) => {
  searchForm.pageNum = page
  fetchCustomerInfo()
}

/**
 * 处理外出申请分页变化。
 *
 * @param page 页码
 * @returns 无返回值
 */
const handleOutingApplicationPageChange = (page) => {
  searchForm.pageNum = page
  fetchOutingApplications()
}

/**
 * 打开审批弹窗。
 *
 * @param row 外出申请行数据
 * @returns 无返回值
 */
const handleApprove = (row) => {
  currentApplication.value = row
  dialogVisible.value = true
}

/**
 * 确认审批结果。
 *
 * @param action 审批动作
 * @returns 无返回值
 */
const confirmApprove = async (action) => {
  approvalAction.value = action
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
      const response = await approveOuting(currentApplication.value.id, {
        approvalStatus: action === 'pass' ? '通过' : '不通过',
        buildingNumber: currentApplication.value.buildingNumber,
        roomNumber: currentApplication.value.roomNumber,
        bedNumber: currentApplication.value.bedNumber,
      })
      ElMessage.success('审批成功')
      fetchOutingApplications()
      fetchCustomerInfo()
    } finally {
      dialogVisible.value = false
    }
  })
}

/**
 * 显示外出申请抽屉。
 *
 * @returns 无返回值
 */
const showOutingApplications = () => {
  drawerVisible.value = true
  fetchOutingApplications()
}

/**
 * 页面初始化时加载客户和申请数据。
 *
 * @returns 无返回值
 */
onMounted(() => {
  fetchCustomerInfo()
  fetchOutingApplications()
})
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.outer :deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-x: auto;
  overflow-y: auto;
  max-height: calc(100vh - 350px);
}

:deep(.el-drawer__header){
  padding: 20px 20px 0 20px;
  margin: 0;
  height: 20px;
  font-weight: 800;
}

.el-drawer__body{
  overflow-y: hidden;
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>
