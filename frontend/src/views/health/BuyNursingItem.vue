<template>
  <div class="buy-nursing-item-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="护理项目名称">
          <el-input v-model="queryForm.itemName" placeholder="请输入护理项目名称" clearable style="width: 200px; margin-right: 10px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchNursingItems">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
      <div style="font-size: 14px;">当前客户：{{customerName}}</div>
    </el-card>

    <el-card class="nursing-item-list-card">
      <template #header>
        <div class="card-header">
          <span>可购买的护理项目</span>
        </div>
      </template>
      <el-table :data="nursingItemList" style="width: 100%" @row-click="addSelectedItem">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="code" label="编号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格">
          <template #default="{ row }"> {{ row.price }} 元/次 </template>
        </el-table-column>
        <el-table-column prop="frequency" label="执行周期" />
        <el-table-column prop="count" label="执行次数">
          <template #default="{ row }"> {{ row.count }} 次 </template>
        </el-table-column>
        <el-table-column prop="desc" label="备注" width="300"/>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-card class="selected-nursing-item-card">
      <template #header>
        <div class="card-header">
          <span>已选护理项目</span>
        </div>
      </template>
      <el-table :data="selectedNursingItems" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="code" label="编号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格">
          <template #default="{ row }"> {{ row.price }} 元/次 </template>
        </el-table-column>
        <el-table-column prop="count" label="执行次数" />
        <el-table-column prop="frequency" label="执行周期" />
        <el-table-column label="服务购买日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.buyDate) }}
          </template>
        </el-table-column>
        <el-table-column label="购买数量" width="180">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="1" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="服务到期日期" width="180">
          <template #default="scope">
            <el-date-picker
              v-model="scope.row.expireDate"
              type="date"
              placeholder="选择日期"
              size="small"
              value-format="YYYY-MM-DD"
              style="width: 150px"
              :disabled-date="disabledExpireDate"
            />
          </template>
        </el-table-column>
        <el-table-column label="总价">
          <template #default="{ row }"> {{ row.price*row.quantity }} 元 </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="removeSelectedItem(scope.$index)"
              >X</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="footer-buttons">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="openPayDialog">保存</el-button>
      </div>
    </el-card>
  </div>

  <!-- 支付二维码弹窗 -->
  <el-dialog v-model="payDialogVisible" title=" " width="500px" :close-on-click-modal="false" :show-close="true">
    <div style="display: flex; align-items: flex-start;">
      <div style="flex: 1; padding-right: 20px;">
        <div style="font-size: 22px; font-weight: bold; margin-bottom: 30px;">确认付款</div>
        <div style="margin-bottom: 16px; font-size: 18px;">客户姓名：<b>{{ payInfo.customerName }}</b></div>
        <div style="margin-bottom: 16px; font-size: 18px;">购买数量：<b>{{ payInfo.totalCount }}</b></div>
        <div style="margin-bottom: 16px; font-size: 18px;">总金额：<b style="color: #e74c3c; font-size: 28px;">￥{{ payInfo.totalAmount.toFixed(2) }}</b></div>
        <div style="margin-bottom: 16px; font-size: 16px; color: #888;">{{ payCountdown }} 秒后自动取消支付</div>
      </div>
      <div style="flex: 1; display: flex; flex-direction: column; align-items: center;">
        <img :src="payInfo.qrCodeUrl" alt="二维码" style="width: 180px; height: 180px; margin-bottom: 10px; border: 4px solid #f5f5f5; border-radius: 8px;" />
        <div style="margin-top: 10px; color: #409EFF; font-size: 16px;">
          <el-icon style="vertical-align: middle;"><i class="el-icon-check"></i></el-icon> 扫码支付
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="handlePayCancel">取消支付</el-button>
      <el-button type="primary" @click="handlePayConfirm">确认支付</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCustomerList, getMyCustomers } from '@/api/customer'
import { getNursingCustomerLevelItems, checkIsPurchased, buyNursingItem } from '@/api/health'
import codePng from '@/assets/code.png'

const router = useRouter()

const customerId = ref(localStorage.getItem('buyNursingItemsCustomerId'))
const customerName = ref(localStorage.getItem('buyNursingItemsCustomerName'))

// 护理项目查询表单
const queryForm = reactive({
  itemName: '',
})

// 护理项目列表
const nursingItemList = ref([])

// 护理项目分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})

// 已选护理项目列表
const selectedNursingItems = ref([])

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

