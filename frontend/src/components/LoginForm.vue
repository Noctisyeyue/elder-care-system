<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    style="margin-top: 25px"
    @keyup.enter="handleSubmit"
  >
    <el-form-item prop="account">
      <el-input
        v-model="form.account"
        size="large"
        placeholder="请输入用户名或邮箱"
        class="custom-height"
      >
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        v-model="form.password"
        type="password"
        size="large"
        placeholder="请输入密码"
        class="custom-height"
        show-password
      >
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <div style="display: flex; justify-content: flex-end; margin-bottom: 18px; font-size: 13px">
      <router-link to="/find-password" class="text-theme">忘记密码？</router-link>
    </div>
    <el-form-item>
      <el-button
        type="primary"
        size="large"
        class="custom-height"
        style="width: 100%"
        :loading="loading"
        @click="handleSubmit"
      >
        登 录
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

interface LoginResponse {
  token: string
  userId: number
  email: string
  realName: string
  roleId: number
  roleKey: string
  roleName: string
  status: number
}

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref<FormInstance>()

const form = ref({
  account: '',
  password: '',
})

const rules: FormRules = {
  account: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const { account, password } = form.value
    const response = await post<LoginResponse>('/user/login', { account, password })
    userStore.setUser(response)
    // 按 roleKey + status 分流跳转
    let target = '/'
    if (response.roleKey === 'caregiver') {
      target = response.status === 0 ? '/pending' : '/home/caregiver'
    } else {
      // super_admin / admin
      target = '/home/admin'
    }
    router.push(target)
    ElMessage.success('登录成功')
  } finally {
    loading.value = false
  }
}
</script>
