<!-- 管理端--子菜单--床位管理 -->
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
        <el-form-item label="入住日期">
          <el-date-picker v-model="searchForm.checkInDate" type="date" placeholder="请选择日期" value-format="YYYY-MM-DD"
            clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">{{ searchForm.history === 1 ? '床位使用列表' : '使用历史' }}</span>
        <div class="flex-c" style="gap: 8px;">
          <el-button :type="searchForm.history === 1 ? 'primary' : ''" @click="searchByStatus(1)">
            <SvgIcon icon="ri:checkbox-circle-line" :size="16" />
            <span>正在使用</span>
          </el-button>
          <el-button :type="searchForm.history === 0 ? 'primary' : ''" @click="searchByStatus(0)">
            <SvgIcon icon="ri:history-line" :size="16" />
            <span>使用历史</span>
          </el-button>
        </div>
      </div>
      <el-table :data="bedList" height="100%" style="width: 100%; font-size: 14px;" size="default" stripe>
        <el-table-column type="selection" width="50" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" />
        <el-table-column prop="gender" label="性别" width="90" />
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="usageStartDate" label="床位使用起始时间" />
        <el-table-column prop="usageEndDate" label="床位使用结束时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <template v-if="searchForm.history === 1">
              <el-button link type="primary" @click="handleEdit(scope.row)">
                <SvgIcon icon="ri:edit-line" :size="16" />
                <span>修改</span>
              </el-button>
              <el-button link type="primary" @click="handleChangeBed(scope.row)">
                <SvgIcon icon="ri:arrow-left-right-line" :size="16" />
                <span>床位调换</span>
              </el-button>
            </template>
            <template v-else> - </template>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize" :page-sizes="[5, 10, 20, 50]"
        layout="total, prev, pager, next, sizes, jumper" :total="pagination.total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </el-card>

    <!-- 修改信息弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改信息" width="500px" align-center @close="editFormRef?.resetFields()">
      <el-form :model="editForm" label-width="140px" :rules="editFormRules" ref="editFormRef">
        <el-form-item label="客户姓名">
          <el-input v-model="editForm.customerName" disabled />
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="editForm.gender" disabled />
        </el-form-item>
        <el-form-item label="楼号">
          <el-input v-model="editForm.buildingNumber" disabled />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="editForm.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="床号">
          <el-input v-model="editForm.bedNumber" disabled />
        </el-form-item>
        <el-form-item label="床位使用起始日期">
          <el-input v-model="editForm.usageStartDate" disabled />
        </el-form-item>
        <el-form-item label="床位使用结束日期" prop="usageEndDate">
          <el-date-picker v-model="editForm.usageEndDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 床位调换弹窗 -->
    <el-dialog v-model="changeBedDialogVisible" title="床位调换" width="650px" align-center
      @close="changeBedFormRef?.resetFields()">
      <el-form :model="changeBedForm" label-width="160px" :rules="changeBedFormRules" ref="changeBedFormRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户姓名">
              <el-input v-model="changeBedForm.customerName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-input v-model="changeBedForm.gender" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="旧楼号">
              <el-input v-model="changeBedForm.oldBuildingNumber" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="旧房间号">
              <el-input v-model="changeBedForm.oldRoomNumber" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="旧床号">
          <el-input v-model="changeBedForm.oldBedNumber" disabled style="width: calc(50% - 10px)" />
        </el-form-item>
        <el-divider />
        <el-form-item label="新楼号" prop="newBuildingNumber">
          <el-select v-model="changeBedForm.newBuildingNumber" placeholder="请选择楼栋" style="width: 100%"
            @change="onSwapBuildingChange">
            <el-option v-for="item in buildingOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="新房间号">
          <el-select v-model="changeBedForm.newRoomNumber" placeholder="请选择房间号" style="width: 100%"
            @change="getAvailableBeds">
            <el-option-group v-for="group in roomOptions" :key="group.label" :label="group.label">
              <el-option v-for="item in group.options" :key="item.value" :label="item.label" :value="item.value" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="新床位号">
          <el-select v-model="changeBedForm.newBedNumber" placeholder="请选择床位号" style="width: 100%">
            <el-option v-for="item in availableBeds" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前床位使用起始日期">
          <el-input v-model="changeBedForm.currentBedUsageStartDate" disabled />
        </el-form-item>
        <el-form-item label="当前床位使用结束日期" prop="currentBedUsageEndDate">
          <el-date-picker v-model="changeBedForm.currentBedUsageEndDate" type="date" placeholder="选择日期"
            value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeBedDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveChangeBed">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBedList, updateBedUsageEndDate, getFreeRooms, getFreeBeds, swapBeds } from '@/api/bed'
