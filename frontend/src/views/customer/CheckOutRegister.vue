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
        <el-button type="success" @click="showCheckoutApplications">查看退住申请</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />

    <!-- 客户信息-->
    <h3 style="margin-bottom: 10px">客户信息</h3>
    <div class="outer">
      <el-table :data="customerInfoList" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="age" label="年龄" min-width="60" />
        <el-table-column prop="gender" label="性别" min-width="60" />
        <el-table-column prop="buildingNumber" label="所属楼房" min-width="100" />
        <el-table-column prop="roomNumber" label="房间号" min-width="80" />
        <el-table-column prop="bedNumber" label="床号" min-width="60" />
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

    <!-- 退住申请表格 - 移至抽屉中 -->
    <el-drawer v-model="drawerVisible" title="退住申请" direction="rtl" size="70%">
      <el-table
        :data="checkoutApplicationList"
        border
        style="width: 100%"
        max-height="calc(100vh - 150px)"
      >
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名" width="90"></el-table-column>
        <el-table-column prop="checkInDate" label="入住时间" width="120"></el-table-column>
        <el-table-column prop="checkOutDate" label="退住时间" width="120"></el-table-column>
        <el-table-column prop="checkOutType" label="退住类型" width="90"></el-table-column>
        <el-table-column prop="checkOutReason" label="退住原因" width="150"></el-table-column>
        <el-table-column prop="approvalTime" label="审批时间" width="120"></el-table-column>
        <el-table-column prop="approvalPerson" label="审批人" width="90"></el-table-column>
        <el-table-column prop="approvalStatus" label="审批状态" width="90"></el-table-column>
        <el-table-column prop="bedNumber" label="床位" width="90"></el-table-column>
        <el-table-column label="操作" width="90">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleApprove(row)"
              :disabled="row.approvalStatus !== '已提交'"
            >
              审批
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="checkoutApplicationTotal"
          :page-size="searchForm.pageSize"
          :current-page="searchForm.pageNum"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchCheckoutApplications(); }"
          @current-change="handleCheckoutApplicationPageChange"
        />
      </div>
    </el-drawer>

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" title="退住申请审批" width="30%">
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
import { get, post } from '@/utils/request'
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

// 退住申请列表数据
const checkoutApplicationList = ref([])
const checkoutApplicationTotal = ref(0)

// 审批对话框相关
const dialogVisible = ref(false)
const currentApplication = ref(null)
const approvalAction = ref('pass')

// 抽屉可见性
const drawerVisible = ref(false)

// 获取客户信息列表
const fetchCustomerInfo = async () => {
  const response = await get('/customer/list', {
    pageNum: searchForm.pageNum,
    pageSize: searchForm.pageSize,
    customerName: searchForm.customerName, // 用于模糊查询
  })
  customerInfoList.value = response.records || []
  customerInfoTotal.value = response.total || 0
}

// 获取退住申请列表
const fetchCheckoutApplications = async () => {
  const response = await get('/customer/checkout/list', {
    pageNum: searchForm.pageNum,
    pageSize: searchForm.pageSize,
    customerName: searchForm.customerName, // 用于模糊查询
  })
  checkoutApplicationList.value = response.records || []
  checkoutApplicationTotal.value = response.total || 0
}

// 处理查询
const handleSearch = () => {
  searchForm.pageNum = 1 // 每次查询都重置到第一页
  fetchCustomerInfo()
  fetchCheckoutApplications()
}

// 客户信息分页变化
const handleCustomerInfoPageChange = (page) => {
  searchForm.pageNum = page
  fetchCustomerInfo()
}

// 退住申请分页变化
const handleCheckoutApplicationPageChange = (page) => {
  searchForm.pageNum = page
  fetchCheckoutApplications()
}

// 打开审批对话框
const handleApprove = (row) => {
  currentApplication.value = row
  dialogVisible.value = true
}

// 确认审批
const confirmApprove = async (action) => {
  approvalAction.value = action
  ElMessageBox.confirm(
    `您确定要${action === 'pass' ? '通过' : '不通过'}此退住申请吗？`,
    '确认审批',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: action === 'pass' ? 'success' : 'warning',
    },
  ).then(async () => {
    try {
      const response = await post(`/customer/checkout/approve/${currentApplication.value.id}`, {
        approvalStatus: action === 'pass' ? '通过' : '不通过',
        checkOutType: currentApplication.value.checkOutType,
        bedNumber: currentApplication.value.bedNumber,
      })
      ElMessage.success('审批成功')
      // 审批成功后刷新列表
      fetchCheckoutApplications()
      fetchCustomerInfo() // 可能客户信息中的床位状态也需要更新
    } finally {
      dialogVisible.value = false // 关闭对话框
    }
  })
}

// 显示退住申请抽屉
const showCheckoutApplications = () => {
  drawerVisible.value = true
  fetchCheckoutApplications() // 每次打开抽屉时刷新数据
}

//页面加载时获取数据
onMounted(() => {
  fetchCustomerInfo()
  fetchCheckoutApplications()
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
  justify-content: flex-end; /* 将分页器靠右对齐 */
}

.outer :deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  overflow-x: auto;
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
  margin-top: 20px;
}
</style>
