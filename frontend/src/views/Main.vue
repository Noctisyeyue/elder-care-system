<template>
  <div class="common-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="asideWidth" class="aside">
        <div class="logo-container">
          <img src="@/assets/logo.svg" alt="logo" class="logo-image" />
          <span v-if="!isCollapse" class="logo-text">Neusoft Care</span>
        </div>

        <el-menu
          class="menu"
          :default-openeds="['默认展开选项，后期会更新为主页自动展开']"
          :collapse="isCollapse"
          :default-active="activePath"
        >
          <!-- 首页菜单项 -->
          <el-menu-item
            index="首页"
            @click="setActiveMenu('home/admin')"
            v-if="userStore.role === 'admin'"
          >
            <el-icon style="margin-left: -1.5pt"><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="首页" @click="setActiveMenu('home/caregiver')" v-else>
            <el-icon style="margin-left: -1.5pt"><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="床位管理" v-if="userStore.role === 'admin'">
            <!-- 子菜单对应父级路由路径 -->
            <template #title>
              <i class="iconfont-sys iconsys-danrenchuang"></i>
              <span>床位管理</span>
            </template>
            <el-menu-item index="床位管理 / 床位示意图" @click="setActiveMenu('bed/map')"
              >床位示意图</el-menu-item
            >
            <el-menu-item index="床位管理 / 床位管理" @click="setActiveMenu('bed/list')">
              床位管理
            </el-menu-item>
          </el-sub-menu>
          <!-- 管理员的的客户管理 -->
          <el-sub-menu index="客户管理" v-if="userStore.role === 'admin'">
            <template #title>
              <el-icon><User /></el-icon>
              <span>客户管理</span>
            </template>
            <el-menu-item index="客户管理 / 入住登记" @click="setActiveMenu('customer/checkIn')"
              >入住登记</el-menu-item
            >
            <el-menu-item index="客户管理 / 外出审核" @click="setActiveMenu('customer/out')"
              >外出审核</el-menu-item
            >
            <el-menu-item index="客户管理 / 退住审核" @click="setActiveMenu('customer/checkOut')"
              >退住审核</el-menu-item
            >
          </el-sub-menu>
          <!-- 健康管家的客户管理 -->
          <el-sub-menu index="客户管理" v-if="userStore.role === 'caregiver'">
            <template #title>
              <el-icon><User /></el-icon>
              <span>客户管理</span>
            </template>
            <el-menu-item index="客户管理 / 外出申请" @click="setActiveMenu('customer/outApply')"
              >外出申请</el-menu-item
            >
            <el-menu-item
              index="客户管理 / 退住申请"
              @click="setActiveMenu('customer/checkOutApply')"
              >退住申请</el-menu-item
            >
          </el-sub-menu>
          <el-sub-menu index="护理管理" v-if="userStore.role === 'admin'">
            <template #title>
              <el-icon><FirstAidKit /></el-icon>
              <span>护理管理</span>
            </template>
            <el-menu-item index="护理管理 / 护理级别" @click="setActiveMenu('nursing/level')"
              >护理级别</el-menu-item
            >
            <el-menu-item index="护理管理 / 护理项目" @click="setActiveMenu('nursing/item')"
              >护理项目</el-menu-item
            >
            <el-menu-item index="护理管理 / 客户护理设置" @click="setActiveMenu('nursing/customer')"
              >客户护理设置</el-menu-item
            >
            <el-menu-item index="护理管理 / 护理记录" @click="setActiveMenu('nursing/record')"
              >护理记录</el-menu-item
            >
          </el-sub-menu>
          <!-- 管理员端的健康管家 -->
          <el-sub-menu index="健康管家" v-if="userStore.role === 'admin'">
            <template #title>
              <el-icon><Service /></el-icon>
              <span>健康管家</span>
            </template>
            <el-menu-item
              index="健康管家 / 设置服务对象"
              @click="setActiveMenu('service/caregiver')"
              >设置服务对象</el-menu-item
            >
            <el-menu-item index="健康管家 / 服务关注" @click="setActiveMenu('service/concern')"
              >服务关注</el-menu-item
            >
          </el-sub-menu>
          <!-- 护工端的健康管家 -->
          <el-sub-menu index="健康管家" v-if="userStore.role === 'caregiver'">
            <template #title>
              <el-icon><Service /></el-icon>
              <span>健康管家</span>
            </template>
            <el-menu-item index="健康管家 / 日常护理" @click="setActiveMenu('service/dailyCare')"
              >日常护理</el-menu-item
            >
            <el-menu-item index="健康管家 / 护理记录" @click="setActiveMenu('service/records')"
              >护理记录</el-menu-item
            >
          </el-sub-menu>
          <el-sub-menu index="膳食管理" v-if="userStore.role === 'admin'">
            <template #title>
              <el-icon><ForkSpoon /></el-icon>
              <span>膳食管理</span>
            </template>
            <el-menu-item index="膳食管理 / 菜品配置" @click="setActiveMenu('diet/status')"
              >菜品配置</el-menu-item
            >
            <el-menu-item index="膳食管理 / 套餐配置" @click="setActiveMenu('diet/package')"
              >套餐配置</el-menu-item
            >
            <el-menu-item index="膳食管理 / 膳食日历" @click="setActiveMenu('diet/calendar')"
              >膳食日历</el-menu-item
            >
            <el-menu-item index="膳食管理 / 膳食配置" @click="setActiveMenu('diet/config')"
              >膳食配置</el-menu-item
            >
          </el-sub-menu>

          <el-sub-menu index="系统管理" v-if="userStore.role === 'admin'">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="系统管理 / 基础数据维护" @click="setActiveMenu('user/list')"
              >基础数据维护</el-menu-item
            >
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <!-- 主体部分 -->
      <el-container class="main-content-container">
        <el-header class="header">
          <div class="header-top-row">
            <div class="header-top-left">
              <el-icon :size="20" class="menu-collapse-btn" @click="toggleAside">
                <Expand v-if="isCollapse" />
                <Fold v-else />
              </el-icon>
              <span class="current-path">{{ currentPathTitle }}</span>
            </div>
            <div class="header-top-right">
              <!-- 通知图标 -->
              <el-popover
                placement="bottom"
                width="260"
                trigger="hover"
                popper-class="notification-popover"
                @show="fetchNotification"
                v-if="userStore.role === 'admin'"
              >
                <template #reference>
                  <div class="notification-icon">
                    <el-icon :size="20" style="margin-right: 15px;">
                      <Bell />
                    </el-icon>
                    <span v-if="notificationData.checkOutCount + notificationData.outApplyCount > 0" class="notification-dot"></span>
                  </div>
                </template>
                <div v-if="notificationLoading" style="text-align:center;padding:20px 0;">
                  <el-icon><Loading /></el-icon>
                </div>
                <div v-else>
                  <div class="notification-title">通知</div>
                  <div class="notification-item">
                    <el-icon class="notification-item-icon"><View /></el-icon>
                    待处理退住申请：{{ notificationData.checkOutApplyCount }} 个
                  </div>
                  <div class="notification-item">
                    <el-icon class="notification-item-icon"><Operation /></el-icon>
                    待处理外出申请：{{ notificationData.outingApplyCount }} 个
                  </div>
                  <div class="notification-item">
                    <el-icon class="notification-item-icon"><ForkSpoon /></el-icon>
                    今日膳食配置：<span :style="{color: notificationData.dietConfigured ? '#52c41a' : '#f56c6c'}">
                      {{ notificationData.dietConfigured ? '已配置' : '未配置' }}
                    </span>
                  </div>
                </div>
              </el-popover>
              <!-- 全屏切换图标 -->
              <div class="fullscreen-toggle-icon" @click="toggleFullscreen">
                <el-icon :size="20" style="margin-right: 15px;">
                  <CloseBold v-if="isFullscreen" />
                  <FullScreen v-else />
                </el-icon>
              </div>
              <!-- AI对话图标加绿色小点 -->
              <div class="ai-chat-icon-wrapper" @click="isChatDrawerVisible = true" style="margin-right: 15px;">
                <el-icon :size="20" class="action-icon">
                  <ChatDotRound />
                </el-icon>
                <span class="ai-chat-dot"></span>
              </div>
              <el-popover
                placement="bottom-start"
                trigger="hover"
                width="260"
                popper-class="user-info-popover"
              >
                <el-image-viewer
                  v-if="imagePreviewVisible"
                  :initial-index="0"
                  :url-list="[selectedImageUrl]"
                  @close="imagePreviewVisible = false"
                />
                <template #reference>
                  <el-avatar :size="40" :src="avatarUrl" style="cursor: pointer" />
                </template>
                <div class="user-info-card">
                  <el-avatar :size="48" :src="avatarUrl" @click="previewImage(avatarUrl)" />
                  <div class="user-info-meta">
                    <div class="user-info-name">{{ userStore.userName || '未登录' }}</div>
                    <div class="user-info-email">{{ email || '未绑定邮箱' }}</div>
                  </div>
                </div>
                <div class="user-info-divider"></div>
                <div class="user-info-logout-btn">
                  <el-button
                    type="primary"
                    size="small"
                    @click="openAvatarDialog"
                    plain
                    style="margin-right: 12px"
                    >修改头像</el-button
                  >
                  <el-button type="danger" size="small" @click="logout" plain>退出登录</el-button>
                  <input
                    ref="avatarInput"
                    type="file"
                    accept="image/*"
                    style="display: none"
                    @change="handleFileChange"
                  />
                </div>
              </el-popover>
            </div>
          </div>
          <div class="header-bottom-row">
            <div class="header-bottom-left">
              <div class="tab-cards-container" ref="tabsRef">
                <div class="tab-cards-scroll" ref="scrollRef">
                  <!-- 这里 scrollRef 已经正确绑定 -->
                  <div
                    v-for="tab in tabs"
                    :key="tab.name"
                    :class="['tab-card', { 'is-active': activeTab === tab.name }]"
                    @click="onTabClick(tab)"
                  >
                    <span class="tab-title">{{ tab.title }}</span>
                    <el-icon class="is-icon-close" @click.stop="removeTab(tab.name)"
                      ><Close
                    /></el-icon>
                  </div>
                </div>
              </div>
            </div>
            <div class="header-bottom-right">
              <el-dropdown @command="handleDropdownCommand">
                <span class="el-dropdown-link">
                  <el-icon><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="refresh">刷新页面</el-dropdown-item>
                    <el-dropdown-item command="closeAllTabs">关闭所有页面</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
    <!-- Chatbot Drawer -->
    <el-drawer
      v-model="isChatDrawerVisible"
      title="Chat Bot"
      direction="rtl"
      :size="600"
      custom-class="chat-drawer"
    >
      <div class="chat-container">
        <div class="chat-body">
          <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
            <el-avatar :size="32" :src="msg.avatar" class="chat-avatar" />
            <div class="message-content">
              <div class="message-bubble" v-html="renderContent(msg.content)"></div>
            </div>
          </div>
        </div>
        <div class="chat-footer">
          <el-input
            v-model="newMessage"
            placeholder="输入消息..."
            resize="none"
            :disabled="sending"
            @keyup.enter="sendMessage"
          />
          <el-button
            type="primary"
            :loading="sending"
            @click="sendMessage"
            :disabled="sending || !newMessage.trim()"
            >发送</el-button
          >
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, watch, computed, nextTick, onMounted } from 'vue'
import {
  ArrowDown,
  Setting,
  User,
  FirstAidKit,
  Service,
  Fold,
  Expand,
  Close,
  ForkSpoon,
  View,
  Operation,
  ChatDotRound,
  HomeFilled,
  Sunny,
  Moon,
  FullScreen,
  CloseBold,
  Bell,
  Loading,
} from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserAvatar, getUserEmail, uploadAvatar } from '@/api/user'
import { renderContent } from '@/utils/contentRenderer'
import { ElMessage, ElImageViewer } from 'element-plus'
import dayjs from 'dayjs'
// 图片预览相关状态
const imagePreviewVisible = ref(false)
const selectedImageUrl = ref('')
// 预览图片方法
const previewImage = (url) => {
  selectedImageUrl.value = url
  imagePreviewVisible.value = true
}
const userStore = useUserStore()

