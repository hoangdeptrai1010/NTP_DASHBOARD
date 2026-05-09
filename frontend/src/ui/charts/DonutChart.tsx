import { type CSSProperties } from 'react'

export interface DonutItem {
  label: string
  value: number
  percentage: number
  color: string
}

interface DonutChartProps {
  data: DonutItem[]
  title: string
}

export function DonutChart({ data, title }: DonutChartProps) {
  const total = data.reduce((sum, item) => sum + item.value, 0)
  
  const center = 50
  const radius = 35
  const circumference = 2 * Math.PI * radius

  // Build segments with cumulative offset
  let cumulativeOffset = 0
  const segments = data.map((item, index) => {
    const fraction = total > 0 ? item.value / total : 0
    const dashLen = fraction * circumference
    const gapLen = circumference - dashLen
    const offset = -cumulativeOffset + circumference * 0.25 // start from top
    cumulativeOffset += dashLen
    return { ...item, dashLen, gapLen, offset, index }
  })

  return (
    <div className="panel donut-chart-panel" style={{ flex: 1 }}>
      <h3 className="panel-title" style={{ marginBottom: '1.5rem', fontSize: '1rem' }}>{title}</h3>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'center' }}>
        <div className="donut-wrapper">
          <svg viewBox="0 0 100 100" width="160" height="160" className="donut-svg">
            {/* Background ring */}
            <circle cx={center} cy={center} r={radius} fill="transparent" stroke="var(--border)" strokeWidth="16" opacity="0.5" />
            
            {total === 0 ? null : segments.map((seg) => (
              <circle
                key={seg.label}
                className="donut-segment"
                cx={center}
                cy={center}
                r={radius}
                fill="transparent"
                stroke={seg.color}
                strokeWidth="16"
                strokeDasharray={`${seg.dashLen} ${seg.gapLen}`}
                strokeDashoffset={seg.offset}
                strokeLinecap="round"
                style={{
                  '--donut-delay': `${seg.index * 200}ms`,
                  '--donut-dash': `${seg.dashLen}`,
                  '--donut-gap': `${seg.gapLen}`,
                  '--donut-offset': `${seg.offset}`,
                } as CSSProperties}
              >
                <title>{`${seg.label}: ${seg.value.toLocaleString('vi-VN')} VND (${seg.percentage}%)`}</title>
              </circle>
            ))}

            {/* Center text */}
            <text x={center} y={center - 4} textAnchor="middle" fill="var(--charcoal)" fontSize="8" fontWeight="700">
              {total > 0 ? `${Math.round(data[0]?.percentage ?? 0)}%` : '—'}
            </text>
            <text x={center} y={center + 6} textAnchor="middle" fill="var(--muted)" fontSize="4.5">
              {data[0]?.label ?? ''}
            </text>
          </svg>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {data.map((item, i) => (
            <div 
              key={item.label} 
              className="donut-legend-item"
              style={{ '--legend-delay': `${300 + i * 120}ms` } as CSSProperties}
            >
              <span className="donut-legend-dot" style={{ backgroundColor: item.color }} />
              <div>
                <div style={{ fontSize: '0.875rem', fontWeight: 600 }}>{item.label}</div>
                <div style={{ fontSize: '0.8125rem', color: 'var(--muted)' }}>
                  {item.percentage}% · {item.value.toLocaleString('vi-VN')}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
