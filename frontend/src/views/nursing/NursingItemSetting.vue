<template>
  <div>
    <div style="margin-bottom: 16px">
      <el-input v-model="searchName" placeholder="护理项目名称" style="width: 200px; margin-right: 8px" />
      <el-button @click="fetchAllProjects">查询</el-button>
      <el-button @click="goBack">返回</el-button>
      <el-button type="success" @click="handleSave">保存</el-button>
    </div>
    <div style="display: flex; gap: 16px">
      <!-- 左侧：所有护理项目 -->
      <div style="flex: 1">
        <div class="table-title">护理项目</div>
        <el-table :data="projects" border style="margin-bottom: 8px">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="frequency" label="执行周次" />
          <el-table-column prop="count" label="执行次数" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button type="primary" size="small" style="margin-left: 3px;" @click="addProject(scope.row)">添加</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="page"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          style="margin-top: 8px"
        />
      </div>
      <!-- 右侧：当前级别下的护理项目 -->
      <div style="flex: 1">
        <div class="table-title">护理项目（{{ levelName }}）</div>
        <el-table :data="configuredProjects" border>
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="code" label="编号" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="frequency" label="执行周次" />
          <el-table-column prop="count" label="执行次数" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button type="danger" size="small" style="margin-left: 3px;" @click="removeProject(scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          class="pagination-right"
          layout="total, sizes, prev, pager, next, jumper"
          :total="configTotal"
          :page-size="configPageSize"
          :current-page="configPage"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleConfigSizeChange"
          @current-change="handleConfigPageChange"
          style="margin-top: 8px"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const levelId = localStorage.getItem('NursingItemSettingLevelId')
const levelName = localStorage.getItem('NursingItemSettingLevelName')

const searchName = ref('')
const allProjects = ref([]) // 全部护理项目
const allConfiguredProjects = ref([]) // 全部已配置项目
const projects = ref([]) // 当前页护理项目
const configuredProjects = ref([]) // 当前页已配置项目
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const configPage = ref(1)
const configPageSize = ref(10)
const configTotal = ref(0)
const isChanged = ref(false)

const updateProjectsPage = () => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  projects.value = allProjects.value.slice(start, end)
  total.value = allProjects.value.length
}
const updateConfiguredProjectsPage = () => {
  const start = (configPage.value - 1) * configPageSize.value
  const end = start + configPageSize.value
  configuredProjects.value = allConfiguredProjects.value.slice(start, end)
  configTotal.value = allConfiguredProjects.value.length
}

const fetchAllProjects = async () => {
  const params = { status: '启用', pageNum: 1, pageSize: 9999, name: searchName.value || '' }
  const res = await get('/nursing/item/list', params)
  // 过滤掉已配置的
  allProjects.value = res.records?.filter(item => !allConfiguredProjects.value.find(p => p.id === item.id)) || []
  updateProjectsPage()
}
const fetchConfiguredProjects = async () => {
  const res = await get('/nursing/level/item/list', { levelId, pageNum: 1, pageSize: 9999 })
  allConfiguredProjects.value = res.records || []
  updateConfiguredProjectsPage()
}
const addProject = (item) => {
  if (!allConfiguredProjects.value.find(p => p.id === item.id)) {
    allConfiguredProjects.value.push(item)
    allProjects.value = allProjects.value.filter(p => p.id !== item.id)
    configPage.value = 1
    updateConfiguredProjectsPage()
    updateProjectsPage()
    isChanged.value = true
  }
}
const removeProject = (item) => {
  if (!allProjects.value.find(p => p.id === item.id)) {
    allProjects.value.push(item)
    allConfiguredProjects.value = allConfiguredProjects.value.filter(p => p.id !== item.id)
    page.value = 1
    updateConfiguredProjectsPage()
    updateProjectsPage()
    isChanged.value = true
  }
}
const handlePageChange = (val) => {
  page.value = val
  updateProjectsPage()
}
const handleConfigPageChange = (val) => {
  configPage.value = val
  updateConfiguredProjectsPage()
}
const handleConfigSizeChange = (size) => {
  configPageSize.value = size
  configPage.value = 1
  updateConfiguredProjectsPage()
}
const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  updateProjectsPage()
}
const handleSave = async () => {
  const itemIds = allConfiguredProjects.value.map(p => p.id)
  await post('/nursing/level/item/save', { levelId, itemIds })
  isChanged.value = false
  ElMessage.success('保存成功！')
}
const goBack = () => {
  router.push({ name: 'NursingLevel' })
}
onMounted(async () => {
  await fetchConfiguredProjects()
  await fetchAllProjects()
})
// 离开提示
onBeforeRouteLeave((to, from, next) => {
  if (isChanged.value) {
    ElMessageBox.confirm('有未保存的变更，确定要离开吗？', '提示', { type: 'warning' })
      .then(() => next())
      .catch(() => next(false))
  } else {
    next()
  }
})
</script>

<style scoped>
.table-title {
  background: #2ba6f2;
  color: #fff;
  padding: 8px;
  margin-bottom: 8px;
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
