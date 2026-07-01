import type { AppRouteRecord } from '@/types/router'
import { homeRoutes } from '@/router/modules/home'
import { bedRoutes } from '@/router/modules/bed'
import { nursingRoutes } from '@/router/modules/nursing'
import { dietRoutes } from '@/router/modules/diet'
import {
  adminCustomerMenu,
  caregiverCustomerMenu,
  adminServiceMenu,
  caregiverServiceMenu,
  systemMenu,
} from '@/router/modules'

function hasRole(meta: AppRouteRecord['meta'], roleKey: string): boolean {
  if (!meta?.roles || meta.roles.length === 0) return true
  return meta.roles.includes(roleKey)
}

function filterMenuItem(item: AppRouteRecord, roleKey: string): AppRouteRecord | null {
  if (item.meta?.isHide) return null
  if (!hasRole(item.meta, roleKey)) return null

  const children = item.children
    ?.map((child) => filterMenuItem(child, roleKey))
    .filter((c): c is AppRouteRecord => c !== null)

  if (children && children.length === 0 && !item.component) return null

  return {
    ...item,
    children: children?.length ? children : undefined,
  }
}

/** 将路由模块转为带完整 path 的菜单项 */
function toMenuRoute(route: AppRouteRecord, parentPath = ''): AppRouteRecord {
  const base = parentPath || ''
  const path = route.path.startsWith('/')
    ? route.path
    : `${base}/${route.path}`.replace(/\/+/g, '/')

  const children = route.children?.map((c) => toMenuRoute(c, path))
  return { ...route, path, children }
}

/** 根据角色生成侧边栏菜单 */
export function buildMenuList(roleKey: string): AppRouteRecord[] {
  const isAdmin = roleKey === 'admin' || roleKey === 'super_admin'
  const isCaregiver = roleKey === 'caregiver'
  const isSuperAdmin = roleKey === 'super_admin'

  const menus: AppRouteRecord[] = []

  if (isAdmin) {
    const homeChild = homeRoutes.children?.find((c) => c.path === 'admin')
    if (homeChild) {
      menus.push(
        toMenuRoute({
          path: '/home/admin',
          name: 'AdminHomeMenu',
          meta: homeChild.meta,
        }),
      )
    }
    menus.push(toMenuRoute(bedRoutes))
    menus.push(adminCustomerMenu)
    menus.push(toMenuRoute(nursingRoutes))
    menus.push(adminServiceMenu)
    menus.push(toMenuRoute(dietRoutes))
    if (isSuperAdmin) {
      menus.push(systemMenu)
    }
  }

  if (isCaregiver) {
    const homeChild = homeRoutes.children?.find((c) => c.path === 'caregiver')
    if (homeChild) {
      menus.push(
        toMenuRoute({
          path: '/home/caregiver',
          name: 'CaregiverHomeMenu',
          meta: homeChild.meta,
        }),
      )
    }
    menus.push(caregiverCustomerMenu)
    menus.push(caregiverServiceMenu)
  }

  return menus
    .map((item) => filterMenuItem(item, roleKey))
    .filter((item): item is AppRouteRecord => item !== null)
}

/** 扁平化菜单用于搜索 */
export function flattenMenus(menus: AppRouteRecord[]): AppRouteRecord[] {
  const result: AppRouteRecord[] = []
  const walk = (items: AppRouteRecord[]) => {
    for (const item of items) {
      if (item.meta?.title && item.path && !item.children?.length) {
        result.push(item)
      }
      if (item.children?.length) walk(item.children)
    }
  }
  walk(menus)
  return result
}

/** 根据 path 获取路由 meta.title */
export function getRouteTitle(path: string, routes: AppRouteRecord[]): string {
  const normalized = path.replace(/\/\d+/g, '').replace(/\/$/, '') || path
  let title = ''
  const walk = (items: AppRouteRecord[], parent = '') => {
    for (const item of items) {
      const fullPath = item.path.startsWith('/')
        ? item.path
        : `${parent}/${item.path}`.replace(/\/+/g, '/')
      if (fullPath === normalized || path.startsWith(fullPath + '/')) {
        if (item.meta?.title) title = item.meta.title
      }
      if (item.children) walk(item.children, fullPath)
    }
  }
  walk(routes)
  return title
}
