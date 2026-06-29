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
        <el-button type="success" @click="goToOutingList">查看外出申请</el-button>
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />

    <!-- 客户信息表格 -->
    <el-table
      :data="customerInfoList"
      border
      style="width: 100%"
      v-loading="loading"
      element-loading-text="加载中..."
      :empty-text="loading ? '加载中...' : '暂无数据'"
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="customerName" label="客户姓名" min-width="100" />
      <el-table-column prop="age" label="年龄" min-width="60" />
      <el-table-column prop="gender" label="性别" min-width="60" />
      <el-table-column prop="buildingNumber" label="所属楼房" min-width="100" />
      <el-table-column prop="roomNumber" label="房间号" min-width="80" />
      <el-table-column prop="bedNumber" label="床号" min-width="60" />
      <el-table-column prop="tel" label="联系电话" min-width="140" />
      <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="showAddOutingDialog(row)">
            外出申请
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination
        class="pagination-right"
        layout="total, sizes, prev, pager, next, jumper"
        :total="customerInfoTotal"
        :page-size="searchForm.pageSize"
        :current-page="searchForm.pageNum"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleCustomerInfoSizeChange"
        @current-change="handleCustomerInfoPageChange"
      />
    </div>

    <!-- 添加外出申请对话框 -->
    <el-dialog v-model="addOutingDialogVisible" title="添加外出申请" width="50%">
      <el-form :model="outingForm" :rules="outingRules" ref="outingFormRef" label-width="120px">
        <el-form-item label="客户姓名">
          <el-input v-model="outingForm.customerName" disabled></el-input>
        </el-form-item>
        <el-form-item label="外出事由" prop="outingReason">
          <el-input
            v-model="outingForm.outingReason"
            type="textarea"
            :rows="3"
            placeholder="请输入外出事由"
          ></el-input>
        </el-form-item>
        <el-form-item label="外出时间" prop="outingDate">
          <el-date-picker
            v-model="outingForm.outingDate"
            type="date"
            placeholder="选择外出日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="预计回院时间" prop="returnDate">
          <el-date-picker
            v-model="outingForm.returnDate"
            type="date"
            placeholder="选择预计回院日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="陪同人" prop="companion">
          <el-input v-model="outingForm.companion" placeholder="请输入陪同人姓名"></el-input>
        </el-form-item>
        <el-form-item label="与老人关系" prop="relationship">
          <el-input v-model="outingForm.relationship" placeholder="请输入与老人关系"></el-input>
        </el-form-item>
        <el-form-item label="陪同人电话" prop="companionPhone">
          <el-input v-model="outingForm.companionPhone" placeholder="请输入陪同人电话"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addOutingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOutingApplication">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCustomers, applyOuting } from '@/api/customer'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 查询表单数据
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

// 客户信息列表数据
const customerInfoList = ref([
  {
    id: 1,
    customerName: '李福星',
    age: 10,
    gender: '男',
    bedNumber: '1001-1',
    nursingLevel: '一级',
  },
])
const customerInfoTotal = ref(0)

// 外出申请表单数据
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

// 监听外出时间变化，重新验证预计回院时间
watch(
  () => outingForm.outingDate,
  () => {
    if (outingForm.returnDate) {
      outingFormRef.value?.validateField('returnDate')
    }
  },
)

// 表单验证规则
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

// 对话框相关
const addOutingDialogVisible = ref(false)
const outingFormRef = ref()
const loading = ref(false)

// 获取我的服务客户列表
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

// 处理查询
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchMyCustomers()
}

// 客户信息分页变化
const handleCustomerInfoPageChange = (page) => {
  searchForm.pageNum = page
  fetchMyCustomers()
}

// 显示添加外出申请对话框
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

// 提交外出申请
const submitOutingApplication = async () => {
  try {
    await outingFormRef.value.validate()

    const response = await applyOuting({
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
    // 清空表单
    outingForm.outingReason = ''
    outingForm.outingDate = ''
    outingForm.returnDate = ''
    outingForm.companion = ''
    outingForm.relationship = ''
    outingForm.companionPhone = ''
    fetchMyCustomers() // 刷新客户列表
  } catch (error) {
    console.error('提交外出申请失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('提交外出申请失败，请重试')
    }
  }
}

// 跳转到外出申请列表页面
const goToOutingList = () => {
  router.push('/customer/outingList')
}

// 页面加载时获取数据
onMounted(() => {
  fetchMyCustomers()
})

const handleCustomerInfoSizeChange = (size) => {
  searchForm.pageSize = size
  searchForm.pageNum = 1
  fetchMyCustomers()
}
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}

.pagination-container {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-table {
  margin-bottom: 20px;
}

.el-date-picker {
  width: 100%;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 306px);
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
