<template>
    <div class="home">
        <!-- 统计信息 -->
        <!-- 统计信息卡片 -->
        <div class="info-cards">
            <el-card class="info-card">
                <div class="info-card-body">
                    <div class="info-card-info">
                        <div class="info-card-title">用户总数</div>
                        <div style="width: 100%; display: flex;">
                            <div class="info-card-number">{{userNum}}</div>
                        </div>
                        <div class="info-card-description">在职护工、医生、护士总数</div>
                    </div>
                    <div class="info-card-icon">
                        <el-icon class="icon"><Avatar /></el-icon>
                    </div>
                </div>
            </el-card>
            <el-card class="info-card">
                <div class="info-card-body">
                    <div class="info-card-info">
                        <div class="info-card-title">客户总数</div>
                        <div style="width: 100%; display: flex;">
                            <div class="info-card-number">{{customerNum}}</div>
                        </div>
                        <div class="info-card-description">正在服务的客户总数</div>
                    </div>
                    <div class="info-card-icon">
                        <el-icon class="icon"><UserFilled /></el-icon>
                    </div>
                </div>
            </el-card>
            <el-card class="info-card">
                <div class="info-card-body">
                    <div class="info-card-info">
                        <div class="info-card-title">空闲床位</div>
                        <div style="width: 100%; display: flex;">
                            <div class="info-card-number">{{freeBedNum}}</div>
                            <div class="info-card-total-number">/{{bedNum}}</div>
                        </div>
                        <div class="info-card-description">剩余床位与总床位的比例</div>
                    </div>
                    <div class="info-card-icon">
                        <el-icon class="icon"><Moon /></el-icon>
                    </div>
                </div>
            </el-card>
            <el-card class="info-card" style="margin-right: 0;">
                <div class="info-card-body">
                    <div class="info-card-info">
                        <div class="info-card-title">新增客户</div>
                        <div style="width: 100%; display: flex;">
                            <div class="info-card-number">{{newCustomerNum}}</div>
                        </div>
                        <div class="info-card-description">今日新入住的客户数量</div>
                    </div>
                    <div class="info-card-icon">
                        <el-icon class="icon"><User /></el-icon>
                    </div>
                </div>
            </el-card>
        </div>
        <!-- 统计信息图表 -->
        <div class="info-charts">
            <el-card class="info-chart" style="flex: 3;">
                <div class="info-title">用户分布</div>
                <div class="info-chart-container" style="height: calc(100% - 40px);">
                    <div id="UserNumChart" style="width: 100%; height: 100%;"></div>
                </div>
            </el-card>
            <el-card class="info-chart" style="flex: 5; margin-right: 0px;">
                <div class="info-title">本年客户数量</div>
                <div class="info-chart-container">
                    <div id="newCustomerNumChart" style="width: 100%; height: 100%;"></div>
                </div>
            </el-card>
        </div>
        <!-- 统计信息表格 -->
        <div class="info-tables">
            <el-card class="info-card">
                <!-- 外出申请表格 -->
                <div class="table-container">
                    <div class="table-title">
                        <div class="info-title">外出申请</div>
                        <div class="info-herf" @click="detail('/customer/out')">查看详情</div>
                    </div>
                    <el-divider style="margin: 0;" />
                    <div id="table">
                        <el-table
                            :data="outingApplicationList"
                            style="width: 100%"
                            v-loading="loading"
                            :empty-text="loading ? '加载中...' : '暂无数据'"
                        >
                            <el-table-column type="index" label="序号" width="60"></el-table-column>
                            <el-table-column prop="customerName" label="客户姓名" width="120"></el-table-column>
                            <el-table-column prop="approvalStatus" label="审批状态" width="100">
                                <template #default="{ row }">
                                <el-tag :type="getStatusType(row.approvalStatus)">
                                    {{ row.approvalStatus }}
                                </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="outingReason" label="外出原因"></el-table-column>
                        </el-table>
                        <div class="pagination-container">
                            <el-pagination
                            class="pagination-right"
                            layout="total, sizes, prev, pager, next, jumper"
                            :total="outingApplicationTotal"
                            :page-size="outingSearchForm.pageSize"
                            :current-page="outingSearchForm.pageNum"
                            :page-sizes="[5, 10, 20, 50]"
                            @size-change="(size) => { outingSearchForm.pageSize = size; outingSearchForm.pageNum = 1; fetchOutingApplications(); }"
                            @current-change="handleOutingApplicationPageChange"
                            />
                        </div>
                    </div>
                </div>
            </el-card>
            <el-card class="info-card" style="margin-right: 0;">
                <!-- 退住申请表格 -->
                <div class="table-container">
                    <div class="table-title">
                        <div class="info-title">退住申请</div>
                        <div class="info-herf" @click="detail('/customer/checkOut')">查看详情</div>
                    </div>
                    <el-divider style="margin: 0;" />
                    <div id="table">
                        <el-table
                            :data="checkoutApplicationList"
                            style="width: 100%"
                            v-loading="loading"
                            :empty-text="loading ? '加载中...' : '暂无数据'"
                        >
                            <el-table-column type="index" label="序号" width="60"></el-table-column>
                            <el-table-column prop="customerName" label="客户姓名" width="120"></el-table-column>
                            <el-table-column prop="approvalStatus" label="审批状态" width="100">
                                <template #default="{ row }">
                                <el-tag :type="getStatusType(row.approvalStatus)">
                                    {{ row.approvalStatus }}
                                </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="checkOutReason" label="退住原因"></el-table-column>
                        </el-table>
                        <div class="pagination-container">
                            <el-pagination
                            class="pagination-right"
                            layout="total, sizes, prev, pager, next, jumper"
                            :total="checkoutApplicationTotal"
                            :page-size="checkOutSearchForm.pageSize"
                            :current-page="checkOutSearchForm.pageNum"
                            :page-sizes="[5, 10, 20, 50]"
                            @size-change="(size) => { checkOutSearchForm.pageSize = size; checkOutSearchForm.pageNum = 1; fetchCheckoutApplications(); }"
                            @current-change="handleCheckoutApplicationPageChange"
                            />
                        </div>
                    </div>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { getUserCount, getUserAvatar, getUserEmail, getRoleNum } from '@/api/user'
