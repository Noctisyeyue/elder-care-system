<!-- 管理端--子菜单--入住登记 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户姓名" clearable @clear="queryCustomers"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="queryCustomers">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
          <el-button type="success" @click="openRegisterDialog">
            <SvgIcon icon="ri:user-add-line" :size="16" />
            <span>登记</span>
          </el-button>
          <el-button type="danger" :disabled="multipleSelection.length === 0" @click="deleteSelectedCustomers">
            <SvgIcon icon="ri:delete-bin-line" :size="16" />
            <span>批量删除</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">客户信息列表</span>
        <div class="flex-c" style="gap: 8px;">
          <el-button :type="activeCustomerType === 'self-care' ? 'primary' : ''" @click="handleTabChange('self-care')">
            <SvgIcon icon="ri:user-line" :size="16" />
            <span>自理老人</span>
          </el-button>
          <el-button :type="activeCustomerType === 'nursing-care' ? 'primary' : ''" @click="handleTabChange('nursing-care')">
            <SvgIcon icon="ri:heart-pulse-line" :size="16" />
            <span>护理老人</span>
          </el-button>
        </div>
      </div>
      <el-table :data="customerList" height="100%" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="bloodType" label="血型" width="60" />
        <el-table-column prop="nation" label="民族" width="60" />
        <el-table-column prop="familyMember" label="家属" min-width="90" />
        <el-table-column prop="familyMemberTel" label="家属电话" min-width="120">
          <template #default="{ row }">
            {{ maskPhone(row.familyMemberTel) }}
          </template>
        </el-table-column>
        <el-table-column prop="idNumber" label="身份证号" min-width="140">
          <template #default="{ row }">
            {{ maskIdNumber(row.idNumber) }}
          </template>
        </el-table-column>
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="dateOfBirth" label="出生日期" width="120" />
        <el-table-column prop="checkInDate" label="入住时间" width="120" />
        <el-table-column prop="nursingLevel" label="护理级别" min-width="90" />
        <el-table-column prop="caregiver" label="护工" min-width="90" />
        <el-table-column prop="physicalMentalStatus" label="身心状况" min-width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="editCustomer(scope.row)">
              <SvgIcon icon="ri:edit-line" :size="16" />
              <span>修改</span>
            </el-button>
            <el-button link type="danger" @click="handleDeleteCustomer(scope.row)">
              <SvgIcon icon="ri:delete-bin-line" :size="16" />
              <span>删除</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </el-card>

    <!-- 登记/修改对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '信息修改' : '入住登记'" width="650px" align-center
      @close="closeDialog">
      <el-form :model="customerForm" label-width="110px" :rules="formRules" ref="customerFormRef">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="customerForm.customerName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="民族" prop="nation">
              <el-input v-model="customerForm.nation" placeholder="请输入民族" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="楼栋" prop="buildingNumber">
              <el-select v-model="customerForm.buildingNumber" placeholder="请选择楼栋" style="width: 100%" @change="onBuildingChange">
                <el-option v-for="item in buildingOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-select v-model="customerForm.roomNumber" placeholder="请选择房间号" style="width: 100%" @change="getAvailableBeds">
                <el-option-group v-for="group in roomOptions" :key="group.label" :label="group.label">
                  <el-option v-for="item in group.options" :key="item.value" :label="item.label" :value="item.value" />
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input v-model="customerForm.age" placeholder="根据身份证号自动计算" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNumber">
              <el-select v-model="customerForm.bedNumber" placeholder="请选择床位号" style="width: 100%">
                <el-option v-for="item in bedOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-input v-model="customerForm.gender" disabled placeholder="根据身份证号自动识别" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入住时间" prop="checkInDate">
              <el-date-picker v-model="customerForm.checkInDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                :disabled="isEdit" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="dateOfBirth">
              <el-input v-model="customerForm.dateOfBirth" disabled placeholder="根据身份证号自动识别" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同到期时间" prop="contractEndDate">
              <el-date-picker v-model="customerForm.contractEndDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idNumber">
              <el-input v-model="customerForm.idNumber" @blur="parseIdNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家属联系电话" prop="familyMemberTel">
              <el-input v-model="customerForm.familyMemberTel" placeholder="请输入家属联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="血型" prop="bloodType">
              <el-select v-model="customerForm.bloodType" placeholder="请选择血型" style="width: 100%">
                <el-option label="A" value="A" />
                <el-option label="B" value="B" />
                <el-option label="O" value="O" />
                <el-option label="AB" value="AB" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家属" prop="familyMember">
              <el-input v-model="customerForm.familyMember" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身心状况" prop="physicalMentalStatus">
              <el-input v-model="customerForm.physicalMentalStatus" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="tel">
              <el-input v-model="customerForm.tel" placeholder="请输入客户联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerList, registerCustomer, updateCustomer, deleteCustomer, releaseCustomerBed } from '@/api/customer'
import { getRoomOptions, getRoomBeds, updateBedStatus } from '@/api/room'
import { BUILDING_OPTIONS, DEFAULT_BUILDING } from '@/utils/building'
import { maskIdNumber, maskPhone } from '@/utils/desensitize'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const buildingOptions = BUILDING_OPTIONS

const searchForm = reactive({
  customerName: '',
  customerType: 'self-care',
})

const customerList = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

const dialogVisible = ref(false)
const isEdit = ref(false)
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
  buildingNumber: DEFAULT_BUILDING,
  roomNumber: '',
  bedNumber: '',
  checkInDate: '',
  contractEndDate: '',
  physicalMentalStatus: '',
  nursingLevel: '',
  caregiver: '',
  customerType: 'self-care',
  nation: '',
  tel: '',
})
const customerFormRef = ref()

