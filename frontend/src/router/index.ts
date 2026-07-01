import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/Login.vue'
import RegisterView from '@/views/Register.vue'
import PendingReviewView from '@/views/PendingReview.vue'
import AppLayout from '@/views/layout/index.vue'
import { layoutChildren } from '@/router/modules'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { buildMenuList } from '@/utils/menu'

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
      path: '/pending',
      name: 'PendingReview',
      component: PendingReviewView,
      meta: { requiresAuth: true },
    },
    {
      path: '/',
      name: 'AppLayout',
      component: AppLayout,
      redirect: '/home/admin',
      children: layoutChildren,
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
})

const WHITELIST = ['/login', '/register']

function getDefaultHome(roleKey: string) {
  return roleKey === 'caregiver' ? '/home/caregiver' : '/home/admin'
}

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const menuStore = useMenuStore()
  userStore.loadFromStorage()

  if (WHITELIST.includes(to.path)) {
    if (userStore.isLoggedIn && to.path === '/login') {
      if (userStore.isPending) {
        next('/pending')
      } else {
        next(getDefaultHome(userStore.roleKey))
      }
      return
    }
    next()
    return
  }

  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  if (userStore.isPending && to.path !== '/pending') {
    next('/pending')
    return
  }

  // 初始化菜单
  if (menuStore.menuList.length === 0 && !userStore.isPending) {
    const home = getDefaultHome(userStore.roleKey)
    menuStore.setMenuList(buildMenuList(userStore.roleKey), home)
  }

  // 个人中心：所有已启用角色可访问
  if (to.path === '/user/center') {
    next()
    return
  }

  // 用户列表仅超管
  if (to.path.startsWith('/user') && !userStore.isSuperAdmin) {
    next(getDefaultHome(userStore.roleKey))
    return
  }

  // meta.roles 校验
  const roles = to.meta?.roles as string[] | undefined
  if (roles?.length && !roles.includes(userStore.roleKey)) {
    next(getDefaultHome(userStore.roleKey))
    return
  }

  next()
})

export default router
