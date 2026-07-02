import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/** 后端业务接口请求实例。 */
const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
})

/** 是否已显示错误提示，避免短时间内重复弹窗。 */
let isErrorMessageShown = false

/**
 * 清理 sessionStorage 中的完整登录态。
 *
 * 登录态失效、401 跳转登录页、退出登录等场景都会复用这里。
 */
function clearAuthFromStorage() {
  ;['token', 'userId', 'realName', 'email', 'roleId', 'roleKey', 'roleName', 'status'].forEach((k) =>
    sessionStorage.removeItem(k),
  )
}

/**
 * 请求拦截器：
 * 1. 从 sessionStorage 读取 token
 * 2. 统一拼接 Authorization 请求头
 * 3. 保证后端 SecurityFilterChain 可以识别当前登录态
 */
service.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error) // 打印错误信息
    return Promise.reject(error)
  },
)

/**
 * 响应拦截器：
 * 1. 统一处理后端 ApiResult 结构
 * 2. 对 401 自动清理登录态并跳转登录页
 * 3. 对 403、500 等错误给出统一提示
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    if (res.code !== 200) {
      if (!isErrorMessageShown) {
        ElMessage.error(res.message || '请求失败')
        isErrorMessageShown = true
        setTimeout(() => {
          isErrorMessageShown = false
        }, 3000)
      }

      // 401 表示登录态失效，清理本地缓存并回到登录页
      if (res.code === 401) {
        clearAuthFromStorage()
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res.data
  },
  (error: AxiosError) => {
    if (error.response) {
      const { status } = error.response

      switch (status) {
        case 401:
          // HTTP 401：后端认为当前请求未认证或登录状态过期
          clearAuthFromStorage()
          ElMessage.error('未登录，请先登录')
          router.push('/login')
          break
        case 403:
          // HTTP 403：已登录，但当前角色没有权限访问
          ElMessage.error('权限不足')
          break
        case 500:
          // HTTP 500：服务端内部错误
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`请求错误: ${status}`)
      }
    } else if (error.message) {
      if (error.message.includes('timeout')) {
        ElMessage.error('请求超时')
      } else {
        ElMessage.error('网络连接错误')
      }
    }

    return Promise.reject(error)
  },
)

/**
 * 发送 GET 请求。
 *
 * @param url 请求地址
 * @param params 查询参数
 * @param config Axios 请求配置
 * @returns 响应数据
 */
export function get<T = unknown>(
  url: string,
  params?: Record<string, unknown>,
  config?: AxiosRequestConfig,
): Promise<T> {
  return service.get(url, { params, ...config })
}

/**
 * 发送 POST 请求。
 *
 * @param url 请求地址
 * @param data 请求体数据
 * @param config Axios 请求配置
 * @returns 响应数据
 */
export function post<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  return service.post(url, data, config)
}

/**
 * 发送 PUT 请求。
 *
 * @param url 请求地址
 * @param data 请求体数据
 * @param config Axios 请求配置
 * @returns 响应数据
 */
export function put<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  return service.put(url, data, config)
}

/**
 * 发送 DELETE 请求。
 *
 * @param url 请求地址
 * @param config Axios 请求配置
 * @returns 响应数据
 */
export function del<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return service.delete(url, config)
}
