<template>
  <div>
    <!-- 查询区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item>
        <el-input
          v-model="searchForm.customerName"
          placeholder="请输入客户姓名"
          clearable
          @clear="queryCustomers"
          style="width: 200px; margin-right: 10px"
          ><template #prefix>
            <el-icon><Search /></el-icon> </template
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="queryCustomers">查询</el-button>
        <el-button type="success" @click="openRegisterDialog">登记</el-button>
        <el-button
          type="danger"
          :disabled="multipleSelection.length === 0"
          @click="deleteSelectedCustomers"
          >批量删除</el-button
        >
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />
    <!-- 客户信息数据列表 -->
    <el-tabs v-model="activeCustomerType" @tab-change="handleTabChange">
      <el-tab-pane label="自理老人" name="self-care"></el-tab-pane>
      <el-tab-pane label="护理老人" name="nursing-care"></el-tab-pane>
    </el-tabs>

    <el-table
      :data="customerList"
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="customerName" label="客户姓名" width="90"></el-table-column>
      <el-table-column prop="gender" label="性别" width="60"></el-table-column>
      <el-table-column prop="bloodType" label="血型" width="60"></el-table-column>
      <el-table-column prop="nation" label="民族" width="60"></el-table-column>
      <el-table-column prop="familyMember" label="家属" width="90"></el-table-column>
      <el-table-column prop="familyMemberTel" label="家属电话" width="120"></el-table-column>
      <el-table-column prop="idNumber" label="身份证号" width="120"></el-table-column>
      <el-table-column prop="buildingNumber" label="楼号" width="60"></el-table-column>
      <el-table-column prop="roomNumber" label="房间号" width="90"></el-table-column>
      <el-table-column prop="bedNumber" label="床号" width="60"></el-table-column>
      <el-table-column prop="dateOfBirth" label="出生日期" width="120"></el-table-column>
      <el-table-column prop="checkInDate" label="入住时间" width="120"></el-table-column>
      <el-table-column prop="nursingLevel" label="护理级别" width="90"></el-table-column>
      <el-table-column prop="caregiver" label="护工" width="90"></el-table-column>
      <el-table-column prop="physicalMentalStatus" label="身心状况" width="90"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button type="text" @click="editCustomer(scope.row)">修改</el-button>
          <el-button type="text" style="color: red" @click="deleteCustomer(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination-right"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      :current-page="currentPage"
      :page-sizes="[5, 10, 20, 50]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <!-- 登记/修改模态框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '信息修改' : '入住登记'" width="60%">
      <el-form :model="customerForm" label-width="120px" :rules="formRules" ref="customerFormRef">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="customerForm.customerName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="民族" prop="nation">
              <el-input v-model="customerForm.nation" placeholder="请输入民族"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="楼栋" prop="buildingNumber">
              <el-input v-model="customerForm.buildingNumber" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-select
                v-model="customerForm.roomNumber"
                placeholder="请选择房间号"
                @change="getAvailableBeds"
              >
                <el-option-group
                  v-for="group in roomOptions"
                  :key="group.label"
                  :label="group.label"
                >
                  <el-option
                    v-for="item in group.options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input
                v-model="customerForm.age"
                placeholder="根据身份证号自动计算"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNumber">
              <el-select v-model="customerForm.bedNumber" placeholder="请选择床位号">
                <el-option
                  v-for="item in bedOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-input v-model="customerForm.gender" disabled placeholder="根据身份证号自动识别"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入住时间" prop="checkInDate">
              <el-date-picker
                v-model="customerForm.checkInDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                :disabled="isEdit"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="dateOfBirth">
              <el-input v-model="customerForm.dateOfBirth" disabled placeholder="根据身份证号自动识别"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同到期时间" prop="contractEndDate">
              <el-date-picker
                v-model="customerForm.contractEndDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idNumber">
              <el-input v-model="customerForm.idNumber" @blur="parseIdNumber"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家属联系电话" prop="familyMemberTel">
              <el-input v-model="customerForm.familyMemberTel" placeholder="请输入家属联系电话"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="血型" prop="bloodType">
              <el-radio-group v-model="customerForm.bloodType">
                <el-radio label="A">A</el-radio>
                <el-radio label="B">B</el-radio>
                <el-radio label="O">O</el-radio>
                <el-radio label="AB">AB</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家属" prop="familyMember">
              <el-input v-model="customerForm.familyMember"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="身心状况" prop="physicalMentalStatus">
              <el-input v-model="customerForm.physicalMentalStatus"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户联系电话" prop="tel">
              <el-input v-model="customerForm.tel" placeholder="请输入客户联系电话"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="submitForm">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post, put, del } from '@/utils/request'