import { BUILDING_OPTIONS, DEFAULT_BUILDING } from '@/utils/building'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const buildingOptions = BUILDING_OPTIONS

/** 查询表单。 */
const searchForm = reactive({
  customerName: '',
  checkInDate: '',
  history: 1,
})

/** 床位列表。 */
const bedList = ref([])

/** 分页信息。 */
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})

/** 修改弹窗是否显示。 */
const editDialogVisible = ref(false)
/** 修改表单。 */
const editForm = reactive({
  id: null,
  customerName: '',
  gender: '',
  buildingNumber: '',
  roomNumber: '',
  bedNumber: '',
  usageStartDate: '',
  usageEndDate: '',
})
const editFormRef = ref()
/** 修改表单校验规则。 */
const editFormRules = {
  usageEndDate: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (editForm.usageStartDate && value < editForm.usageStartDate) {
          callback(new Error('床位使用结束日期不能小于起始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

/** 床位调换弹窗是否显示。 */
const changeBedDialogVisible = ref(false)
/** 床位调换表单。 */
const changeBedForm = reactive({
  id: null,
  customerName: '',
  oldBuildingNumber: '',
  oldRoomNumber: '',
  oldBedNumber: '',
  gender: '',
  newBuildingNumber: DEFAULT_BUILDING,
  newRoomNumber: '',
  currentBedUsageStartDate: '',
  newBedNumber: '',
  currentBedUsageEndDate: '',
})
const changeBedFormRef = ref()
/** 床位调换表单校验规则。 */
const changeBedFormRules = {
  currentBedUsageEndDate: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (changeBedForm.currentBedUsageStartDate && value < changeBedForm.currentBedUsageStartDate) {
          callback(new Error('床位使用结束日期不能小于起始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
}

/** 房间号选项。 */
const roomOptions = ref([])

/** 可用床位选项。 */
const availableBeds = ref([])

/**
 * 查询床位使用详情列表。
 */
const fetchBedList = async () => {
  try {
    const params = {
      customerName: searchForm.customerName,
      checkInDate: searchForm.checkInDate
        ? new Date(searchForm.checkInDate).toISOString().slice(0, 10)
        : '',
      history: searchForm.history,
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
    }
    const response = await getBedList(params)
    bedList.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('获取床位列表失败:', error)
    ElMessage.error('获取床位列表失败')
  }
}

/**
 * 执行查询。
 */
const handleSearch = () => {
  pagination.currentPage = 1
  fetchBedList()
}

/**
 * 按床位使用状态筛选。
 *
 * @param history 使用状态
 */
const searchByStatus = (history) => {
  searchForm.history = history
  handleSearch()
}

/**
 * 处理分页大小变化。
 *
 * @param val 每页数量
 */
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchBedList()
}

/**
 * 处理当前页变化。
 *
 * @param val 页码
 */
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchBedList()
}

/**
 * 打开修改弹窗。
 *
 * @param row 当前床位记录
 */
const handleEdit = (row) => {
  Object.assign(editForm, row)
  editDialogVisible.value = true
}

/**
 * 保存床位修改。
 */
const saveEdit = () => {
  editFormRef.value?.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请检查表单填写！')
      return
    }
    ElMessageBox.confirm('确定保存修改吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          await updateBedUsageEndDate(editForm.id, editForm.usageEndDate)
          fetchBedList()
          ElMessage.success('修改成功！')
          editDialogVisible.value = false
        } catch (error) {
          console.error('保存修改失败:', error)
          ElMessage.error('保存修改失败')
        }
      })
      .catch(() => {
        ElMessage.info('已取消保存。')
      })
  })
}

