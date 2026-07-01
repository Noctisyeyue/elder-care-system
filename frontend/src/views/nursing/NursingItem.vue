<!-- 管理端--子菜单--护理项目 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="项目名称">
          <el-input v-model="query.name" placeholder="请输入护理项目名称" clearable @clear="fetchList"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
          <el-button type="success" @click="openEditDialog()">
            <SvgIcon icon="ri:add-line" :size="16" />
            <span>添加</span>
          </el-button>
          <el-button type="danger" :disabled="multipleSelection.length === 0" @click="deleteSelectedItems">
            <SvgIcon icon="ri:delete-bin-line" :size="16" />
            <span>批量删除</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">护理项目列表</span>
        <div class="flex-c" style="gap: 8px;">
          <el-button :type="query.status === '启用' ? 'primary' : ''" @click="query.status = '启用'; fetchList()">
            <SvgIcon icon="ri:checkbox-circle-line" :size="16" />
            <span>启用</span>
          </el-button>
          <el-button :type="query.status === '停用' ? 'primary' : ''" @click="query.status = '停用'; fetchList()">
            <SvgIcon icon="ri:close-circle-line" :size="16" />
            <span>停用</span>
          </el-button>
        </div>
      </div>
      <el-table :data="list" height="100%" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="code" label="编号" width="150" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="frequency" label="执行周期" width="120" />
        <el-table-column prop="count" label="总次数" width="120" />
        <el-table-column prop="desc" label="描述" min-width="120" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEditDialog(scope.row)">
              <SvgIcon icon="ri:edit-line" :size="16" />
              <span>修改</span>
            </el-button>
            <el-button link type="danger" @click="removeItem(scope.row)">
              <SvgIcon icon="ri:delete-bin-line" :size="16" />
              <span>删除</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="table-pagination" background
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="editDialog.visible" :title="editDialog.title" width="500px" align-center
      @close="formRef?.resetFields()">
      <el-form :model="editDialog.form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="编号" prop="code">
          <el-input v-model="editDialog.form.code" :disabled="!!editDialog.form.id" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="editDialog.form.name" />
        </el-form-item>
        <el-form-item label="执行周期" prop="frequency">
          <el-input v-model="editDialog.form.frequency" />
        </el-form-item>
        <el-form-item label="总次数" prop="count">
          <el-input v-model="editDialog.form.count" type="number" />
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input v-model="editDialog.form.desc" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editDialog.form.status">
            <el-option label="启用" value="启用" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getNursingItemList, addNursingItem, updateNursingItem, deleteNursingItem } from '@/api/nursing'
import { ElMessage, ElMessageBox } from 'element-plus'
import SvgIcon from '@/components/base/svg-icon/index.vue'

/** 查询条件。 */
const query = reactive({ name: '', status: '启用' })

const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const list = ref([])
const formRef = ref(null)

const rules = {
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  frequency: [{ required: true, message: '请输入执行周期', trigger: 'blur' }],
  count: [{ required: true, message: '请输入可行次数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const fetchList = async () => {
  const res = await getNursingItemList({
    name: query.name, status: query.status, pageNum: currentPage.value, pageSize: pageSize.value,
  })
  list.value = res.records || []
  total.value = res.total || 0
}

const editDialog = reactive({
  visible: false, title: '',
  form: { id: null, code: '', name: '', frequency: '', count: '', desc: '', status: '启用' },
})

function openEditDialog(row) {
  if (row) {
    editDialog.title = '编辑护理项目'
    Object.assign(editDialog.form, row)
  } else {
    editDialog.title = '添加护理项目'
    Object.assign(editDialog.form, { id: null, code: '', name: '', frequency: '', count: '', desc: '', status: '启用' })
  }
  editDialog.visible = true
}

async function saveItem() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (editDialog.form.id) { await updateNursingItem(editDialog.form) }
    else { await addNursingItem(editDialog.form) }
    editDialog.visible = false
    fetchList()
    ElMessage.success('保存成功')
  } catch { ElMessage.error('请输入完整信息') }
}

function removeItem(row) {
  ElMessageBox.confirm(`确定要删除护理项目"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await deleteNursingItem(row.id)
    ElMessage.success('删除成功')
    fetchList()
  }).catch(() => {})
}

const multipleSelection = ref([])
function handleSelectionChange(val) { multipleSelection.value = val }

function deleteSelectedItems() {
  if (multipleSelection.value.length === 0) { ElMessage.warning('请先选择要删除的项目'); return }
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 个护理项目吗？`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(async () => {
    for (const row of multipleSelection.value) { await deleteNursingItem(row.id) }
    ElMessage.success('批量删除成功！')
    fetchList()
  }).catch(() => { ElMessage.info('已取消删除') })
}

function handlePageChange(val) { currentPage.value = val; fetchList() }
function handleSizeChange(size) { pageSize.value = size; currentPage.value = 1; fetchList() }

onMounted(fetchList)
</script>

<style scoped>
/* 样式由全局管理 */
</style>
