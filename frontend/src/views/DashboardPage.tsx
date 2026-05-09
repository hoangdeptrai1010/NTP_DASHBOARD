import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useChart } from '../hooks/useChart'
import { fetchDepartments, fetchSnapshot } from '../services/dashboardService'
import { authStore } from '../stores/authStore'
import type { Period } from '../types'
import { KpiCard } from '../ui/KpiCard'
import { RevenuePanel } from '../ui/RevenuePanel'
import { DonutChart } from '../ui/charts/DonutChart'
import { StackedBarChart } from '../ui/charts/StackedBarChart'

const periods: Array<{ value: Period; label: string }> = [
  { value: 'week', label: 'Tháng' },
  { value: 'quarter', label: 'Quý' },
  { value: 'year', label: 'Năm' },
]

export function DashboardPage() {
  const user = authStore((state) => state.user)
  const canPickDepartment = user?.role === 'DIRECTOR'
  const revenueChart = useChart('bar')
  const [period, setPeriod] = useState<Period>('week')
  const [departmentId, setDepartmentId] = useState<number | null>(null)

  const departmentsQuery = useQuery({
    queryKey: ['departments'],
    queryFn: fetchDepartments,
  })

  const effectiveDepartmentId = canPickDepartment ? departmentId : user?.departmentId ?? null

  const snapshotQuery = useQuery({
    queryKey: ['dashboard-snapshot', period, effectiveDepartmentId],
    queryFn: () => fetchSnapshot(period, effectiveDepartmentId),
  })

  const snapshot = snapshotQuery.data
  const departments = departmentsQuery.data ?? []
  const scopeLabel = canPickDepartment
    ? departmentId
      ? departments.find((department) => department.id === departmentId)?.name ?? 'Khoa đã chọn'
      : 'Toàn bệnh viện'
    : user?.departmentName ?? 'Khoa hiện tại'

  return (
    <div className="dashboard-page">
      <header className="page-header">
        <div className="page-heading">
          <span className="eyebrow">Tổng quan</span>
          <div className="page-heading__row">
            <h1>Dashboard tổng quan</h1>
            <span className="live-pill">Oracle live</span>
          </div>
          <p>
            Theo dõi doanh thu, lưu lượng điều trị và tín hiệu vận hành cho{' '}
            <strong>{scopeLabel}</strong>.
          </p>
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

      {snapshotQuery.isLoading ? <div className="panel">Đang tải dữ liệu từ Oracle...</div> : null}
      {snapshotQuery.isError ? (
        <div className="panel">Không tải được dashboard. Hãy kiểm tra backend hoặc kết nối Oracle.</div>
      ) : null}

      {snapshot ? (
        <>
          <section className="kpi-grid">
            {snapshot.kpis.map((item) => (
              <KpiCard item={item} key={item.key} />
            ))}
          </section>

          <section className="dashboard-board">
            <RevenuePanel
              chartType={revenueChart.chartType}
              onChartTypeChange={revenueChart.setChartType}
              revenue={snapshot.revenue}
            />

            <div className="support-grid">
              {snapshot.topDepartments && snapshot.topDepartments.length > 0 ? (
                <StackedBarChart 
                  title="Top khoa có doanh thu cao nhất" 
                  data={snapshot.topDepartments} 
                />
              ) : (
                <div className="panel">
                  <h3 className="panel-title" style={{ marginBottom: '1rem', fontSize: '1rem' }}>Top khoa có doanh thu cao nhất</h3>
                  <p style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>Đang tải dữ liệu...</p>
                </div>
              )}
              {snapshot.serviceMedicineRatio && snapshot.serviceMedicineRatio.length > 0 ? (
                <DonutChart 
                  title="Tỉ lệ Dịch vụ so với Thuốc" 
                  data={snapshot.serviceMedicineRatio.map((item, i) => ({
                    ...item,
                    color: i === 0 ? '#378ADD' : '#F4B41A'
                  }))} 
                />
              ) : (
                <div className="panel">
                  <h3 className="panel-title" style={{ marginBottom: '1rem', fontSize: '1rem' }}>Tỉ lệ Dịch vụ so với Thuốc</h3>
                  <p style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>Đang tải dữ liệu...</p>
                </div>
              )}
            </div>
          </section>
        </>
      ) : null}
    </div>
  )
}