const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const asideWidth = computed(() => (isCollapse.value ? '64px' : '230px'))

const tabs = ref([])
const activeTab = ref(route.path)
const activeMenu = ref(route.path)

// 聊天相关
const avatarUrl = ref('https://img95.699pic.com/element/40112/2503.png_300.png')
const email = ref('')
const botAvatarUrl = ref(new URL('@/assets/AIBot.svg', import.meta.url).href)
const isChatDrawerVisible = ref(false)
const newMessage = ref('')
const sending = ref(false)
const messages = ref([
  {
    role: 'bot',
    content: '你好！我是你的AI助手，有什么可以帮你的吗？',
    avatar: botAvatarUrl.value,
  },
])

const avatarDialogVisible = ref(false)
const avatarInput = ref(null)
const openAvatarDialog = () => {
  if (avatarInput.value) {
    avatarInput.value.click()
  }
}

// 全屏切换相关
const isFullscreen = ref(false)
function toggleFullscreen() {
  if (!isFullscreen.value) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}
document.addEventListener('fullscreenchange', () => {
  isFullscreen.value = !!document.fullscreenElement
})

// tab切换
function onTabClick(tab) {
  if (tab.name !== activeTab.value) {
    activeTab.value = tab.name
    router.push(tab.name)
  }
}

