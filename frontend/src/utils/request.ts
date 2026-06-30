import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/** 后端业务接口请求实例。 */
const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
})

/** LLM 流式接口请求实例。 */
const LLMService = axios.create({
  baseURL: 'http://localhost:8000',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/** 是否已显示错误提示，避免短时间内重复弹窗。 */
let isErrorMessageShown = false

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

      if (res.code === 401) {
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
          ElMessage.error('未登录，请先登录')
          router.push('/login')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 500:
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

/**
 * 发送 LLM 流式 POST 请求。
 *
 * @param url 请求地址
 * @param data 请求体数据
 * @param onChunk 流式片段回调
 * @param onComplete 完成回调
 * @param onError 错误回调
 * @returns 无返回值
 */
export async function streamPost(
  url: string,
  data?: unknown,
  onChunk?: (chunk: string) => void,
  onComplete?: () => void,
  onError?: (error: Error) => void,
): Promise<void> {
  try {
    const token = sessionStorage.getItem('token')
    const response = await fetch(`${LLMService.defaults.baseURL}${url}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain',
        Accept: 'text/plain',
        'Cache-Control': 'no-cache',
        Connection: 'keep-alive',
        ...(token && { Authorization: `Bearer ${token}` }),
      },
      body: typeof data === 'string' ? data : JSON.stringify(data),
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`)
    }

    if (!response.body) {
      throw new Error('No response body')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')

    let totalChunks = 0

    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        break
      }

      const chunk = decoder.decode(value, { stream: true })
      if (chunk) {
        totalChunks++
        onChunk?.(chunk)
      }
    }

    onComplete?.()
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : '未知错误'
    console.error('Stream request error:', error)
    onError?.(new Error(errorMessage))
  }
}
