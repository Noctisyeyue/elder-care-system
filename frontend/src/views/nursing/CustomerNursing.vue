<!-- 管理端--子菜单--客户护理设置 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="客户姓名">
          <el-input v-model="queryName" placeholder="请输入客户姓名" clearable @clear="fetchCustomerList"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchCustomerList">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">客户信息列表</span>
      </div>
      <el-table :data="customerList" height="100%" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户姓名" min-width="100" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="buildingNumber" label="楼号" width="90" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床号" width="80" />
        <el-table-column prop="tel" label="联系电话" min-width="140" />
        <el-table-column prop="nursingLevel" label="护理级别" min-width="100" />
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <div class="flex-c" style="gap: 6px;">
              <el-button type="primary" size="small" @click="openSetNursingDialog(scope.row)">
                <SvgIcon icon="ri:settings-4-line" :size="14" />
                <span>设置护理级别</span>
              </el-button>
              <el-button v-if="scope.row.levelId" type="danger" size="small"
                @click="removeNursingLevel(scope.row)">
                <SvgIcon icon="ri:close-circle-line" :size="14" />
                <span>移除</span>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="page"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </el-card>

    <!-- 设置护理级别弹窗 -->
    <el-dialog v-model="setNursingDialogVisible" title="客户护理设置" width="1200px" align-center
      @close="setNursingDialogVisible = false">
      <div class="dialog-body">
      <el-form label-width="100px">
        <el-form-item label="当前护理级别" v-if="currentCustomer?.levelId">
          <el-tag type="success">{{ currentCustomer.nursingLevel }}</el-tag>
        </el-form-item>
        <el-form-item label="修改护理级别">
          <el-select v-model="nursingForm.levelId" placeholder="请选择护理级别" @change="fetchNursingItems">
            <el-option v-for="item in nursingLevelList" :key="item.id" :label="item.level" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 当前护理项目列表 -->
      <div v-if="currentCustomer?.levelId" style="margin-bottom: 20px">
        <h4>当前护理项目</h4>
        <el-table :data="currentNursingItems" stripe style="margin-bottom: 12px">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="frequency" label="执行周次" />
          <el-table-column prop="count" label="已用次数" />
          <el-table-column prop="totalCount" label="总次数" />
          <el-table-column prop="buyDate" label="购买日期" />
          <el-table-column prop="buyCount" label="购买数量" />
          <el-table-column prop="expireDate" label="到期日期" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.expireDate < getToday() ? 'danger' : 'success'">
                {{ scope.row.expireDate < getToday() ? '已过期' : '使用中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <h4>新增护理项目</h4>
      <el-table :data="nursingForm.items" stripe style="margin-bottom: 10px">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="code" label="编号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="frequency" label="执行周次" />
        <el-table-column prop="count" label="总次数" />
        <el-table-column prop="buyDate" label="服务购买日期" width="160">
          <template #default="scope">
            <el-date-picker v-model="scope.row.buyDate" type="date" placeholder="选择日期" size="small"
              style="width: 140px" />
          </template>
        </el-table-column>
        <el-table-column prop="buyCount" label="购买数量" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.buyCount" :min="0" size="small" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="服务到期日期" width="180">
          <template #default="scope">
            <el-date-picker v-model="scope.row.expireDate" type="date" placeholder="选择日期" size="small"
              style="width: 160px" />
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="justify-content: center;" background
        v-model:current-page="itemPage"
        v-model:page-size="itemPageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="itemTotal"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleItemSizeChange"
        @current-change="handleItemPageChange" />
      </div>
      <template #footer>
        <el-button @click="setNursingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePreSubmitNursingSetting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCustomerList } from '@/api/customer'
import { getNursingLevelList, getNursingLevelItems, getCustomerNursingItems, addCustomerNursingLevel, removeCustomerNursingLevel } from '@/api/nursing'
import { ElMessage, ElMessageBox } from 'element-plus'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const queryName = ref('')
const customerList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const nursingLevelList = ref([])
const setNursingDialogVisible = ref(false)
const currentCustomer = ref(null)
const nursingForm = reactive({ levelId: null, items: [] })
const currentNursingItems = ref([])

const itemPage = ref(1)
const itemPageSize = ref(10)
const itemTotal = ref(0)

function handlePreSubmitNursingSetting() { submitNursingSetting() }

const fetchCustomerList = async () => {
  const res = await getCustomerList({ customerName: queryName.value, pageNum: page.value, pageSize: pageSize.value })
  customerList.value = res.records || []
  total.value = res.total || 0
}

function handlePageChange(val) { page.value = val; fetchCustomerList() }
function handleSizeChange(size) { pageSize.value = size; page.value = 1; fetchCustomerList() }

const fetchNursingLevels = async () => {
  const res = await getNursingLevelList({ status: '启用', pageNum: 1, pageSize: 9999 })
  nursingLevelList.value = res.records || []
}

const fetchNursingItems = async (levelId = nursingForm.levelId) => {
  if (!levelId) return
  const res = await getNursingLevelItems({ levelId, pageNum: itemPage.value, pageSize: itemPageSize.value })
  nursingForm.items = (res.records || []).map(item => ({
    ...item, buyDate: getToday(), buyCount: 1, expireDate: getDefaultExpireDate(),
  }))
  itemTotal.value = res.total || 0
}

const openSetNursingDialog = async (customer) => {
  currentCustomer.value = customer
  setNursingDialogVisible.value = true
  nursingForm.levelId = null
  nursingForm.items = []
  itemPage.value = 1
  await fetchNursingLevels()
  if (customer.levelId) {
    const res = await getCustomerNursingItems(customer.id)
    currentNursingItems.value = res
  }
}

const submitNursingSetting = async () => {
  if (!nursingForm.levelId) { ElMessage.error('请选择护理级别'); return }
  if (!nursingForm.items.length) { ElMessage.error('该护理级别下无护理项目'); return }
  await addCustomerNursingLevel({
    customerId: currentCustomer.value.id,
    levelId: nursingForm.levelId,
    items: nursingForm.items.filter(item => item.buyCount > 0).map(item => ({
      itemId: item.id, buyDate: item.buyDate, buyCount: item.buyCount, expireDate: item.expireDate,
    })),
  })
  ElMessage.success('设置成功')
  setNursingDialogVisible.value = false
  fetchCustomerList()
}

const removeNursingLevel = (customer) => {
  ElMessageBox.confirm('移除后将删除该客户所有当前级别的护理项目，是否继续？', '警告', { type: 'warning' })
    .then(async () => {
      await removeCustomerNursingLevel(customer.id, customer.levelId)
      ElMessage.success('移除成功')
      fetchCustomerList()
    })
}

function getToday() { return new Date().toISOString().slice(0, 10) }
function getDefaultExpireDate() {
  const date = new Date(); date.setMonth(date.getMonth() + 3); return date.toISOString().slice(0, 10)
}

function handleItemPageChange(val) { itemPage.value = val; fetchNursingItems() }
function handleItemSizeChange(size) { itemPageSize.value = size; itemPage.value = 1; fetchNursingItems() }

onMounted(fetchCustomerList)
</script>

<style scoped>
.dialog-body {
  height: 67vh;
  overflow-y: auto;
}
</style>