import { Search } from '@element-plus/icons-vue'
// 搜索表单数据
const searchForm = reactive({
  customerName: '',
  customerType: 'self-care', // 默认查询自理老人
})

// 客户信息列表数据
const customerList = ref([
  {
    id: 1,
    customerName: '李福星',
    gender: '男',
    bloodType: 'O',
    familyMember: '小小力',
    dateOfBirth: '1948-01-01',
    familyMemberTel: '13812345678',
    idNumber: '110101194801011234',
    buildingNumber: '606',
    roomNumber: '101',
    bedNumber: '1',
    checkInDate: '2023-01-01',
    contractEndDate: '2024-01-01',
    physicalMentalStatus: '良好',
    nursingLevel: '',
    caregiver: '',
    nation:'',
    customerType: 'self-care',
  },
])

// 分页数据
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

// 登记/修改模态框相关
const dialogVisible = ref(false)
const isEdit = ref(false) // 标记是否为修改操作
const customerForm = reactive({
  id: null,
  customerName: '',
  age: null,
  gender: '男',
  dateOfBirth: '',
  idNumber: '',
  bloodType: 'O',
  familyMember: '',
  familyMemberTel: '',
  buildingNumber: '606', // 楼栋固定为 606
  roomNumber: '',
  bedNumber: '',
  checkInDate: '',
  contractEndDate: '',
  physicalMentalStatus: '',
  nursingLevel: '', // 护理级别，登记时不展示，修改时可能需要
  caregiver: '', // 护工，登记时不展示，修改时可能需要
  customerType: 'self-care',
  nation: '', // 新增民族
  tel: '', // 新增客户联系电话
})
const customerFormRef = ref()

// 房间号和床位号选项
const roomOptions = ref([])
const bedOptions = ref([])
const oldbednumber = ref()
const num = ref()

// 当前激活的老人类型 Tab
const activeCustomerType = ref('self-care')

