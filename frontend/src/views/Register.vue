<template>
  <div class="auth-page">
    <LoginLeftView />
    <div class="auth-right">
      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">护工注册</h3>
          <p class="sub-title">请填写以下信息完成注册</p>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="80px"
            style="margin-top: 25px"
            @keyup.enter="handleSubmit"
          >
            <el-form-item label="用户名" prop="userName">
              <el-input
                v-model="form.userName"
                size="large"
                placeholder="4-20 位字母、数字或下划线"
                class="custom-height"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="form.email"
                size="large"
                placeholder="请输入邮箱"
                class="custom-height"
              >
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="验证码" prop="code">
              <el-row :gutter="10">
                <el-col :span="15">
                  <el-input
                    v-model="form.code"
                    size="large"
                    placeholder="6 位数字验证码"
                    class="custom-height"
                  >
                    <template #prefix>
                      <el-icon><Key /></el-icon>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="9">
                  <el-button
                    style="width: 100%; height: 40px"
                    :disabled="codeButtonDisabled"
                    @click="handleSendCode"
                  >
                    {{ codeButtonText }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                size="large"
                placeholder="6-18 位密码"
                class="custom-height"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="form.realName"
                size="large"
                placeholder="请输入真实姓名"
                class="custom-height"
              >
                <template #prefix>
                  <el-icon><Postcard /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="form.phone"
                size="large"
                placeholder="请输入手机号"
                class="custom-height"
              >
                <template #prefix>
                  <el-icon><Iphone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-select
                v-model="form.gender"
                placeholder="请选择性别"
                size="large"
                style="width: 100%"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="custom-height"
                style="width: 100%"
                :loading="loading"
                @click="handleSubmit"
              >
                注 册
              </el-button>
            </el-form-item>
          </el-form>
          <div class="auth-footer-link">
            已有账号？
            <router-link to="/login" class="text-theme">返回登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register, sendRegisterCode } from '@/api/user'
import { User, Message, Key, Lock, Postcard, Iphone } from '@element-plus/icons-vue'
import LoginLeftView from '@/components/auth/LoginLeftView.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  userName: '',
  email: '',
  code: '',
  password: '',
  realName: '',
  phone: '',
  gender: '',
})

const rules: FormRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]{4,20}$/,
      message: '用户名为 4-20 位字母、数字或下划线',
      trigger: 'blur',
    },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为 6 位数字', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 18, message: '密码长度为 6-18 位', trigger: 'blur' },
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
}

// 验证码倒计时
const codeButtonDisabled = ref(false)
const codeButtonText = ref('获取验证码')
let countdown = 60
let timer: number | null = null

const startCountdown = () => {
  codeButtonDisabled.value = true
  countdown = 60
  codeButtonText.value = `${countdown}s 后重获`
  timer = window.setInterval(() => {
    countdown--
    if (countdown <= 0) {
      clearTimer()
      codeButtonText.value = '获取验证码'
      codeButtonDisabled.value = false
    } else {
      codeButtonText.value = `${countdown}s 后重获`
    }
  }, 1000)
}

const clearTimer = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

onUnmounted(clearTimer)

const handleSendCode = async () => {
  try {
    await formRef.value?.validateField('email')
  } catch {
    return
  }
  try {
    await sendRegisterCode({ email: form.email })
    ElMessage.success('验证码已发送，5 分钟内有效')
    startCountdown()
  } catch {
    // 拦截器已弹出错误消息
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
    await register({ ...form })
    ElMessage.success('注册成功，请等待管理员审核')
    router.push('/login')
  } catch {
    // 拦截器已弹出错误消息
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 收紧表单项间距，7 个字段在 650px 内不溢出 */
:deep(.el-form-item) {
  margin-bottom: 12px;
}
</style>
