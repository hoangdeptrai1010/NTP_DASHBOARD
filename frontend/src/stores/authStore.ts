import { create } from 'zustand'
import type { Role, UserProfile } from '../types'

interface AuthState {
  accessToken: string | null
  user: UserProfile | null
  isBootstrapped: boolean
  setSession: (token: string, user: UserProfile) => void
  setAccessToken: (token: string) => void
  finishBootstrap: () => void
  logout: () => void
}

export const authStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  isBootstrapped: false,
  setSession: (token, user) => set({ accessToken: token, user, isBootstrapped: true }),
  setAccessToken: (token) => set({ accessToken: token }),
  finishBootstrap: () => set({ isBootstrapped: true }),
  logout: () => set({ accessToken: null, user: null, isBootstrapped: true }),
}))

export const roleLabels: Record<Role, string> = {
  DIRECTOR: 'Director',
  DEPARTMENT_HEAD: 'Department head',
  DOCTOR: 'Doctor',
}
