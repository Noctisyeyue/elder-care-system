import type { AppRouteRecord } from '@/types/router'
import { homeRoutes } from './home'
import { bedRoutes } from './bed'
import { customerRoutes, adminCustomerMenu, caregiverCustomerMenu } from './customer'
import { nursingRoutes } from './nursing'
import { serviceRoutes, adminServiceMenu, caregiverServiceMenu } from './service'
import { dietRoutes } from './diet'
import { userRoutes, systemMenu, userCenterMenu } from './user'

/** 布局内业务路由（router children） */
export const layoutChildren: AppRouteRecord[] = [
  homeRoutes,
  bedRoutes,
  customerRoutes,
  nursingRoutes,
  serviceRoutes,
  dietRoutes,
  userRoutes,
]

export {
  homeRoutes,
  bedRoutes,
  customerRoutes,
  nursingRoutes,
  serviceRoutes,
  dietRoutes,
  userRoutes,
  adminCustomerMenu,
  caregiverCustomerMenu,
  adminServiceMenu,
  caregiverServiceMenu,
  systemMenu,
  userCenterMenu,
}
