import type { AppRouteRecord } from '@/types/router'
import AdminHomeView from '@/views/home/AdminHome.vue'
import CaregiverHomeView from '@/views/home/CareGiverHome.vue'

export const homeRoutes: AppRouteRecord = {
  path: 'home',
  name: 'Home',
  meta: { title: '首页', isHide: true },
  children: [
    {
      path: 'admin',
      name: 'AdminHome',
      component: AdminHomeView,
      meta: {
        title: '首页',
        icon: 'ri:home-4-line',
        roles: ['super_admin', 'admin'],
        isFirstLevel: true,
      },
    },
    {
      path: 'caregiver',
      name: 'CaregiverHome',
      component: CaregiverHomeView,
      meta: {
        title: '首页',
        icon: 'ri:home-4-line',
        roles: ['caregiver'],
        isFirstLevel: true,
      },
    },
  ],
}
