<template>
  <div class="user-center">
    <el-row :gutter="20">
      <!-- 左侧资料卡 -->
      <el-col :xs="24" :md="8">
        <el-card class="profile-card">
          <div class="profile-bg" :style="{ backgroundImage: `url(${profileBg})` }"></div>
          <div class="profile-body">
            <el-avatar :size="80" :src="avatarUrl" />
            <h3>{{ profile.realName || '未设置' }}</h3>
            <p class="role">{{ profile.roleName || userStore.roleName }}</p>
            <ul class="info-list">
              <li><span>用户名</span>{{ profile.userName }}</li>
              <li><span>邮箱</span>{{ profile.email || '未绑定' }}</li>
              <li><span>手机</span>{{ profile.phone || '未绑定' }}</li>
              <li><span>性别</span>{{ profile.gender || '未设置' }}</li>
            </ul>
            <el-button type="primary" plain @click="triggerAvatarUpload">更换头像</el-button>
            <input
              ref="avatarInput"
              type="file"
              accept="image/jpeg,image/png"
              style="display: none"
              @change="handleAvatarChange"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧设置区 -->
      <el-col :xs="24" :md="16">
        <!-- 基本信息（可直接保存） -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button v-if="!editing" type="primary" link @click="editing = true">编辑</el-button>
            </div>
          </template>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="80px"
            :disabled="!editing"
          >
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            <el-form-item label="手机" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item v-if="editing">
              <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 账号安全（邮箱只读，修改需验证码） -->
        <el-card class="section-card">
          <template #header>
            <span>账号安全</span>
          </template>
          <p class="card-desc">邮箱用于登录、找回密码和接收验证码。修改邮箱需要验证新邮箱，修改成功后需要重新登录。</p>
          <div class="security-items">
            <div class="security-item">
              <span class="security-label">用户名</span>
              <span class="security-value">{{ profile.userName }}</span>
              <span class="security-extra"></span>
            </div>
            <div class="security-item">
              <span class="security-label">角色</span>
              <span class="security-value">{{ profile.roleName || userStore.roleName }}</span>
              <span class="security-extra"></span>
            </div>
            <div class="security-item">
              <span class="security-label">当前邮箱</span>
              <span class="security-value">{{ profile.email || '未绑定' }}</span>
              <el-button type="primary" link @click="openEmailDialog">修改邮箱</el-button>
            </div>
          </div>
        </el-card>

        <!-- 密码安全 -->
        <el-card class="section-card">
          <template #header>
            <div class="password-card-header">
              <h4>更改密码</h4>
              <p>为了账号安全，建议定期更换密码。修改成功后会自动退出登录。</p>
            </div>
          </template>
          <el-row :gutter="24">
            <el-col :xs="24" :lg="14">
              <el-form
                ref="pwdFormRef"
                :model="pwdForm"
                :rules="pwdRules"
                label-position="top"
              >
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input
                    v-model="pwdForm.oldPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
                <el-row :gutter="16">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="新密码" prop="newPassword">
                      <el-input
                        v-model="pwdForm.newPassword"
                        type="password"
                        placeholder="请输入新密码"
                        show-password
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="确认新密码" prop="confirmPassword">
                      <el-input
                        v-model="pwdForm.confirmPassword"
                        type="password"
                        placeholder="请再次输入新密码"
                        show-password
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </el-col>
            <el-col :xs="24" :lg="10">
              <div class="security-tips">
                <h5>安全建议</h5>
                <ul>
                  <li>新密码至少 6 位，规则与用户管理新增用户保持一致。</li>
                  <li>避免使用生日、手机号、工号等容易被猜测的信息。</li>
                  <li>修改成功后请使用新密码重新登录系统。</li>
                </ul>
              </div>
            </el-col>
          </el-row>
          <div class="password-actions">
            <el-button type="primary" :loading="changingPwd" @click="handleChangePassword">
              更新密码
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改邮箱弹窗 -->
    <el-dialog
      v-model="emailDialogVisible"
      title="修改邮箱"
      width="480px"
      :close-on-click-modal="false"
      @closed="resetEmailForm"
    >
      <p class="email-dialog-tip">当前邮箱：{{ profile.email || '未绑定' }}</p>
      <el-form
        ref="emailFormRef"
        :model="emailForm"
        :rules="emailRules"
        label-width="80px"
      >
        <el-form-item label="新邮箱" prop="newEmail">
          <el-input v-model="emailForm.newEmail" placeholder="请输入新邮箱" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="15">
              <el-input v-model="emailForm.code" placeholder="6 位数字验证码" />
            </el-col>
            <el-col :span="9">
              <el-button
                style="width: 100%"
                :disabled="emailCodeBtnDisabled"
                @click="handleSendEmailCode"
              >
                {{ emailCodeBtnText }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="emailDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="changingEmail" @click="handleChangeEmail">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import {
  getProfile,
  updateProfile,
  getUserAvatar,
  uploadAvatar,
  changePassword,
  sendEmailChangeCode,
  changeEmail,
  type UserProfile,
  type ProfileUpdateForm,
  type PasswordUpdateForm,
} from '@/api/user'
import { useUserStore } from '@/stores/user'
import profileBg from '@/assets/profile_background.jpg'

