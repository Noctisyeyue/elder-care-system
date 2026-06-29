<template>
  <div>
    <!-- 顶部操作 -->
    <div style="margin-bottom: 20px">
      <el-button type="primary" @click="openEditDialog()">添加</el-button>
    </div>
    <div style="margin-bottom: 20px">
      <el-radio-group v-model="query.status" @change="fetchLevelList">
        <el-radio-button value="启用">启用</el-radio-button>
        <el-radio-button value="停用">停用</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 护理级别表格 -->
    <el-table :data="levelList" style="width: 100%" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="level" label="护理级别" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="text" @click="openEditDialog(scope.row)">修改</el-button>
          <el-button type="text" @click="goToItemConfig(scope.row)" style="color: #67c23a"
            >护理项目配置</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pagination-right"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize.value"
      :current-page="page"
      :page-sizes="[5, 10, 20, 50]"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      style="margin-top: 20px"
    />

    <!-- 添加/编辑护理级别弹窗 -->
    <el-dialog v-model="editDialog.visible" :title="editDialog.title" width="400px">
      <el-form :model="editDialog.form" label-width="80px">
        <el-form-item label="护理级别">
          <el-input v-model="editDialog.form.level" :disabled="!!editDialog.form.id" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editDialog.form.status">
            <el-option label="启用" value="启用" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveLevel">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post, put } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 查询参数
const query = reactive({
  status: '启用',
})

// 护理级别列表
const levelList = ref([])

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取护理级别列表
const fetchLevelList = async () => {
  const res = await get('/nursing/level/list', { status: query.status, pageNum: page.value, pageSize: pageSize.value })
  levelList.value = res.records || []
  total.value = res.total || 0
}

// 编辑弹窗
const editDialog = reactive({
  visible: false,
  title: '添加/编辑(同一个模态框)',
  form: {
    id: null,
    level: '',
    status: '启用',
  },
})

// 打开编辑弹窗
const openEditDialog = (row) => {
  if (row) {
    editDialog.title = '编辑护理级别'
    editDialog.form.id = row.id
    editDialog.form.level = row.level
    editDialog.form.status = row.status
  } else {
    editDialog.title = '添加护理级别'
    editDialog.form.id = null
    editDialog.form.level = ''
    editDialog.form.status = '启用'
  }
  editDialog.visible = true
}

// 保存护理级别
const saveLevel = async () => {
  if (!editDialog.form.level) {
    ElMessage.error('请输入护理级别名称')
    return
  }
  if (editDialog.form.id) {
    // 修改
    await put('/nursing/level/update', {
      id: editDialog.form.id,
      status: editDialog.form.status,
    })
  } else {
    // 新增
    await post('/nursing/level/add', {
      level: editDialog.form.level,
      status: editDialog.form.status,
    })
  }
  editDialog.visible = false
  fetchLevelList()
}

const router = useRouter()

const goToItemConfig = (level) => {
  router.push({name: 'NursingItemSetting'})
  if(localStorage.getItem('NursingItemSettingLevelId')){
    localStorage.removeItem('NursingItemSettingLevelId')
  }
  if(localStorage.getItem('NursingItemSettingLevelName')){
    localStorage.removeItem('NursingItemSettingLevelName')
  }
  localStorage.setItem('NursingItemSettingLevelId', level.id)
  localStorage.setItem('NursingItemSettingLevelName', level.level)
}

const handlePageChange = (val) => {
  page.value = val
  fetchLevelList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  fetchLevelList()
}

onMounted(() => {
  fetchLevelList()
})
</script>

<style scoped>
/* 待美化样式 */
:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 337px);
}

.pagination-right {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
