<template>
  <el-popover
    placement="bottom-end"
    :width="260"
    trigger="hover"
    :show-arrow="false"
    :offset="10"
    :hide-after="0"
    popper-class="user-menu-popover"
  >
    <template #reference>
      <div class="user-menu-trigger">
        <el-avatar :size="36" :src="avatarUrl" />
        <el-icon class="trigger-arrow max-md:!hidden"><ArrowDown /></el-icon>
      </div>
    </template>

    <div class="user-menu-panel">
      <div class="user-info-card">
        <el-avatar :size="40" :src="avatarUrl" />
        <div class="user-info-meta">
          <div class="user-info-name">{{ userStore.realName || '未登录' }}</div>
          <div class="user-info-email">{{ displayEmail }}</div>
        </div>
      </div>

      <ul class="menu-list">
        <li class="menu-item" @click="goCenter">
          <SvgIcon icon="ri:user-3-line" :size="16" />
          <span>个人中心</span>
        </li>
        <li class="menu-item" @click="lockScreen">
          <SvgIcon icon="ri:lock-line" :size="16" />
          <span>锁定屏幕</span>
        </li>
      </ul>

      <div class="logout-btn" @click="logout">退出登录</div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getUserAvatar, getUserEmail } from '@/api/user'
import { mittBus } from '@/utils/mitt'
import SvgIcon from '@/components/base/svg-icon/index.vue'

defineOptions({ name: 'UserMenu' })

const router = useRouter()
const userStore = useUserStore()
const avatarUrl = ref('https://img95.699pic.com/element/40112/2503.png_300.png')
const email = ref('')

const displayEmail = computed(
  () => email.value || userStore.email || '未绑定邮箱',
)

function goCenter() {
  router.push('/user/center')
}

function lockScreen() {
  mittBus.emit('openLockScreen')
}

function logout() {
  userStore.logout()
  router.replace('/login')
}

onMounted(async () => {
  try {
    const [avatar, userEmail] = await Promise.all([
      getUserAvatar(),
      getUserEmail(),
    ])
    avatarUrl.value = avatar
    email.value = userEmail
  } catch {
    email.value = userStore.email
  }
})
</script>

<style scoped>
.user-menu-trigger {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
}

.user-menu-trigger:hover {
  background: var(--ui-hover-color);
}

.trigger-arrow {
  font-size: 12px;
  color: var(--ui-gray-500);
}

.user-menu-panel {
  padding: 4px 0;
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 4px 12px;
}

.user-info-meta {
  flex: 1;
  min-width: 0;
}

.user-info-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--ui-gray-800);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-info-email {
  margin-top: 4px;
  font-size: 12px;
  color: var(--ui-gray-500);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-list {
  list-style: none;
  margin: 0;
  padding: 12px 0 8px;
  border-top: 1px solid var(--default-border);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 4px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--ui-gray-700);
  cursor: pointer;
  transition: background-color 0.2s;
}

.menu-item:hover {
  background: var(--ui-hover-color);
}

.logout-btn {
  margin-top: 8px;
  padding: 8px 0;
  text-align: center;
  font-size: 12px;
  color: var(--ui-gray-600);
  border: 1px solid var(--ui-gray-400);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  color: #f56c6c;
  border-color: #f56c6c;
  background: rgba(245, 108, 108, 0.06);
}
</style>

<style>
.user-menu-popover.el-popover {
  padding: 12px 16px !important;
}
</style>