/**
 * 打开床位调换弹窗。
 *
 * @param row 当前床位记录
 */
const handleChangeBed = async (row) => {
  Object.assign(changeBedForm, {
    id: row.id,
    customerName: row.customerName,
    oldBuildingNumber: row.buildingNumber || DEFAULT_BUILDING,
    oldRoomNumber: row.roomNumber || '',
    oldBedNumber: row.bedNumber || '',
    gender: row.gender,
    newBuildingNumber: row.buildingNumber || DEFAULT_BUILDING,
    newRoomNumber: '',
    currentBedUsageStartDate: row.usageStartDate,
    newBedNumber: '',
    currentBedUsageEndDate: row.usageEndDate,
  })
  availableBeds.value = []

  try {
    const response = await getFreeRooms(changeBedForm.newBuildingNumber)
    roomOptions.value = response
  } catch (error) {
    console.error('获取有空闲床位的房间号失败:', error)
    ElMessage.error('获取有空闲床位的房间号失败')
  }

  changeBedDialogVisible.value = true
}

/**
 * 调换楼栋变化时重新加载房间。
 */
const onSwapBuildingChange = async () => {
  changeBedForm.newRoomNumber = ''
  changeBedForm.newBedNumber = ''
  availableBeds.value = []
  try {
    const response = await getFreeRooms(changeBedForm.newBuildingNumber)
    roomOptions.value = response
  } catch (error) {
    console.error('获取有空闲床位的房间号失败:', error)
    ElMessage.error('获取有空闲床位的房间号失败')
  }
}

/**
 * 获取可用床位。
 *
 * @param roomNumber 房间号
 */
const getAvailableBeds = async (roomNumber) => {
  try {
    const response = await getFreeBeds(roomNumber, changeBedForm.newBuildingNumber)
    availableBeds.value = response
  } catch (error) {
    console.error('获取可用床位失败:', error)
    ElMessage.error('获取可用床位失败')
  }
}

/**
 * 保存床位调换。
 */
const saveChangeBed = () => {
  changeBedFormRef.value?.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请检查表单填写！')
      return
    }
    if (!changeBedForm.newRoomNumber || !changeBedForm.newBedNumber) {
      ElMessage.warning('请选择新房间号和新床位号！')
      return
    }

    ElMessageBox.confirm('确定进行床位调换吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        try {
          const swapData = {
            oldBedId: changeBedForm.id,
            oldBedEndDate: new Date().toISOString().slice(0, 10),
            newBuildingNumber: changeBedForm.newBuildingNumber,
            newRoomNumber: changeBedForm.newRoomNumber,
            newBedNumber: changeBedForm.newBedNumber,
            newBedStartDate: new Date().toISOString().slice(0, 10),
            newBedEndDate: changeBedForm.currentBedUsageEndDate,
          }
          await swapBeds(swapData)

          fetchBedList()
          ElMessage.success('床位调换成功！')
          changeBedDialogVisible.value = false
        } catch (error) {
          console.error('床位调换失败:', error)
          ElMessage.error('床位调换失败')
        }
      })
      .catch(() => {
        ElMessage.info('已取消床位调换。')
      })
  })
}

/**
 * 组件挂载时加载床位列表。
 */
onMounted(() => {
  fetchBedList()
})
</script>

<style scoped>
/* 样式已迁移到 el-ui.scss（表格全局）和 app.scss（art-table-card 内） */
</style>
