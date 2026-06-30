<template>
  <div>
    <!-- <el-row :gutter="20">
      <el-col :span="12"> -->
        <!-- 客户信息查询表单 -->
        <el-form
          :inline="true"
          :model="customerQueryForm"
          class="customer-query-form search-form-outside-card"
        >
          <el-form-item>
            <el-input
              v-model="customerQueryForm.name"
              placeholder="请输入客户姓名"
              clearable
              @clear="searchCustomers"
              style="width: 200px; margin-right: 10px"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchCustomers">查询</el-button>
          </el-form-item>
        </el-form>

        <!-- 客户信息列表 -->
        <div class="card-header">
          <span>客户信息</span>
        </div>
        <el-table
          :data="customerList"
          highlight-current-row
          @current-change="handleCustomerSelect"
          style="width: 100%"
        >
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="customerName" label="客户姓名" min-width="100" />
          <el-table-column prop="age" label="年龄" min-width="60" />
          <el-table-column prop="gender" label="性别" min-width="60" />
          <el-table-column prop="buildingNumber" label="楼号" width="90" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="bedNumber" label="床号" width="80" />
          <el-table-column prop="tel" label="联系电话" min-width="140" />
          <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" size="small" @click="goToPurchasedNursingItems(scope.row)">查看已购护理</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="customerPagination.total"
          :page-size="customerPagination.pageSize"
          :current-page="customerPagination.currentPage"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="(size) => { customerPagination.pageSize = size; customerPagination.currentPage = 1; searchCustomers(); }"
          @current-change="handleCustomerCurrentChange"
        />
      <!-- </el-col>
    </el-row> -->

    <!-- 续费对话框 -->
    <el-dialog v-model="renewalDialogVisible" title="护理项目续费" width="50%">
      <el-form :model="renewalForm" label-width="150px">
        <el-form-item label="客户姓名">
          <el-input v-model="renewalForm.customerName" disabled />
        </el-form-item>
        <el-form-item label="项目编号">
          <el-input v-model="renewalForm.code" disabled />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="renewalForm.name" disabled />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="renewalForm.price" disabled />
        </el-form-item>
        <el-form-item label="原剩余执行次数">
          <el-input v-model="renewalForm.originalQuantity" disabled />
        </el-form-item>
        <el-form-item label="购买份数">
          <el-input-number
            v-model="renewalForm.newQuantity"
            :min="1"
            @change="calculateTotalQuantity"
          />
        </el-form-item>
        <el-form-item label="购买后剩余执行次数">
          <el-input v-model="renewalForm.totalQuantity" disabled />
        </el-form-item>
        <el-form-item label="服务到期时间">
          <el-date-picker
            v-model="renewalForm.expireDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renewalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRenewal">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList, getMyCustomers } from '@/api/customer'
import { getPurchasedItems, renewNursingItem } from '@/api/health'
import { Search } from '@element-plus/icons-vue'
const router = useRouter()

// 客户查询表单
const customerQueryForm = reactive({
  name: '',
})

// 客户列表
const customerList = ref([])
const selectedCustomer = ref({}) // 选中的客户

// 客户分页
const customerPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})

// 已购护理项目列表
const purchasedNursingItems = ref([])

// 已购护理项目分页
const itemPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})