import { getCustomerCount, getCustomerMonthCount, getCustomerYearCount, getCheckOutList, getOutingList } from '@/api/customer'
import { getFreeBedCount, getBedCount } from '@/api/bed'
import { ElMessage } from 'element-plus'
import { useRouter} from 'vue-router'
import { useSettingStore } from '@/stores/setting'
import {
    Avatar,
    UserFilled,
    User,
    Moon,
} from '@element-plus/icons-vue'
// 图表引用
import * as echarts from 'echarts/core';
import {
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
} from 'echarts/components';
import { LineChart, PieChart } from 'echarts/charts';
import { UniversalTransition, LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

const userNum = ref(0)
const customerNum = ref(0)
const freeBedNum = ref(0)
const bedNum = ref(0)
const newCustomerNum = ref(0)
const yearCustomerNum = ref([])
const userRoleNum = ref([])

const router = useRouter()
const settingStore = useSettingStore()

function getChartTheme() {
    const dark = document.documentElement.classList.contains('dark')
    return {
        text: dark ? 'rgba(255,255,255,0.85)' : '#303133',
        subText: dark ? 'rgba(255,255,255,0.55)' : '#909399',
        splitLine: dark ? 'rgba(255,255,255,0.12)' : '#e5e5e5',
        pieBorder: dark ? '#161618' : '#ffffff',
        labelLine: dark ? 'rgba(255,255,255,0.35)' : 'rgba(0,0,0,0.3)',
        areaTop: dark ? 'rgba(64,158,255,0.35)' : 'rgb(200,210,255)',
        areaBottom: dark ? 'rgba(64,158,255,0.05)' : 'rgb(255, 255, 255)',
    }
}

function buildUserNumChartOption() {
    const theme = getChartTheme()
    return {
        backgroundColor: 'transparent',
        tooltip: { trigger: 'item' },
        legend: {
            top: '90%',
            left: 'center',
            textStyle: {
                fontSize: 16,
                color: theme.text,
            },
        },
        series: [{
            name: 'Access From',
            type: 'pie',
            top: '-20%',
            radius: ['50%', '40%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 20,
                borderColor: theme.pieBorder,
                borderWidth: 2,
            },
            label: {
                show: true,
                fontSize: 16,
                color: theme.text,
            },
            labelLine: {
                lineStyle: { color: theme.labelLine },
                smooth: 0.2,
                length: 50,
                length2: 20,
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 20,
                    fontWeight: 'bold',
                },
            },
            data: Array.isArray(userRoleNum.value) ? userRoleNum.value : [],
            color: ['rgb(170,170,255)', 'rgb(109,147,255)', 'rgb(50,200,255)'],
        }],
    }
}

