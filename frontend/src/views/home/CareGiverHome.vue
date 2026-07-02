<template>
  <div class="home-dashboard">
    <!-- 欢迎卡片 -->
    <div class="welcome-row">
      <el-card class="welcome-card" shadow="never">
        <div class="welcome-body">
          <div class="welcome-info">
            <h2>欢迎回来，{{ userStore.realName }}</h2>
            <p class="welcome-tip">今日工作顺利，照顾好老人也照顾好自己</p>
            <div class="welcome-stats">
              <div class="welcome-stat">
                <span class="welcome-stat-value">{{ dailyCareCount }}</span>
                <span class="welcome-stat-label">今日护理次数</span>
              </div>
              <div class="welcome-stat">
                <span class="welcome-stat-value" :class="compareClass">
                  {{ compareText }}
                </span>
                <span class="welcome-stat-label">较昨日</span>
              </div>
            </div>
          </div>
          <div class="welcome-decor">
            <SvgIcon icon="ri:heart-fill" :size="80" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计卡片 -->
    <div class="info-cards">
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">累计护理次数</div>
            <div class="info-card-number">{{ totalCareCount.toLocaleString() }}</div>
            <div class="info-card-description">已执行的护理总次数</div>
          </div>
          <div class="info-card-icon icon-care">
            <SvgIcon icon="ri:heart-pulse-line" :size="24" />
          </div>
        </div>
      </el-card>
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">累计护理人数</div>
            <div class="info-card-number">{{ totalCaredPeople.toLocaleString() }}</div>
            <div class="info-card-description">累计服务的客户数量</div>
          </div>
          <div class="info-card-icon icon-people">
            <SvgIcon icon="ri:user-heart-line" :size="24" />
          </div>
        </div>
      </el-card>
      <el-card class="info-card" shadow="never">
        <div class="info-card-body">
          <div class="info-card-info">
            <div class="info-card-title">未完成护理项目</div>
            <div class="info-card-number">{{ uncompletedCareCount }}</div>
            <div class="info-card-description">待执行的护理次数</div>
          </div>
          <div class="info-card-icon icon-pending">
            <SvgIcon icon="ri:time-line" :size="24" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区：1行3列 -->
    <div class="core-charts">
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">服务客户趋势</span>
        </template>
        <div class="chart-body" ref="lineChartRef"></div>
      </el-card>
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">外出申请情况</span>
        </template>
        <div class="chart-body" ref="pieChartRef"></div>
      </el-card>
      <el-card class="chart-card" shadow="never">
        <template #header>
          <span class="card-title">退住申请情况</span>
        </template>
        <div class="chart-body" ref="checkoutPieChartRef"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { useSettingStore } from '@/stores/setting'
