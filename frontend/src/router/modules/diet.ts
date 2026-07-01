import type { AppRouteRecord } from '@/types/router'
import DietManageView from '@/views/diet/DietManage.vue'
import DietCalendarView from '@/views/diet/DietCalendar.vue'
import DietConfigView from '@/views/diet/DietConfig.vue'
import DietStatusView from '@/views/diet/DietStatus.vue'
import DietPackageConfigView from '@/views/diet/MealPackageConfig.vue'

export const dietRoutes: AppRouteRecord = {
  path: 'diet',
  name: 'DietManagement',
  component: DietManageView,
  redirect: '/diet/calendar',
  meta: {
    title: '膳食管理',
    icon: 'ri:restaurant-line',
    roles: ['super_admin', 'admin'],
  },
  children: [
    {
      path: 'status',
      name: 'DietStatus',
      component: DietStatusView,
      meta: {
        title: '菜品配置',
        icon: 'ri:bowl-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'package',
      name: 'DietPackageConfig',
      component: DietPackageConfigView,
      meta: {
        title: '套餐配置',
        icon: 'ri:box-3-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'calendar',
      name: 'DietCalendar',
      component: DietCalendarView,
      meta: {
        title: '膳食日历',
        icon: 'ri:calendar-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'config',
      name: 'DietConfig',
      component: DietConfigView,
      meta: {
        title: '膳食配置',
        icon: 'ri:settings-3-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
  ],
}
