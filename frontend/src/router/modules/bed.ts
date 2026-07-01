import type { AppRouteRecord } from '@/types/router'
import BedManageView from '@/views/bed/BedManage.vue'
import BedMapView from '@/views/bed/BedMap.vue'
import BedListView from '@/views/bed/BedList.vue'

export const bedRoutes: AppRouteRecord = {
  path: 'bed',
  name: 'BedManagement',
  component: BedManageView,
  redirect: '/bed/map',
  meta: {
    title: '床位管理',
    icon: 'ri:hotel-bed-line',
    roles: ['super_admin', 'admin'],
  },
  children: [
    {
      path: 'map',
      name: 'BedMap',
      component: BedMapView,
      meta: {
        title: '床位示意图',
        icon: 'ri:map-2-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'list',
      name: 'BedList',
      component: BedListView,
      meta: {
        title: '床位管理',
        icon: 'ri:hotel-bed-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
  ],
}