// 工具函数：将日期字符串加一天，返回yyyy-MM-dd格式
const addOneDay = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  date.setDate(date.getDate() + 1)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 禁止选择今天及之前的日期
const disabledExpireDate = (date) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return date.getTime() <= today.getTime();
};

// 查询护理项目列表
const searchNursingItems = async () => {
  const params = {
    customerId: customerId.value,
    name: queryForm.itemName,
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize,
  }
  const res = await getNursingCustomerLevelItems(params)
  nursingItemList.value = res.records || []
  pagination.total = res.total || 0
}

// 处理护理项目分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  searchNursingItems()
}

// 处理护理项目当前页改变
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  searchNursingItems()
}

// 将选中的护理项目添加到已选列表
const addSelectedItem = async (row) => {
  // 检查是否已在已选列表中
  const exists = selectedNursingItems.value.some((item) => item.id === row.id)
  if (exists) {
    ElMessage.warning('该护理项目已在已选列表中')
    return
  }

  try {
    // 检查当前客户是否已有该护理项目
    const res = await checkIsPurchased({
      customerId: customerId.value,
      itemId: row.id,
    })

    if (res) {
      ElMessage.warning('该客户已购买此护理项目')
      return
    }

    // 默认数量为1，服务到期时间为当前日期
    const today = new Date()
    const year = today.getFullYear()
    const month = (today.getMonth() + 1).toString().padStart(2, '0')
    const day = today.getDate().toString().padStart(2, '0')
    const defaultExpireTime = `${year}-${month}-${day}`

    selectedNursingItems.value.push({
      ...row,
      quantity: 1,
      buyDate: defaultExpireTime, // 购买日期默认为当前日期
      expireDate: addOneDay(row.expireDate || defaultExpireTime), // 显示为后端时间的明天
    })
  } catch (error) {
    console.error('检查护理项目失败:', error)
    ElMessage.error('检查护理项目失败')
  }
}

// 从已选列表中移除护理项目
const removeSelectedItem = (index) => {
  selectedNursingItems.value.splice(index, 1)
}

// 支付弹窗相关
const payDialogVisible = ref(false)
const payCountdown = ref(60)
let payCountdownTimer = null
const payInfo = ref({
  customerName: customerName.value || '',
  totalCount: 0,
  totalAmount: 0,
  qrCodeUrl: codePng,
})
const openPayDialog = () => {
  payInfo.value.customerName = customerName.value || ''
  payInfo.value.totalCount = selectedNursingItems.value.reduce((sum, item) => sum + Number(item.quantity), 0)
  payInfo.value.totalAmount = selectedNursingItems.value.reduce((sum, item) => sum + Number(item.quantity) * Number(item.price), 0)
  payCountdown.value = 60
  payDialogVisible.value = true
  if (payCountdownTimer) clearInterval(payCountdownTimer)
  payCountdownTimer = setInterval(() => {
    payCountdown.value--
    if (payCountdown.value <= 0) {
      clearInterval(payCountdownTimer)
      payDialogVisible.value = false
      ElMessage.info('支付超时，已自动取消')
    }
  }, 1000)
}
const closePayDialog = () => {
  payDialogVisible.value = false
  if (payCountdownTimer) clearInterval(payCountdownTimer)
}
const handlePayCancel = () => {
  closePayDialog()
}
const handlePayConfirm = async () => {
  closePayDialog()
  await saveBuyItems()
}

// 保存已选护理项目（仅供支付弹窗确认时调用）
const saveBuyItems = async () => {
  if (selectedNursingItems.value.length === 0) {
    ElMessage.warning('请选择要购买的护理项目')
    return
  }
  try {
    const payload = selectedNursingItems.value.map((item) => ({
      customerId: customerId.value,
      itemId: item.id,
      buyCount: item.quantity,
      buyDate: item.buyDate,
      expireDate: item.expireDate,
    }))
    await buyNursingItem(payload)
    ElMessage.success('购买护理项目成功')
    router.push({ path: '/service/concern' }) // 返回服务关注页面
  } catch (error) {
    console.error('购买护理项目失败:', error)
    ElMessage.error('购买护理项目失败')
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 组件挂载时查询护理项目列表
onMounted(() => {
  searchNursingItems()
})
</script>

<style scoped>
.buy-nursing-item-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-card :deep(.el-card__body){
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nursing-item-list-card,
.selected-nursing-item-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form .el-form-item {
  margin-bottom: 0;
}

.footer-buttons {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
