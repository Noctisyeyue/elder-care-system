<!-- 管理端--首页 -->
<template>
  <div class="home-dashboard">
    <!-- 统计卡片区 -->
    <div class="info-cards">
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">用户总数</div>
            <div class="info-card-number">{{ userNum }}</div>
            <div class="info-card-description">在职护工、医生、护士总数</div>
          </div>
          <div class="info-card-icon icon-users">
            <SvgIcon icon="ri:team-line" :size="24" />
          </div>
        </div>
      </el-card>
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">客户总数</div>
            <div class="info-card-number">{{ customerNum }}</div>
            <div class="info-card-description">正在服务的客户总数</div>
          </div>
          <div class="info-card-icon icon-customers">
            <SvgIcon icon="ri:user-heart-line" :size="24" />
          </div>
        </div>
      </el-card>
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">空闲床位</div>
            <div class="info-card-number">
              {{ freeBedNum }}
              <span class="info-card-total-number">/{{ bedNum }}</span>
            </div>
            <div class="info-card-description">剩余床位与总床位的比例</div>
          </div>
          <div class="info-card-icon icon-beds">
            <SvgIcon icon="ri:hotel-bed-line" :size="24" />
          </div>
        </div>
      </el-card>
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">新增客户</div>
            <div class="info-card-number">{{ newCustomerNum }}</div>
            <div class="info-card-description">本月新入住的客户数量</div>
          </div>
          <div class="info-card-icon icon-new">
            <SvgIcon icon="ri:user-add-line" :size="24" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 核心图表区：1行2列（左3:右7） -->
    <div class="core-charts">
      <el-card class="chart-card" shadow="never" style="flex: 3;">
        <template #header>
          <div class="card-header">
            <span class="card-title">床位状态分布</span>
          </div>
        </template>
        <div class="chart-body" id="BedStatusChart"></div>
      </el-card>
      <el-card class="chart-card" shadow="never" style="flex: 7;">
        <template #header>
          <div class="card-header">
            <span class="card-title">年度入住趋势</span>
          </div>
        </template>
        <div class="chart-body" id="YearTrendChart"></div>
      </el-card>
    </div>

    <!-- 多维分析区：1行3列 -->
    <div class="analysis-charts">
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">客户护理级别分布</span>
        </template>
        <div class="chart-body" id="NursingLevelChart"></div>
      </el-card>
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">人员角色分布</span>
        </template>
        <div class="chart-body" id="RoleDistChart"></div>
      </el-card>
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">本周膳食配餐量</span>
        </template>
        <div class="chart-body" id="WeeklyMealChart"></div>
      </el-card>
    </div>

    <!-- 底部表格区 -->
    <div class="info-tables">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">外出申请</span>
            <span class="link-more" @click="detail('/customer/out')">查看详情</span>
          </div>
        </template>
        <div class="table-wrapper">
          <el-table
            :data="outingApplicationList"
            v-loading="loading"
            :empty-text="loading ? '加载中...' : '暂无数据'"
            size="default"
          >
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="customerName" label="客户姓名" width="120" />
            <el-table-column prop="approvalStatus" label="审批状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.approvalStatus)" size="small">
                  {{ row.approvalStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="outingReason" label="外出原因" show-overflow-tooltip />
          </el-table>
        </div>
        <div class="table-pagination">
          <el-pagination
            background
            v-model:current-page="outingSearchForm.pageNum"
            v-model:page-size="outingSearchForm.pageSize"
            layout="prev, pager, next"
            :total="outingApplicationTotal"
            small
            @size-change="() => {}"
            @current-change="handleOutingApplicationPageChange"
          />
        </div>
      </el-card>

      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">退住申请</span>
            <span class="link-more" @click="detail('/customer/checkOut')">查看详情</span>
          </div>
        </template>
        <div class="table-wrapper">
          <el-table
            :data="checkoutApplicationList"
            v-loading="loading"
            :empty-text="loading ? '加载中...' : '暂无数据'"
            size="default"
          >
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="customerName" label="客户姓名" width="120" />
            <el-table-column prop="approvalStatus" label="审批状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.approvalStatus)" size="small">
                  {{ row.approvalStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="checkOutReason" label="退住原因" />
          </el-table>
        </div>
        <div class="table-pagination">
          <el-pagination
            background
            v-model:current-page="checkOutSearchForm.pageNum"
            v-model:page-size="checkOutSearchForm.pageSize"
            layout="prev, pager, next"
            :total="checkoutApplicationTotal"
            small
            @size-change="() => {}"
            @current-change="handleCheckoutApplicationPageChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { getUserCount, getRoleNum } from '@/api/user'
import {
  getCustomerCount,
  getCustomerMonthCount,
  getCustomerYearCount,
  getCheckOutList,
  getOutingList,
  getNursingLevelDistribution
} from '@/api/customer'
import { getBedStatusDistribution } from '@/api/bed'
import { getWeeklyMealCount } from '@/api/diet'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useSettingStore } from '@/stores/setting'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'

const userNum = ref(0)
const customerNum = ref(0)
const freeBedNum = ref(0)
const bedNum = ref(0)
const usedBedNum = ref(0)
const outBedNum = ref(0)
const newCustomerNum = ref(0)
const yearCustomerNum = ref([])
const userRoleNum = ref([])
const nursingLevelData = ref([])
const weeklyMealData = ref([])

const router = useRouter()
const settingStore = useSettingStore()
const loading = ref(false)

/** 退住申请（固定每页3条） */
const checkOutSearchForm = reactive({ customerName: '', pageNum: 1, pageSize: 3 })
const checkoutApplicationList = ref([])
const checkoutApplicationTotal = ref(0)

/** 外出申请（固定每页3条） */
const outingSearchForm = reactive({ customerName: '', pageNum: 1, pageSize: 3 })
const outingApplicationList = ref([])
const outingApplicationTotal = ref(0)

let bedStatusChart, yearTrendChart, nursingLevelChart, roleDistChart, weeklyMealChart
let themeObserver

/**
 * 获取暗色模式下的图表主题色
 */
function getChartTheme() {
  const dark = document.documentElement.classList.contains('dark')
  return {
    text: dark ? 'rgba(255,255,255,0.85)' : '#303133',
    subText: dark ? 'rgba(255,255,255,0.55)' : '#909399',
    splitLine: dark ? 'rgba(255,255,255,0.12)' : '#e5e5e5',
    pieBorder: dark ? '#161618' : '#ffffff',
  }
}

/**
 * 初始化床位状态环形图
 */
function initBedStatusChart() {
  const dom = document.getElementById('BedStatusChart')
  if (!dom) return
  bedStatusChart = echarts.init(dom)
  const t = getChartTheme()
  bedStatusChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', formatter: '{b}: {c}张 ({d}%)' },
    legend: {
      bottom: 0,
      left: 'center',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: t.subText, fontSize: 12 },
    },
    series: [{
      type: 'pie',
      radius: ['55%', '75%'],
      center: ['50%', '42%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 6,
        borderColor: t.pieBorder,
        borderWidth: 2,
      },
      label: {
        show: true,
        position: 'center',
        formatter: `{total|${bedNum.value}}\n{name|总床位}`,
        rich: {
          total: { fontSize: 26, fontWeight: 'bold', color: t.text, lineHeight: 36 },
          name: { fontSize: 12, color: t.subText },
        },
      },
      emphasis: {
        label: { show: true },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.15)' },
      },
      data: [
        { value: freeBedNum.value, name: '空闲', itemStyle: { color: '#22c55e' } },
        { value: usedBedNum.value, name: '占用', itemStyle: { color: '#3b82f6' } },
        { value: outBedNum.value, name: '外出', itemStyle: { color: '#f97316' } },
      ],
    }],
  })
}

