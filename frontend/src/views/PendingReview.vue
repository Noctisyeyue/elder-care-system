<template>
  <div class="pending-container">
    <el-card class="pending-card" shadow="hover">
      <el-icon class="pending-icon"><Clock /></el-icon>
      <h2>账号待审核</h2>
      <p class="pending-text">您的护工账号已提交，请等待超级管理员审核。</p>
      <p class="pending-hint">审核通过后，点击下方按钮或刷新页面，重新登录即可进入系统。</p>
      <el-button type="primary" :loading="checking" @click="checkStatus">刷新检测审核状态</el-button>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserStatus } from '@/api/user'
import { Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const checking = ref(false)

// 查询数据库最新状态：审核通过则登出并跳登录页重新登录（重签 JWT）
const checkStatus = async () => {
  checking.value = true
  try {
    const status = await getUserStatus()
    if (status === 1) {
      // 审核通过：旧 JWT 里 status 仍是 0，必须重新登录拿新 JWT
      ElMessage.success('审核已通过，请重新登录')
      userStore.logout()
      router.replace('/login')
    } else if (status === 2) {
      ElMessage.error('账号已被禁用，请联系管理员')
      userStore.logout()
      router.replace('/login')
    } else {
      ElMessage.info('仍在审核中，请耐心等待')
    }
  } finally {
    checking.value = false
  }
}

// 进入页面时自动检测一次（浏览器刷新也会触发 onMounted）
onMounted(() => {
  checkStatus()
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.pending-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f7fa;
}

.pending-card {
  width: 420px;
  border-radius: 10px;
  box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08);
  padding: 32px 24px;
  text-align: center;
}

.pending-icon {
  font-size: 56px;
  color: #e6a23c;
}

.pending-card h2 {
  margin: 16px 0 12px;
  font-size: 1.5rem;
  color: #222;
}

.pending-text {
  font-size: 15px;
  color: #444;
  margin-bottom: 8px;
}

.pending-hint {
  font-size: 13px;
  color: #999;
  margin-bottom: 24px;
}
</style>
