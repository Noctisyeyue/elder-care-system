<template>
  <div>
    <el-button type="default" @click="goBack" style="margin-bottom: 16px;">返回</el-button>
    <div class="card-header">
      <span>{{ customerName }}已购护理项目</span>
      <el-button type="primary" @click="goToBuyNursingItem" :disabled="!customerId">购买护理项目</el-button>
    </div>
    <el-table :data="purchasedNursingItems" style="width: 100%">
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户" />
      <el-table-column prop="code" label="项目编号" />
      <el-table-column prop="name" label="项目名称" />
      <el-table-column prop="remain" label="剩余次数">
        <template #default="{ row }"> {{ row.remain }} 次 </template>
      </el-table-column>
      <el-table-column prop="expireDate" label="服务到期时间">
        <template #default="{ row }">
          {{ formatDate(row.expireDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="150">
        <template #default="{ row }">
          <div class="status-tags">
            <el-tag :type="new Date(row.expireDate) < new Date() ? 'danger' : 'primary'" size="small"
              class="status-tag">
              {{ new Date(row.expireDate) < new Date() ? '到期' : '未到期' }} 
            </el-tag>
                <el-tag :type="row.remain <= 0 ? 'danger' : 'primary'" size="small" class="status-tag">
                  {{ row.remain <= 0 ? '次数用尽' : '次数正常' }} 
                </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="openRenewalDialog(scope.row)">续费</el-button>
          <el-button link type="danger" size="small" @click="removeNursingItem(scope.row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pagination-right" layout="total, sizes, prev, pager, next, jumper"
      :total="itemPagination.total" :page-size="itemPagination.pageSize" :current-page="itemPagination.currentPage"
      :page-sizes="[5, 10, 20, 50]"
      @size-change="(size) => { itemPagination.pageSize = size; itemPagination.currentPage = 1; searchPurchasedNursingItems(); }"
      @current-change="handleItemCurrentChange" />
    <!-- 续费对话框复用原有逻辑 -->
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
          <el-date-picker v-model="renewalForm.expireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="renewalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="() => { renewalDialogVisible = false; confirmRenewal(); }">确定</el-button>
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


const router = useRouter()
const customerId = localStorage.getItem('purchasedNursingItemsCustomerId')
const customerName = localStorage.getItem('purchasedNursingItemsCustomerName')

const purchasedNursingItems = ref([])
const itemPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})

const renewalDialogVisible = ref(false)
const renewalForm = reactive({
  customerName: '',
  code: '',
  name: '',
  executionTimes: 0,
  originalQuantity: 0,
  newQuantity: 1,
  totalQuantity: 0,
  expireDate: '',
  itemId: '',
  customerId: '',
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const searchPurchasedNursingItems = async () => {
  if (!customerId) return
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

const handleItemCurrentChange = (val) => {
  itemPagination.currentPage = val
  searchPurchasedNursingItems()
}

const openRenewalDialog = (item) => {
  renewalForm.customerName = item.customerName
  renewalForm.code = item.code
  renewalForm.name = item.name
  renewalForm.originalQuantity = item.remain
  renewalForm.executionTimes = item.executionTimes
  renewalForm.newQuantity = 1
  renewalForm.totalQuantity = item.remain + item.executionTimes
  renewalForm.expireDate = item.expireDate
  renewalForm.itemId = item.id
  renewalForm.customerId = customerId
  renewalDialogVisible.value = true
}

const calculateTotalQuantity = () => {
  renewalForm.totalQuantity = renewalForm.originalQuantity + renewalForm.newQuantity * renewalForm.executionTimes
}

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
    searchPurchasedNursingItems()
  } catch (error) {
    console.error('续费失败:', error)
    ElMessage.error('续费失败')
  }
}

const removeNursingItem = async (item) => {
  ElMessageBox.confirm(`确定要移除 "${item.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await del(`/nursing/remove/${item.id}`)
      ElMessage.success('移除成功')
      searchPurchasedNursingItems()
    })
    .catch(() => {
      ElMessage.info('已取消移除')
    })
}

const goToBuyNursingItem = () => {
  if (customerId) {
    router.push({
      path: '/service/nursingItem'
    })
    if (localStorage.getItem('buyNursingItemsCustomerId')) {
      localStorage.removeItem('buyNursingItemsCustomerId')
    }
    if (localStorage.getItem('buyNursingItemsCustomerName')) {
      localStorage.removeItem('buyNursingItemsCustomerName')
    }
    localStorage.setItem('buyNursingItemsCustomerId', customerId)
    localStorage.setItem('buyNursingItemsCustomerName', customerName)
  } else {
    ElMessage.warning('请先选择一个客户')
  }
}

const goBack = () => {
  router.push({
    path: '/service/concern'
  })
}

onMounted(() => {
  searchPurchasedNursingItems()
})
</script>

<style scoped>
.card-header {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

:deep(.el-table__body-wrapper .el-scrollbar__wrap) {
  overflow-y: auto;
  max-height: calc(100vh - 255px);
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
