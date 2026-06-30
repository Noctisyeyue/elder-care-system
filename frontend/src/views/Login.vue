<template>
  <div class="login-container">
    <!-- 左侧图片及介绍区域 -->
    <div class="login-image">
      <div class="logo-bar">
        <img src="@/assets/login/logo.png" alt="logo" class="logo-img" />
      </div>
      <div class="center-content">
        <img src="@/assets/login/background.png" alt="background" class="bg-img" />
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-form-container">
      <el-card class="login-card" shadow="hover">
        <h2>东软颐养中心</h2>
        <el-tabs v-model="activeTab" stretch>
          <el-tab-pane label="管理员登录" name="admin">
            <LoginForm role="admin" />
          </el-tab-pane>
          <el-tab-pane label="健康管家登录" name="caregiver">
            <LoginForm role="caregiver" />
          </el-tab-pane>
        </el-tabs>
        <div class="register-link">
          护工注册请 <span @click="goRegister">点此注册</span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import LoginForm from '@/components/LoginForm.vue'
const activeTab = ref('admin')
const router = useRouter()
const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
}

.login-image {
  flex: 1;
  background: #fff;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  overflow: hidden;
}

.logo-bar {
  position: absolute;
  top: 18px;
  left: 18px;
  z-index: 2;
}

.logo-img {
  width: 200px;
  display: block;
}

.center-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.bg-img {
  width: 90%;
}

.login-form-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.login-card {
  width: 380px;
  border-radius: 10px;
  box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08);
  padding: 0px 24px 24px 24px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 24px;
  font-size: 1.8rem;
  font-weight: bold;
  color: #222;
}

.register-link {
  text-align: center;
  margin-top: 12px;
  font-size: 13px;
  color: #666;
}

.register-link span {
  color: #409eff;
  cursor: pointer;
}
</style>
