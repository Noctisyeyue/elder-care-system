<template>
  <div class="pending-container">
    <el-card class="pending-card" shadow="hover">
      <el-icon class="pending-icon"><Clock /></el-icon>
      <h2>账号待审核</h2>
      <p class="pending-text">您的护工账号已提交，请等待超级管理员审核。</p>
      <p class="pending-hint">审核通过后即可登录使用系统。</p>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Clock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

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