// tab关闭
function removeTab(targetName) {
  const idx = tabs.value.findIndex((tab) => tab.name === targetName)
  if (idx !== -1) {
    tabs.value.splice(idx, 1)
    // 如果关闭的是当前tab，切换到最后一个tab
    if (activeTab.value === targetName) {
      const nextTab = tabs.value[idx] || tabs.value[idx - 1]
      if (nextTab) {
        router.push(nextTab.name)
        activeTab.value = nextTab.name
      }
    }
    // 如果关闭的是最后一个tab，则重定向到首页
    if (tabs.value.length === 0) {
      if (userStore.isAdmin) {
        router.push('/home/admin')
        activeTab.value = '/home/admin'
        tabs.value.push({
          title: tabMap['/home/admin'] || '未知',
          name: '/home/admin',
        })
      } else {
        router.push('/home/caregiver')
        activeTab.value = '/home/caregiver'
        tabs.value.push({
          title: tabMap['/home/caregiver'] || '未知',
          name: '/home/caregiver',
        })
      }
    }
  }
}

// 这里可以根据路由path映射标签页标题
const tabMap = {
  // 首页
  '/home/admin': '首页',
  '/home/caregiver': '首页',
  // 床位管理
  '/bed/map': '床位示意图',
  '/bed/list': '床位管理',
  // 客户管理(管理员)
  '/customer/checkIn': '入住登记',
  '/customer/checkOut': '退住审核',
  '/customer/out': '外出审核',
  // 客户管理(健康管家)
  '/customer/outApply': '外出申请',
  '/customer/outingList': '外出申请列表',
  '/customer/checkOutApply': '退住申请',
  '/customer/checkOutList': '退住申请列表',
  // 护理管理
  '/nursing/level': '护理级别',
  '/nursing/itemSetting': '护理项目配置',
  '/nursing/item': '护理项目',
  '/nursing/customer': '客户护理设置',
  '/nursing/record': '护理记录',
  // 健康管家(管理员)
  '/service/caregiver': '设置服务对象',
  '/service/config': '设置服务客户',
  '/service/concern': '服务关注',
  '/service/purchased-nursing-items': '已购护理',
  '/service/nursingItem': '购买护理项目',
  // 健康管家(健康管家)
  '/service/dailyCare': '日常护理',
  '/service/items': '客户护理',
  '/service/records': '护理记录',
  // 膳食管理
  '/diet/calendar': '膳食日历',
  '/diet/config': '膳食配置',
  '/diet/package': '套餐配置',
  '/diet/status': '菜品配置',
  // 系统管理
  '/user/list': '基础数据维护',
}
// 这里可以根据路由path映射顶边栏路径
const titleMap = {
  // 首页
  '/home/admin': '首页',
  '/home/caregiver': '首页',
  // 床位管理
  '/bed/map': '床位管理 / 床位示意图',
  '/bed/list': '床位管理 / 床位管理',
  // 客户管理(管理员)
  '/customer/checkIn': '客户管理 / 入住登记',
  '/customer/checkOut': '客户管理 / 退住审核',
  '/customer/out': '客户管理 / 外出审核',
  // 客户管理(健康管家)
  '/customer/outApply': '客户管理 / 外出申请',
  '/customer/outingList': '客户管理 / 外出申请 / 外出申请列表',
  '/customer/checkOutApply': '客户管理 / 退住申请',
  '/customer/checkOutList': '客户管理 / 退住申请 / 退住申请列表',
  // 护理管理
  '/nursing/level': '护理管理 / 护理级别',
  '/nursing/itemSetting': '护理管理 / 护理级别 / 护理项目配置',
  '/nursing/item': '护理管理 / 护理项目',
  '/nursing/customer': '护理管理 / 客户护理设置',
  '/nursing/record': '护理管理 / 护理记录',
  // 健康管家(管理员)
  '/service/caregiver': '健康管家 / 设置服务对象',
  '/service/config': '健康管家 / 设置服务对象 / 设置服务客户',
  '/service/concern': '健康管家 / 服务关注',
  '/service/purchased-nursing-items': '健康管家 / 服务关注 / 已购护理',
  '/service/nursingItem': '健康管家 / 服务关注 / 已购护理 / 购买护理项目',
  // 健康管家(健康管家)
  '/service/dailyCare': '健康管家 / 日常护理',
  '/service/items': '健康管家 / 日常护理 / 客户护理',
  '/service/records': '健康管家 / 护理记录',
  // 膳食管理
  '/diet/calendar': '膳食管理 / 膳食日历',
  '/diet/config': '膳食管理 / 膳食配置',
  '/diet/package': '膳食管理 / 套餐配置',
  '/diet/status': '膳食管理 / 菜品配置',
  // 系统管理
  '/user/list': '系统管理 / 基础数据维护',
}
function setActiveMenu(menuIndex) {
  const fullPath = '/' + menuIndex
  if (!tabs.value.find((tab) => tab.name === fullPath)) {
    tabs.value.push({ title: tabMap[fullPath] || '未知', name: fullPath })
  }
  router.push(fullPath)
  activeTab.value = fullPath
}

