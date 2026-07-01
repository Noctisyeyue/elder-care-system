import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/Login.vue'
import RegisterView from '@/views/Register.vue'
import FindPasswordView from '@/views/FindPassword.vue'
import PendingReviewView from '@/views/PendingReview.vue'
import MainView from '@/views/Main.vue'
import BedManageView from '@/views/bed/BedManage.vue'
import BedMapView from '@/views/bed/BedMap.vue'
import BedListView from '@/views/bed/BedList.vue'
import UserManageView from '@/views/user/UserManage.vue'
import UserListView from '@/views/user/UserList.vue'
import CustomerManageView from '@/views/customer/CustomerManage.vue'
import CheckInRegisterView from '@/views/customer/CheckInRegister.vue'
import CheckOutRegisterView from '@/views/customer/CheckOutRegister.vue'
import OutingRegisterView from '@/views/customer/OutingRegister.vue'
import OutingApplyView from '@/views/customer/OutingApply.vue'
import OutingListView from '@/views/customer/OutingList.vue'
import CheckOutApplyView from '@/views/customer/CheckOutApply.vue'
import CheckOutListView from '@/views/customer/CheckOutList.vue'
import NursingManageView from '@/views/nursing/NursingManage.vue'
import NursingLevelView from '@/views/nursing/NursingLevel.vue'
import NursingItemView from '@/views/nursing/NursingItem.vue'
import CustomerNursingView from '@/views/nursing/CustomerNursing.vue'
import NursingRecordView from '@/views/nursing/NursingRecord.vue'
import NursingItemSettingView from '@/views/nursing/NursingItemSetting.vue'
import ServiceManageView from '@/views/health/ServiceManage.vue'
import ServiceConcernView from '@/views/health/ServiceConcern.vue'
import ServiceConfigView from '@/views/health/ServiceConfig.vue'
import CaregiverView from '@/views/health/Caregiver.vue'
import BuyNursingItemView from '@/views/health/BuyNursingItem.vue'
import DietManageView from '@/views/diet/DietManage.vue'
import DietCalendarView from '@/views/diet/DietCalendar.vue'
import DietConfigView from '@/views/diet/DietConfig.vue'
import DietStatusView from '@/views/diet/DietStatus.vue'
import DietPackageConfigView from '@/views/diet/MealPackageConfig.vue'
import CaregiverDailyCareView from '@/views/health/CaregiverDailyCare.vue'
import CaregiverNursingRecordsView from '@/views/health/CaregiverNursingRecords.vue'
import CaregiverCustomerCareItemsView from '@/views/health/CaregiverCustomerCareItems.vue'
import PurchasedNursingItemsView from '@/views/health/PurchasedNursingItems.vue'
import AdminHomeView from '@/views/home/AdminHome.vue'
import CaregiverHomeView from '@/views/home/CaregiverHome.vue'

import { useUserStore } from '@/stores/user'

