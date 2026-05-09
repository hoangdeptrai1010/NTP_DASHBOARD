import type { ReactNode } from 'react'
import type { ChartType } from '../../hooks/useChart'
import { ChartTypeSwitcher } from './ChartTypeSwitcher'

interface ChartCardProps {
  title: string
  subtitle?: string
  children: ReactNode
  chartType?: ChartType
  typeOptions?: ChartType[]
  onTypeChange?: (type: ChartType) => void
}

export function ChartCard({
  title,
  subtitle,
  children,
  chartType,
  typeOptions,
  onTypeChange,
}: ChartCardProps) {
  return (
    <section className="chart-card">
      <div className="chart-card__header">
        <div className="chart-card__meta">
          <h2 className="chart-card__title">{title}</h2>
          {subtitle ? <p className="chart-card__sub">{subtitle}</p> : null}
        </div>
        {chartType && typeOptions && onTypeChange ? (
          <ChartTypeSwitcher onChange={onTypeChange} options={typeOptions} value={chartType} />
        ) : null}
      </div>
      <div className="chart-card__body">{children}</div>
    </section>
  )
}
