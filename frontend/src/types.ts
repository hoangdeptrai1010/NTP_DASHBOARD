export type Role = 'DIRECTOR' | 'DEPARTMENT_HEAD' | 'DOCTOR'
export type Period = 'week' | 'quarter' | 'year'

export interface UserProfile {
  id: number
  username: string
  fullName: string
  role: Role
  departmentId: number
  departmentName: string
}

export interface LoginResponse {
  accessToken: string
  user: UserProfile
}

export interface KpiCard {
  key: string
  label: string
  value: string
  trend: string
  tone: string
}

export interface RevenuePoint {
  label: string
  amount: number
}

export interface RevenueResponse {
  period: Period
  departmentId: number | null
  points: RevenuePoint[]
}


export interface DashboardSnapshot {
  kpis: KpiCard[]
  revenue: RevenueResponse
  serviceMedicineRatio: RatioItem[]
  topDepartments: DeptCompareItem[]
}

export interface Department {
  id: number
  name: string
}

export interface RatioItem {
  label: string
  value: number
  percentage: number
}

export interface DeptCompareItem {
  deptName: string
  bhytAmount: number
  patientAmount: number
}

export interface AnalysisResponse {
  treatmentRatio: RatioItem[]
  paymentRatio: RatioItem[]
  topDepartments: DeptCompareItem[]
}
