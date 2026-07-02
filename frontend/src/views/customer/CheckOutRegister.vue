<!-- 管理端--子菜单--退住审核 -->
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
          <el-button type="success" @click="openDirectCheckoutDialog">
            <SvgIcon icon="ri:logout-box-r-line" :size="16" />
            <span>办理自理老人退住</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 退住申请表格 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">退住申请</span>
      </div>
      <el-table :data="checkoutApplicationList" height="100%" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" width="90" />
        <el-table-column prop="checkInDate" label="入住时间" width="120" />
        <el-table-column prop="checkOutDate" label="退住时间" width="120" />
        <el-table-column prop="checkOutType" label="退住类型" width="90" />
        <el-table-column prop="checkOutReason" label="退住原因" min-width="150" />
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
        <el-table-column prop="bedNumber" label="床号" width="80" />
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
        :total="checkoutApplicationTotal"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchCheckoutApplications(); }"
        @current-change="handleCheckoutApplicationPageChange" />
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="dialogVisible" title="退住申请审批" width="450px" align-center>
      <p style="text-align: center; font-size: 15px;">确定要通过或不通过此退住申请吗？</p>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmApprove('reject')">不通过</el-button>
        <el-button type="primary" @click="confirmApprove('pass')">通过</el-button>
      </template>
    </el-dialog>

    <!-- 管理员直接办理自理老人退住 -->
    <el-dialog v-model="directCheckoutDialogVisible" title="办理自理老人退住" width="520px" align-center
      @close="directCheckoutFormRef?.resetFields()">
      <el-form :model="directCheckoutForm" :rules="directCheckoutRules" ref="directCheckoutFormRef" label-width="100px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="directCheckoutForm.customerId" placeholder="请选择自理老人" filterable style="width: 100%"
            :loading="selfCareCustomerLoading" @change="handleDirectCustomerChange">
            <el-option v-for="item in selfCareCustomerList" :key="item.id" :label="formatCustomerOption(item)"
              :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="退住时间" prop="checkOutDate">
          <el-date-picker v-model="directCheckoutForm.checkOutDate" type="date" placeholder="选择退住日期"
            format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退住类型" prop="checkOutType">
          <el-select v-model="directCheckoutForm.checkOutType" placeholder="请选择退住类型" style="width: 100%">
            <el-option label="正常退住" value="正常退住" />
            <el-option label="死亡退住" value="死亡退住" />
          </el-select>
        </el-form-item>
        <el-form-item label="退住原因" prop="checkOutReason">
          <el-input v-model="directCheckoutForm.checkOutReason" type="textarea" :rows="3" placeholder="请输入退住原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="directCheckoutDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="directCheckoutSubmitting" @click="submitDirectCheckout">
          确认办理退住
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCheckOutList, approveCheckOut, getCustomerList, directCheckOut } from '@/api/customer'
import SvgIcon from '@/components/base/svg-icon/index.vue'

/** 退住申请列表查询条件。 */
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

/** 管理员退住审核列表。 */
const checkoutApplicationList = ref([])
const checkoutApplicationTotal = ref(0)

/** 护工退住申请审批弹窗状态。 */
const dialogVisible = ref(false)
const currentApplication = ref(null)

/** 自理老人直接退住办理弹窗状态。 */
const directCheckoutDialogVisible = ref(false)
const directCheckoutFormRef = ref()
const selfCareCustomerList = ref([])
const selfCareCustomerLoading = ref(false)
const directCheckoutSubmitting = ref(false)

/** 管理员直接办理自理老人退住的表单。 */
const directCheckoutForm = reactive({
  customerId: '',
  customerName: '',
  checkOutDate: '',
  checkOutType: '',
  checkOutReason: '',
})

