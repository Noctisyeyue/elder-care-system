<template>
  <el-form :model="form" label-position="top" @submit.prevent="handleSubmit">
    <el-form-item label="用户名">
      <el-input v-model="form.userName" placeholder="请输入用户名" required />
    </el-form-item>
    <el-form-item label="密码">
      <el-input
        v-model="form.password"
        type="password"
        placeholder="请输入密码"
        show-password
        required
      />
    </el-form-item>
    <el-button type="primary" native-type="submit" style="width: 100%" :loading="loading"
      >登录</el-button
    >
    <div class="b"><span @click="handleForgetPassword">忘记密码？</span></div>
  </el-form>
      <!-- 找回密码 -->
    <el-dialog
  @close="clearForm('findPasswordForm')"
  title="找回密码"
  v-model="dialogFormVisible"
  width="520px"
  class="find-password-dialog"
>
  <el-form
    :model="findPasswordForm"
    :rules="findPasswordRules"
    ref="findPasswordFormRef"
    label-width="80px"
    class="find-password-form"
  >
    <el-form-item label="邮箱" prop="email">
      <el-input
        placeholder="请填写邮箱"
        v-model="findPasswordForm.email"
      ></el-input>
    </el-form-item>
    <el-form-item label="新密码" prop="password">
      <el-input
        placeholder="请填写 6-18 位密码"
        type="password"
        v-model="findPasswordForm.password"
        show-password
      ></el-input>
    </el-form-item>
    <el-form-item label="验证码" prop="code">
      <el-row>
        <el-col :span="18">
          <el-input
            placeholder="请填写6位数字验证码"
            type="text"
            v-model="findPasswordForm.code"
          ></el-input>
        </el-col>
        <el-col :span="6">
          <el-button
            style="margin-left: 10px;"
            @click="sendEmailCode()"
            :disabled="disabled"
          >
            {{ msg }}
          </el-button>
        </el-col>
      </el-row>
    </el-form-item>
  </el-form>
  <div slot="footer" class="dialog-footer" style="display: flex; justify-content: center;">
    <el-button
      type="danger"
      :loading="loading"
      @click="submitFindPassword('findPasswordForm')"
    >
      确认修改
    </el-button>
  </div>
</el-dialog>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import type { FormRules } from 'element-plus'
import { ElNotification } from 'element-plus';

interface LoginResponse {
  token: string
  email: string
}
const props = defineProps({
  role: {
    type: String,
    required: true,
    validator: (value: string) => ['admin', 'caregiver'].includes(value),
  },
})

const router = useRouter()
const loading = ref(false)
const form = ref({
  userName: '',
  password: '',
})
const userStore = useUserStore()
const handleSubmit = async () => {
  loading.value = true
  try {
    const { userName, password } = form.value

    const response = await post<LoginResponse>('/user/login', {
      userName,
      password,
      role: props.role,
    })

    userStore.setUser(response.token, props.role, userName, response.email || '')
    // const testToken = 'This is a test token'
    // userStore.setUser(testToken, props.role, userName)
    router.push('/')
    ElMessage.success('登录成功')
  } finally {
    loading.value = false
  }
}

const loginForm = ref({
  email: '',
  password: ''
})
const findPasswordForm = ref({
  email: '',
  password: '',
  code: null
})

const disabled = ref(false)
const msg = ref('点击获取验证码')
const count = ref(60)
const timer = ref<number | null>(null)
const dialogFormVisible = ref(false)
const loginFormRef = ref()
const findPasswordFormRef = ref()
const findPasswordRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] },
  ],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 18, message: '密码长度为6-18位', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' },
  ],
}
const submitFindPassword = (formName: string) => {
  const form = formName === 'findPasswordForm' ? findPasswordFormRef.value : loginFormRef.value
  form.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      post("/common/code/findPassword", {
        email: findPasswordForm.value.email,
        password: findPasswordForm.value.password,
        code: findPasswordForm.value.code
      }).then(res => {
        if (res) {
          alert('密码修改成功')
          dialogFormVisible.value = false
        }
      }).finally(() => {
        loading.value = false
      })
    }
  })
}
const sendEmailCode = async () => {
  if (!findPasswordFormRef.value) return;
  try {
    await findPasswordFormRef.value.validateField('email');
  } catch (err) {
    // 校验不通过
    return;
  }
  // 校验通过，执行后续逻辑
ElNotification({
  title: '验证码已发送',
  message: '验证码已发送到您的邮箱（5分钟内有效），请查收',
  type: 'success'
});
  disabled.value = true;
  let countdown = count.value;
  msg.value = `${countdown}s后重新获取`;
  timer.value = window.setInterval(() => {
    countdown--;
    msg.value = `${countdown}s后重新获取`;
    if (countdown <= 0) {
      clearInterval(timer.value!);
      msg.value = '点击获取验证码';
      disabled.value = false;
      countdown = 60;
    }
  }, 1000);

  const res = await post<any>('/common/code/request', {
    email: findPasswordForm.value.email
  });
  console.log('验证码请求响应:', res);
  const res2 = await post('/common/code/email', {
    email: findPasswordForm.value.email,
    code: res.permissionCode
  });
};
const clearForm = (formName: string) => {
  if (formName === 'loginForm') {
    Object.assign(loginForm.value, { email: '', password: '' });
  } else if (formName === 'findPasswordForm') {
    Object.assign(findPasswordForm.value, { email: '', password: '', code: null });
    if (findPasswordFormRef.value) {
      findPasswordFormRef.value.clearValidate();
    }
  }
};
const handleForgetPassword = () => {
  dialogFormVisible.value = true;
  console.log(dialogFormVisible.value); // 应该输出 true
};
</script>
<style>
.btn-r {
  width: 100px;
  float: right;
}

.btn-l {
  width: 100px;
  float: left;
}

.b span {
  font-size: 12px;
  cursor: pointer;
  display: inline-block;
  margin-top: 5px;
}

/* 找回密码弹窗样式优化 */
.find-password-dialog .el-dialog__body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 300px;
  padding-top: 0px;
  padding-bottom: 0px;
}
.find-password-form .el-form-item {
  margin-bottom: 28px;
margin-right: 15px;
}
.find-password-form .el-input {
  height: 44px;
  font-size: 16px;
}
.find-password-form .el-button {
  height: 40px;
  margin-right: -15px;
}
</style>
