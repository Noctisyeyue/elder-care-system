import type { AppRouteRecord } from '@/types/router'
import UserManageView from '@/views/user/UserManage.vue'
import UserListView from '@/views/user/UserList.vue'

export const userRoutes: AppRouteRecord = {
  path: 'user',
  name: 'UserManagement',
  component: UserManageView,
  redirect: '/user/list',
  meta: {
    title: '系统管理',
    icon: 'ri:settings-3-line',
    roles: ['super_admin'],
    isHide: true,
  },
  children: [
    {
      path: 'list',
      name: 'UserList',
      component: UserListView,
      meta: {
        title: '基础数据维护',
        icon: 'ri:database-2-line',
        roles: ['super_admin'],
        keepAlive: true,
      },
    },
    {
      path: 'center',
      name: 'UserCenter',
      component: () => import('@/views/system/user-center/index.vue'),
      meta: {
        title: '个人中心',
        icon: 'ri:user-3-line',
        isHide: true,
        isHideTab: false,
        isFirstLevel: true,
        roles: ['super_admin', 'admin', 'caregiver'],
        keepAlive: true,
      },
    },
  ],
}

/** 个人中心侧边栏菜单项（与首页同级） */
export const userCenterMenu: AppRouteRecord = {
  path: '/user/center',
  name: 'UserCenterMenu',
  meta: {
    title: '个人中心',
    icon: 'ri:user-3-line',
    isFirstLevel: true,
    roles: ['super_admin', 'admin', 'caregiver'],
    keepAlive: true,
  },
}

/** 系统管理侧边栏菜单（仅超管可见） */
export const systemMenu: AppRouteRecord = {
  path: '/user',
  name: 'SystemMenu',
  meta: { title: '系统管理', icon: 'ri:settings-3-line', roles: ['super_admin'] },
  children: [
    {
      path: '/user/list',
      name: 'UserListMenu',
      meta: { title: '基础数据维护', icon: 'ri:database-2-line', roles: ['super_admin'] },
    },
  ],
}
