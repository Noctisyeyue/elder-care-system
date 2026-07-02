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
              <span>查看已购护理</span>
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

    <!-- 已购护理抽屉 -->
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
            <el-button type="success" @click="openBuyDialog">
              <SvgIcon icon="ri:add-circle-line" :size="14" />
              <span>购买护理项目</span>
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
              <el-button link type="primary" size="small" @click="openRenewalDialog(scope.row)">续费</el-button>
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

      <!-- 续费弹窗 -->
      <el-dialog v-model="renewalDialogVisible" title="护理项目续费" width="450px" align-center append-to-body
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
          <el-form-item label="购买份数">
            <el-input-number v-model="renewalForm.newQuantity" :min="1" @change="calculateTotalQuantity" />
          </el-form-item>
          <el-form-item label="购买后剩余次数">
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

    <!-- 购买护理项目弹窗 -->
    <el-dialog v-model="buyDialogVisible" title="购买护理项目" width="1200px" align-center @close="handleBuyDialogClose">
      <div class="buy-dialog-content">
        <!-- 可购项目 -->
        <h4 style="margin-bottom: 12px;">可购买的护理项目</h4>
        <el-table :data="availableItems" stripe style="width: 100%" max-height="250" @row-click="addSelectedItem">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="frequency" label="执行周期" />
          <el-table-column prop="count" label="总次数" />
          <el-table-column prop="desc" label="备注" min-width="300" />
        </el-table>
        <el-pagination style="justify-content: center; padding-top: 10px;" background
          v-model:current-page="buyPagination.currentPage"
          v-model:page-size="buyPagination.pageSize"
          layout="prev, pager, next"
          :total="buyPagination.total"
          :page-sizes="[10, 20, 50]"
          @size-change="handleBuySizeChange"
          @current-change="handleBuyCurrentChange" />

        <!-- 已选项目 -->
        <h4 style="margin: 20px 0 12px;">已选护理项目</h4>
        <el-table :data="selectedItems" stripe style="width: 100%" max-height="220">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" min-width="100" />
          <el-table-column prop="name" label="名称" min-width="100" />
          <el-table-column prop="count" label="总次数" width="80" />
          <el-table-column prop="frequency" label="执行周期" min-width="100" />
          <el-table-column label="服务购买日期" width="140">
            <template #default="{ row }"> {{ row.buyDate }} </template>
          </el-table-column>
          <el-table-column label="购买数量" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" size="small"  style="width: 100px" />
            </template>
          </el-table-column>
          <el-table-column label="服务到期日期" width="220">
            <template #default="scope">
              <el-date-picker v-model="scope.row.expireDate" type="date" placeholder="选择日期" size="small"
                value-format="YYYY-MM-DD" :disabled-date="disabledExpireDate" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="scope">
              <el-button link type="danger" size="small" @click="removeSelectedItem(scope.$index)">
                <SvgIcon icon="ri:close-line" :size="16" />
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBuyItems">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList } from '@/api/customer'
import {
  getPurchasedItems,
  renewNursingItem,
  getNursingCustomerLevelItems,
  checkIsPurchased,
  buyNursingItem,
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

/** 已购护理抽屉。 */
const drawerVisible = ref(false)
const drawerTitle = ref('')
const itemSearchName = ref('')

/** 已购护理项目。 */
const purchasedNursingItems = ref([])
const itemPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

/** 续费。 */
const renewalDialogVisible = ref(false)
const renewalFormRef = ref()
const renewalForm = reactive({
  customerName: '', code: '', name: '', executionTimes: 0,
  originalQuantity: 0, newQuantity: 1, totalQuantity: 0,
  expireDate: '', itemId: '', customerId: '',
})

/** 购买护理弹窗。 */
const buyDialogVisible = ref(false)
const buyQueryForm = reactive({ itemName: '' })
const buyPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const availableItems = ref([])
const selectedItems = ref([])

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

function getToday() {
  return new Date().toISOString().slice(0, 10)
}

function disabledExpireDate(date) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date.getTime() <= today.getTime()
}

function addOneDay(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  date.setDate(date.getDate() + 1)
  return date.toISOString().slice(0, 10)
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

// ===== 已购护理抽屉 =====

function openPurchasedDrawer(row) {
  selectedCustomer.value = row
  drawerTitle.value = `${row.customerName} - 已购护理项目`
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
    ElMessage.error('查询已购护理项目失败')
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

// ===== 续费 =====

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
    ElMessage.success('续费成功')
    renewalDialogVisible.value = false
    searchPurchasedNursingItems()
  } catch (error) {
    console.error('续费失败:', error)
    ElMessage.error('续费失败')
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

// ===== 购买护理项目弹窗 =====

function openBuyDialog() {
  buyDialogVisible.value = true
  buyQueryForm.itemName = ''
  buyPagination.currentPage = 1
  selectedItems.value = []
  loadAvailableItems()
}

async function loadAvailableItems() {
  try {
    const res = await getNursingCustomerLevelItems({
      customerId: selectedCustomer.value.id,
      name: '',
      pageNum: buyPagination.currentPage,
      pageSize: buyPagination.pageSize,
    })
    availableItems.value = res.records || []
    buyPagination.total = res.total || 0
  } catch (error) {
    console.error('查询护理项目失败:', error)
  }
}

async function addSelectedItem(row) {
  const exists = selectedItems.value.some(item => item.id === row.id)
  if (exists) {
    ElMessage.warning('该护理项目已在已选列表中')
    return
  }

  try {
    const res = await checkIsPurchased({ customerId: selectedCustomer.value.id, itemId: row.id })
    if (res) {
      ElMessage.warning('该客户已购买此护理项目')
      return
    }

    selectedItems.value.push({
      ...row,
      quantity: 1,
      buyDate: getToday(),
      expireDate: addOneDay(row.expireDate || getToday()),
    })
  } catch (error) {
    console.error('检查护理项目失败:', error)
  }
}

function removeSelectedItem(index) {
  selectedItems.value.splice(index, 1)
}

function handleBuySizeChange(size) {
  buyPagination.pageSize = size
  buyPagination.currentPage = 1
  loadAvailableItems()
}

function handleBuyCurrentChange(val) {
  buyPagination.currentPage = val
  loadAvailableItems()
}

async function saveBuyItems() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要购买的护理项目')
    return
  }

  try {
    const payload = selectedItems.value.map(item => ({
      customerId: selectedCustomer.value.id,
      itemId: item.id,
      buyCount: item.quantity,
      buyDate: item.buyDate,
      expireDate: item.expireDate,
    }))
    await buyNursingItem(payload)
    ElMessage.success('购买护理项目成功')
    buyDialogVisible.value = false
    searchPurchasedNursingItems()
  } catch (error) {
    console.error('购买护理项目失败:', error)
    ElMessage.error('购买护理项目失败')
  }
}

function handleBuyDialogClose() {
  buyQueryForm.itemName = ''
  selectedItems.value = []
}

onMounted(() => {
  searchCustomers()
})
</script>

<style scoped>
.drawer-content {
  padding: 0 8px;
}

.buy-dialog-content {
  height: 67vh;
  overflow-y: auto;
}

.status-tags {
  display: flex;
  gap: 8px;
}

:deep(.el-drawer__body) {
  padding: 16px;
}
</style>