const roomOptions = ref([])
const bedOptions = ref([])
const activeCustomerType = ref('self-care')

const checkInDateRule = [
  { required: true, message: '请选择入住时间', trigger: 'change' },
  {
    validator: (rule, value, callback) => {
      if (!value) { callback(); return }
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
  buildingNumber: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  bedNumber: [{ required: true, message: '请选择床位号', trigger: 'change' }],
  checkInDate: checkInDateRule,
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

const multipleSelection = ref([])

const queryCustomers = async () => {
  try {
    const params = {
      customerName: searchForm.customerName,
      customerType: searchForm.customerType,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    }
    const res = await getCustomerList(params)
    customerList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取客户列表失败：', error)
  }
}

const openRegisterDialog = () => {
  isEdit.value = false
  resetForm()
  formRules.checkInDate = checkInDateRule
  dialogVisible.value = true
  customerFormRef.value?.clearValidate()
  fetchRoomOptions()
}

const editCustomer = (row) => {
  isEdit.value = true
  Object.assign(customerForm, row)
  formRules.checkInDate = []
  dialogVisible.value = true
  customerFormRef.value?.clearValidate()
  fetchRoomOptions()
  getAvailableBeds(customerForm.roomNumber, true)
}

const handleDeleteCustomer = (row) => {
  ElMessageBox.confirm(`确定要删除客户 ${row.customerName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await deleteCustomer(row.id)
      await updateBedStatus(row.buildingNumber, row.roomNumber, row.bedNumber, 'free')
      await releaseCustomerBed(row.id, row.bedNumber)
      ElMessage.success('删除成功！')
      queryCustomers()
    })
    .catch(() => { ElMessage.info('已取消删除') })
}

const handleSizeChange = (val) => { pageSize.value = val; queryCustomers() }
const handleCurrentChange = (val) => { currentPage.value = val; queryCustomers() }

const handleTabChange = (name) => {
  activeCustomerType.value = name
  searchForm.customerType = name
  currentPage.value = 1
  queryCustomers()
}

const fetchRoomOptions = async () => {
  try {
    const res = await getRoomOptions(customerForm.buildingNumber)
    roomOptions.value = res
  } catch (error) {
    console.error('获取房间选项失败：', error)
  }
}

const onBuildingChange = () => {
  customerForm.roomNumber = ''
  customerForm.bedNumber = ''
  bedOptions.value = []
  fetchRoomOptions()
}

const getAvailableBeds = async (roomNumber, keepCurrentBed = false) => {
  const previousBed = keepCurrentBed ? String(customerForm.bedNumber || '').replace(/号床$/, '') : ''
  if (!keepCurrentBed) customerForm.bedNumber = ''
  try {
    const res = await getRoomBeds(roomNumber, customerForm.buildingNumber)
    bedOptions.value = (res || []).map((bed) => ({
      ...bed,
      label: bed.label ? bed.label : `${bed.value}号床`,
    }))
    if (isEdit.value && previousBed && !bedOptions.value.some((bed) => String(bed.value) === previousBed)) {
      bedOptions.value.push({ value: previousBed, label: `${previousBed}号床` })
    }
    if (previousBed) customerForm.bedNumber = previousBed
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
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) age--
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
          await updateCustomer(customerForm.id, customerForm)
          ElMessage.success('修改成功！')
        } else {
          customerForm.customerType = customerForm.customerType === 'self-care' ? '0' : '1'
          await registerCustomer(customerForm)
          ElMessage.success('登记成功！')
        }
        dialogVisible.value = false
        queryCustomers()
      } catch (error) {
        console.error('保存客户信息失败：', error)
      }
    } else {
      ElMessage.error('请检查表单填写！')
    }
  })
}

const resetForm = () => {
  customerFormRef.value?.resetFields()
  bedOptions.value = []
  Object.assign(customerForm, {
    id: null, customerName: '', age: null, gender: '', dateOfBirth: '', idNumber: '',
    bloodType: 'O', familyMember: '', familyMemberTel: '', buildingNumber: DEFAULT_BUILDING,
    roomNumber: '', bedNumber: '', checkInDate: '', contractEndDate: '',
    physicalMentalStatus: '', nursingLevel: '', caregiver: '', nation: '', tel: '',
  })
}

const handleSelectionChange = (val) => { multipleSelection.value = val }

const deleteSelectedCustomers = () => {
  if (multipleSelection.value.length === 0) { ElMessage.warning('请先选择要删除的客户'); return }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 位客户吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  })
    .then(async () => {
      for (const row of multipleSelection.value) {
        await deleteCustomer(row.id)
        await updateBedStatus(row.buildingNumber, row.roomNumber, row.bedNumber, 'free')
        await releaseCustomerBed(row.id, row.bedNumber)
      }
      ElMessage.success('批量删除成功！')
      queryCustomers()
    })
    .catch(() => { ElMessage.info('已取消删除') })
}

const closeDialog = () => {
  dialogVisible.value = false
  customerFormRef.value?.clearValidate()
}

function parseIdNumber() {
  const id = customerForm.idNumber
  if (!id || id.length < 18) { customerForm.gender = ''; customerForm.dateOfBirth = ''; customerForm.age = null; return }
  customerForm.dateOfBirth = `${id.substring(6, 10)}-${id.substring(10, 12)}-${id.substring(12, 14)}`
  customerForm.gender = id.charAt(16) % 2 === 1 ? '男' : '女'
  calculateAge(customerForm.dateOfBirth)
}

onMounted(() => { queryCustomers() })
</script>

<style scoped>
/* 样式由 el-ui.scss + app.scss 全局管理 */
</style>