function buildYearCustomerChartOption() {
    const theme = getChartTheme()
    const yearData = Array.isArray(yearCustomerNum.value) ? yearCustomerNum.value : []
    return {
        backgroundColor: 'transparent',
        color: 'rgb(119,161,255)',
        tooltip: { trigger: 'axis' },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
        },
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            data: yearData.map(item => item.month),
            axisLabel: { color: theme.subText },
            axisLine: { lineStyle: { color: theme.splitLine } },
        }],
        yAxis: [{
            type: 'value',
            axisLabel: { color: theme.subText },
            axisLine: { show: false },
            splitLine: {
                lineStyle: {
                    color: theme.splitLine,
                    width: 1,
                    type: 'dashed',
                },
            },
        }],
        series: [{
            type: 'line',
            smooth: true,
            lineStyle: { width: 3 },
            showSymbol: false,
            areaStyle: {
                opacity: 0.7,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: theme.areaTop },
                    { offset: 1, color: theme.areaBottom },
                ]),
            },
            emphasis: { focus: 'series' },
            data: yearData.map(item => item.num),
        }],
    }
}

function initCharts() {
    userNumChartDom = document.getElementById('UserNumChart')
    if (!userNumChartDom) {
        ElMessage.error('图表容器未找到，请刷新页面重试')
        return
    }
    userNumChart = echarts.init(userNumChartDom)
    userNumChart.setOption(buildUserNumChartOption())

    yearCustomerNumChartDom = document.getElementById('newCustomerNumChart')
    if (!yearCustomerNumChartDom) {
        ElMessage.error('图表容器未找到，请刷新页面重试')
        return
    }
    yearCustomerNumChart = echarts.init(yearCustomerNumChartDom)
    yearCustomerNumChart.setOption(buildYearCustomerChartOption())
}

function updateCharts() {
    if (userNumChart) userNumChart.setOption(buildUserNumChartOption())
    if (yearCustomerNumChart) yearCustomerNumChart.setOption(buildYearCustomerChartOption())
}

function handleChartResize() {
    userNumChart?.resize()
    yearCustomerNumChart?.resize()
}

// 查询表单数据
const checkOutSearchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})
const outingSearchForm = reactive({
  customerName: '',
  pageNum: 1,
  pageSize: 10,
})

// 退住申请列表数据
const checkoutApplicationList = ref([])
const checkoutApplicationTotal = ref(0)

// 外出申请列表数据
const outingApplicationList = ref([])
const outingApplicationTotal = ref(0)

// 图表数据
var yearCustomerNumChartDom
var yearCustomerNumChart
var userNumChartDom
var userNumChart
let themeObserver
// 获取统计信息
const fetchInfo = async () => {
    await Promise.all([
        fetchUserNumInfo(),
        fetchCustomerNumInfo(),
        fetchFreeBedNumInfo(),
        fetchNewCustomerNumInfo(),
        fetchYearCustomerNumInfo(),
        fetchUserRoleNumInfo(),
        fetchBedNumInfo(),
    ]);
}

const fetchUserNumInfo = async () => {
    const response = await getUserCount()
    userNum.value = response
}

const fetchCustomerNumInfo = async () => {
    const response = await getCustomerCount()
    customerNum.value = response
}

const fetchFreeBedNumInfo = async () => {
    const response = await getFreeBedCount()
    freeBedNum.value = response
}

const fetchBedNumInfo = async () => {
    const response = await getBedCount()
    bedNum.value = response
}

const fetchNewCustomerNumInfo = async () => {
    const currentDate = new Date()
    const month = (currentDate.getMonth()+1)<10?("0" + (currentDate.getMonth()+1)):(currentDate.getMonth()+1)
    const date = currentDate.getFullYear() + "-" + month
    const response = await getCustomerMonthCount(date)
    newCustomerNum.value = response
}

const fetchYearCustomerNumInfo = async () => {
    const currentDate = new Date()
    const year = currentDate.getFullYear()
    const response = await getCustomerYearCount(year)
    var result = []
    const monthes = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    for (var i = 0; i < 12; i++) {
        result[i] = ({month: monthes[i],num: response[i]})
    }
    yearCustomerNum.value = result
}

const fetchUserRoleNumInfo = async () => { 
    const response = await getRoleNum()
    userRoleNum.value = response
}

// 获取退住申请列表
const fetchCheckoutApplications = async () => {
    const response = await getCheckOutList({
        pageNum: checkOutSearchForm.pageNum,
        pageSize: checkOutSearchForm.pageSize,
        customerName: checkOutSearchForm.customerName, // 用于模糊查询
    })
    checkoutApplicationList.value = response.records || []
    checkoutApplicationTotal.value = response.total || 0
}