/**
 * 初始化年度入住趋势折线图
 */
function initYearTrendChart() {
  const dom = document.getElementById('YearTrendChart')
  if (!dom) return
  yearTrendChart = echarts.init(dom)
  const t = getChartTheme()
  const data = Array.isArray(yearCustomerNum.value) ? yearCustomerNum.value : []
  yearTrendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    grid: {
      left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true,
    },
    xAxis: {
      type: 'category', boundaryGap: false,
      data: data.map(i => i.month),
      axisLabel: { color: t.subText, fontSize: 12 },
      axisLine: { lineStyle: { color: t.splitLine } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: t.subText, fontSize: 12 },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: t.splitLine, type: 'dashed' } },
    },
    series: [{
      type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, showSymbol: false,
      lineStyle: { width: 3, color: '#409eff' },
      itemStyle: { color: '#409eff', borderWidth: 2, borderColor: '#fff' },
      areaStyle: {
        opacity: 0.8,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.02)' },
        ]),
      },
      emphasis: { focus: 'series' },
      data: data.map(i => i.num),
    }],
  })
}

/**
 * 初始化护理级别分布横向柱状图
 */
function initNursingLevelChart() {
  const dom = document.getElementById('NursingLevelChart')
  if (!dom) return
  nursingLevelChart = echarts.init(dom)
  const t = getChartTheme()
  const data = nursingLevelData.value
  nursingLevelChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '10%', bottom: '3%', top: '3%', containLabel: true },
    xAxis: {
      type: 'value',
      axisLabel: { color: t.subText, fontSize: 11 },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: t.splitLine, type: 'dashed' } },
    },
    yAxis: {
      type: 'category', data: data.map(i => i.name),
      axisLabel: { color: t.text, fontSize: 12 },
      axisLine: { show: false }, axisTick: { show: false },
    },
    series: [{
      type: 'bar', barWidth: 14, data: data.map(i => i.value),
      itemStyle: {
        borderRadius: [0, 7, 7, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#60a5fa' }, { offset: 1, color: '#3b82f6' },
        ]),
      },
    }],
  })
}