const currentPathTitle = computed(() => {
  const newPath = route.path
  var mapPath
  const regex = /\d/
  if (regex.test(newPath)) {
    const parts = newPath.split('/')
    mapPath = `/${parts[1]}/${parts[2]}`
  } else {
    mapPath = newPath
  }
  return titleMap[mapPath] || ''
})

const activePath = computed(() => {
  const parts = currentPathTitle.value.split('/')
  const result = parts[0] + '/' + parts[1]
  if (parts.length == 1) {
    return parts[0]
  } else if (parts.length == 2) {
    return result
  } else if (parts.length > 2) {
    return result.slice(0, result.length - 1)
  }
})

// 路由变化时，自动激活tab并添加到tabs
watch(
  () => route.path,
  (newPath) => {
    const regex = /\d/
    var mapPath
    if (regex.test(newPath)) {
      const parts = newPath.split('/')
      mapPath = `/${parts[1]}/${parts[2]}`
    } else if (newPath !== '/') {
      mapPath = newPath
    } else if (newPath === '/') {
      if (userStore.isAdmin) {
        mapPath = '/home/admin'
        router.push(mapPath)
      } else {
        mapPath = '/home/caregiver'
        router.push(mapPath)
      }
      return
    }
    activeTab.value = newPath
    activeMenu.value = newPath
    // 判断是否已存在
    if (!tabs.value.find((tab) => tab.name === newPath)) {
      tabs.value.push({
        title: tabMap[mapPath] || '未知',
        name: newPath,
      })
    }
  },
  { immediate: true },
)

