<template>
  <div class="auth-page">
    <LoginLeftView />
    <div class="auth-right">
      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">找回密码</h3>
          <p class="sub-title">请通过邮箱验证重置密码</p>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            style="margin-top: 25px"
            @keyup.enter="handleSubmit"
          >
            <el-form-item prop="email">
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
            <el-form-item prop="code">
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
                    :disabled="codeBtnDisabled"
                    @click="handleSendCode"
                  >
                    {{ codeBtnText }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                size="large"
                placeholder="请输入 6-18 位新密码"
                class="custom-height"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
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
                确认修改
              </el-button>
            </el-form-item>
          </el-form>
          <div class="auth-footer-link">
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
import { ElMessage, ElNotification } from 'element-plus'
import { post } from '@/utils/request'
import { Message, Key, Lock } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import LoginLeftView from '@/components/auth/LoginLeftView.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  email: '',
  code: '',
  password: '',
})

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为 6 位数字', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 18, message: '密码长度为 6-18 位', trigger: 'blur' },
  ],
}

// 验证码倒计时
const codeBtnDisabled = ref(false)
const codeBtnText = ref('获取验证码')
let countdown = 60
let timer: number | null = null

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
    const res = await post<any>('/common/code/request', { email: form.email })
    await post('/common/code/email', { email: form.email, code: res.permissionCode })
    ElNotification({
      title: '验证码已发送',
      message: '验证码已发送到您的邮箱（5 分钟内有效），请查收',
      type: 'success',
    })
    codeBtnDisabled.value = true
    countdown = 60
    codeBtnText.value = `${countdown}s 后重获`
    timer = window.setInterval(() => {
      countdown--
      if (countdown <= 0) {
        clearTimer()
        codeBtnText.value = '获取验证码'
        codeBtnDisabled.value = false
      } else {
        codeBtnText.value = `${countdown}s 后重获`
      }
    }, 1000)
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
    await post('/common/code/findPassword', {
      email: form.email,
      password: form.password,
      code: form.code,
    })
    ElMessage.success('密码修改成功')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>
