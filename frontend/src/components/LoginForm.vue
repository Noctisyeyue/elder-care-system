<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    :validate-on-rule-change="false"
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
    <el-form-item v-if="captchaEnabled" prop="code">
      <div style="display: flex; gap: 10px; width: 100%">
        <el-input
          v-model="form.code"
          size="large"
          placeholder="请输入验证码"
          class="custom-height"
          style="flex: 1"
        >
          <template #prefix>
            <el-icon><Key /></el-icon>
          </template>
        </el-input>
        <img
          :src="captchaImg"
          title="点击刷新验证码"
          class="captcha-img"
          style="width: 120px; height: 44px; border-radius: 4px; cursor: pointer"
          @click="loadCaptcha"
        />
      </div>
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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '@/utils/request'
import { getCaptcha } from '@/api/user'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { User, Lock, Key } from '@element-plus/icons-vue'
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
  code: '',
  uuid: '',
})

const captchaEnabled = ref(false)
const captchaImg = ref('')

const rules = computed<FormRules>(() => ({
  account: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  ...(captchaEnabled.value
    ? { code: [{ required: true, message: '请输入验证码', trigger: 'blur' }] }
    : {}),
}))

const loadCaptcha = async () => {
  try {
    const captcha = await getCaptcha()
    captchaEnabled.value = captcha.captchaEnabled
    if (captcha.captchaEnabled) {
      captchaImg.value = captcha.img || ''
      form.value.uuid = captcha.uuid || ''
      form.value.code = ''
    } else {
      captchaImg.value = ''
      form.value.uuid = ''
      form.value.code = ''
    }
  } catch {
    ElMessage.error('验证码加载失败，请刷新页面重试')
  }
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
    const { account, password, code, uuid } = form.value
    const response = await post<LoginResponse>('/user/login', { account, password, code, uuid })
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
  } catch {
    // 失败提示由请求拦截器处理，这里只刷新验证码
    if (captchaEnabled.value) {
      await loadCaptcha()
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})
</script>
