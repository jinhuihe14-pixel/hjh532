import axios, { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import { message } from 'antd'
import { useAuthStore } from '@/store/auth'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = useAuthStore.getState().token
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      message.error(res.message || '请求失败')
      if (res.code === 401) {
        useAuthStore.getState().logout()
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  (error) => {
    const status = error.response?.status
    const messageText = error.response?.data?.message || error.message
    if (status === 401) {
      useAuthStore.getState().logout()
      window.location.href = '/login'
    } else if (status === 403) {
      message.error('权限不足')
    } else {
      message.error(messageText)
    }
    return Promise.reject(error)
  }
)

export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export default service
