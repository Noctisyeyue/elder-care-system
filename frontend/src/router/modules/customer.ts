import type { AppRouteRecord } from '@/types/router'
import CustomerManageView from '@/views/customer/CustomerManage.vue'
import CheckInRegisterView from '@/views/customer/CheckInRegister.vue'
import CheckOutRegisterView from '@/views/customer/CheckOutRegister.vue'
import OutingRegisterView from '@/views/customer/OutingRegister.vue'
import OutingApplyView from '@/views/customer/OutingApply.vue'
import OutingListView from '@/views/customer/OutingList.vue'
import CheckOutApplyView from '@/views/customer/CheckOutApply.vue'
import CheckOutListView from '@/views/customer/CheckOutList.vue'

export const customerRoutes: AppRouteRecord = {
  path: 'customer',
  name: 'CustomerManagement',
  component: CustomerManageView,
  redirect: '/customer/checkIn',
  meta: { title: '客户管理', icon: 'ri:user-3-line', isHide: true },
  children: [
    {
      path: 'checkIn',
      name: 'CheckInRegister',
      component: CheckInRegisterView,
      meta: {
        title: '入住登记',
        icon: 'ri:user-add-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'checkOut',
      name: 'CheckOutRegister',
      component: CheckOutRegisterView,
      meta: {
        title: '退住审核',
        icon: 'ri:logout-box-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'out',
      name: 'OutingRegister',
      component: OutingRegisterView,
      meta: {
        title: '外出审核',
        icon: 'ri:walk-line',
        roles: ['super_admin', 'admin'],
        keepAlive: true,
      },
    },
    {
      path: 'outApply',
      name: 'OutingApply',
      component: OutingApplyView,
      meta: {
        title: '外出申请',
        icon: 'ri:walk-line',
        roles: ['caregiver'],
        keepAlive: true,
      },
    },
    {
      path: 'outingList',
      name: 'OutingList',
      component: OutingListView,
      meta: {
        title: '外出申请列表',
        icon: 'ri:list-check',
        roles: ['caregiver'],
        isHide: true,
        keepAlive: true,
      },
    },
    {
      path: 'checkOutApply',
      name: 'CheckOutApply',
      component: CheckOutApplyView,
      meta: {
        title: '退住申请',
        icon: 'ri:logout-box-r-line',
        roles: ['caregiver'],
        keepAlive: true,
      },
    },
    {
      path: 'checkOutList',
      name: 'CheckOutList',
      component: CheckOutListView,
      meta: {
        title: '退住申请列表',
        icon: 'ri:list-check-2',
        roles: ['caregiver'],
        isHide: true,
        keepAlive: true,
      },
    },
  ],
}

/** 管理员可见的客户管理子菜单 */
export const adminCustomerMenu: AppRouteRecord = {
  path: '/customer',
  name: 'CustomerMenuAdmin',
  meta: { title: '客户管理', icon: 'ri:user-3-line', roles: ['super_admin', 'admin'] },
  children: [
    {
      path: '/customer/checkIn',
      name: 'CheckInMenu',
      meta: { title: '入住登记', icon: 'ri:user-add-line', roles: ['super_admin', 'admin'] },
    },
    {
      path: '/customer/out',
      name: 'OutingMenu',
      meta: { title: '外出审核', icon: 'ri:walk-line', roles: ['super_admin', 'admin'] },
    },
    {
      path: '/customer/checkOut',
      name: 'CheckOutMenu',
      meta: { title: '退住审核', icon: 'ri:logout-box-line', roles: ['super_admin', 'admin'] },
    },
  ],
}

/** 护工可见的客户管理子菜单 */
export const caregiverCustomerMenu: AppRouteRecord = {
  path: '/customer',
  name: 'CustomerMenuCaregiver',
  meta: { title: '客户管理', icon: 'ri:user-3-line', roles: ['caregiver'] },
  children: [
    {
      path: '/customer/outApply',
      name: 'OutingApplyMenu',
      meta: { title: '外出申请', icon: 'ri:walk-line', roles: ['caregiver'] },
    },
    {
      path: '/customer/checkOutApply',
      name: 'CheckOutApplyMenu',
      meta: { title: '退住申请', icon: 'ri:logout-box-r-line', roles: ['caregiver'] },
    },
  ],
}
