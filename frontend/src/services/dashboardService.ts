import { api } from './api'
import type { DashboardSnapshot, Department, Period } from '../types'

export async function fetchSnapshot(period: Period, departmentId?: number | null) {
  const { data } = await api.get<DashboardSnapshot>('/api/dashboard/snapshot', {
    params: { period, departmentId: departmentId ?? undefined },
  })
  return data
}

export async function fetchDepartments() {
  const { data } = await api.get<Department[]>('/api/departments')
  return data
}

export async function fetchAnalysis(period: Period, departmentId?: number | null) {
  const { data } = await api.get<import('../types').AnalysisResponse>('/api/dashboard/analysis', {
    params: { period, departmentId: departmentId ?? undefined },
  })
  return data
}

