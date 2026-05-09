import { api } from './api'
import type { LoginResponse } from '../types'

export async function login(payload: {
  username: string
  password: string
}) {
  const { data } = await api.post<LoginResponse>('/api/auth/login', payload)
  return data
}

export async function refreshSession() {
  const { data } = await api.post<LoginResponse>(
    '/api/auth/refresh',
    {},
    { _skipAuthRefresh: true },
  )
  return data
}

export async function logout() {
  await api.post('/api/auth/logout')
}
