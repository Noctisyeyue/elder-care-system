<template>
  <div class="caregiver-home">
    <el-row :gutter="20">
      <el-col :span="12">
        <!-- Top Welcome Card -->
        <el-card shadow="never" class="welcome-card">
          <el-row align="middle">
            <el-col :span="16">
              <div class="welcome-content">
                <div class="welcome-text">
                  <h2>欢迎回来&nbsp;&nbsp;&nbsp;&nbsp;{{ userStore.realName }}</h2>
                </div>
                <el-row class="stats-row">
                  <el-col :span="8">
                    <div class="stat-item">
                      <span class="stat-value">
                        {{ dailyCareCount }}
                      </span>
                      <span class="stat-label">今日护理次数</span>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="stat-item">
                      <span class="stat-value" v-if="compareCareCount > 0">
                        {{ compareCareCount }}
                        <el-icon color="#67c23a"><CaretTop /></el-icon>
                      </span>
                      <span class="stat-value" v-if="compareCareCount == 0">
                        {{ compareCareCount }}
                        <el-icon color="#aaaaaa" style="width: 20px;"><SemiSelect/></el-icon>
                      </span>
                      <span class="stat-value" v-if="compareCareCount < 0">
                        {{ compareCareCount }}
                        <el-icon color="#ee0000"><CaretBottom /></el-icon>
                      </span>
                      <span class="stat-label">较昨日</span>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-col>
            <el-col :span="8" class="welcome-image-col">
              <img src="https://neuhealth.oss-cn-beijing.aliyuncs.com/caregiver.png" alt="welcome" class="welcome-image" />
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stats-card">
          <div class="stats-card-header">
            <span class="stats-card-title">累计护理次数</span>
            <span class="stats-card-value">{{ totalCareCount.toLocaleString() }}</span>
          </div>
          <div class="stats-card-chart" ref="chartRef" style="height: 140px; width: 100%"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stats-card">
          <div class="stats-card-header">
            <span class="stats-card-title">累计护理人数</span>
            <span class="stats-card-value">{{ totalCaredPeople.toLocaleString() }}</span>
          </div>
          <div class="stats-card-chart" ref="barChartRef" style="height: 100px; width: 100%"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Service Trend Chart -->
    <el-row :gutter="20" class="second-row">
      <el-col :span="12">
        <el-card shadow="never" class="trend-card">
          <div class="trend-card-header">
            <span class="trend-card-title">服务客户趋势</span>
            <span class="trend-card-subtitle">月度数量对比</span>
          </div>
          <div class="trend-card-chart" ref="lineChartRef" style="height: 350px; width: 100%"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="status-card new-status-card">
          <div class="new-status-header">
            <div class="new-status-title">外出申请情况</div>
            <div class="new-status-subtitle">按状态区分</div>
          </div>
          <div class="new-status-chart-wrapper">
            <div ref="pieChartRef" class="new-status-chart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="status-card new-status-card">
          <div class="new-status-header">
            <div class="new-status-title">退住申请情况</div>
            <div class="new-status-subtitle">按状态区分</div>
          </div>
          <div class="new-status-chart-wrapper">
            <div ref="checkoutPieChartRef" class="new-status-chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue';
import { useUserStore } from '@/stores/user';
import { getCaregiverHomeStats } from '@/api/health'
import { ElCard, ElRow, ElCol, ElIcon } from 'element-plus';
import { CaretTop, Check, Close, Upload, Delete, CaretBottom, Minus, SemiSelect } from '@element-plus/icons-vue';
import * as echarts from 'echarts';

const userStore = useUserStore();
const chartRef = ref(null);
let chartInstance = null;
const barChartRef = ref(null);
let barChartInstance = null;
const lineChartRef = ref(null);
let lineChartInstance = null;
const pieChartRef = ref(null);
let pieChartInstance = null;
const checkoutPieChartRef = ref(null);
let checkoutPieChartInstance = null;

const dailyCareCount = ref(0);
const compareCareCount = ref(0);