// 表单验证规则
const checkInDateRule = [
  { required: true, message: '请选择入住时间', trigger: 'change' },
  {
    validator: (rule, value, callback) => {
      if (!value) {
        callback()
        return
      }
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const selected = new Date(value)
      selected.setHours(0, 0, 0, 0)
      if (selected < today) {
        callback(new Error('入住时间不能小于今天'))
      } else {
        callback()
      }
    },
    trigger: 'change',
  },
]
const formRules = reactive({
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  nation: [{ required: true, message: '请输入民族', trigger: 'blur' }],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|X|x)$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  tel: [
    { required: true, message: '请输入客户联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  roomNumber: [{ required: true, message: '请选择房间号', trigger: 'change' }],
  bedNumber: [{ required: true, message: '请选择床位号', trigger: 'change' }],
  checkInDate: checkInDateRule, // 默认登记规则
  contractEndDate: [
    { required: true, message: '请选择合同到期时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (customerForm.checkInDate && value < customerForm.checkInDate) {
          callback(new Error('合同到期时间不能小于入住时间'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
  familyMemberTel: [
    { required: true, message: '请输入家属联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
})

// 选中的多行
const multipleSelection = ref([])

// 方法
const queryCustomers = async () => {
  try {
    const params = {
      customerName: searchForm.customerName,
      customerType: searchForm.customerType,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    }
    const res = await get('/customer/list', params)
    customerList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取客户列表失败：', error)
  }
}

const openRegisterDialog = () => {

  isEdit.value = false
  resetForm()
  // 入住时间需要验证
  formRules.checkInDate = checkInDateRule
  dialogVisible.value = true
  customerFormRef.value?.clearValidate()
  getRoomOptions() // 获取房间号选项
  customerFormRef.value?.clearValidate()

}

const editCustomer = (row) => {
  isEdit.value = true
  num.value = row.bedNumber
  row.bedNumber = row.bedNumber +'号床'
  Object.assign(customerForm, row) // 填充表单数据
  row.bedNumber = num.value
  // 修改时不需要入住时间验证
  formRules.checkInDate = []
  dialogVisible.value = true
  customerFormRef.value?.clearValidate()
  getRoomOptions() // 获取房间号选项
  getAvailableBeds(customerForm.roomNumber)
  customerFormRef.value?.clearValidate()// 获取当前房间的床位
}

const deleteCustomer = (row) => {
  ElMessageBox.confirm(`确定要删除客户 ${row.customerName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await del(`/customer/${row.id}`)
      await put(`/room/${row.roomNumber}/bed/${row.bedNumber}/status`, { status: 'free' })
      await del(`/customer/${row.id}/bed/${row.bedNumber}`)
      ElMessage.success('删除成功！')
      queryCustomers()
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  queryCustomers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  queryCustomers()
}

const handleTabChange = (name) => {
  searchForm.customerType = name
  queryCustomers()
}

const getRoomOptions = async () => {
  try {
    const res = await get('/room/options')
    roomOptions.value = res
  } catch (error) {
    console.error('获取房间选项失败：', error)
  }
}

const getAvailableBeds = async (roomNumber) => {
  // 清空当前的床位号选择
  customerForm.bedNumber = '';
  try {
    const res = await get(`/room/${roomNumber}/beds`)
    bedOptions.value = (res || []).map(bed => ({
      ...bed,
      label: bed.label ? bed.label : `${bed.value}号床`,
    }))
    oldbednumber.value = customerForm.bedNumber
    // 如果是修改操作，且当前床位不在可用床位中，则清空床位选择
    if (isEdit.value && !bedOptions.value.some((bed) => bed.value === customerForm.bedNumber)) {
      customerForm.bedNumber = oldbednumber.value
    }
  } catch (error) {
    console.error('获取可用床位失败：', error)
  }
}

const calculateAge = (dateString) => {
  if (dateString) {
    const today = new Date()
    const birthDate = new Date(dateString)
    let age = today.getFullYear() - birthDate.getFullYear()
    const m = today.getMonth() - birthDate.getMonth()
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
      age--
    }
    customerForm.age = age
  } else {
    customerForm.age = null
  }
}

const submitForm = () => {
  customerFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          // 修改操作
          await put(`/customer/update/${customerForm.id}`, customerForm)
          ElMessage.success('修改成功！')
        } else {
          // 登记操作
          if(customerForm.customerType=='self-care'){
            customerForm.customerType='0'
          }else{
            customerForm.customerType='1'
          }
          await post('/customer/register', customerForm)
          ElMessage.success('登记成功！')
        }
        dialogVisible.value = false
        queryCustomers()
      } catch (error) {
        console.error('保存客户信息失败：', error)
      }
    } else {
      ElMessage.error('请检查表单填写！')
      return false
    }
  })
}

const resetForm = () => {
  customerFormRef.value?.resetFields()
  bedOptions.value=[]
  Object.assign(customerForm, {
    id: null,
    customerName: '',
    age: null,
    gender: '',
    dateOfBirth: '',
    idNumber: '',
    bloodType: 'O',
    familyMember: '',
    familyMemberTel: '',
    buildingNumber: '606',
    roomNumber: '',
    bedNumber: '',
    checkInDate: '',
    contractEndDate: '',
    physicalMentalStatus: '',
    nursingLevel: '',
    caregiver: '',
    nation: '',
    tel: '',
  })
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const deleteSelectedCustomers = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请先选择要删除的客户')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 位客户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      for (const row of multipleSelection.value) {
        await del(`/customer/${row.id}`)
        await put(`/room/${row.roomNumber}/bed/${row.bedNumber}/status`, { status: 'free' })
        await del(`/customer/${row.id}/bed/${row.bedNumber}`)
      }
      ElMessage.success('批量删除成功！')
      queryCustomers()
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

const closeDialog = () => {
  dialogVisible.value = false
  customerFormRef.value?.clearValidate()
}

// 新增身份证号解析函数
function parseIdNumber() {
  const id = customerForm.idNumber
  if (!id || id.length < 18) {
    customerForm.gender = ''
    customerForm.dateOfBirth = ''
    customerForm.age = null
    return
  }
  // 出生日期
  const year = id.substring(6, 10)
  const month = id.substring(10, 12)
  const day = id.substring(12, 14)
  customerForm.dateOfBirth = `${year}-${month}-${day}`
  // 性别
  const genderCode = id.charAt(16)
  customerForm.gender = genderCode % 2 === 1 ? '男' : '女'
  // 年龄
  calculateAge(customerForm.dateOfBirth)
}

// 初始化查询数据
onMounted(() => {
  queryCustomers()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 363px);
  overflow-x: auto;
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