/**
 * 初始化人员角色分布饼图
 */
function initRoleDistChart() {
  const dom = document.getElementById('RoleDistChart')
  if (!dom) return
  roleDistChart = echarts.init(dom)
  const t = getChartTheme()
  const data = Array.isArray(userRoleNum.value) ? userRoleNum.value : []
  roleDistChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: {
      bottom: 0, left: 'center',
      itemWidth: 8, itemHeight: 8,
      textStyle: { color: t.subText, fontSize: 11 },
    },
    series: [{
      type: 'pie', radius: ['45%', '65%'], center: ['50%', '42%'],
      itemStyle: { borderRadius: 4, borderColor: t.pieBorder, borderWidth: 2 },
      label: { show: false },
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.15)' } },
      data: data.map(i => ({ value: i.value, name: i.name })),
      color: ['#409eff', '#22c55e', '#f97316', '#8b5cf6'],
    }],
  })
}

/**
 * 初始化本周配餐量柱状图
 */
function initWeeklyMealChart() {
  const dom = document.getElementById('WeeklyMealChart')
  if (!dom) return
  weeklyMealChart = echarts.init(dom)
  const t = getChartTheme()
  const data = weeklyMealData.value
  weeklyMealChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category', data: data.map(i => i.dayOfWeek),
      axisLabel: { color: t.subText, fontSize: 11 },
      axisLine: { lineStyle: { color: t.splitLine } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: t.subText, fontSize: 11 },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: t.splitLine, type: 'dashed' } },
    },
    series: [{
      type: 'bar', barWidth: 16, data: data.map(i => i.count),
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#34d399' }, { offset: 1, color: '#22c55e' },
        ]),
      },
    }],
  })
}

/** 初始化所有图表 */
function initAllCharts() {
  nextTick(() => {
    initBedStatusChart()
    initYearTrendChart()
    initNursingLevelChart()
    initRoleDistChart()
    initWeeklyMealChart()
  })
}

/** 更新所有图表 */
function updateAllCharts() {
  if (bedStatusChart) initBedStatusChart()
  if (yearTrendChart) initYearTrendChart()
  if (nursingLevelChart) initNursingLevelChart()
  if (roleDistChart) initRoleDistChart()
  if (weeklyMealChart) initWeeklyMealChart()
}

function handleResize() {
  bedStatusChart?.resize(); yearTrendChart?.resize()
  nursingLevelChart?.resize(); roleDistChart?.resize(); weeklyMealChart?.resize()
}

async function fetchUserNumInfo() { userNum.value = await getUserCount() }
async function fetchCustomerNumInfo() { customerNum.value = await getCustomerCount() }

/**
 * 获取床位状态分布数据
 */
async function fetchBedInfo() {
  const res = await getBedStatusDistribution()
  freeBedNum.value = res?.free || 0
  usedBedNum.value = res?.used || 0
  outBedNum.value = res?.out || 0
  bedNum.value = res?.total || 0
}

async function fetchNewCustomerNumInfo() {
  const d = new Date()
  const date = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
  newCustomerNum.value = await getCustomerMonthCount(date)
}

async function fetchYearCustomerNumInfo() {
  const year = new Date().getFullYear()
  const res = await getCustomerYearCount(String(year))
  const m = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  yearCustomerNum.value = (res || []).map((num, i) => ({ month: m[i], num }))
}

async function fetchUserRoleNumInfo() { userRoleNum.value = await getRoleNum() || [] }
async function fetchNursingLevelData() { nursingLevelData.value = await getNursingLevelDistribution() || [] }
async function fetchWeeklyMealData() { weeklyMealData.value = await getWeeklyMealCount() || [] }

async function fetchInfo() {
  await Promise.all([
    fetchUserNumInfo(), fetchCustomerNumInfo(), fetchBedInfo(),
    fetchNewCustomerNumInfo(), fetchYearCustomerNumInfo(), fetchUserRoleNumInfo(),
    fetchNursingLevelData(), fetchWeeklyMealData(),
  ])
}

async function fetchCheckoutApplications() {
  const res = await getCheckOutList({
    pageNum: checkOutSearchForm.pageNum, pageSize: checkOutSearchForm.pageSize, customerName: checkOutSearchForm.customerName,
  })
  checkoutApplicationList.value = res.records || []
  checkoutApplicationTotal.value = res.total || 0
}

