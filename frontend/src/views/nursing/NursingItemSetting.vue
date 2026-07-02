<!-- 管理端--子菜单--护理级别--护理项目配置 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" @submit.prevent class="search-form">
        <el-form-item label="项目名称">
          <el-input v-model="searchName" placeholder="请输入项目名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchAllProjects">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
          <el-button @click="goBack">
            <SvgIcon icon="ri:arrow-left-line" :size="16" />
            <span>返回</span>
          </el-button>
          <el-button type="success" @click="handleSave">
            <SvgIcon icon="ri:save-line" :size="16" />
            <span>保存</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 双栏表格 -->
    <div style="display: flex; gap: 16px; flex: 1; min-height: 0;">
      <!-- 左：可选项目 -->
      <el-card class="art-table-card" shadow="never" style="flex: 1; margin-top: 0;">
        <div class="table-header">
          <span class="table-header-title">护理项目</span>
        </div>
        <el-table :data="projects" height="100%" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="frequency" label="执行周次" />
          <el-table-column prop="count" label="总次数" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button link type="primary" @click="addProject(scope.row)">
                <SvgIcon icon="ri:add-circle-line" :size="16" />
                <span>添加</span>
              </el-button>
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

      <!-- 右：已配项目 -->
      <el-card class="art-table-card" shadow="never" style="flex: 1; margin-top: 0;">
        <div class="table-header">
          <span class="table-header-title">护理项目（{{ levelName }}）</span>
        </div>
        <el-table :data="configuredProjects" height="100%" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="frequency" label="执行周次" />
          <el-table-column prop="count" label="总次数" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button link type="danger" @click="removeProject(scope.row)">
                <SvgIcon icon="ri:close-circle-line" :size="16" />
                <span>移除</span>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="table-pagination" background
          v-model:current-page="configPage"
          v-model:page-size="configPageSize"
          layout="total, prev, pager, next, sizes, jumper"
          :total="configTotal"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleConfigSizeChange"
          @current-change="handleConfigPageChange" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { getNursingItemList, getNursingLevelItems, saveNursingLevelItems } from '@/api/nursing'
import { ElMessage, ElMessageBox } from 'element-plus'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const router = useRouter()
const levelId = localStorage.getItem('NursingItemSettingLevelId')
const levelName = localStorage.getItem('NursingItemSettingLevelName')

const searchName = ref('')
const allProjects = ref([])
const allConfiguredProjects = ref([])
const projects = ref([])
const configuredProjects = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const configPage = ref(1)
const configPageSize = ref(10)
const configTotal = ref(0)
const isChanged = ref(false)

// 左侧可选项目本地分页。
function updateProjectsPage() {
  const start = (page.value - 1) * pageSize.value
  projects.value = allProjects.value.slice(start, start + pageSize.value)
  total.value = allProjects.value.length
}

// 右侧已配置项目本地分页。
function updateConfiguredProjectsPage() {
  const start = (configPage.value - 1) * configPageSize.value
  configuredProjects.value = allConfiguredProjects.value.slice(start, start + configPageSize.value)
  configTotal.value = allConfiguredProjects.value.length
}

async function fetchAllProjects() {
  const params = { status: '启用', pageNum: 1, pageSize: 9999, name: searchName.value || '' }
  const res = await getNursingItemList(params)
  allProjects.value = res.records?.filter(item => !allConfiguredProjects.value.find(p => p.id === item.id)) || []
  updateProjectsPage()
}
async function fetchConfiguredProjects() {
  const res = await getNursingLevelItems({ levelId, pageNum: 1, pageSize: 9999 })
  allConfiguredProjects.value = res.records || []
  updateConfiguredProjectsPage()
}

function addProject(item) {
  if (!allConfiguredProjects.value.find(p => p.id === item.id)) {
    allConfiguredProjects.value.push(item)
    allProjects.value = allProjects.value.filter(p => p.id !== item.id)
    configPage.value = 1
    updateConfiguredProjectsPage()
    updateProjectsPage()
    isChanged.value = true
  }
}

function removeProject(item) {
  if (!allProjects.value.find(p => p.id === item.id)) {
    allProjects.value.push(item)
    allConfiguredProjects.value = allConfiguredProjects.value.filter(p => p.id !== item.id)
    page.value = 1
    updateConfiguredProjectsPage()
    updateProjectsPage()
    isChanged.value = true
  }
}

function handlePageChange(val) {
  page.value = val
  updateProjectsPage()
}

function handleConfigPageChange(val) {
  configPage.value = val
  updateConfiguredProjectsPage()
}

function handleConfigSizeChange(size) {
  configPageSize.value = size
  configPage.value = 1
  updateConfiguredProjectsPage()
}

function handleSizeChange(size) {
  pageSize.value = size
  page.value = 1
  updateProjectsPage()
}

async function handleSave() {
  await saveNursingLevelItems(levelId, allConfiguredProjects.value.map(p => p.id))
  isChanged.value = false
  ElMessage.success('保存成功！')
}

function goBack() {
  router.push({ name: 'NursingLevel' })
}

onMounted(async () => {
  await fetchConfiguredProjects()
  await fetchAllProjects()
})

onBeforeRouteLeave((to, from, next) => {
  if (isChanged.value) {
    ElMessageBox.confirm('有未保存的变更，确定要离开吗？', '提示', { type: 'warning' })
      .then(() => next()).catch(() => next(false))
  } else {
    next()
  }
})
</script>

<style scoped>
/* 样式由全局管理 */
</style>