async function sendMessage() {
  const messageContent = newMessage.value.trim()
  if (!messageContent) return
  const userMsg = {
    role: 'user',
    content: newMessage.value,
    avatar: avatarUrl.value,
  }
  messages.value.push(userMsg)
  sending.value = true

  // 插入一个空的bot消息用于流式追加
  const botMsg = {
    role: 'bot',
    content: '',
    avatar: botAvatarUrl.value,
  }
  messages.value.push(botMsg)

  try {
    await streamPost(
      '/chatStream',
      messageContent, // 👈 直接发送字符串
      (chunk) => {
        console.log('收到chunk:', chunk)
        botMsg.content += chunk
        // 强制响应式更新
        messages.value = [...messages.value]
        // 强制立即更新DOM并滚动
        nextTick(() => {
          scrollToBottom()
          // 强制浏览器重绘
          document.body.offsetHeight
        })
      },
      () => {
        console.log('聊天响应完成')
      },
      (error) => {
        console.error('聊天请求失败:', error)
        botMsg.content += `\n[出错了，请稍后重试: ${error.message}]`
        messages.value = [...messages.value]
        nextTick(() => {
          scrollToBottom()
        })
      },
    )
  } catch (e) {
    console.error('聊天请求失败:', e)
    botMsg.content += '\n[出错了，请稍后重试]'
    messages.value = [...messages.value]
    nextTick(() => {
      scrollToBottom()
    })
  } finally {
    sending.value = false
    newMessage.value = ''
    await nextTick()
    scrollToBottom()
  }
}

function scrollToBottom() {
  const chatBody = document.querySelector('.chat-body')
  if (chatBody) {
    chatBody.scrollTop = chatBody.scrollHeight
  }
}

