<template>
  <div v-if="isLocked" class="screen-lock">
    <div class="lock-panel">
      <el-avatar :size="64" :src="avatarUrl" />
      <p class="user-name">{{ userStore.realName }}</p>
      <el-form @submit.prevent="handleUnlock">
        <el-input
          v-model="password"
          type="password"
          placeholder="请输入锁屏密码"
          show-password
          @keyup.enter="handleUnlock"
        />
        <el-button type="primary" class="unlock-btn" @click="handleUnlock">解锁</el-button>
        <el-button text @click="toLogin">返回登录</el-button>
      </el-form>
    </div>
  </div>

  <el-dialog
    v-model="setPasswordVisible"
    title="设置锁屏密码"
    width="400px"
    :close-on-click-modal="false"
  >
    <el-input
      v-model="newPassword"
      type="password"
      placeholder="请输入锁屏密码（至少4位）"
      show-password
    />
    <template #footer>
      <el-button @click="setPasswordVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSetPassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CryptoJS from 'crypto-js'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserAvatar } from '@/api/user'
import { mittBus } from '@/utils/mitt'

defineOptions({ name: 'ScreenLock' })

const LOCK_KEY = 'elder-care-lock-pwd'
const SECRET = 'elder-care-lock-secret'

const router = useRouter()
const userStore = useUserStore()
const isLocked = ref(false)
const password = ref('')
const newPassword = ref('')
const setPasswordVisible = ref(false)
const avatarUrl = ref('https://img95.699pic.com/element/40112/2503.png_300.png')

function encrypt(pwd: string) {
  return CryptoJS.AES.encrypt(pwd, SECRET).toString()
}

function decrypt(cipher: string) {
  try {
    const bytes = CryptoJS.AES.decrypt(cipher, SECRET)
    return bytes.toString(CryptoJS.enc.Utf8)
  } catch {
    return ''
  }
}

function getStoredPassword() {
  return localStorage.getItem(`${LOCK_KEY}-${userStore.userId}`) || ''
}

function openLockScreen() {
  const stored = getStoredPassword()
  if (!stored) {
    setPasswordVisible.value = true
    return
  }
  isLocked.value = true
  password.value = ''
}

function handleSetPassword() {
  if (newPassword.value.length < 4) {
    ElMessage.warning('密码至少4位')
    return
  }
  localStorage.setItem(`${LOCK_KEY}-${userStore.userId}`, encrypt(newPassword.value))
  setPasswordVisible.value = false
  isLocked.value = true
  newPassword.value = ''
  ElMessage.success('锁屏密码已设置')
}

function handleUnlock() {
  const stored = decrypt(getStoredPassword())
  if (password.value === stored) {
    isLocked.value = false
    password.value = ''
  } else {
    ElMessage.error('密码错误')
  }
}

function toLogin() {
  isLocked.value = false
  userStore.logout()
  router.replace('/login')
}

onMounted(async () => {
  mittBus.on('openLockScreen', openLockScreen)
  try {
    avatarUrl.value = await getUserAvatar()
  } catch {
    /* default */
  }
})

onUnmounted(() => {
  mittBus.off('openLockScreen', openLockScreen)
})
</script>

<style scoped>
.screen-lock {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.lock-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  background: var(--default-box-color);
  border-radius: 12px;
  width: 320px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.user-name {
  margin: 16px 0;
  font-size: 16px;
  font-weight: 500;
}

.unlock-btn {
  width: 100%;
  margin-top: 12px;
}
</style>
