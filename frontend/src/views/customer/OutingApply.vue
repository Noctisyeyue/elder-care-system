<!-- 护工端--子菜单--外出申请 -->
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
          <el-button type="success" @click="goToOutingList">
            <SvgIcon icon="ri:file-list-3-line" :size="16" />
            <span>查看外出申请</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">服务客户列表</span>
      </div>
      <el-table :data="customerInfoList" height="100%" stripe style="width: 100%" v-loading="loading" element-loading-text="加载中..."
        :empty-text="loading ? '加载中...' : '暂无数据'">
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
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showAddOutingDialog(row)">
              <SvgIcon icon="ri:walk-line" :size="14" />
              <span>外出申请</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="customerInfoTotal"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleCustomerInfoSizeChange"
        @current-change="handleCustomerInfoPageChange" />
    </el-card>

    <!-- 添加外出申请对话框 -->
    <el-dialog v-model="addOutingDialogVisible" title="添加外出申请" width="500px" align-center
      @close="outingFormRef?.resetFields()">
      <el-form :model="outingForm" :rules="outingRules" ref="outingFormRef" label-width="110px">
        <el-form-item label="客户姓名">
          <el-input v-model="outingForm.customerName" disabled />
        </el-form-item>
        <el-form-item label="外出事由" prop="outingReason">
          <el-input v-model="outingForm.outingReason" type="textarea" :rows="3" placeholder="请输入外出事由" />
        </el-form-item>
        <el-form-item label="外出时间" prop="outingDate">
          <el-date-picker v-model="outingForm.outingDate" type="date" placeholder="选择外出日期" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计回院时间" prop="returnDate">
          <el-date-picker v-model="outingForm.returnDate" type="date" placeholder="选择预计回院日期" format="YYYY-MM-DD"
            value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="陪同人" prop="companion">
          <el-input v-model="outingForm.companion" placeholder="请输入陪同人姓名" />
        </el-form-item>
        <el-form-item label="与老人关系" prop="relationship">
          <el-input v-model="outingForm.relationship" placeholder="请输入与老人关系" />
        </el-form-item>
        <el-form-item label="陪同人电话" prop="companionPhone">
          <el-input v-model="outingForm.companionPhone" placeholder="请输入陪同人电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOutingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOutingApplication">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCustomers, applyOuting } from '@/api/customer'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

const customerInfoList = ref([])
const customerInfoTotal = ref(0)

const outingForm = reactive({
  customerName: '',
  customerId: '',
  outingReason: '',
  outingDate: '',
  returnDate: '',
  companion: '',
  relationship: '',
  companionPhone: '',
})

watch(
  () => outingForm.outingDate,
  () => {
    if (outingForm.returnDate) {
      outingFormRef.value?.validateField('returnDate')
    }
  },
)

const outingRules = {
  outingReason: [{ required: true, message: '请输入外出事由', trigger: 'blur' }],
  outingDate: [
    { required: true, message: '请选择外出日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        if (value && new Date(value) < today) {
          callback(new Error('外出日期不能是过去的日期'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
  returnDate: [
    { required: true, message: '请选择预计回院日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (outingForm.outingDate && value && new Date(value) <= new Date(outingForm.outingDate)) {
          callback(new Error('预计回院日期必须晚于外出日期'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
  companion: [],
  relationship: [],
  companionPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
}

const addOutingDialogVisible = ref(false)
const outingFormRef = ref()
const loading = ref(false)

const fetchMyCustomers = async () => {
  loading.value = true
  try {
    const response = await getMyCustomers({
      pageNum: searchForm.pageNum,
      pageSize: searchForm.pageSize,
      customerName: searchForm.customerName,
    })
    customerInfoList.value = response.list || []
    customerInfoTotal.value = response.total || 0
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.pageNum = 1
  fetchMyCustomers()
}

const handleCustomerInfoPageChange = (page) => {
  searchForm.pageNum = page
  fetchMyCustomers()
}

const showAddOutingDialog = (row) => {
  outingForm.customerName = row.customerName
  outingForm.customerId = row.id
  outingForm.outingReason = ''
  outingForm.outingDate = ''
  outingForm.returnDate = ''
  outingForm.companion = ''
  outingForm.relationship = ''
  outingForm.companionPhone = ''
  addOutingDialogVisible.value = true
}

const submitOutingApplication = async () => {
  try {
    await outingFormRef.value.validate()
    await applyOuting({
      customerId: outingForm.customerId,
      customerName: outingForm.customerName,
      outingReason: outingForm.outingReason,
      outingDate: outingForm.outingDate,
      returnDate: outingForm.returnDate,
      companion: outingForm.companion,
      relationship: outingForm.relationship,
      companionPhone: outingForm.companionPhone,
    })
    ElMessage.success('外出申请提交成功')
    addOutingDialogVisible.value = false
    fetchMyCustomers()
  } catch (error) {
    console.error('提交外出申请失败:', error)
    ElMessage.error(error.message || '提交外出申请失败，请重试')
  }
}

const goToOutingList = () => {
  router.push('/customer/outingList')
}

const handleCustomerInfoSizeChange = (size) => {
  searchForm.pageSize = size
  searchForm.pageNum = 1
  fetchMyCustomers()
}

onMounted(() => {
  fetchMyCustomers()
})
</script>

<style scoped>
/* 样式由 el-ui.scss + app.scss 全局管理 */
</style>
