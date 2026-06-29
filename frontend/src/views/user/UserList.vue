<template>
  <div>
    <!-- 查询和操作栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item>
        <el-input
          v-model="searchForm.userName"
          placeholder="请输入用户姓名"
          clearable
          @clear="handleSearch"
          style="width: 200px; margin-right: 10px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="primary" @click="openDialog('add')">添加</el-button>
        <el-button type="danger" :disabled="selectedUsers.length === 0" @click="handleBatchDelete"
          >批量删除</el-button
        >
      </el-form-item>
    </el-form>
    <el-divider style="margin-top: 0; margin-bottom: 10px" />
    <!-- 用户列表表格 -->
    <el-table :data="userList" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="员工类型" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button type="text" @click="openDialog(scope.row)">修改</el-button>
          <el-button type="text" style="color: red" @click="handleDelete(scope.row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑用户弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" :disabled="dialogTitle === '编辑用户'" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="员工类型" prop="role">
          <el-select v-model="form.role" placeholder="请选择">
            <el-option label="护士" value="护士" />
            <el-option label="医生" value="医生" />
            <el-option label="护工" value="护工" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分页组件 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      @current-change="handleSearch"
      @size-change="handleSearch"
      layout="total, sizes, prev, pager, next, jumper"
      :page-sizes="[5, 10, 20, 50]"
      style="margin-top: 20px"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ElMessageBox } from 'element-plus'
import { get, post, del } from '@/utils/request'
import { Search } from '@element-plus/icons-vue'
const searchForm = reactive({ userName: '' })
const userList = ref([
  {
    id: 1,
    userName: 'jett',
    realName: '李福星',
    phone: '1432543223',
    gender: '男',
    email: '20223367@stu.neu.edu.cn',
    role: '护工',
  },
])

const dialogVisible = ref(false)
const dialogTitle = ref('添加/编辑（同一个模态框）')
const form = reactive({
  userName: '',
  realName: '',
  phone: '',
  email: '',
  gender: '男',
  role: '护工',
})
const formRef = ref()
const rules = {
  userName: [{ required: true, message: '请输入用户编号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  role: [{ required: true, message: '请选择员工类型', trigger: 'change' }],
}

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedUsers = ref([])

// 后端data需要返回：
// {
//   list: Array<{
//     id: number;
//     userName: string;
//     realName: string;
//     phone: string;
//     gender: string;
//     email: string;
//     role: string;
//   }>;
//   total: number;
// }
function handleSearch() {
  // searchForm.userName 为空时，查询所有用户；有值时，按姓名筛选
  get('/user/list', {
    userName: searchForm.userName,
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  }).then((res) => {
    // 后端返回 { list: [...], total: 100 }
    userList.value = res.list || []
    total.value = res.total || 0
  })
}

onMounted(() => {
  handleSearch()
})

function openDialog(row) {
  if (row === 'add') {
    dialogTitle.value = '添加用户'
    Object.assign(form, {
      userName: '',
      realName: '',
      phone: '',
      gender: '男',
      role: '护工',
      email: '',
    })
  } else {
    dialogTitle.value = '编辑用户'
    Object.assign(form, row)
  }
  dialogVisible.value = true
}

function handleSave() {
  formRef.value.validate((valid) => {
    if (valid) {
      if (dialogTitle.value === '添加用户') {
        // 密码默认设置为手机号后6位
        const password = form.phone.slice(-6)
        post('/user/add', {
          ...form,
          password,
        }).then(() => {
          ElMessage.success('添加成功（密码为手机号后6位）')
          handleSearch() // 重新加载列表
          dialogVisible.value = false
        })
      } else {
        // 调用编辑接口
        post('/user/update', form).then(() => {
          ElMessage.success('编辑成功')
          handleSearch() // 重新加载列表
          dialogVisible.value = false
        })
      }
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户【${row.realName || row.userName}】吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      post('/user/del', { userNames: [row.userName] }).then(() => {
        ElMessage.success('删除成功')
        handleSearch() // 重新加载列表
      })
    })
    .catch(() => {})
}

function handleSelectionChange(val) {
  selectedUsers.value = val
}

function handleBatchDelete() {
  if (selectedUsers.value.length === 0) return
  const userNames = selectedUsers.value.map((user) => user.userName)
  ElMessageBox.confirm(`确定要批量删除选中的${userNames.length}个用户吗？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      post('/user/del', { userNames }).then(() => {
        ElMessage.success('批量删除成功')
        handleSearch()
        selectedUsers.value = []
      })
    })
    .catch(() => {})
}
</script>

<style scoped>
.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}
.el-row {
  align-items: center;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-table__body-wrapper .el-scrollbar__wrap){
  overflow-y: auto;
  max-height: calc(100vh - 286px);
}
</style>
