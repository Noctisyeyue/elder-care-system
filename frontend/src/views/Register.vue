<template>
  <div class="register-container">
    <el-card class="register-card" shadow="hover">
      <h2>护工注册</h2>
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="4-20 位字母、数字或下划线" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-row :gutter="10">
            <el-col :span="15">
              <el-input v-model="form.code" placeholder="请输入 6 位验证码" />
            </el-col>
            <el-col :span="9">
              <el-button
                style="width: 100%"
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
            placeholder="请输入 6-18 位密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-button
          type="primary"
          native-type="submit"
          style="width: 100%"
          :loading="loading"
        >
          注册
        </el-button>
        <div class="footer-link">
          已有账号？<span @click="goLogin">返回登录</span>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register, sendRegisterCode } from '@/api/user'

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

// 验证码按钮倒计时
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
  // 先单独校验邮箱字段
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
    // 拦截器已弹出错误消息，这里无需重复提示
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

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f7fa;
}

.register-card {
  width: 420px;
  border-radius: 10px;
  box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08);
  padding: 12px 24px 24px 24px;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 24px;
  font-size: 1.6rem;
  font-weight: bold;
  color: #222;
}

.footer-link {
  text-align: center;
  margin-top: 12px;
  font-size: 13px;
  color: #666;
}

.footer-link span {
  color: #409eff;
  cursor: pointer;
}
</style>