// 新增的统计数据
const totalCareCount = ref(0);
const completedCareCount = ref(0);
const uncompletedCareCount = ref(0);
const totalCaredPeople = ref(0);
const months = [
  'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
];
const serviceTrendData = ref({
  months,
  counts: [],
});
const outingApplicationStatus = ref({
  approved: 0,
  rejected: 0,
  submitted: 0,
  cancelled: 0,
});
const checkoutApplicationStatus = ref({
  approved: 0,
  rejected: 0,
  submitted: 0,
  cancelled: 0,
});

// ECharts的配置
const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)',
      },
      legend: {
        top: '85%',
        left: 'center',
        orient: 'horizontal',
        textStyle: {
          fontSize: 16,
        },
        width: 240,
        data: ['已完成', '未完成'],
      },
      series: [
        {
          name: '护理统计',
          type: 'pie',
          top: '-15%',
          radius: ['35%', '50%'],
          center: ['50%', '50%'],
          itemStyle: {
            borderColor: '#fff',
            borderWidth: 4,
            borderRadius: 12
          },
          label: {
            show: true,
            position: 'outside',
            formatter: '{b}',
            color: '#333',
            fontSize: 12
          },
          labelLine: {
            show: true,
            length: 10,
            length2: 10,
            smooth: true,
            lineStyle: {
              width: 3,
              type: 'solid'
            }
          },
          data: [
            { value: completedCareCount.value, name: '已完成', itemStyle: { color: '#e5e6eb' } },
            { value: uncompletedCareCount.value, name: '未完成', itemStyle: { color: '#409EFF' } }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        }
      ],
    };
    chartInstance.setOption(option);
  }
};

const initBarChart = () => {
  if (barChartRef.value) {
    barChartInstance = echarts.init(barChartRef.value);
    const option = {
      grid: {
        left: '0%',
        right: '0%',
        bottom: '0%',
        top: '10%',
        containLabel: false,
      },
      xAxis: {
        type: 'category',
        show: false,
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      },
      yAxis: {
        type: 'value',
        show: false,
      },
      series: [
        {
          data: [5, 8, 3, 9, 6, 7],
          type: 'bar',
          barWidth: '50%',
          itemStyle: {
            color: '#409EFF',
            borderRadius: [5, 5, 5, 5],
          },
        },
      ],
      tooltip: {
        show: false,
      },
    };
    barChartInstance.setOption(option);
  }
};

const initLineChart = () => {
  if (lineChartRef.value) {
    lineChartInstance = echarts.init(lineChartRef.value);
    const option = {
      color: 'rgb(119,161,255)',
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      tooltip: {
        trigger: 'axis',
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: serviceTrendData.value.months,
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '服务客户数',
          type: 'line',
          smooth: true,
          data: serviceTrendData.value.counts,
          showSymbol: false,
          itemStyle: { color: '#409EFF' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                  offset: 0,
                  color: 'rgb(200,210,255)'
              },
              {
                  offset: 1,
                  color: 'rgb(255, 255, 255)'
              },
            ]),
          },
        },
      ],
    };
    lineChartInstance.setOption(option);
  }
};

