import type { RouteRecordRaw } from 'vue-router'

/** 路由 meta 扩展 */
export interface RouteMeta {
  title?: string
  icon?: string
  roles?: string[]
  keepAlive?: boolean
  isHide?: boolean
  isHideTab?: boolean
  isFirstLevel?: boolean
  activePath?: string
}

export type AppRouteRecord = RouteRecordRaw & {
  meta?: RouteMeta
  children?: AppRouteRecord[]
}