/**
 * 应用路由实例。
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'LoginView',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'RegisterView',
      component: RegisterView,
    },
    {
      path: '/find-password',
      name: 'FindPasswordView',
      component: FindPasswordView,
    },
    {
      path: '/pending',
      name: 'PendingReview',
      component: PendingReviewView,
      meta: { requiresAuth: true },
    },
    {
      path: '/',
      name: 'MainView',
      component: MainView,
      children: [
        {
          path: 'home',
          name: 'Home',
          children: [
            { path: 'admin', name: 'AdminHome', component: AdminHomeView },
            { path: 'caregiver', name: 'CaregiverHome', component: CaregiverHomeView },
          ],
        },
        {
          path: 'bed',
          name: 'BedManagement',
          component: BedManageView,
          redirect: '/bed/map',
          children: [
            { path: 'map', name: 'BedMap', component: BedMapView },
            { path: 'list', name: 'BedList', component: BedListView },
          ],
        },
        {
          path: 'user',
          name: 'UserManagement',
          component: UserManageView,
          redirect: '/user/list',
          children: [{ path: 'list', name: 'UserList', component: UserListView }],
        },
        {
          path: 'customer',
          name: 'CustomerManagement',
          component: CustomerManageView,
          redirect: '/customer/checkIn',
          children: [
            { path: 'checkIn', name: 'CheckInRegister', component: CheckInRegisterView },
            { path: 'checkOut', name: 'CheckOutRegister', component: CheckOutRegisterView },
            { path: 'out', name: 'OutingRegister', component: OutingRegisterView },
            { path: 'outApply', name: 'OutingApply', component: OutingApplyView },
            { path: 'outingList', name: 'OutingList', component: OutingListView },
            { path: 'checkOutApply', name: 'CheckOutApply', component: CheckOutApplyView },
            { path: 'checkOutList', name: 'CheckOutList', component: CheckOutListView },
          ],
        },
        {
          path: 'nursing',
          name: 'NursingManagement',
          component: NursingManageView,
          redirect: '/nursing/level',
          children: [
            { path: 'level', name: 'NursingLevel', component: NursingLevelView },
            {
              path: 'itemSetting',
              name: 'NursingItemSetting',
              component: NursingItemSettingView,
            },
            { path: 'item', name: 'NursingItem', component: NursingItemView },
            { path: 'record', name: 'NursingRecord', component: NursingRecordView },
            { path: 'customer', name: 'CustomerNursing', component: CustomerNursingView },
          ],
        },
        {
          path: 'service',
          name: 'ServiceManagement',
          component: ServiceManageView,
          redirect: '/service/caregiver',
          children: [
            { path: 'caregiver', name: 'Caregiver', component: CaregiverView },
            { path: 'config', name: 'ServiceConfig', component: ServiceConfigView },
            { path: 'concern', name: 'ServiceConcern', component: ServiceConcernView },
            { path: 'nursingItem', name: 'BuyNursingItem', component: BuyNursingItemView },
            { path: 'dailyCare', name: 'DailyCare', component: CaregiverDailyCareView },
            { path: 'records', name: 'CaregiverNursingRecords', component: CaregiverNursingRecordsView },
            { path: 'items', name: 'CustomerNursingItems', component: CaregiverCustomerCareItemsView },
            { path: 'purchased-nursing-items', name: 'PurchasedNursingItems', component: PurchasedNursingItemsView },
          ],
        },
        {
          path: 'diet',
          name: 'DietManagement',
          component: DietManageView,
          redirect: '/diet/calendar',
          children: [
            { path: 'calendar', name: 'DietCalendar', component: DietCalendarView },
            { path: 'config', name: 'DietConfig', component: DietConfigView },
            { path: 'status', name: 'DietStatus', component: DietStatusView },
            { path: 'package', name: 'DietPackageConfig', component: DietPackageConfigView },
          ],
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
})

/** 不需要登录即可访问的白名单路径 */
const WHITELIST = ['/login', '/register', '/find-password']

/**
 * 全局路由守卫：统一处理登录态、待审核跳转、重复登录拦截。
 */
router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  userStore.loadFromStorage()

  // 白名单路径直接放行
  if (WHITELIST.includes(to.path)) {
    // 已登录用户访问登录页，按角色直接跳对应首页
    if (userStore.isLoggedIn && to.path === '/login') {
      if (userStore.isPending) {
        next('/pending')
      } else if (userStore.isCaregiver) {
        next('/home/caregiver')
      } else {
        next('/home/admin')
      }
      return
    }
    next()
    return
  }

  // 未登录访问受保护页面 → 跳登录
  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 待审核账号只能停留在等待审核页
  if (userStore.isPending && to.path !== '/pending') {
    next('/pending')
    return
  }

  // 用户管理类路由仅超级管理员可访问
  if (to.path.startsWith('/user') && !userStore.isSuperAdmin) {
    next(userStore.isCaregiver ? '/home/caregiver' : '/home/admin')
    return
  }

  next()
})

export default router
