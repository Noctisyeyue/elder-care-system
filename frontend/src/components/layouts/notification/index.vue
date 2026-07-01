<template>
  <el-popover
    v-if="userStore.isAdmin"
    placement="bottom"
    :width="260"
    trigger="hover"
    popper-class="notification-popover"
    @show="fetchNotification"
  >
    <template #reference>
      <div class="notification-wrap">
        <IconButton icon="ri:notification-2-line" title="通知" />
        <span v-if="hasUnread" class="notification-dot"></span>
      </div>
    </template>
    <div v-if="loading" class="loading-wrap">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>
    <div v-else>
      <div class="notification-title">通知</div>
      <div class="notification-item">
        <SvgIcon icon="ri:eye-line" :size="16" />
        待处理退住申请：{{ data.checkOutApplyCount }} 个
      </div>
      <div class="notification-item">
        <SvgIcon icon="ri:settings-3-line" :size="16" />
        待处理外出申请：{{ data.outingApplyCount }} 个
      </div>
      <div class="notification-item">
        <SvgIcon icon="ri:user-line" :size="16" />
        新注册待审核：{{ data.pendingUserCount }} 个
      </div>
      <div class="notification-item">
        <SvgIcon icon="ri:restaurant-line" :size="16" />
        今日膳食配置：
        <span :style="{ color: data.dietConfigured ? '#52c41a' : '#f56c6c' }">
          {{ data.dietConfigured ? '已配置' : '未配置' }}
        </span>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { get } from '@/utils/request'
import { useUserStore } from '@/stores/user'
import IconButton from '@/components/base/icon-button/index.vue'
import SvgIcon from '@/components/base/svg-icon/index.vue'

defineOptions({ name: 'Notification' })

const userStore = useUserStore()
const loading = ref(false)
const data = ref({
  checkOutApplyCount: 0,
  outingApplyCount: 0,
  pendingUserCount: 0,
  dietConfigured: false,
})

const hasUnread = computed(
  () =>
    data.value.checkOutApplyCount +
      data.value.outingApplyCount +
      data.value.pendingUserCount >
    0,
)

async function fetchNotification() {
  loading.value = true
  try {
    const today = dayjs().format('YYYY-MM-DD')
    data.value = await get(`/common/notification?date=${today}`)
  } catch {
    ElMessage.error('获取通知失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (userStore.isAdmin) fetchNotification()
})
</script>

<style scoped>
.notification-wrap {
  position: relative;
  display: inline-flex;
}

.notification-dot {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 6px;
  height: 6px;
  background: #f56c6c;
  border-radius: 50%;
}

.notification-title {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 8px;
  color: var(--ui-gray-800);
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  font-size: 13px;
  color: var(--ui-gray-600);
}

.loading-wrap {
  text-align: center;
  padding: 20px 0;
}
</style>