defineOptions({ name: 'UserCenter' })

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()
const avatarInput = ref<HTMLInputElement>()
const editing = ref(false)
const saving = ref(false)
const changingPwd = ref(false)
const avatarUrl = ref('https://img95.699pic.com/element/40112/2503.png_300.png')

const profile = ref<Partial<UserProfile>>({})
const form = reactive<ProfileUpdateForm>({
  realName: '',
  phone: '',
  gender: '',
})

const rules: FormRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }],
}

const pwdForm = reactive<PasswordUpdateForm>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

// ====== 邮箱修改 ======
const emailDialogVisible = ref(false)
const emailFormRef = ref<FormInstance>()
const changingEmail = ref(false)
const emailForm = reactive({
  newEmail: '',
  code: '',
})
const emailRules: FormRules = {
  newEmail: [
    { required: true, message: '请输入新邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为 6 位数字', trigger: 'blur' },
  ],
}

const emailCodeBtnDisabled = ref(false)
const emailCodeBtnText = ref('获取验证码')
let emailCountdown = 60
let emailTimer: number | null = null

const clearEmailTimer = () => {
  if (emailTimer) {
    clearInterval(emailTimer)
    emailTimer = null
  }
}
onUnmounted(clearEmailTimer)

function openEmailDialog() {
  emailDialogVisible.value = true
}

function resetEmailForm() {
  emailForm.newEmail = ''
  emailForm.code = ''
  emailCodeBtnDisabled.value = false
  emailCodeBtnText.value = '获取验证码'
  clearEmailTimer()
  emailFormRef.value?.clearValidate()
}

async function handleSendEmailCode() {
  try {
    await emailFormRef.value?.validateField('newEmail')
  } catch {
    return
  }
  // 前端校验：新邮箱不能等于当前邮箱
  if (emailForm.newEmail === profile.value.email) {
    ElMessage.warning('新邮箱不能与当前邮箱相同')
    return
  }
  try {
    await sendEmailChangeCode({ newEmail: emailForm.newEmail })
    ElMessage.success('验证码已发送，5 分钟内有效')
    emailCodeBtnDisabled.value = true
    emailCountdown = 60
    emailCodeBtnText.value = `${emailCountdown}s 后重获`
    emailTimer = window.setInterval(() => {
      emailCountdown--
      if (emailCountdown <= 0) {
        clearEmailTimer()
        emailCodeBtnText.value = '获取验证码'
        emailCodeBtnDisabled.value = false
      } else {
        emailCodeBtnText.value = `${emailCountdown}s 后重获`
      }
    }, 1000)
  } catch {
    // 拦截器已弹出错误
  }
}

async function handleChangeEmail() {
  if (!emailFormRef.value) return
  try {
    await emailFormRef.value.validate()
  } catch {
    return
  }
  changingEmail.value = true
  try {
    await changeEmail({ newEmail: emailForm.newEmail, code: emailForm.code })
    ElMessage.success('邮箱修改成功，请重新登录')
    userStore.logout()
    router.replace('/login')
  } finally {
    changingEmail.value = false
  }
}

