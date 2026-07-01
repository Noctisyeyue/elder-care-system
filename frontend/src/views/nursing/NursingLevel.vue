<!-- 管理端--子菜单--护理级别 -->
<template>
  <div class="art-full-height">
    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">护理级别列表</span>
        <div class="flex-c" style="gap: 8px;">
          <el-button :type="query.status === '启用' ? 'primary' : ''" @click="query.status = '启用'; fetchLevelList()">
            <SvgIcon icon="ri:checkbox-circle-line" :size="16" />
            <span>启用</span>
          </el-button>
          <el-button :type="query.status === '停用' ? 'primary' : ''" @click="query.status = '停用'; fetchLevelList()">
            <SvgIcon icon="ri:close-circle-line" :size="16" />
            <span>停用</span>
          </el-button>
          <el-button type="success" @click="openEditDialog()">
            <SvgIcon icon="ri:add-line" :size="16" />
            <span>添加</span>
          </el-button>
        </div>
      </div>
      <el-table :data="levelList" height="100%" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="level" label="护理级别" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="openEditDialog(scope.row)">
              <SvgIcon icon="ri:edit-line" :size="16" />
              <span>修改</span>
            </el-button>
            <el-button link type="success" @click="goToItemConfig(scope.row)">
              <SvgIcon icon="ri:settings-4-line" :size="16" />
              <span>护理项目配置</span>
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

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="editDialog.visible" :title="editDialog.title" width="400px" align-center
      @close="editDialog.visible = false">
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
import { getNursingLevelList, addNursingLevel, updateNursingLevel } from '@/api/nursing'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const router = useRouter()

/** 查询条件。 */
const query = reactive({ status: '启用' })

/** 护理级别列表。 */
const levelList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchLevelList = async () => {
  const res = await getNursingLevelList({ status: query.status, pageNum: page.value, pageSize: pageSize.value })
  levelList.value = res.records || []
  total.value = res.total || 0
}

/** 编辑弹窗。 */
const editDialog = reactive({
  visible: false,
  title: '',
  form: { id: null, level: '', status: '启用' },
})

function openEditDialog(row) {
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

async function saveLevel() {
  if (!editDialog.form.level) { ElMessage.error('请输入护理级别名称'); return }
  if (editDialog.form.id) {
    await updateNursingLevel({ id: editDialog.form.id, status: editDialog.form.status })
  } else {
    await addNursingLevel({ level: editDialog.form.level, status: editDialog.form.status })
  }
  editDialog.visible = false
  fetchLevelList()
}

function goToItemConfig(level) {
  localStorage.setItem('NursingItemSettingLevelId', level.id)
  localStorage.setItem('NursingItemSettingLevelName', level.level)
  router.push({ name: 'NursingItemSetting' })
}

function handlePageChange(val) { page.value = val; fetchLevelList() }
function handleSizeChange(size) { pageSize.value = size; page.value = 1; fetchLevelList() }

onMounted(fetchLevelList)
</script>

<style scoped>
/* 样式由全局管理 */
</style>
