<!-- 管理端--子菜单--服务关注 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="customerQueryForm" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="customerQueryForm.name" placeholder="请输入客户姓名" clearable @clear="searchCustomers"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCustomers">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 客户表格 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">客户信息</span>
      </div>
      <el-table :data="customerList" height="100%" stripe style="width: 100%"
        @row-click="handleCustomerSelect">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="tel" label="联系电话" min-width="140" />
        <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openPurchasedDrawer(scope.row)">
              <SvgIcon icon="ri:file-list-3-line" :size="14" />
              <span>查看护理项目</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background v-model:current-page="customerPagination.currentPage"
        v-model:page-size="customerPagination.pageSize" layout="total, prev, pager, next, sizes, jumper"
        :total="customerPagination.total" :page-sizes="[5, 10, 20, 50]"
        @size-change="handleCustomerSizeChange"
        @current-change="handleCustomerCurrentChange" />
    </el-card>

    <!-- 护理项目抽屉 -->
    <el-drawer v-model="drawerVisible" :title="drawerTitle" direction="rtl" size="60%">
      <div class="drawer-content">
        <div class="flex-cb mb-4">
          <el-input v-model="itemSearchName" placeholder="搜索项目名称" clearable @clear="handleDrawerSearch"
            style="width: 220px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
          <div class="flex-c" style="gap: 8px;">
            <el-button type="primary" @click="handleDrawerSearch">
              <SvgIcon icon="ri:search-line" :size="14" />
              <span>查询</span>
            </el-button>
          </div>
        </div>

        <el-table :data="purchasedNursingItems" stripe style="width: 100%" max-height="calc(100vh - 300px)">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="customerName" label="客户" />
          <el-table-column prop="code" label="项目编号" />
          <el-table-column prop="name" label="项目名称" />
          <el-table-column prop="remain" label="剩余次数">
            <template #default="{ row }"> {{ row.remain }} 次 </template>
          </el-table-column>
          <el-table-column prop="expireDate" label="服务到期时间" width="120">
            <template #default="{ row }"> {{ formatDate(row.expireDate) }} </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="new Date(row.expireDate) < new Date() ? 'danger' : 'primary'" size="small">
                {{ new Date(row.expireDate) < new Date() ? '到期' : '未到期' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openRenewalDialog(scope.row)">调整次数</el-button>
              <el-button link type="danger" size="small" @click="removeNursingItem(scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination style="justify-content: center; padding-top: 10px;" background v-model:current-page="itemPagination.currentPage"
          v-model:page-size="itemPagination.pageSize" layout="total, prev, pager, next, sizes, jumper"
          :total="itemPagination.total" :page-sizes="[5, 10, 20, 50]"
          @size-change="handleItemSizeChange"
          @current-change="handleItemCurrentChange" />
      </div>

      <!-- 调整次数弹窗 -->
      <el-dialog v-model="renewalDialogVisible" title="调整护理项目次数" width="450px" align-center append-to-body
        @close="renewalFormRef?.resetFields()">
        <el-form :model="renewalForm" ref="renewalFormRef" label-width="120px">
          <el-form-item label="客户姓名">
            <el-input v-model="renewalForm.customerName" disabled />
          </el-form-item>
          <el-form-item label="项目名称">
            <el-input v-model="renewalForm.name" disabled />
          </el-form-item>
          <el-form-item label="剩余次数">
            <el-input v-model="renewalForm.originalQuantity" disabled />
          </el-form-item>
          <el-form-item label="增加数量">
            <el-input-number v-model="renewalForm.newQuantity" :min="1" @change="calculateTotalQuantity" />
          </el-form-item>
          <el-form-item label="调整后剩余次数">
            <el-input v-model="renewalForm.totalQuantity" disabled />
          </el-form-item>
          <el-form-item label="服务到期时间">
            <el-date-picker v-model="renewalForm.expireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
              style="width: 100%" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="renewalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRenewal">确定</el-button>
        </template>
      </el-dialog>
    </el-drawer>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList } from '@/api/customer'
import {
  getPurchasedItems,
  renewNursingItem,
} from '@/api/health'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import { del } from '@/utils/request'

/** 客户查询表单。 */
const customerQueryForm = reactive({ name: '' })

/** 客户列表。 */
const customerList = ref([])
const selectedCustomer = ref({})

/** 客户分页。 */
const customerPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

/** 护理项目抽屉。 */
const drawerVisible = ref(false)
const drawerTitle = ref('')
const itemSearchName = ref('')

/** 护理项目。 */
const purchasedNursingItems = ref([])
const itemPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

/** 调整护理项目次数。 */
const renewalDialogVisible = ref(false)
const renewalFormRef = ref()
const renewalForm = reactive({
  customerName: '', code: '', name: '', executionTimes: 0,
  originalQuantity: 0, newQuantity: 1, totalQuantity: 0,
  expireDate: '', itemId: '', customerId: '',
})

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

// ===== 客户列表 =====

async function searchCustomers() {
  const res = await getCustomerList({
    customerName: customerQueryForm.name,
    pageNum: customerPagination.currentPage,
    pageSize: customerPagination.pageSize,
    customerType: 'nursing-care',
  })
  customerList.value = res.records || []
  customerPagination.total = res.total || 0
  selectedCustomer.value = {}
}

function handleCustomerCurrentChange(val) {
  customerPagination.currentPage = val
  searchCustomers()
}

function handleCustomerSizeChange(size) {
  customerPagination.pageSize = size
  customerPagination.currentPage = 1
  searchCustomers()
}

function handleCustomerSelect(row) {
  selectedCustomer.value = row || {}
}

// ===== 护理项目抽屉 =====

function openPurchasedDrawer(row) {
  selectedCustomer.value = row
  drawerTitle.value = `${row.customerName} - 护理项目记录`
  itemPagination.currentPage = 1
  drawerVisible.value = true
  searchPurchasedNursingItems()
}

function handleDrawerSearch() {
  itemPagination.currentPage = 1
  searchPurchasedNursingItems()
}

async function searchPurchasedNursingItems() {
  if (!selectedCustomer.value.id) return
  try {
    const params = {
      customerId: selectedCustomer.value.id,
      pageNum: itemPagination.currentPage,
      pageSize: itemPagination.pageSize,
      itemName: itemSearchName.value,
    }
    const res = await getPurchasedItems(params)
    purchasedNursingItems.value = res.list || []
    itemPagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('查询护理项目失败')
  }
}

function handleItemCurrentChange(val) {
  itemPagination.currentPage = val
  searchPurchasedNursingItems()
}

function handleItemSizeChange(size) {
  itemPagination.pageSize = size
  itemPagination.currentPage = 1
  searchPurchasedNursingItems()
}

// ===== 调整次数 =====

function openRenewalDialog(item) {
  renewalForm.customerName = selectedCustomer.value.customerName
  renewalForm.code = item.code
  renewalForm.name = item.name
  renewalForm.originalQuantity = item.remain
  renewalForm.executionTimes = item.executionTimes || 1
  renewalForm.newQuantity = 1
  renewalForm.totalQuantity = item.remain + (item.executionTimes || 1)
  renewalForm.expireDate = item.expireDate
  renewalForm.itemId = item.id
  renewalForm.customerId = selectedCustomer.value.id
  renewalDialogVisible.value = true
}

function calculateTotalQuantity() {
  renewalForm.totalQuantity = renewalForm.originalQuantity + renewalForm.newQuantity * renewalForm.executionTimes
}

async function confirmRenewal() {
  try {
    await renewNursingItem({
      customerId: renewalForm.customerId,
      itemId: renewalForm.itemId,
      purchasingTimes: renewalForm.newQuantity,
      expireDate: renewalForm.expireDate,
    })
    ElMessage.success('调整成功')
    renewalDialogVisible.value = false
    searchPurchasedNursingItems()
  } catch (error) {
    console.error('调整失败:', error)
    ElMessage.error('调整失败')
  }
}

async function removeNursingItem(item) {
  try {
    await ElMessageBox.confirm(`确定要移除 "${item.name}" 吗？`, '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    })
    await del(`/nursing/remove/${item.id}`)
    ElMessage.success('移除成功')
    searchPurchasedNursingItems()
  } catch (error) {
    if (error !== 'cancel') ElMessage.info('已取消移除')
  }
}

onMounted(() => {
  searchCustomers()
})
</script>

<style scoped>
.drawer-content {
  padding: 0 8px;
}

.status-tags {
  display: flex;
  gap: 8px;
}

:deep(.el-drawer__body) {
  padding: 16px;
}
</style>