// 获取外出申请列表
const fetchOutingApplications = async () => {
    const response = await getOutingList({
        pageNum: outingSearchForm.pageNum,
        pageSize: outingSearchForm.pageSize,
        customerName: outingSearchForm.customerName,
    })
    outingApplicationList.value = response.records || []
    outingApplicationTotal.value = response.total || 0
}

// 退住申请分页变化
const handleCheckoutApplicationPageChange = (page) => {
    checkOutSearchForm.pageNum = page
    fetchCheckoutApplications()
}

// 外出申请分页变化
const handleOutingApplicationPageChange = (page) => {
    outingSearchForm.pageNum = page
    fetchOutingApplications()
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case '已提交':
      return 'warning'
    case '通过':
      return 'success'
    case '不通过':
      return 'danger'
    default:
      return 'info'
  }
}

// 跳转详情页面
function detail(path) {
  router.push(path)
}

// 图表初始化
echarts.use([
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition,
  PieChart,
  LabelLayout
]);

onMounted(() => {
    fetchCheckoutApplications()
    fetchOutingApplications()
    fetchInfo().then(() => {
        nextTick(() => initCharts())
    })
    themeObserver = new MutationObserver(() => updateCharts())
    themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
    window.addEventListener('resize', handleChartResize)
})

watch(() => settingStore.systemThemeMode, () => {
    nextTick(() => updateCharts())
})

onUnmounted(() => {
    themeObserver?.disconnect()
    window.removeEventListener('resize', handleChartResize)
    userNumChart?.dispose()
    yearCustomerNumChart?.dispose()
})
</script>

<style scoped>
.pagination-container {
    margin: 10px 0;
    display: flex;
    justify-content: flex-end; /* 将分页器靠右对齐 */
}

.pagination-right {
    display: flex;
    justify-content: flex-end;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
    overflow-y: auto;
    height: 198px;
}

.info-cards{
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 20px;
}

.info-card{
    flex: 1;
    margin-right: 20px;
    border-radius: 10px;
}

.info-card :deep(.el-card__body){
    padding: 20px 10px;
}

.el-card.is-always-shadow{
    box-shadow: none;
}

.info-card-body{
    height: 100px;
    display: flex;
    flex-direction: row;
}

.info-card-info{
    height: 100px;
    flex: 1;
    margin: 0 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.info-card-title{
    width: 100%;
    height: 14px;
    font-size: 15px;
    line-height: 14px;
    color: var(--ui-gray-600);
}

.info-card-number{
    margin-top: 10px;
    font-size: 28px;
    font-weight: 400;
    color: var(--ui-gray-800);
}

.info-card-total-number{
    margin-top: 24px;
    font-size: 14px;
    font-weight: 400;
    color: var(--ui-gray-500);
}

.info-card-description{
    width: 100%;
    margin-top: 10px;
    font-size: 13px;
    color: var(--ui-gray-500);
}

.info-card-icon{
    flex: 1%;
    display: contents;
}

.icon{
    top: 0;
    right: 20px;
    bottom: 0;
    width: 52px;
    height: 52px;
    margin: auto;
    overflow: hidden;
    font-size: 22px;
    line-height: 52px;
    color: var(--el-color-primary) !important;
    text-align: center;
    background-color: var(--el-color-primary-light-9);
    border-radius: 12px;
}

.info-charts{
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 20px;
    height: 420px;
}

.info-chart{
    height: 380px;
    padding: 20px 10px;
    background-color: var(--default-box-color);
    margin-right: 20px;
    border: 1px solid var(--default-border);
    border-radius: 10px;
}

.info-chart :deep(.el-card__body){
    height: 100%;
    padding: 0;
}

.table-title{
    height: 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.info-title{
    font-weight: 500;
    text-align: left;
    padding: 0;
    font-size: 16px;
    color: var(--ui-gray-800);
}

.info-herf{
    font-size: 14px;
    font-weight: 400;
    color: var(--ui-gray-500);
    text-align: left;
    cursor: pointer;
    transition: color 0.2s;
}

.info-herf:hover{
    color: var(--theme-color);
}

.info-chart-container{
    height: calc(100% - 40px);
}

.info-tables{
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 20px;
}

.table-container{
    width: calc(100% - 20px);
    background-color: transparent;
    padding: 0 10px;
    max-height: 374px;
}

.home :deep(.el-pagination) {
    --el-pagination-button-bg-color: var(--ui-gray-100);
    --el-pagination-button-color: var(--ui-gray-600);
    --el-pagination-hover-color: var(--theme-color);
}

</style>