async function fetchOutingApplications() {
  const res = await getOutingList({
    pageNum: outingSearchForm.pageNum, pageSize: outingSearchForm.pageSize, customerName: outingSearchForm.customerName,
  })
  outingApplicationList.value = res.records || []
  outingApplicationTotal.value = res.total || 0
}

function handleCheckoutApplicationPageChange(page) { checkOutSearchForm.pageNum = page; fetchCheckoutApplications() }
function handleOutingApplicationPageChange(page) { outingSearchForm.pageNum = page; fetchOutingApplications() }

function getStatusType(status) {
  switch (status) {
    case '已提交': return 'warning'
    case '通过': return 'success'
    case '不通过': return 'danger'
    default: return 'info'
  }
}

function detail(path) { router.push(path) }

echarts.use([TitleComponent, TooltipComponent, GridComponent, LegendComponent, LineChart, PieChart, BarChart, CanvasRenderer])

onMounted(async () => {
  await fetchInfo()
  await fetchOutingApplications()
  await fetchCheckoutApplications()
  initAllCharts()
  themeObserver = new MutationObserver(() => updateAllCharts())
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
  window.addEventListener('resize', handleResize)
})

watch(() => settingStore.systemThemeMode, () => nextTick(() => updateAllCharts()))

onUnmounted(() => {
  themeObserver?.disconnect()
  window.removeEventListener('resize', handleResize)
  bedStatusChart?.dispose(); yearTrendChart?.dispose(); nursingLevelChart?.dispose()
  roleDistChart?.dispose(); weeklyMealChart?.dispose()
})
</script>

<style scoped>
.home-dashboard {
  padding: 4px;
}

/* ========== 通用卡片规范 ========== */
:deep(.el-card) {
  border-radius: 10px;
  border: 1px solid var(--default-border);
  background: var(--default-box-color);
  transition: all 0.25s ease;
}

:deep(.el-card:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

:deep(.el-card__header) {
  padding: 14px 18px;
  border-bottom: 1px solid var(--default-border);
}

:deep(.el-card__body) {
  padding: 18px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--ui-gray-800);
  position: relative;
  padding-left: 10px;
}

.card-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 14px;
  border-radius: 2px;
  background: #409eff;
}

.link-more {
  font-size: 13px;
  color: var(--ui-gray-500);
  cursor: pointer;
  transition: color 0.2s;
}

.link-more:hover {
  color: #409eff;
}

/* ========== 统计卡片 ========== */
.info-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.info-card {
  flex: 1;
  min-width: 220px;
}

.info-card :deep(.el-card__body) {
  padding: 18px 20px;
}

.info-card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.info-card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-card-title {
  font-size: 14px;
  color: var(--ui-gray-500);
  line-height: 1;
}

.info-card-number {
  font-size: 30px;
  font-weight: 600;
  color: var(--ui-gray-800);
  line-height: 1.2;
  display: flex;
  align-items: baseline;
}

.info-card-total-number {
  font-size: 15px;
  font-weight: 400;
  color: var(--ui-gray-400);
  margin-left: 4px;
}

.info-card-description {
  font-size: 12px;
  color: var(--ui-gray-400);
  line-height: 1;
}

.info-card-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.icon-users { background: rgba(64, 158, 255, 0.1); color: #409eff; }
.icon-customers { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
.icon-beds { background: rgba(139, 92, 246, 0.1); color: #8b5cf6; }
.icon-new { background: rgba(249, 115, 22, 0.1); color: #f97316; }

/* ========== 图表区域 ========== */
.core-charts {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  height: 360px;
}

.analysis-charts {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  height: 300px;
}

.chart-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chart-card :deep(.el-card__body) {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.chart-body {
  flex: 1;
  min-height: 0;
  width: 100%;
}

/* ========== 表格区域 ========== */
.info-tables {
  display: flex;
  gap: 16px;
}

.table-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.table-wrapper {
  flex: 1;
  overflow: hidden;
}

:deep(.el-table th) {
  background: var(--art-gray-100);
  color: var(--ui-gray-700);
  font-weight: 600;
}

.table-pagination {
  display: flex;
  justify-content: center;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--default-border);
}

@media (max-width: 1200px) {
  .analysis-charts { flex-wrap: wrap; height: auto; }
  .analysis-charts .chart-card { min-width: calc(50% - 8px); height: 300px; }
}

@media (max-width: 768px) {
  .core-charts, .info-tables { flex-direction: column; height: auto; }
  .analysis-charts .chart-card { min-width: 100%; }
}

/* 强制表格单元格单行省略，不显示tooltip */
:deep(.el-table .cell) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
