<!-- 超管--子菜单--用户管理 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户姓名">
          <el-input v-model="searchForm.userName" placeholder="请输入用户姓名" clearable @clear="handleSearch"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <SvgIcon icon="ri:search-line" :size="16" />
            <span>查询</span>
          </el-button>
          <el-button type="primary" @click="openDialog('add')">
            <SvgIcon icon="ri:user-add-line" :size="16" />
            <span>添加</span>
          </el-button>
          <el-button type="danger" :disabled="selectedUsers.length === 0" @click="handleBatchDelete">
            <SvgIcon icon="ri:delete-bin-line" :size="16" />
            <span>批量删除</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">用户列表</span>
      </div>
      <el-table :data="userList" height="100%" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="电话" min-width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="员工类型" min-width="100" />
        <el-table-column label="状态" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success" size="small">启用</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger" size="small">禁用</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openDialog(scope.row)">
              <SvgIcon icon="ri:edit-line" :size="16" />
              <span>修改</span>
            </el-button>
            <el-button v-if="scope.row.status === 0" link type="success" @click="handleAudit(scope.row)">
              <SvgIcon icon="ri:check-line" :size="16" />
              <span>通过审核</span>
            </el-button>
            <el-button v-if="scope.row.status === 1" link type="warning" @click="handleDisable(scope.row)">
              <SvgIcon icon="ri:forbid-line" :size="16" />
              <span>禁用</span>
            </el-button>
            <el-button v-if="scope.row.status === 2" link type="success" @click="handleEnable(scope.row)">
              <SvgIcon icon="ri:checkbox-circle-line" :size="16" />
              <span>启用</span>
            </el-button>
            <el-button link type="warning" @click="handleResetPwd(scope.row)">
              <SvgIcon icon="ri:lock-line" :size="16" />
              <span>重置密码</span>
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">
              <SvgIcon icon="ri:delete-bin-line" :size="16" />
              <span>移除</span>
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
        @size-change="handleSearch"
        @current-change="handleSearch" />
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" align-center
      @close="formRef?.resetFields()">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
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
          <el-select v-model="form.role" placeholder="请选择" disabled>
            <el-option label="管理员" value="管理员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserList, addUser, updateUser, deleteUsers, auditUser, enableUser, disableUser, resetUserPwd,
} from '@/api/user'
import SvgIcon from '@/components/base/svg-icon/index.vue'

const searchForm = reactive({ userName: '' })
const userList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({ userName: '', realName: '', phone: '', email: '', gender: '男', role: '管理员' })
const formRef = ref()
const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
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

function handleSearch() {
  getUserList({ userName: searchForm.userName, pageNum: currentPage.value, pageSize: pageSize.value }).then((res) => {
    userList.value = res.list || []
    total.value = res.total || 0
  })
}

function openDialog(row) {
  if (row === 'add') {
    dialogTitle.value = '添加用户'
    Object.assign(form, { userName: '', realName: '', phone: '', gender: '男', role: '管理员', email: '' })
  } else {
    dialogTitle.value = '编辑用户'
    Object.assign(form, row)
  }
  dialogVisible.value = true
}

function handleSave() {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (dialogTitle.value === '添加用户') {
        addUser({ ...form, password: form.phone.slice(-6) }).then(() => {
          ElMessage.success('添加成功（密码为手机号后6位）')
          dialogVisible.value = false
          handleSearch()
        })
      } else {
        updateUser(form).then(() => {
          ElMessage.success('编辑成功')
          dialogVisible.value = false
          handleSearch()
        })
      }
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户【${row.realName || row.userName}】吗？`, '确认删除', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => { deleteUsers([row.userName]).then(() => { ElMessage.success('删除成功'); handleSearch() }) }).catch(() => {})
}

function handleAudit(row) {
  ElMessageBox.confirm(`确定要通过【${row.realName || row.userName}】的审核吗？`, '审核确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
  }).then(() => { auditUser(row.userId).then(() => { ElMessage.success('审核通过'); handleSearch() }) }).catch(() => {})
}

function handleDisable(row) {
  ElMessageBox.confirm(`确定要禁用【${row.realName || row.userName}】吗？`, '禁用确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => { disableUser(row.userId).then(() => { ElMessage.success('已禁用'); handleSearch() }) }).catch(() => {})
}

function handleEnable(row) {
  ElMessageBox.confirm(`确定要重新启用【${row.realName || row.userName}】吗？`, '启用确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'info',
  }).then(() => { enableUser(row.userId).then(() => { ElMessage.success('已启用'); handleSearch() }) }).catch(() => {})
}

function handleResetPwd(row) {
  ElMessageBox.confirm(`确定要重置【${row.realName || row.userName}】的密码吗？`, '重置密码确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => { resetUserPwd(row.userId).then(() => { ElMessage.success('密码已重置为123456') }) }).catch(() => {})
}

function handleSelectionChange(val) { selectedUsers.value = val }

function handleBatchDelete() {
  if (selectedUsers.value.length === 0) return
  const names = selectedUsers.value.map(u => u.userName)
  ElMessageBox.confirm(`确定要批量删除选中的${names.length}个用户吗？`, '批量删除确认', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    deleteUsers(names).then(() => { ElMessage.success('批量删除成功'); handleSearch(); selectedUsers.value = [] })
  }).catch(() => {})
}

onMounted(handleSearch)
</script>

<style scoped>
/* 样式由全局管理 */
</style>
