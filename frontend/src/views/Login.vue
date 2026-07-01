<!--
  登录页面
  移植自 Art Design Pro (Daymychen/art-design-pro, MIT)
-->

<template>
  <div class="auth-page">
    <LoginLeftView />
    <div class="auth-right">
      <div class="auth-right-wrap">
        <div class="form">
          <h3 class="title">欢迎登录</h3>
          <p class="sub-title">东软颐养中心后台管理系统</p>
          <LoginForm />
          <div class="auth-footer-link">
            护工注册请
            <router-link to="/register" class="text-theme">点此注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginLeftView from '@/components/auth/LoginLeftView.vue'
import LoginForm from '@/components/LoginForm.vue'
</script>
