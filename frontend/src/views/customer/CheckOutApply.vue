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
          style="width: 200px; margin-right: 10px">
          <template #prefix>
            <el-icon><Search /></el-icon> </template>
          </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="goToCheckOutList">查看退住申请</el-button>
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
          <el-button type="primary" size="small" @click="showAddCheckOutDialog(row)">
            退住申请
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
        @size-change="(size) => { searchForm.pageSize = size; searchForm.pageNum = 1; fetchMyCustomers(); }"
        @current-change="handleCustomerInfoPageChange"
      />
    </div>

    <!-- 添加退住申请对话框 -->
    <el-dialog v-model="addCheckOutDialogVisible" title="添加退住申请" width="50%">
      <el-form
        :model="checkOutForm"
        :rules="checkOutRules"
        ref="checkOutFormRef"
        label-width="120px"
      >
        <el-form-item label="客户姓名">
          <el-input v-model="checkOutForm.customerName" disabled></el-input>
        </el-form-item>
        <el-form-item label="退住时间" prop="checkOutDate">
          <el-date-picker
            v-model="checkOutForm.checkOutDate"
            type="date"
            placeholder="选择退住日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="退住类型" prop="checkOutType">
          <el-select
            v-model="checkOutForm.checkOutType"
            placeholder="请选择退住类型"
            style="width: 100%"
          >
            <el-option label="正常退住" value="正常退住"></el-option>
            <el-option label="死亡退住" value="死亡退住"></el-option>
            <el-option label="保留床位" value="保留床位"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="退住原因" prop="checkOutReason">
          <el-input
            v-model="checkOutForm.checkOutReason"
            type="textarea"
            :rows="3"
            placeholder="请输入退住原因"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addCheckOutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCheckOutApplication">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCustomers, applyCheckOut } from '@/api/customer'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

/** 查询表单。 */
const searchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

/** 客户列表。 */
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

/** 退住申请表单。 */
const checkOutForm = reactive({
  customerName: '',
  customerId: '',
  checkOutType: '',
  checkOutReason: '',
  checkOutDate: '',
})

/** 退住申请表单校验规则。 */
const checkOutRules = {
  checkOutType: [{ required: true, message: '请选择退住类型', trigger: 'change' }],
  checkOutReason: [{ required: true, message: '请输入退住原因', trigger: 'blur' }],
  checkOutDate: [
    { required: true, message: '请选择退住日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
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

/** 弹窗是否显示。 */
const addCheckOutDialogVisible = ref(false)
const checkOutFormRef = ref()
const loading = ref(false)

/**
 * 查询当前服务客户列表。
 *
 * @returns 无返回值
 */
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

/**
 * 执行查询。
 *
 * @returns 无返回值
 */
const handleSearch = () => {
  searchForm.pageNum = 1
  fetchMyCustomers()
}

/**
 * 处理分页变化。
 *
 * @param page 页码
 * @returns 无返回值
 */
const handleCustomerInfoPageChange = (page) => {
  searchForm.pageNum = page
  fetchMyCustomers()
}

/**
 * 打开退住申请弹窗。
 *
 * @param row 客户行数据
 * @returns 无返回值
 */
const showAddCheckOutDialog = (row) => {
  checkOutForm.customerName = row.customerName
  checkOutForm.customerId = row.id
  checkOutForm.checkOutType = ''
  checkOutForm.checkOutReason = ''
  checkOutForm.checkOutDate = ''
  addCheckOutDialogVisible.value = true
}

/**
 * 提交退住申请。
 *
 * @returns 无返回值
 */
const submitCheckOutApplication = async () => {
  try {
    await checkOutFormRef.value.validate()

    const response = await applyCheckOut({
      customerId: checkOutForm.customerId,
      customerName: checkOutForm.customerName,
      checkOutType: checkOutForm.checkOutType,
      checkOutReason: checkOutForm.checkOutReason,
      checkOutDate: checkOutForm.checkOutDate,
    })

    ElMessage.success('退住申请提交成功')
    addCheckOutDialogVisible.value = false
    fetchMyCustomers()
  } catch (error) {
    console.error('提交退住申请失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('提交退住申请失败，请重试')
    }
  }
}

/**
 * 跳转到退住申请列表页。
 *
 * @returns 无返回值
 */
const goToCheckOutList = () => {
  router.push('/customer/checkOutList')
}

/**
 * 页面初始化时加载客户列表。
 *
 * @returns 无返回值
 */
onMounted(() => {
  fetchMyCustomers()
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
