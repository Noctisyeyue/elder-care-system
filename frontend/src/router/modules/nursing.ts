import type { AppRouteRecord } from '@/types/router'
import NursingManageView from '@/views/nursing/NursingManage.vue'
import NursingLevelView from '@/views/nursing/NursingLevel.vue'
import NursingItemView from '@/views/nursing/NursingItem.vue'
import CustomerNursingView from '@/views/nursing/CustomerNursing.vue'
import NursingRecordView from '@/views/nursing/NursingRecord.vue'
import NursingItemSettingView from '@/views/nursing/NursingItemSetting.vue'

export const nursingRoutes: AppRouteRecord = {
  path: 'nursing',
  name: 'NursingManagement',
  component: NursingManageView,
  redirect: '/nursing/level',
  meta: {
    title: '护理管理',
    icon: 'ri:first-aid-kit-line',
    roles: ['super_admin', 'admin'],
  },
  children: [
    {
      path: 'level',
      name: 'NursingLevel',
      component: NursingLevelView,
      meta: {
        title: '护理级别',
        icon: 'ri:stack-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'itemSetting',
      name: 'NursingItemSetting',
      component: NursingItemSettingView,
      meta: {
        title: '护理项目配置',
        icon: 'ri:settings-3-line',
        roles: ['super_admin', 'admin'],
        isHide: true,
        keepAlive: true,
      },
    },
    {
      path: 'item',
      name: 'NursingItem',
      component: NursingItemView,
      meta: {
        title: '护理项目',
        icon: 'ri:heart-pulse-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'customer',
      name: 'CustomerNursing',
      component: CustomerNursingView,
      meta: {
        title: '客户护理设置',
        icon: 'ri:user-heart-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'record',
      name: 'NursingRecord',
      component: NursingRecordView,
      meta: {
        title: '护理记录',
        icon: 'ri:file-list-3-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
  ],
}
