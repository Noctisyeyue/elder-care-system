<!--
  登录/注册/忘记密码 左侧品牌区域
  移植自 Art Design Pro (Daymychen/art-design-pro, MIT)
  源文件: src/components/core/views/login/LoginLeftView.vue
-->

<template>
  <div class="login-left-view">
    <!-- 左上角品牌名 -->
    <div class="lv-logo">
      <h1 class="lv-logo-title">东软颐养中心</h1>
    </div>

    <!-- 居中插画 -->
    <div class="lv-illustration">
      <img src="@/assets/login/login_icon.svg" alt="illustration" class="lv-illustration-img" />
    </div>

    <!-- 底部文字 -->
    <div class="lv-text">
      <h1>智慧养老 · 用心服务</h1>
      <p>让每一位长者都能享受高品质的晚年生活</p>
    </div>

    <!-- 几何装饰元素 -->
    <div class="geometric-decorations">
      <!-- 圆环 -->
      <div
        class="geo-element circle-outline animate-fade-in-up"
        style="animation-delay: 0s"
      ></div>

      <!-- 旋转方块 -->
      <div
        class="geo-element square-rotated animate-fade-in-left"
        style="animation-delay: 0s"
      ></div>

      <!-- 小圆点 -->
      <div
        class="geo-element circle-small animate-fade-in-up"
        style="animation-delay: 0.3s"
      ></div>

      <!-- 右下旋转方块 -->
      <div
        class="geo-element square-bottom-right animate-fade-in-right"
        style="animation-delay: 0s"
      ></div>

      <!-- 大背景泡泡 -->
      <div
        class="geo-element bg-bubble animate-scale-in"
        style="animation-delay: 0.5s"
      ></div>

      <!-- 太阳（右上角纯装饰） -->
      <div
        class="geo-element circle-top-right animate-fade-in-down"
        style="animation-delay: 0.5s"
      ></div>

      <!-- 装饰圆点 x3 -->
      <div
        class="geo-element dot dot-top-left animate-bounce-in"
        style="animation-delay: 0s"
      ></div>
      <div
        class="geo-element dot dot-top-right animate-bounce-in"
        style="animation-delay: 0s"
      ></div>
      <div
        class="geo-element dot dot-center-right animate-bounce-in"
        style="animation-delay: 0s"
      ></div>

      <!-- 渐变方块组 -->
      <div class="squares-group">
        <i
          class="geo-element square square-blue animate-fade-in-left-rotated-blue"
          style="animation-delay: 0.2s"
        ></i>
        <i
          class="geo-element square square-pink animate-fade-in-left-rotated-pink"
          style="animation-delay: 0.4s"
        ></i>
        <i
          class="geo-element square square-purple animate-fade-in-left-no-rotation"
          style="animation-delay: 0.6s"
        ></i>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'

defineOptions({ name: 'LoginLeftView' })

// 认证页面全屏无滚动：挂载时禁用 body/html 滚动，卸载时恢复
onMounted(() => {
  document.documentElement.style.overflow = 'hidden'
  document.body.style.overflow = 'hidden'
})
onUnmounted(() => {
  document.documentElement.style.overflow = ''
  document.body.style.overflow = ''
})
</script>

<style scoped>
/* ---- 左侧整体 ---- */
.login-left-view {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1;
  box-sizing: border-box;
  width: 65vw;
  height: 100%;
  padding: 15px;
  overflow: hidden;
  background-color: var(--el-color-primary-light-9);
}

/* ---- Logo 区域 ---- */
.lv-logo {
  position: relative;
  z-index: 100;
  display: flex;
  align-items: center;
}

.lv-logo-title {
  font-size: 20px;
  font-weight: 400;
}

/* ---- 居中插画 ---- */
.lv-illustration {
  position: absolute;
  inset: 0 0 10.5%;
  z-index: 10;
  width: 40%;
  margin: auto;
  animation: slideInLeft 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

.lv-illustration-img {
  width: 100%;
  height: 100%;
}

/* ---- 底部文字 ---- */
.lv-text {
  position: absolute;
  bottom: 80px;
  width: 100%;
  text-align: center;
  animation: slideInLeft 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

.lv-text h1 {
  font-size: 24px;
  font-weight: 400;
  color: #111827;
  margin: 0;
}

.lv-text p {
  margin-top: 10px;
  font-size: 14px;
  color: #4b5563;
}

/* ---- 1600px 断点 ---- */
@media (max-width: 1600px) {
  .login-left-view {
    width: 60vw;
  }

  .lv-text {
    bottom: 40px;
  }
}
</style>
