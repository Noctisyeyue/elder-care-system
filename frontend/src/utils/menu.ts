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
  userCenterMenu,
} from '@/router/modules'

/**
 * 判断当前角色是否可访问该菜单 meta。
 *
 * @param meta 路由 meta
 * @param roleKey 当前用户角色
 * @return 是否可访问
 */
function hasRole(meta: AppRouteRecord['meta'], roleKey: string): boolean {
  if (!meta?.roles || meta.roles.length === 0) return true
  return meta.roles.includes(roleKey)
}

/**
 * 按角色过滤单个菜单项。
 *
 * @param item 菜单项
 * @param roleKey 当前用户角色
 * @return 过滤后的菜单项，不可见时返回 null
 */
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

/**
 * 将路由模块转为带完整 path 的菜单项。
 *
 * @param route 路由记录
 * @param parentPath 父级路径
 * @return 带完整路径的菜单项
 */
function toMenuRoute(route: AppRouteRecord, parentPath = ''): AppRouteRecord {
  const base = parentPath || ''
  const path = route.path.startsWith('/')
    ? route.path
    : `${base}/${route.path}`.replace(/\/+/g, '/')

  const children = route.children?.map((c) => toMenuRoute(c, path))
  return { ...route, path, children }
}

/**
 * 根据角色生成侧边栏菜单。
 *
 * @param roleKey 当前用户角色
 * @return 侧边栏菜单列表
 */
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
    menus.push(toMenuRoute(userCenterMenu))
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
    menus.push(toMenuRoute(userCenterMenu))
    menus.push(caregiverCustomerMenu)
    menus.push(caregiverServiceMenu)
  }

  return menus
    .map((item) => filterMenuItem(item, roleKey))
    .filter((item): item is AppRouteRecord => item !== null)
}

/**
 * 扁平化菜单用于全局搜索。
 *
 * @param menus 菜单树
 * @return 扁平化后的菜单项列表
 */
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

/**
 * 根据 path 获取路由 meta.title。
 *
 * @param path 当前路径
 * @param routes 路由列表
 * @return 匹配到的标题，未找到时返回空字符串
 */
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

/**
 * 根据 path 生成面包屑标题链。
 *
 * @param path 当前路径
 * @param routes 路由列表
 * @return 面包屑标题数组
 */
export function getBreadcrumbTitles(path: string, routes: AppRouteRecord[]): string[] {
  const normalized = path.replace(/\/\d+/g, '').replace(/\/$/, '') || path

  /**
   * 在路由树中查找与当前路径完全匹配的节点链。
   *
   * @param items 路由列表
   * @param parent 父级路径
   * @param chain 当前匹配链
   * @return 匹配到的路由链，未找到时返回 null
   */
  function findChain(
    items: AppRouteRecord[],
    parent = '',
    chain: AppRouteRecord[] = [],
  ): AppRouteRecord[] | null {
    for (const item of items) {
      const fullPath = item.path.startsWith('/')
        ? item.path
        : `${parent}/${item.path}`.replace(/\/+/g, '/')
      const currentChain = [...chain, item]

      if (fullPath === normalized) {
        return currentChain
      }

      if (item.children?.length) {
        const found = findChain(item.children, fullPath, currentChain)
        if (found) return found
      }
    }
    return null
  }

  const chain = findChain(routes)
  if (!chain) return []

  const firstLevelIndex = chain.findIndex((item) => item.meta?.isFirstLevel)
  const visibleChain = firstLevelIndex >= 0 ? chain.slice(firstLevelIndex) : chain

  return visibleChain
    .map((item) => item.meta?.title)
    .filter((title): title is string => !!title)
}
