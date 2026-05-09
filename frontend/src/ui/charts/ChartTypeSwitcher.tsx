import type { ReactNode } from 'react'
import type { ChartType } from '../../hooks/useChart'

interface ChartTypeSwitcherProps {
  options: ChartType[]
  value: ChartType
  onChange: (type: ChartType) => void
}

const labels: Record<ChartType, string> = {
  bar: 'Cột',
  line: 'Đường',
}

const icons: Record<ChartType, ReactNode> = {
  bar: (
    <svg viewBox="0 0 14 14" width="14" height="14" fill="none">
      <rect x="1" y="6" width="3" height="7" rx="1" fill="currentColor" />
      <rect x="5.5" y="3" width="3" height="10" rx="1" fill="currentColor" />
      <rect x="10" y="1" width="3" height="12" rx="1" fill="currentColor" />
    </svg>
  ),
  line: (
    <svg viewBox="0 0 14 14" width="14" height="14" fill="none">
      <polyline
        fill="none"
        points="1,11 4,6 7,8 10,3 13,5"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.5"
      />
    </svg>
  ),
}

export function ChartTypeSwitcher({ options, value, onChange }: ChartTypeSwitcherProps) {
  return (
    <div className="chart-type-switcher" role="group" aria-label="Chọn loại biểu đồ">
      {options.map((type) => (
        <button
          key={type}
          aria-pressed={value === type}
          className={value === type ? 'ct-btn active' : 'ct-btn'}
          onClick={() => onChange(type)}
          title={labels[type]}
          type="button"
        >
          {icons[type]}
          <span>{labels[type]}</span>
        </button>
      ))}
    </div>
  )
}