/** 直接办理退住的表单校验规则。 */
const directCheckoutRules = {
  customerId: [{ required: true, message: '请选择自理老人', trigger: 'change' }],
  checkOutType: [{ required: true, message: '请选择退住类型', trigger: 'change' }],
  checkOutReason: [{ required: true, message: '请输入退住原因', trigger: 'blur' }],
  checkOutDate: [
    { required: true, message: '请选择退住日期', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        if (value && new Date(value) < today) {
          callback(new Error('退住日期不能是过去的日期'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

/** 查询退住申请列表，供管理员审批。 */
const fetchCheckoutApplications = async () => {
  const response = await getCheckOutList({
    pageNum: searchForm.pageNum,
    pageSize: searchForm.pageSize,
    customerName: searchForm.customerName,
  })
  checkoutApplicationList.value = response.records || []
  checkoutApplicationTotal.value = response.total || 0
}

/** 按客户姓名重新查询退住申请。 */
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchCheckoutApplications()
}

/** 处理退住申请列表页码变化。 */
const handleCheckoutApplicationPageChange = (page) => {
  searchForm.pageNum = page
  fetchCheckoutApplications()
}

/** 打开退住申请审批弹窗。 */
const handleApprove = (row) => {
  currentApplication.value = row
  dialogVisible.value = true
}

/** 拼接自理老人下拉选项文案，带上床位信息便于管理员确认。 */
const formatCustomerOption = (item) => {
  const bedText = item.buildingNumber && item.roomNumber && item.bedNumber
    ? `（${item.buildingNumber}楼 ${item.roomNumber}房 ${item.bedNumber}床）`
    : ''
  return `${item.customerName}${bedText}`
}

/** 重置自理老人直接退住表单。 */
const resetDirectCheckoutForm = () => {
  directCheckoutForm.customerId = ''
  directCheckoutForm.customerName = ''
  directCheckoutForm.checkOutDate = ''
  directCheckoutForm.checkOutType = ''
  directCheckoutForm.checkOutReason = ''
}

/** 查询当前在住的自理老人，供管理员直接办理退住时选择。 */
const fetchSelfCareCustomers = async () => {
  selfCareCustomerLoading.value = true
  try {
    const response = await getCustomerList({
      customerType: 'self-care',
      pageNum: 1,
      pageSize: 999,
    })
    selfCareCustomerList.value = response.records || []
  } catch (error) {
    console.error('获取自理老人列表失败:', error)
    ElMessage.error('获取自理老人列表失败')
  } finally {
    selfCareCustomerLoading.value = false
  }
}

/** 打开自理老人直接退住弹窗，并刷新可选客户列表。 */
const openDirectCheckoutDialog = async () => {
  resetDirectCheckoutForm()
  directCheckoutDialogVisible.value = true
  await fetchSelfCareCustomers()
}

/** 选择客户后保存客户姓名，提交时一并传给后端兼容现有请求结构。 */
const handleDirectCustomerChange = (customerId) => {
  const customer = selfCareCustomerList.value.find((item) => item.id === customerId)
  directCheckoutForm.customerName = customer?.customerName || ''
}

/** 提交管理员直接办理退住请求。 */
const submitDirectCheckout = async () => {
  try {
    await directCheckoutFormRef.value.validate()
    directCheckoutSubmitting.value = true
    await directCheckOut({
      customerId: directCheckoutForm.customerId,
      customerName: directCheckoutForm.customerName,
      checkOutDate: directCheckoutForm.checkOutDate,
      checkOutType: directCheckoutForm.checkOutType,
      checkOutReason: directCheckoutForm.checkOutReason,
    })
    ElMessage.success('退住办理成功')
    directCheckoutDialogVisible.value = false
    fetchCheckoutApplications()
  } catch (error) {
    if (error instanceof Error) {
      console.error('办理自理老人退住失败:', error)
    }
  } finally {
    directCheckoutSubmitting.value = false
  }
}

/** 提交退住申请审批结果。 */
const confirmApprove = async (action) => {
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
      await approveCheckOut(currentApplication.value.id, {
        approvalStatus: action === 'pass' ? '通过' : '不通过',
        checkOutType: currentApplication.value.checkOutType,
        buildingNumber: currentApplication.value.buildingNumber,
        roomNumber: currentApplication.value.roomNumber,
        bedNumber: currentApplication.value.bedNumber,
      })
      ElMessage.success('审批成功')
      fetchCheckoutApplications()
    } finally {
      dialogVisible.value = false
    }
  })
}

onMounted(() => {
  fetchCheckoutApplications()
})
</script>

<style scoped>
/* 样式由 el-ui.scss + app.scss 全局管理 */
</style>