function logout() {
  userStore.logout() // 清除token和用户信息
  router.replace('/login')
}

function handleDropdownCommand(command) {
  if (command === 'refresh') {
    window.location.reload()
  } else if (command === 'closeAllTabs') {
    tabs.value = []
    if (router.currentRoute.value.fullPath === '/home/admin') {
      activeTab.value = '/home/admin'
      activeMenu.value = '/home/admin'
      // 判断是否已存在
      if (!tabs.value.find((tab) => tab.name === '/home/admin')) {
        tabs.value.push({
          title: tabMap['/home/admin'] || '未知',
          name: '/home/admin',
        })
      }
    } else if (router.currentRoute.value.fullPath === '/home/caregiver') {
      activeTab.value = '/home/caregiver'
      activeMenu.value = '/home/caregiver'
      // 判断是否已存在
      if (!tabs.value.find((tab) => tab.name === '/home/caregiver')) {
        tabs.value.push({
          title: tabMap['/home/caregiver'] || '未知',
          name: '/home/caregiver',
        })
      }
    }
    // 可选：跳转到首页或登录页
    if (userStore.role === 'admin') {
      activeTab.value = '/home/admin'
      router.push('/home/admin')
    } else {
      activeTab.value = '/home/caregiver'
      router.push('/home/caregiver')
    }
  }
}

function toggleAside() {
  isCollapse.value = !isCollapse.value
}

// 滚动相关状态
const tabsRef = ref(null)
const scrollRef = ref(null)

// 滚动事件处理
const listenerScroll = () => {
  const container = tabsRef.value
  const scrollContent = scrollRef.value

  if (!container || !scrollContent) return

  container.addEventListener(
    'wheel',
    (event) => {
      event.preventDefault()
      const delta = Math.abs(event.deltaX) > Math.abs(event.deltaY) ? event.deltaX : event.deltaY
      scrollContent.scrollLeft += delta
    },
    { passive: false },
  )
}

// 处理文件选择
const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    // 执行上传前验证
    const isValid = beforeAvatarUpload(file)
    if (isValid) {
      // 执行上传逻辑
      const formData = new FormData()
      formData.append('file', file) // 假设后端接口接收的字段名为 'file'

      uploadAvatar(formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }).then((response) => {
        // 上传成功
        handleAvatarSuccess(response)
      })
    }
  }
}

