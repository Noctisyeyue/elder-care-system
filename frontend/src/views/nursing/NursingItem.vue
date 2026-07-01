<template>
  <div>
    <!-- 查询条件 -->
    <div style="margin-bottom: 10px">
      <el-input v-model="query.name" placeholder="请输入护理项目名称" clearable @clear="fetchList"
        style="width: 200px; margin-right: 10px"><template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="fetchList">查询</el-button>
      <el-button type="success" @click="openEditDialog()">添加</el-button>
      <el-button type="danger" :disabled="multipleSelection.length === 0" @click="deleteSelectedItems">批量删除</el-button>
    </div>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />
    <div style="margin-bottom: 20px">
      <el-radio-group v-model="query.status" @change="fetchList">
        <el-radio-button value="启用">启用</el-radio-button>
        <el-radio-button value="停用">停用</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 护理项目表格 -->
    <el-table :data="list" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="60" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="code" label="编号" width="150" />
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="frequency" label="执行周期" width="120" />
      <el-table-column prop="count" label="单份总次数" width="120" />
      <el-table-column prop="desc" label="描述" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" @click="openEditDialog(scope.row)">修改</el-button>
          <el-button type="text" style="color: red" @click="removeItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin: 20px 0">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50]"
        :total="total" @current-change="fetchList" @size-change="fetchList"
        layout="total, sizes, prev, pager, next, jumper" />
    </div>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="editDialog.visible" :title="editDialog.title" width="500px">
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
        <el-form-item label="单份总次数" prop="count">
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
import { Search } from '@element-plus/icons-vue'
/** 查询条件。 */
const query = reactive({
  name: '',
  status: '启用',
})

/** 分页信息。 */
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

/** 护理项目列表。 */
const list = ref([
  {
    id: 1,
    code: '#1001',
    name: '采背',
    frequency: 2,
    count: 3,
    desc: '很爽',
    status: '启用',
  },
])

/** 表单引用。 */
const formRef = ref(null)

/** 表单校验规则。 */
const rules = {
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  frequency: [{ required: true, message: '请输入执行周期', trigger: 'blur' }],
  count: [{ required: true, message: '请输入可行次数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

/**
 * 查询护理项目列表。
 *
 * @returns 无返回值
 */
const fetchList = async () => {
  const res = await getNursingItemList({
    name: query.name,
    status: query.status,
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  })
  list.value = res.records || []
  total.value = res.total || 0
}

/** 编辑弹窗状态。 */
const editDialog = reactive({
  visible: false,
  title: '添加/编辑护理项目',
  form: {
    id: null,
    code: '',
    name: '',
    frequency: '',
    count: '',
    desc: '',
    status: '启用',
  },
})

/**
 * 打开编辑弹窗。
 *
 * @param row 护理项目行数据
 * @returns 无返回值
 */
const openEditDialog = (row) => {
  if (row) {
    editDialog.title = '编辑护理项目'
    Object.assign(editDialog.form, row)
  } else {
    editDialog.title = '添加护理项目'
    Object.assign(editDialog.form, {
      id: null,
      code: '',
      name: '',
      frequency: '',
      count: '',
      desc: '',
      status: '启用',
    })
  }
  editDialog.visible = true
}

/**
 * 保存护理项目。
 *
 * @returns 无返回值
 */
const saveItem = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (editDialog.form.id) {
      await updateNursingItem(editDialog.form)
    } else {
      await addNursingItem(editDialog.form)
    }
    editDialog.visible = false
    fetchList()
    ElMessage.success('保存成功')
  } catch (error) {
    // 表单验证失败
    ElMessage.error('请输入完整信息')
  }
}

/**
 * 删除护理项目。
 *
 * @param row 护理项目行数据
 * @returns 无返回值
 */
const removeItem = (row) => {
  ElMessageBox.confirm('确定要删除该护理项目吗？', '提示', {
    type: 'warning',
  }).then(async () => {
    await deleteNursingItem(row.id)
    fetchList()
  })
}

/** 当前选中的护理项目。 */
const multipleSelection = ref([])

/**
 * 更新多选结果。
 *
 * @param val 选中行
 * @returns 无返回值
 */
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

/**
 * 批量删除护理项目。
 *
 * @returns 无返回值
 */
const deleteSelectedItems = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请先选择要删除的项目')
    return
  }
  ElMessageBox.confirm(
    `确定要删除选中的 ${multipleSelection.value.length} 个护理项目吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    },
  )
    .then(async () => {
      for (const row of multipleSelection.value) {
        await deleteNursingItem(row.id)
      }
      ElMessage.success('批量删除成功！')
      fetchList()
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

/**
 * 页面初始化时加载护理项目列表。
 *
 * @returns 无返回值
 */
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap) {
  overflow-y: auto;
  max-height: calc(100vh - 340px);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