// 续费对话框
const renewalDialogVisible = ref(false)
const renewalForm = reactive({
  customerName: '',
  code: '',
  name: '',
  price: '',
  executionTimes: 0,
  originalQuantity: 0,
  newQuantity: 1,
  totalQuantity: 0,
  expireDate: '',
  itemId: '', // 用于续费的护理项目ID
  customerId: '', // 用于续费的客户ID
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

// 查询客户信息列表
const searchCustomers = async () => {
  const params = {
    customerName: customerQueryForm.name,
    pageNum: customerPagination.currentPage,
    pageSize: customerPagination.pageSize,
    customerType: 'nursing-care',
  }
  const res = await getCustomerList(params)
  customerList.value = res.records || []
  customerPagination.total = res.total || 0
  selectedCustomer.value = {}
  purchasedNursingItems.value = []
}

// 处理客户当前页改变
const handleCustomerCurrentChange = (val) => {
  customerPagination.currentPage = val
  searchCustomers()
}

// 选中客户后查询该客户的已购护理项目
const handleCustomerSelect = (row) => {
  if (row) {
    selectedCustomer.value = row
    searchPurchasedNursingItems(row.id)
  } else {
    selectedCustomer.value = {}
    purchasedNursingItems.value = []
  }
}

// 查询客户已购买的护理服务项目列表
const searchPurchasedNursingItems = async (customerId) => {
  try {
    const params = {
      customerId: customerId,
      pageNum: itemPagination.currentPage,
      pageSize: itemPagination.pageSize,
    }
    const res = await getPurchasedItems(params)
    purchasedNursingItems.value = res.list || []
    itemPagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('查询已购护理项目失败')
  }
}

// 处理已购护理项目当前页改变
const handleItemCurrentChange = (val) => {
  itemPagination.currentPage = val
  searchPurchasedNursingItems(selectedCustomer.value.id)
}

// 打开续费对话框
const openRenewalDialog = (item) => {
  console.log(item.executionTimes)
  renewalForm.customerName = selectedCustomer.value.customerName
  renewalForm.code = item.code
  renewalForm.name = item.name
  renewalForm.price = item.price
  renewalForm.originalQuantity = item.remain
  renewalForm.executionTimes = item.executionTimes
  renewalForm.newQuantity = 1
  renewalForm.totalQuantity = item.remain + item.executionTimes
  renewalForm.expireDate = item.expireDate
  renewalForm.itemId = item.id
  renewalForm.customerId = selectedCustomer.value.id
  renewalDialogVisible.value = true
}

// 计算总数量
const calculateTotalQuantity = () => {
  renewalForm.totalQuantity = renewalForm.originalQuantity + renewalForm.newQuantity * renewalForm.executionTimes
}

// 确认续费
const confirmRenewal = async () => {
  try {
    const payload = {
      customerId: renewalForm.customerId,
      itemId: renewalForm.itemId,
      purchasingTimes: renewalForm.newQuantity,
      expireDate: renewalForm.expireDate,
    }
    await renewNursingItem(payload)
    ElMessage.success('续费成功')
    renewalDialogVisible.value = false
    searchPurchasedNursingItems(selectedCustomer.value.id)
  } catch (error) {
    console.error('续费失败:', error)
    ElMessage.error('续费失败')
  }
}

// 移除护理项目
const removeNursingItem = async (item) => {
  ElMessageBox.confirm(`确定要移除 "${item.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await del(`/nursing/remove/${item.id}`)
      ElMessage.success('移除成功')
      searchPurchasedNursingItems(selectedCustomer.value.id)
    })
    .catch(() => {
      ElMessage.info('已取消移除')
    })
}

// 跳转到购买护理项目页面
const goToPurchasedNursingItems = (row) => {
  console.log(row)
  if (row && row.id) {
    router.push({path: '/service/purchased-nursing-items'})
    if(localStorage.getItem('purchasedNursingItemsCustomerId') == row.id){
      localStorage.removeItem('purchasedNursingItemsCustomerId')
    }
    if(localStorage.getItem('purchasedNursingItemsCustomerName')){
      localStorage.removeItem('purchasedNursingItemsCustomerName')
    }
    localStorage.setItem('purchasedNursingItemsCustomerId', row.id)
    localStorage.setItem('purchasedNursingItemsCustomerName', row.customerName)
  } else {
    ElMessage.warning('请选择客户')
  }
}

// 组件挂载时查询客户列表
onMounted(() => {
  searchCustomers()
})
</script>

<style scoped>
.el-row {
  height: 100%;
  align-items: flex-start;
}

.el-col {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.box-card {
  margin-bottom: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.el-table {
  flex: 1;
}

.el-pagination {
  margin-top: 10px;
  text-align: right;
}

.card-header {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.customer-query-form .el-form-item {
  margin-bottom: 0;
}

.search-form-outside-card {
  margin-bottom: 19px;
}

.status-tags {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
}

.status-tag {
  min-width: 60px;
  text-align: center;
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 314px);
}
</style>
