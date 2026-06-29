import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})
const LLMService = axios.create({
  baseURL: 'http://localhost:8000',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})
let isErrorMessageShown = false // 全局变量，用于控制错误提示是否已经显示
// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 获取本地存储的 token
    const token = sessionStorage.getItem('token')
    if (token) {
      // 添加 token 到请求头
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error) // 打印错误信息
    return Promise.reject(error)
  },
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 返回格式为 { code, data, message }
    const res = response.data

    // 示例：根据业务状态码判断请求是否成功
    if (res.code !== 200) {
      if (!isErrorMessageShown) {
        ElMessage.error(res.message || '请求失败')
        isErrorMessageShown = true // 标记错误提示已显示
        setTimeout(() => {
          isErrorMessageShown = false // 一段时间后重置状态
        }, 3000) // 例如3秒后允许再次显示错误提示
      }

      // 特殊状态码处理
      if (res.code === 401) {
        // 未登录状态，跳转到登录页
        router.push('/login')
      }

      // 返回 reject 使请求进入 catch 流程
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    // 请求成功，返回数据
    return res.data // 只返回核心数据部分
  },
  (error: AxiosError) => {
    // 处理 HTTP 错误
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
      // 处理网络错误（如超时）
      if (error.message.includes('timeout')) {
        ElMessage.error('请求超时')
      } else {
        ElMessage.error('网络连接错误')
      }
    }

    return Promise.reject(error)
  },
)

export function get<T = unknown>(
  url: string,
  params?: Record<string, unknown>,
  config?: AxiosRequestConfig,
): Promise<T> {
  return service.get(url, { params, ...config })
}

export function post<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  return service.post(url, data, config)
}

export function put<T = unknown>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig,
): Promise<T> {
  return service.put(url, data, config)
}

export function del<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return service.delete(url, config)
}

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

    // console.log('开始读取流式响应...')
    let totalChunks = 0

    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        // console.log('流式读取完成')
        break
      }

      const chunk = decoder.decode(value, { stream: true })
      if (chunk) {
        totalChunks++
        // console.log(`收到第 ${totalChunks} 个chunk:`, chunk)
        onChunk?.(chunk)
      }
    }

    // console.log(`总共收到 ${totalChunks} 个chunks`)
    onComplete?.()
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : '未知错误'
    console.error('Stream request error:', error)
    onError?.(new Error(errorMessage))
  }
}