const initPieChart = () => {
  if (pieChartRef.value) {
    pieChartInstance = echarts.init(pieChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)',
      },
      legend: {
          top: '85%',
          left: 'center',
          orient: 'horizontal',
          textStyle:{
              fontSize: 16,
          },
          width:240,
      },
      series: [
        {
          name: '外出申请',
          type: 'pie',
          top: '-15%',
          radius: ['35%', '50%'],
          center: ['50%', '50%'],
          itemStyle: {
            borderColor: '#fff',
            borderWidth: 4,
            borderRadius: 12
          },
          label: {
            show: true,
            position: 'outside',
            formatter: '{b}',
            color: '#333',
            //fontWeight: 'bold',
            fontSize: 12
          },
          labelLine: {
            show: true,
            length: 10,
            length2: 10,
            smooth: true,
            lineStyle: {
              width: 3,
              type: 'solid'
            }
          },
          data: [
            {
              value: outingApplicationStatus.value.approved,
              name: '已通过',
              itemStyle: { color: '#409EFF' },
            },
            {
              value: outingApplicationStatus.value.rejected,
              name: '不通过',
              itemStyle: { color: '#AAAAFF' },
            },
            {
              value: outingApplicationStatus.value.submitted,
              name: '已提交',
              itemStyle: { color: '#87CEFA' },
            },
            {
              value: outingApplicationStatus.value.cancelled,
              name: '已撤销',
              itemStyle: { color: '#e5e6eb' },
            },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    };
    pieChartInstance.setOption(option);
  }
};

const initCheckoutPieChart = () => {
  if (checkoutPieChartRef.value) {
    checkoutPieChartInstance = echarts.init(checkoutPieChartRef.value);
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)',
      },
      legend: {
          top: '85%',
          left: 'center',
          orient: 'horizontal',
          textStyle:{
              fontSize: 16,
          },
          width:240,
      },
      series: [
        {
          name: '退住申请',
          type: 'pie',
          top: '-15%',
          radius: ['35%', '50%'],
          center: ['50%', '50%'],
          itemStyle: {
            borderColor: '#fff',
            borderWidth: 4,
            borderRadius: 12
          },
          label: {
            show: true,
            position: 'outside',
            formatter: '{b}',
            color: '#333',
            //fontWeight: 'bold',
            fontSize: 12
          },
          labelLine: {
            show: true,
            length: 10,
            length2: 10,
            smooth: true,
            lineStyle: {
              width: 3,
              type: 'solid'
            }
          },
          data: [
            {
              value: checkoutApplicationStatus.value.approved,
              name: '已通过',
              itemStyle: { color: '#409EFF' },
            },
            {
              value: checkoutApplicationStatus.value.rejected,
              name: '不通过',
              itemStyle: { color: '#AAAAFF' },
            },
            {
              value: checkoutApplicationStatus.value.submitted,
              name: '已提交',
              itemStyle: { color: '#87CEFA' },
            },
            {
              value: checkoutApplicationStatus.value.cancelled,
              name: '已撤销',
              itemStyle: { color: '#e5e6eb' },
            },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    };
    checkoutPieChartInstance.setOption(option);
  }
};

// 更新图表数据
const updateChart = () => {
    if (chartInstance) {
        chartInstance.setOption({
            series: [{
                data: [
                    { value: completedCareCount.value, name: '已完成', itemStyle: { color: '#e5e6eb' } },
                    { value: uncompletedCareCount.value, name: '未完成' }
                ]
            }]
        });
    }
};

// 模拟从后端获取数据
const fetchData = async () => {
  try {
    // 1. 先用模拟数据兜底
    // dailyCareCount.value = 10;
    // compareCareCount.value = 35;
    // totalCareCount.value = 205216;
    // completedCareCount.value = 150000;
    // uncompletedCareCount.value = 55216;
    // totalCaredPeople.value = 55231;
    // serviceTrendData.value = {
    //   months,
    //   counts: [50, 25, 70, 35, 20, 45, 60, 55, 80, 65, 75, 90],
    // };
    // outingApplicationStatus.value = {
    //   approved: 120,
    //   rejected: 30,
    //   submitted: 58,
    //   cancelled: 10,
    // };
    // checkoutApplicationStatus.value = {
    //   approved: 80,
    //   rejected: 15,
    //   submitted: 25,
    //   cancelled: 5,
    // };

    // 2. 请求后端API，若有数据则覆盖
    const res = await getCaregiverHomeStats();
    if (res && typeof res === 'object') {
      if (typeof res.dailyCareCount === 'number') dailyCareCount.value = res.dailyCareCount;
      if (typeof res.compareCareCount === 'number') compareCareCount.value = res.compareCareCount;
      if (typeof res.totalCareCount === 'number') totalCareCount.value = res.totalCareCount;
      if (typeof res.completedCareCount === 'number') completedCareCount.value = res.completedCareCount;
      if (typeof res.uncompletedCareCount === 'number') uncompletedCareCount.value = res.uncompletedCareCount;
      if (typeof res.totalCaredPeople === 'number') totalCaredPeople.value = res.totalCaredPeople;
      if (Array.isArray(res.counts) && res.counts.length === 12) {
        serviceTrendData.value = {
          months,
          counts: res.counts,
        };
      }
      if (res.outingApplicationStatus && typeof res.outingApplicationStatus === 'object') {
        outingApplicationStatus.value = {
          ...outingApplicationStatus.value,
          ...res.outingApplicationStatus,
        };
      }
      if (res.checkoutApplicationStatus && typeof res.checkoutApplicationStatus === 'object') {
        checkoutApplicationStatus.value = {
          ...checkoutApplicationStatus.value,
          ...res.checkoutApplicationStatus,
        };
      }
      // 用户名同步到 store（如果后端返回且 store 中尚无值）
      if (typeof res.userName === 'string' && !userStore.realName) {
        userStore.realName = res.userName
      }
    }

    // 数据获取后更新图表
    await nextTick();
    if (!chartInstance) {
      initChart();
    } else {
      updateChart();
    }
    initBarChart();
    initLineChart();
    initPieChart();
    initCheckoutPieChart();
  } catch (error) {
    console.error('获取护工首页数据失败:', error);
  }
};

onMounted(() => {
  fetchData();
});

// 监听数据变化，并更新图表
watch([completedCareCount, uncompletedCareCount], () => {
    updateChart();
}, { deep: true });
</script>

<style scoped>
.caregiver-home {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei',
    '\\5FAE软雅黑', Arial, sans-serif;
}

.welcome-card {
  background-color: #eef3ff; /* RGB(238, 243, 255) */
  border: none;
  border-radius: 12px;
  height: 100%;
  overflow: hidden; /* 确保图片圆角生效 */
}

:deep(.welcome-card .el-card__body) {
  padding: 15px 20px;
}

.welcome-content {
  display: flex;
  flex-direction: column;
  justify-content: center; /* 垂直居中 */
  height: 100%;
  padding: 15px 20px;
}

.welcome-text h2 {
  font-size: 28px; /* 增大字体 */
  font-weight: 600;
  color: #303133;
  margin: 0;
  margin-bottom: 50px; /* 增加与下方统计的间距 */
}

.stats-row {
  margin-top: 0; /* 移除顶部外边距，由 welcome-text 控制间距 */
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
}

.stat-value .el-icon {
  margin-left: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.welcome-image-col {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.welcome-image {
  max-width: 150px;
  height: auto;
}

/* 统计卡片样式 */
.stats-card {
  border-radius: 12px;
  height: 100%;
  border: none;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

:deep(.stats-card .el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 15px 20px;
}

.stats-card-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.stats-card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stats-card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stats-card-chart {
  display: flex;
  justify-content: center;
  align-items: center;
}

.second-row {
  margin-top: 20px;
}

.trend-card {
  border-radius: 12px;
  border: none;
}

.trend-card-header {
  margin-bottom: 20px;
}

.trend-card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.trend-card-subtitle {
  font-size: 14px;
  color: #909399;
  margin-left: 10px;
}

.status-card {
  border-radius: 12px;
  border: none;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  padding-top: 0;
}

.status-card-header {
  width: 100%;
  margin-bottom: 0;
  padding-top: 0;
}

.status-card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.status-card-subtitle {
  font-size: 14px;
  color: #909399;
  margin-left: 10px;
}

.status-card-chart {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 180px;
}

:deep(.status-card .el-card__body) {
  padding-top: 0 !important;
}

.new-status-card {
  border-radius: 12px;
  border: none;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 0;
}

.new-status-header {
  padding: 18px 0 0 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.new-status-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.new-status-subtitle {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
  line-height: 1;
}

.new-status-chart-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 180px;
}

.new-status-chart {
  width: 100%;
  height: 360px;
}
</style>
