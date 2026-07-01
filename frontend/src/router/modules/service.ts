import type { AppRouteRecord } from '@/types/router'
import ServiceManageView from '@/views/health/ServiceManage.vue'
import ServiceConcernView from '@/views/health/ServiceConcern.vue'
import ServiceConfigView from '@/views/health/ServiceConfig.vue'
import CaregiverView from '@/views/health/Caregiver.vue'
import BuyNursingItemView from '@/views/health/BuyNursingItem.vue'
import CaregiverDailyCareView from '@/views/health/CaregiverDailyCare.vue'
import CaregiverNursingRecordsView from '@/views/health/CaregiverNursingRecords.vue'
import CaregiverCustomerCareItemsView from '@/views/health/CaregiverCustomerCareItems.vue'
import PurchasedNursingItemsView from '@/views/health/PurchasedNursingItems.vue'

export const serviceRoutes: AppRouteRecord = {
  path: 'service',
  name: 'ServiceManagement',
  component: ServiceManageView,
  redirect: '/service/caregiver',
  meta: { title: '健康管家', icon: 'ri:service-line', isHide: true },
  children: [
    {
      path: 'caregiver',
      name: 'Caregiver',
      component: CaregiverView,
      meta: {
        title: '设置服务对象',
        icon: 'ri:user-settings-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'config',
      name: 'ServiceConfig',
      component: ServiceConfigView,
      meta: {
        title: '设置服务客户',
        icon: 'ri:settings-4-line',
        roles: ['super_admin', 'admin'],
        isHide: true,
        keepAlive: true,
      },
    },
    {
      path: 'concern',
      name: 'ServiceConcern',
      component: ServiceConcernView,
      meta: {
        title: '服务关注',
        icon: 'ri:eye-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'nursingItem',
      name: 'BuyNursingItem',
      component: BuyNursingItemView,
      meta: {
        title: '购买护理项目',
        icon: 'ri:shopping-cart-line',
        roles: ['super_admin', 'admin'],
        isHide: true,
        keepAlive: true,
      },
    },
    {
      path: 'dailyCare',
      name: 'DailyCare',
      component: CaregiverDailyCareView,
      meta: {
        title: '日常护理',
        icon: 'ri:heart-2-line',
        roles: ['caregiver'],
        keepAlive: true,
      },
    },
    {
      path: 'records',
      name: 'CaregiverNursingRecords',
      component: CaregiverNursingRecordsView,
      meta: {
        title: '护理记录',
        icon: 'ri:file-list-3-line',
        roles: ['caregiver'],
        keepAlive: true,
      },
    },
    {
      path: 'items',
      name: 'CustomerNursingItems',
      component: CaregiverCustomerCareItemsView,
      meta: {
        title: '客户护理',
        icon: 'ri:nurse-line',
        roles: ['caregiver'],
        isHide: true,
        keepAlive: true,
      },
    },
    {
      path: 'purchased-nursing-items',
      name: 'PurchasedNursingItems',
      component: PurchasedNursingItemsView,
      meta: {
        title: '已购护理',
        icon: 'ri:shopping-bag-line',
        roles: ['super_admin', 'admin'],
        isHide: true,
        keepAlive: true,
      },
    },
  ],
}

/** 管理员健康管家菜单 */
export const adminServiceMenu: AppRouteRecord = {
  path: '/service',
  name: 'ServiceMenuAdmin',
  meta: { title: '健康管家', icon: 'ri:service-line', roles: ['super_admin', 'admin'] },
  children: [
    {
      path: '/service/caregiver',
      name: 'CaregiverMenu',
      meta: { title: '设置服务对象', icon: 'ri:user-settings-line', roles: ['super_admin', 'admin'] },
    },
    {
      path: '/service/concern',
      name: 'ServiceConcernMenu',
      meta: { title: '服务关注', icon: 'ri:eye-line', roles: ['super_admin', 'admin'] },
    },
  ],
}

/** 护工健康管家菜单 */
export const caregiverServiceMenu: AppRouteRecord = {
  path: '/service',
  name: 'ServiceMenuCaregiver',
  meta: { title: '健康管家', icon: 'ri:service-line', roles: ['caregiver'] },
  children: [
    {
      path: '/service/dailyCare',
      name: 'DailyCareMenu',
      meta: { title: '日常护理', icon: 'ri:heart-2-line', roles: ['caregiver'] },
    },
    {
      path: '/service/records',
      name: 'RecordsMenu',
      meta: { title: '护理记录', icon: 'ri:file-list-3-line', roles: ['caregiver'] },
    },
  ],
}