// 上传之前检查
const beforeAvatarUpload = (rawFile) => {
  if (!['image/jpeg', 'image/png'].includes(rawFile.type)) {
    ElMessage.error('请上传JPG或PNG格式的图片')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

// 上传成功处理
const handleAvatarSuccess = (response) => {
  avatarUrl.value = response // 更新头像 URL
  ElMessage.success('头像更新成功')
}

// 通知相关状态
const notificationLoading = ref(false)
const notificationData = ref({
  checkOutCount: 0,
  outApplyCount: 0,
  dietConfigured: false,
})

const fetchNotification = async () => {
  notificationLoading.value = true
  try {
    const today = dayjs().format('YYYY-MM-DD')
    const res = await get(`/common/notification?date=${today}`)
    notificationData.value = res
  } catch (e) {
    ElMessage.error('获取通知失败')
  } finally {
    notificationLoading.value = false
  }
}

onMounted(() => {
  if (tabsRef.value && scrollRef.value) {
    listenerScroll() // 绑定事件
  }
  // 向后端请求头像
  getUserAvatar().then((response) => {
    avatarUrl.value = response
  })
  // 向后端请求邮箱
  getUserEmail().then((response) => {
    email.value = response
  })
})
</script>

<style scoped>
@import '../assets/icons/iconfont.css';
.aside {
  background: #fff;
  border-right: 1px solid #eee;
  height: 100vh;
  overflow: hidden;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  border-bottom: 1px solid #eee;
  gap: 15px;
  padding: 0 10px;
}

.logo-image {
  width: 24px;
  height: 24px;
}

.logo-text {
  font-size: 18px;
  color: #333;
  font-family: Arial, sans-serif;
  white-space: nowrap;
  overflow: hidden;
}

.menu {
  border-right: none;
  transition: height 0.3s ease;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

/* Adjust menu in collapsed state */
.el-menu--collapse {
  width: 64px;
}
.el-menu--collapse .el-sub-menu__title span,
.el-menu--collapse .el-menu-item span {
  height: 0;
  width: 0;
  overflow: hidden;
  visibility: hidden;
  display: inline-block;
}
/* Ensure icons are centered when collapsed */
.el-menu--collapse .el-sub-menu__title .el-icon,
.el-menu--collapse .el-menu-item .el-icon {
  margin-left: 10px;
  margin-right: 0px;
}
.el-menu--collapse .el-sub-menu__title .iconfont-sys,
.el-menu--collapse .el-menu-item .iconfont-sys {
  margin-left: 4px;
}

.header {
  display: flex;
  flex-direction: column;
  background: #fafbfc;
  height: auto;
  padding: 10px 0 0 0 !important;
  margin: 0 !important;
  border-bottom: 1px solid #e4e7ed;
}

.el-header {
  border-bottom: 1px solid #e4e7ed;
}

.header-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
  padding-left: 20px;
  padding-right: 10px;
}

.header-top-row .el-icon {
  color: rgb(120, 130, 157);
}

.header-top-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.menu-collapse-btn {
  cursor: pointer;
  color: rgb(120, 130, 157);
  font-size: 20px;
}

.current-path {
  font-size: 14px;
  color: rgb(120, 130, 157);
}

.header-top-right {
  display: flex;
  align-items: center;
  gap: 0px;
  justify-content: flex-end;
}

.header-bottom-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 50px;
  padding-left: 20px;
  padding-right: 10px;
}

.header-bottom-left {
  flex: 1;
  width: calc(100% - 60px);
}

.tab-cards-container {
  flex: 1;
  overflow: hidden;
}

.tab-cards-scroll {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  width: 100%;
  overflow-x: auto;
  scroll-behavior: smooth;
}

.tab-cards-scroll::-webkit-scrollbar {
  display: none;
}

.tab-card {
  height: 28px;
  line-height: 28px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #606266;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  display: flex;
  align-items: center;
  white-space: nowrap;
  font-size: 13px;
}

.tab-card.is-active {
  background-color: #409eff;
  color: #fff;
  border-color: #409eff;
}

.tab-card .tab-title {
  margin-right: 4px;
  font-size: 13px;
}

.tab-card .is-icon-close {
  cursor: pointer;
  font-size: 12px;
  color: #606266;
}

.tab-card.is-active .is-icon-close {
  color: #fff;
}

.header-bottom-right {
  display: flex;
  background-color: #fff;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
}

.header-bottom-right .el-dropdown-link {
  height: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-bottom-right .el-dropdown-link .el-icon {
  margin: 0;
}

.main {
  background: #fafbfc;
  padding: 20px;
}

.main-content-container {
  height: 100vh;
  border: none !important;
  box-shadow: none !important;
}

.menu-icon {
  width: 16px;
  height: 16px;
}

.el-sub-menu .el-icon,
.el-sub-menu .iconfont-sys {
  width: 1em;
  margin-right: 8px;
  text-align: center;
  vertical-align: -0.15em;
}

.iconfont-sys {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

/* Chatbot styles */
.action-icon {
  cursor: pointer;
  color: #606266;
  font-size: 20px;
  margin-right: 15px;
}

:deep(.chat-drawer .el-drawer__header) {
  margin-bottom: 0;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-body {
  flex-grow: 1;
  overflow-y: auto;
  padding: 15px 10px;
}

.chat-footer {
  display: flex;
  align-items: center;
  padding: 8px;
  border-top: 1px solid #eee;
}

.chat-footer .el-input {
  margin-right: 10px;
}

.message {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message .chat-avatar {
  flex-shrink: 0;
}

/* 确保SVG头像背景透明 */
.message .chat-avatar :deep(img) {
  background: transparent !important;
}

.message .chat-avatar :deep(svg) {
  background: transparent !important;
}

.message .message-content {
  display: flex;
  flex-direction: column;
  max-width: 75%;
}

.message .message-bubble {
  padding: 0px 10px;
  border-radius: 5px;
  background-color: #f0f2f5;
  color: #333;
  line-height: 1.5;
  word-wrap: break-word;
  font-size: 14px;
}

/* User message */
.message.user {
  flex-direction: row-reverse;
}

.message.user .chat-avatar {
  margin-left: 12px;
}

.message.user .message-content {
  align-items: flex-end;
}

.message.user .message-bubble {
  background-color: #409eff;
  color: white;
  border-top-right-radius: 4px;
}

/* Bot message */
.message.bot {
  flex-direction: row;
}

.message.bot .chat-avatar {
  margin-right: 12px;
  background: transparent !important;
}

.message.bot .message-content {
  align-items: flex-start;
}

.message.bot .message-bubble {
  border-top-left-radius: 4px;
}

/* 数学公式样式 */
.message .message-bubble :deep(.katex) {
  font-size: 1em;
  line-height: 1.5;
}

.message .message-bubble :deep(.katex-display) {
  margin: 0.5em 0;
  text-align: center;
}

.message .message-bubble :deep(.katex-html) {
  white-space: normal;
}

/* 确保数学公式在聊天气泡中正确换行 */
.message .message-bubble {
  overflow-wrap: break-word;
  word-wrap: break-word;
  word-break: break-word;
}

/* Markdown内容样式 */
.message .message-bubble :deep(h1),
.message .message-bubble :deep(h2),
.message .message-bubble :deep(h3),
.message .message-bubble :deep(h4),
.message .message-bubble :deep(h5),
.message .message-bubble :deep(h6) {
  margin: 10px 0 5px 0;
  font-weight: bold;
  color: inherit;
}

.message .message-bubble :deep(h1) {
  font-size: 1.2em;
}
.message .message-bubble :deep(h2) {
  font-size: 1.1em;
}
.message .message-bubble :deep(h3) {
  font-size: 1.05em;
}

.message .message-bubble :deep(strong),
.message .message-bubble :deep(b) {
  font-weight: bold;
  color: inherit;
}

.message .message-bubble :deep(em),
.message .message-bubble :deep(i) {
  font-style: italic;
  color: inherit;
}

.message .message-bubble :deep(ul),
.message .message-bubble :deep(ol) {
  margin: 5px 0;
  padding-left: 20px;
}

.message .message-bubble :deep(li) {
  margin: 2px 0;
  color: inherit;
}

.message .message-bubble :deep(code) {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px 4px;
  border-radius: 3px;
  font-family: monospace;
  font-size: 0.9em;
}

.message .message-bubble :deep(pre) {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 10px;
  border-radius: 5px;
  overflow-x: auto;
  margin: 5px 0;
}

.message .message-bubble :deep(blockquote) {
  border-left: 3px solid #ddd;
  margin: 5px 0;
  padding-left: 10px;
  color: #666;
}

.message .message-bubble :deep(hr) {
  border: none;
  border-top: 1px solid #ddd;
  margin: 10px 0;
}

/* 确保所有文本颜色继承父元素 */
.message .message-bubble :deep(*) {
  color: inherit;
}

/* 为bot头像添加特殊样式 */
.message.bot .chat-avatar {
  background: transparent !important;
}

.message.bot .chat-avatar :deep(*) {
  background: transparent !important;
}

/* 确保Element Plus头像组件背景透明 */
.message .chat-avatar :deep(.el-avatar) {
  background: transparent !important;
}

.message .chat-avatar :deep(.el-avatar img) {
  background: transparent !important;
}

/* 用户信息弹窗样式 */
.user-info-popover {
  padding: 18px 20px 18px 18px;
  border-radius: 12px;
  min-width: 240px;
  max-width: 280px;
}
.user-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-info-meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.user-info-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}
.user-info-email {
  font-size: 14px;
  color: #8c8c8c;
}
.user-info-divider {
  height: 1px;
  background: #eee;
  margin: 16px 0 12px 0;
  width: 100%;
}
.user-info-logout-btn {
  display: flex;
  justify-content: center;
  align-items: center;
}
.ai-chat-icon-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.ai-chat-dot {
  position: absolute;
  right: 6px;
  width: 8px;
  height: 8px;
  background: #52c41a;
  border-radius: 50%;
  border: 2px solid #fff;
  z-index: 2;
  box-shadow: 0 0 2px #52c41a;
}
.fullscreen-toggle-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}
.fullscreen-toggle-icon:hover {
  color: #409eff;
}

/* Notification Popover Styles */
.notification-icon {
  position: relative;
  display: inline-block;
  margin-right: 15px;
  margin-top: 4px;
  cursor: pointer;
  color: #606266;
}
.notification-dot {
  position: absolute;
  right: 2px;
  top: 2px;
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
  border: 2px solid #fff;
  z-index: 2;
}
.notification-popover {
  padding: 16px 20px 16px 20px;
  border-radius: 10px;
}
.notification-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}
.notification-item {
  font-size: 14px;
  margin-bottom: 8px;
  color: #666;
  display: flex;
  align-items: center;
}
.notification-item-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #409eff;
}
</style>
