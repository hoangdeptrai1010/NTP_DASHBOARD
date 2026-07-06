import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { fetchDepartments, fetchAnalysis } from '../services/dashboardService'
import { authStore } from '../stores/authStore'
import type { Period } from '../types'
import { DonutChart } from '../ui/charts/DonutChart'
import { StackedBarChart } from '../ui/charts/StackedBarChart'

const periods: Array<{ value: Period; label: string }> = [
  { value: 'month', label: 'Tháng' },
  { value: 'quarter', label: 'Quý' },
  { value: 'year', label: 'Năm' },
]

export function ReportPage() {
  const user = authStore((state) => state.user)
  const canPickDepartment = user?.role === 'DIRECTOR'
  const [period, setPeriod] = useState<Period>('month')
  const [departmentId, setDepartmentId] = useState<number | null>(null)

  const departmentsQuery = useQuery({
    queryKey: ['departments'],
    queryFn: fetchDepartments,
  })

  const effectiveDepartmentId = canPickDepartment ? departmentId : user?.departmentId ?? null

  const analysisQuery = useQuery({
    queryKey: ['dashboard-analysis', period, effectiveDepartmentId],
    queryFn: () => fetchAnalysis(period, effectiveDepartmentId),
  })

  const analysis = analysisQuery.data
  const departments = departmentsQuery.data ?? []

  return (
    <div className="dashboard-page">
      <header className="page-header">
        <div className="page-heading">
          <span className="eyebrow">Báo cáo</span>
          <h1>Phân tích BHYT & Điều trị</h1>
        </div>

        <div className="toolbar-controls">
          {periods.map((item) => (
            <button
              key={item.value}
              className={item.value === period ? 'filter-chip active' : 'filter-chip'}
              onClick={() => setPeriod(item.value)}
              type="button"
            >
              {item.label}
            </button>
          ))}

          {canPickDepartment ? (
            <select
              className="department-select"
              onChange={(event) =>
                setDepartmentId(event.target.value ? Number(event.target.value) : null)
              }
              value={departmentId ?? ''}
            >
              <option value="">Tất cả khoa</option>
              {departments.map((department) => (
                <option key={department.id} value={department.id}>
                  {department.name}
                </option>
              ))}
            </select>
          ) : null}
        </div>
      </header>

      {analysisQuery.isLoading ? <div className="panel">Đang tải dữ liệu phân tích...</div> : null}
      {analysisQuery.isError ? <div className="panel" style={{ color: 'red' }}>Lỗi khi tải dữ liệu. Hãy chắc chắn bạn đã KHỞI ĐỘNG LẠI Spring Boot Backend!</div> : null}
      
      {analysis ? (
        <>
          <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap' }}>
            <DonutChart 
              title="Tỉ lệ điều trị Nội trú / Ngoại trú" 
              data={analysis.treatmentRatio.map((item, i) => ({
                ...item,
                color: i === 0 ? '#1D9E75' : i === 1 ? '#F4B41A' : '#1C1C1C'
              }))} 
            />
            <DonutChart 
              title="Tỉ lệ BHYT / Bệnh nhân tự trả" 
              data={analysis.paymentRatio.map((item, i) => ({
                ...item,
                color: i === 0 ? '#378ADD' : '#FF8A65'
              }))} 
            />
          </div>
          <StackedBarChart 
            title="Top 10 Khoa có doanh thu cao nhất (BHYT vs Tự trả)" 
            data={analysis.topDepartments} 
          />
        </>
      ) : null}
    </div>
  )
}
