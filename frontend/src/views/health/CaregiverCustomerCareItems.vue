<!-- 护工端--子菜单--日常护理--登记护理 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="项目名称">
          <el-input v-model="searchForm.itemName" placeholder="请输入项目名称" clearable @clear="handleSearch"
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
          <el-button @click="goBack">
            <SvgIcon icon="ri:arrow-left-line" :size="16" />
            <span>返回</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">护理项目列表</span>
      </div>
      <el-table :data="nursingItems" height="100%" stripe style="width: 100%" v-loading="loading"
        element-loading-text="加载中..." :empty-text="loading ? '加载中...' : '暂无护理项目'">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="code" label="项目编号" width="120" />
        <el-table-column prop="name" label="项目名称" min-width="100" show-overflow-tooltip />
        <el-table-column prop="remain" label="剩余次数" width="120" />
        <el-table-column prop="expireDate" label="服务到期时间" width="120">
          <template #default="{ row }"> {{ formatDate(row.expireDate) }} </template>
        </el-table-column>
        <el-table-column label="状态" width="170">
          <template #default="{ row }">
            <div class="status-tags">
              <el-tag :type="new Date(row.expireDate) < new Date() ? 'danger' : 'primary'" size="small">
                {{ new Date(row.expireDate) < new Date() ? '到期' : '未到期' }}
              </el-tag>
              <el-tag :type="row.remain <= 0 ? 'danger' : 'primary'" size="small">
                {{ row.remain <= 0 ? '次数用尽' : '次数正常' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openNursingDialog(scope.row)">登记护理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </el-card>

    <!-- 登记护理弹窗 -->
    <el-dialog v-model="nursingDialogVisible" title="登记护理记录" width="450px" align-center
      @close="nursingDialogVisible = false">
      <el-form :model="nursingForm" label-width="110px">
        <el-form-item label="护理时间" required>
          <el-date-picker v-model="nursingForm.nursingTime" type="date" value-format="YYYY-MM-DD" disabled
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="剩余次数">
          <el-input v-model="currentRemain" disabled />
        </el-form-item>
        <el-form-item label="本次护理次数" required>
          <el-input-number v-model="nursingForm.times" :min="1" :max="currentRemain" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nursingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNursingRecord">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPurchasedItems, addNursingRecord } from '@/api/health'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const router = useRouter()
const customerId = localStorage.getItem('caregiverCustomerCareItemsCustomerId')
const allNursingItems = ref([])
const nursingItems = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const nursingDialogVisible = ref(false)
const currentRemain = ref(0)
const nursingForm = reactive({ nursingTime: '', times: 1, itemId: '', itemName: '', itemCode: '' })
const searchForm = reactive({ itemName: '' })

// 日期只用于表格展示，接口仍使用原始日期字段。
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

// 后端一次取全量，前端负责当前客户护理项目的本地分页。
function updatePage() {
  const start = (pageNum.value - 1) * pageSize.value
  nursingItems.value = allNursingItems.value.slice(start, start + pageSize.value)
  total.value = allNursingItems.value.length
}

async function fetchNursingItems() {
  loading.value = true
  try {
    const res = await getPurchasedItems({
      customerId,
      pageNum: 1,
      pageSize: 9999,
      itemName: searchForm.itemName,
    })
    allNursingItems.value = res.list || []
    pageNum.value = 1
    updatePage()
  } finally {
    loading.value = false
  }
}

function handlePageChange(val) {
  pageNum.value = val
  updatePage()
}

function handleSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  updatePage()
}

function handleSearch() {
  fetchNursingItems()
}

function openNursingDialog(item) {
  const isExpired = new Date(item.expireDate) < new Date()
  const isOwed = item.remain <= 0
  if (isExpired || isOwed) {
    ElMessage.error('该护理项目已到期或次数用尽，请联系管理员处理')
    return
  }

  currentRemain.value = item.remain
  nursingForm.nursingTime = new Date().toISOString().slice(0, 10)
  nursingForm.times = 1
  nursingForm.itemId = item.id
  nursingForm.itemName = item.name
  nursingForm.itemCode = item.code
  nursingDialogVisible.value = true
}

async function submitNursingRecord() {
  if (!nursingForm.nursingTime || !nursingForm.times) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (nursingForm.times > currentRemain.value) {
    ElMessage.error(`添加次数不能超过剩余次数(${currentRemain.value}次)`)
    return
  }

  await addNursingRecord({
    customerId,
    itemId: nursingForm.itemId,
    name: nursingForm.itemName,
    code: nursingForm.itemCode,
    nursingTime: nursingForm.nursingTime,
    times: nursingForm.times,
  })
  ElMessage.success('护理记录登记成功')
  nursingDialogVisible.value = false
  fetchNursingItems()
}

function goBack() {
  router.back()
}

onMounted(fetchNursingItems)
</script>

<style scoped>
.status-tags {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: nowrap;
  white-space: nowrap;
}
</style>