import { getCaregiverHomeStats } from '@/api/health'
import SvgIcon from '@/components/base/svg-icon/index.vue'
import * as echarts from 'echarts/core'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { LineChart, PieChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'

const userStore = useUserStore()
const settingStore = useSettingStore()

const dailyCareCount = ref(0)
const compareCareCount = ref(0)
const totalCareCount = ref(0)
const completedCareCount = ref(0)
const uncompletedCareCount = ref(0)
const totalCaredPeople = ref(0)
const serviceTrendData = ref({ months: [], counts: [] })
const outingApplicationStatus = ref({ approved: 0, rejected: 0, submitted: 0, cancelled: 0 })
const checkoutApplicationStatus = ref({ approved: 0, rejected: 0, submitted: 0, cancelled: 0 })

const lineChartRef = ref(null)
const pieChartRef = ref(null)
const checkoutPieChartRef = ref(null)
let lineChartInstance, pieChartInstance, checkoutPieChartInstance, themeObserver

const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']

/** 较昨日比较样式 */
const compareClass = computed(() => {
  if (compareCareCount.value > 0) return 'compare-up'
  if (compareCareCount.value < 0) return 'compare-down'
  return 'compare-same'
})

const compareText = computed(() => {
  const v = compareCareCount.value
  if (v > 0) return `+${v}`
  return String(v)
})

/** 护理进度百分比 */
const progressPercent = computed(() => {
  const total = completedCareCount.value + uncompletedCareCount.value
  if (!total) return 0
  return Math.round((completedCareCount.value / total) * 100)
})

/**
 * 获取图表主题色
 */
function getChartTheme() {
  const dark = document.documentElement.classList.contains('dark')
  return {
    text: dark ? 'rgba(255,255,255,0.85)' : '#303133',
    subText: dark ? 'rgba(255,255,255,0.55)' : '#909399',
    splitLine: dark ? 'rgba(255,255,255,0.12)' : '#eef0f2',
    pieBorder: dark ? '#161618' : '#ffffff',
    labelLine: dark ? 'rgba(255,255,255,0.35)' : 'rgba(0,0,0,0.3)',
    areaTop: dark ? 'rgba(64,158,255,0.35)' : 'rgba(64,158,255,0.2)',
    areaBottom: dark ? 'rgba(64,158,255,0.05)' : 'rgba(64,158,255,0.02)',
    inactiveColor: dark ? '#52525b' : '#e5e6eb',
  }
}

function buildLineChartOption() {
  const t = getChartTheme()
  return {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: { color: '#303133' },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '8%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: serviceTrendData.value.months,
      axisLabel: { color: t.subText, fontSize: 12 },
      axisLine: { lineStyle: { color: t.splitLine } },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: t.subText, fontSize: 12 },
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: t.splitLine, type: 'dashed' } },
    },
    series: [{
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      showSymbol: false,
      lineStyle: { width: 3, color: '#409eff' },
      itemStyle: { color: '#409eff', borderWidth: 2, borderColor: '#fff' },
      areaStyle: {
        opacity: 0.8,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: t.areaTop },
          { offset: 1, color: t.areaBottom },
        ]),
      },
      emphasis: {
        focus: 'series',
        itemStyle: { shadowBlur: 10, shadowColor: 'rgba(64, 158, 255, 0.3)' },
      },
      data: serviceTrendData.value.counts,
    }],
  }
}

function buildStatusPieOption(statusData) {
  const t = getChartTheme()
  return {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: { color: '#303133' },
    },
    legend: {
      bottom: 0,
      left: 'center',
      itemWidth: 8,
      itemHeight: 8,
      itemGap: 12,
      textStyle: { color: t.subText, fontSize: 12 },
    },
    series: [{
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['50%', '42%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 6,
        borderColor: t.pieBorder,
        borderWidth: 2,
      },
      label: { show: false },
      emphasis: {
        itemStyle: {
          shadowBlur: 12,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.12)',
        },
      },
      data: [
        { value: statusData.approved, name: '已通过', itemStyle: { color: '#409eff' } },
        { value: statusData.rejected, name: '不通过', itemStyle: { color: '#8b5cf6' } },
        { value: statusData.submitted, name: '已提交', itemStyle: { color: '#f97316' } },
        { value: statusData.cancelled, name: '已撤销', itemStyle: { color: t.inactiveColor } },
      ],
    }],
  }
}

function initAllCharts() {
  if (lineChartRef.value) {
    lineChartInstance = echarts.init(lineChartRef.value)
    lineChartInstance.setOption(buildLineChartOption())
  }
  if (pieChartRef.value) {
    pieChartInstance = echarts.init(pieChartRef.value)
    pieChartInstance.setOption(buildStatusPieOption(outingApplicationStatus.value))
  }
  if (checkoutPieChartRef.value) {
    checkoutPieChartInstance = echarts.init(checkoutPieChartRef.value)
    checkoutPieChartInstance.setOption(buildStatusPieOption(checkoutApplicationStatus.value))
  }
}

function updateAllCharts() {
  lineChartInstance?.setOption(buildLineChartOption())
  pieChartInstance?.setOption(buildStatusPieOption(outingApplicationStatus.value))
  checkoutPieChartInstance?.setOption(buildStatusPieOption(checkoutApplicationStatus.value))
}

function handleResize() {
  lineChartInstance?.resize()
  pieChartInstance?.resize()
  checkoutPieChartInstance?.resize()
}

