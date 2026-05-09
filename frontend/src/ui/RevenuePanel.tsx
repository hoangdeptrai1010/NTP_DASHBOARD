import type { ChartType } from '../hooks/useChart'
import type { RevenueResponse } from '../types'
import { ChartCard } from './charts/ChartCard'
import { TrendChart } from './charts/TrendChart'

const PERIOD_LABELS: Record<RevenueResponse['period'], string> = {
  week: 'Theo tháng · 12 tháng gần nhất',
  quarter: 'Theo quý · 8 quý gần nhất',
  year: 'Theo năm · Tất cả các năm',
}

const PERIOD_BADGES: Record<RevenueResponse['period'], string> = {
  week: 'Tuần',
  quarter: 'Quý',
  year: 'Năm',
}

const currencyFormatter = new Intl.NumberFormat('vi-VN')

function formatCurrency(value: number) {
  return `${currencyFormatter.format(value)} VND`
}

function getChartColor(chartType: ChartType) {
  switch (chartType) {
    case 'line':
      return '#378ADD'
    default:
      return '#1C1C1C'
  }
}

export function RevenuePanel({
  revenue,
  chartType,
  onChartTypeChange,
}: {
  revenue: RevenueResponse
  chartType: ChartType
  onChartTypeChange: (type: ChartType) => void
}) {
  const totalRevenue = revenue.points.reduce((sum, point) => sum + point.amount, 0)
  const peakPoint =
    revenue.points.length === 0
      ? null
      : revenue.points.reduce((currentPeak, point) =>
          point.amount > currentPeak.amount ? point : currentPeak,
        )

  const averageRevenue =
    revenue.points.length === 0 ? 0 : Math.round(totalRevenue / Math.max(revenue.points.length, 1))

  const highlights = [
    { accent: 'emerald', label: 'Tổng doanh thu', value: formatCurrency(totalRevenue) },
    {
      accent: 'orange',
      label: 'Mốc cao nhất',
      value: peakPoint ? `${peakPoint.label} · ${formatCurrency(peakPoint.amount)}` : '—',
    },
    { accent: 'blue', label: 'Doanh thu bình quân', value: formatCurrency(averageRevenue) },
    { accent: 'violet', label: 'Kỳ hiển thị', value: PERIOD_BADGES[revenue.period] },
  ] as const

  return (
    <ChartCard
      title="Doanh thu theo thời gian"
      subtitle={PERIOD_LABELS[revenue.period] ?? 'Dữ liệu tổng hợp từ Oracle'}
      chartType={chartType}
      typeOptions={['bar', 'line']}
      onTypeChange={onChartTypeChange}
    >
      {revenue.points.length === 0 ? (
        <div className="chart-empty">
          <strong>Chưa có dữ liệu doanh thu cho bộ lọc hiện tại.</strong>
          <p>Thử đổi kỳ xem hoặc phạm vi khoa để nạp thêm dữ liệu.</p>
        </div>
      ) : (
        <>
          <div className="revenue-summary">
            {highlights.map((item) => (
              <div className={`summary-pill summary-pill--${item.accent}`} key={item.label}>
                <span>{item.label}</span>
                <strong>{item.value}</strong>
              </div>
            ))}
          </div>

          <TrendChart
            ariaLabel="Biểu đồ doanh thu theo thời gian"
            color={getChartColor(chartType)}
            data={revenue.points}
            type={chartType}
          />
        </>
      )}
    </ChartCard>
  )
}