// ====== 资料加载 ======
async function loadProfile() {
  const [profileData, avatar] = await Promise.all([
    getProfile(),
    getUserAvatar().catch(() => avatarUrl.value),
  ])
  profile.value = profileData
  avatarUrl.value = avatar as string
  Object.assign(form, {
    realName: profileData.realName || '',
    phone: profileData.phone || '',
    gender: profileData.gender || '',
  })
}

function cancelEdit() {
  editing.value = false
  Object.assign(form, {
    realName: profile.value.realName || '',
    phone: profile.value.phone || '',
    gender: profile.value.gender || '',
  })
}

async function handleSave() {
  if (!formRef.value) return
  await formRef.value.validate()
  saving.value = true
  try {
    await updateProfile(form)
    userStore.realName = form.realName
    sessionStorage.setItem('realName', form.realName)
    await loadProfile()
    editing.value = false
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

function triggerAvatarUpload() {
  avatarInput.value?.click()
}

async function handleAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (!['image/jpeg', 'image/png'].includes(file.type)) {
    ElMessage.error('请上传 JPG 或 PNG 格式图片')
    return
  }
  if (file.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB')
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  avatarUrl.value = await uploadAvatar(formData)
  ElMessage.success('头像更新成功')
}

async function handleChangePassword() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate()
  changingPwd.value = true
  try {
    await changePassword(pwdForm)
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.replace('/login')
  } finally {
    changingPwd.value = false
  }
}

onMounted(() => {
  loadProfile().catch(() => ElMessage.error('加载资料失败'))
})
</script>

<style scoped>
.user-center {
  max-width: 1100px;
  margin: 0 auto;
}

.profile-card {
  overflow: hidden;
}

.profile-bg {
  height: 160px;
  margin: -20px -20px 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.profile-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: -40px;
  padding-bottom: 20px;
}

.profile-body :deep(.el-avatar) {
  border: 3px solid var(--default-box-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.profile-body h3 {
  margin: 12px 0 4px;
  font-size: 18px;
}

.role {
  color: var(--ui-gray-500);
  font-size: 14px;
  margin-bottom: 16px;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0 0 20px;
  width: 100%;
}

.info-list li {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--default-border);
  font-size: 14px;
}

.info-list span {
  color: var(--ui-gray-500);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 账号安全 & 密码卡片间距 */
.section-card {
  margin-top: 20px;
}

.card-desc {
  margin: 0 0 16px;
  font-size: 13px;
  color: var(--ui-gray-500);
  line-height: 1.6;
}

/* 账号安全列表项 */
.security-items {
  border-top: 1px solid var(--default-border);
}

.security-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--default-border);
  font-size: 14px;
}

.security-label {
  width: 80px;
  flex-shrink: 0;
  color: var(--ui-gray-500);
}

.security-value {
  flex: 1;
  color: var(--ui-gray-800);
}

/* 密码卡片 */
.password-card-header h4 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: var(--ui-gray-800);
}

.password-card-header p {
  margin: 0;
  font-size: 13px;
  color: var(--ui-gray-500);
  line-height: 1.5;
}

.security-tips {
  height: 100%;
  padding: 16px;
  border-radius: 8px;
  background: var(--ui-gray-100);
  border: 1px solid var(--default-border);
}

.security-tips h5 {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--ui-gray-800);
}

.security-tips ul {
  margin: 0;
  padding-left: 18px;
}

.security-tips li {
  font-size: 13px;
  color: var(--ui-gray-600);
  line-height: 1.7;
  margin-bottom: 8px;
}

.security-tips li:last-child {
  margin-bottom: 0;
}

.password-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
  padding-top: 16px;
  border-top: 1px solid var(--default-border);
}

/* 邮箱弹窗 */
.email-dialog-tip {
  margin: 0 0 16px;
  font-size: 13px;
  color: var(--ui-gray-500);
}

@media (max-width: 992px) {
  .security-tips {
    margin-top: 8px;
  }
}
</style>