async function fetchData() {
  try {
    const res = await getCaregiverHomeStats()
    if (res) {
      dailyCareCount.value = res.dailyCareCount || 0
      compareCareCount.value = res.compareCareCount || 0
      totalCareCount.value = res.totalCareCount || 0
      completedCareCount.value = res.completedCareCount || 0
      uncompletedCareCount.value = res.uncompletedCareCount || 0
      totalCaredPeople.value = res.totalCaredPeople || 0
      if (Array.isArray(res.counts)) {
        serviceTrendData.value = { months, counts: res.counts }
      }
      if (res.outingApplicationStatus) {
        outingApplicationStatus.value = { approved: 0, rejected: 0, submitted: 0, cancelled: 0, ...res.outingApplicationStatus }
      }
      if (res.checkoutApplicationStatus) {
        checkoutApplicationStatus.value = { approved: 0, rejected: 0, submitted: 0, cancelled: 0, ...res.checkoutApplicationStatus }
      }
      if (res.userName && !userStore.realName) userStore.realName = res.userName
    }
    await nextTick()
    if (!lineChartInstance) { initAllCharts() } else { updateAllCharts() }
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

echarts.use([TitleComponent, TooltipComponent, GridComponent, LegendComponent, LineChart, PieChart, CanvasRenderer])

onMounted(() => {
  fetchData()
  themeObserver = new MutationObserver(() => updateAllCharts())
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
  window.addEventListener('resize', handleResize)
})

watch(() => settingStore.systemThemeMode, () => nextTick(() => updateAllCharts()))

onUnmounted(() => {
  themeObserver?.disconnect()
  window.removeEventListener('resize', handleResize)
  lineChartInstance?.dispose(); pieChartInstance?.dispose(); checkoutPieChartInstance?.dispose()
})
</script>

<style scoped>
.home-dashboard {
  padding: 4px;
}

/* 全局卡片规范 */
:deep(.el-card) {
  border-radius: 12px;
  border: 1px solid var(--default-border);
  background: var(--default-box-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-card:hover) {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid var(--default-border);
}

:deep(.el-card__body) {
  padding: 20px;
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
  background: linear-gradient(180deg, #409eff 0%, #60a5fa 100%);
}

/* 欢迎卡片 */
.welcome-row {
  margin-bottom: 16px;
}

.welcome-card {
  background: linear-gradient(135deg, #e0f2fe 0%, #eef2ff 50%, #f0fdf4 100%) !important;
  border: none !important;
  overflow: hidden;
  position: relative;
}

html.dark .welcome-card {
  background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #052e16 100%) !important;
}

.welcome-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
}

.welcome-info {
  flex: 1;
  z-index: 2;
}

.welcome-info h2 {
  font-size: 26px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
}

html.dark .welcome-info h2 {
  color: #f1f5f9;
}

.welcome-tip {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 20px 0;
}

html.dark .welcome-tip {
  color: #94a3b8;
}

.welcome-stats {
  display: flex;
  gap: 40px;
}

.welcome-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.welcome-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
}

html.dark .welcome-stat-value {
  color: #f1f5f9;
}

.welcome-stat-label {
  font-size: 13px;
  color: #64748b;
}

html.dark .welcome-stat-label {
  color: #94a3b8;
}

.compare-up {
  color: #22c55e !important;
}

.compare-down {
  color: #ef4444 !important;
}

.compare-same {
  color: #94a3b8 !important;
}

.welcome-decor {
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(64, 158, 255, 0.15);
  z-index: 1;
}

html.dark .welcome-decor {
  color: rgba(64, 158, 255, 0.1);
}

/* 统计卡片 */
.info-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.info-card {
  flex: 1;
  min-width: 240px;
}

.info-card :deep(.el-card__body) {
  padding: 20px 22px;
}

.info-card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.info-card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.info-card-title {
  font-size: 14px;
  color: var(--ui-gray-500);
  line-height: 1;
}

.info-card-number {
  font-size: 32px;
  font-weight: 700;
  color: var(--ui-gray-800);
  line-height: 1.1;
}

.info-card-description {
  font-size: 12px;
  color: var(--ui-gray-400);
  line-height: 1;
}

.info-card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease;
}

.info-card:hover .info-card-icon {
  transform: scale(1.08);
}

.icon-care {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.icon-people {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.icon-pending {
  background: rgba(249, 115, 22, 0.1);
  color: #f97316;
}

/* 图表区 */
.core-charts {
  display: flex;
  gap: 16px;
  height: 360px;
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

/* 响应式适配 */
@media (max-width: 1200px) {
  .core-charts {
    flex-wrap: wrap;
    height: auto;
  }
  .chart-card {
    min-width: calc(50% - 8px);
    height: 340px;
  }
}

@media (max-width: 768px) {
  .core-charts {
    flex-direction: column;
  }
  .chart-card {
    min-width: 100%;
  }
  .welcome-stats {
    gap: 24px;
  }
  .welcome-decor {
    display: none;
  }
}
</style>