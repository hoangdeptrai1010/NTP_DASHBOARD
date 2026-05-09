import axios, { type AxiosRequestConfig } from 'axios'
import { authStore } from '../stores/authStore'
import type { LoginResponse } from '../types'

declare module 'axios' {
  interface AxiosRequestConfig {
    _retry?: boolean
    _skipAuthRefresh?: boolean
  }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL?.trim() || undefined

export const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
})

let refreshPromise: Promise<string> | null = null

function setAuthorizationHeader(config: AxiosRequestConfig, token: string) {
  if (!config.headers) {
    config.headers = {}
  }

  const headers = config.headers as {
    Authorization?: string
    set?: (name: string, value: string) => void
  }

  if (typeof headers.set === 'function') {
    headers.set('Authorization', `Bearer ${token}`)
    return
  }

  headers.Authorization = `Bearer ${token}`
}

async function refreshAccessToken() {
  const { data } = await api.post<LoginResponse>(
    '/api/auth/refresh',
    {},
    { _skipAuthRefresh: true },
  )
  authStore.getState().setSession(data.accessToken, data.user)
  return data.accessToken
}

api.interceptors.request.use((config) => {
  const token = authStore.getState().accessToken
  if (token) {
    setAuthorizationHeader(config, token)
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as AxiosRequestConfig | undefined
    const shouldRetry =
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !originalRequest._skipAuthRefresh

    if (shouldRetry) {
      originalRequest._retry = true

      try {
        refreshPromise ??= refreshAccessToken().finally(() => {
          refreshPromise = null
        })

        const accessToken = await refreshPromise
        authStore.getState().setAccessToken(accessToken)
        setAuthorizationHeader(originalRequest, accessToken)
        return api(originalRequest)
      } catch {
        authStore.getState().logout()
      }
    }

    return Promise.reject(error)
  },
)
